package qdrop.ses;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;



import JCommonTools.CC;

public class Query {
	
	public enum Type {query, folder, link};
	
    /**
     *  Уникальный идентификатор запроса.
     */
    public int Code;
    /**
     *  Название запроса.
     */
    public String Name;
    /**
     * Тип запроса
     */
    public Type TypeItem;
    /**
     *  Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE 
     *  statement or an SQL statement that returns nothing.
     */
    public boolean IsExecuteUpdate;
    /**
     *  Дата создания запроса. 
     */
    public Date DateCreate;
    /**
     *  Дата последнего изменения запроса.
     */
    public Date DateLastModified;
    /**
     *  Флаг видимости запроса.
     *  true - видим в пользовательском режиме
     *  false - невидим в пользовательском режиме 
     */
    public boolean Hidden;    
    /**
     *  Автор запроса.
     */
	public String Author;
    /**
     *  Замечания к запросу.
     */
	public String Note;
	/**
	 * Код БД для которой создан запрос.
	 * Если код = 0, то это DBCode "предка", если у предка = 0, то у сессии DBCodeDefault.
	 */
	public int DBCode;
    /**
     *  Текст запроса. SQL выражение.
     */
	public String SQL;
    /**
     *  Текст XSLT преобразования.
     */
	public String XSLT;
    /**
     *  Имя файла с изображением (*.ico, *.jpg и т.п.) для запроса.
     */
	public String ImageName;
    /** 
     * Коллекция параметров.
     */
    @XmlElementWrapper (name = "Params")
    @XmlElement (name = "Param")
    public ArrayList<Param> Params;

    /**
     * Предок.
     */
    //public Query Parent;
    /**
     * Коллекция "детей", подзапросов
     */
    @XmlElementWrapper (name = "Children4")
    @XmlElement(name="Query4")
    public ArrayList<Query> Children;
    
    /**
     *  "Ссылка" на элемент - код.
     */
    public int Link;
    
    
    public Query()
    {
        Code = 0;
        DBCode = 0;
        Name = "New Query (v4)";
        TypeItem = Type.query;
        IsExecuteUpdate = false;
        DateCreate = new Date();
        DateLastModified = new Date();
        Hidden = false;
        Author = CC.STR_EMPTY;
        Note = CC.STR_EMPTY;
        SQL = null; //CC.STR_EMPTY;
        XSLT = null; //CC.STR_EMPTY;
        ImageName = CC.STR_EMPTY;
        Link = 0;

        Params = new ArrayList<Param>();
        //Parent = null;
        Children = null;
    }

    @Override
    public String toString() 
    {
    	return Name;
    }
}
