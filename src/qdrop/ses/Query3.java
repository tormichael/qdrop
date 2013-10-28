package qdrop.ses;

import java.util.Date;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;



import JCommonTools.CC;

/**
 * Запрос, группа или ярлык на запрос/группу
 * @author U1168
 * create: 13.10.2009
 * update: 22.10.2009
 */
public class Query3 
{
    /**
     *  Уникальный идентификатор запроса.
     */
    public int Code;
    /**
     *  Название запроса.
     */
    public String Name;
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
     *  Текст запроса. SQL выражение.
     */
	public String Text;
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
    public ArrayList<Param3> Params;
    
    public Query3()
    {
	    Code = 0;
	    Name = "New Query (v4)";
	    DateCreate = new Date();
	    DateLastModified = new Date();
	    Hidden = false;
	    Author = CC.STR_EMPTY;
	    Note = CC.STR_EMPTY;
	    Text = null; //CC.STR_EMPTY;
	    XSLT = null; //CC.STR_EMPTY;
	    ImageName = CC.STR_EMPTY;
	
	    Params = new ArrayList<Param3>();
    }

    @Override
    public String toString() 
    {
    	return Name;
    }
}
