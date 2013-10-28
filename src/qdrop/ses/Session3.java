package qdrop.ses;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import qdrop.WorkSession;

import JCommonTools.CC;

/**
 * Сессия - совокупность запросов объединенных, как правило, или одним соединением с БД
 * или логикой задачи.
 * Версия 3.0
 * @author M.Tor
 * create: 13.10.2009
 * update: 15.12.2009
*/
@XmlRootElement (name = "Session")
public class Session3 
{
    /**
     *  Уникальный идентификатор сессии.
     */
    public int Code;
    /**
     * Название сессии.
     */
    public String Title;
    /**
     * Комментарии к сессии.
     */
    public String Note;
    /**
     * Строка соединения с БД.
     */
    public String DBConnection;
    /**
     *  Начальные символы параметров.
     */
    public String ParamBegDelim;
    /**
     *  Конечные символы параметров.
     */
    public String ParamEndDelim;
    /**
     *  Хеш сумма пароля для входа в режим редактирования/проектирования сессии.
     */
    public String Hash;
    /**
     *  Местонахождение изображение используемых в сессии. 
     */
    public String ImagePath;
    /**
     *  Имя файла с изображением (*.ico, *.jpg и т.п.) для сессии.
     */
    public String ImageName;
    /**
     *  Коллекция общих параметров.
     */
    @XmlElementWrapper (name = "Params")
    @XmlElement (name = "Param")
    public ArrayList<Param3> Params;
    /**
     * Коллекция запросов
     */
    @XmlElementWrapper (name = "Queries")
    @XmlElement (name = "Query")
    public ArrayList<Query3> Queries;
    
    public Session3()
    {
        Code = 0;
        Title = "New Session (v3)";
        Note = CC.STR_EMPTY;
        DBConnection = CC.STR_EMPTY;
        ParamBegDelim = WorkSession.PARAM_BEG_DELIM_DEFAULT;
        ParamEndDelim = WorkSession.PARAM_END_DELIM_DEFAULT;
        Hash = CC.STR_EMPTY;
        ImagePath = CC.STR_EMPTY;
        ImageName = CC.STR_EMPTY;

        Params = new ArrayList<Param3>(4);
        Queries = new ArrayList<Query3>(8);
    }
    
    public Query3 getQuery(int aCode)
    {
    	Query3 ret = null;
    	for (Query3 qq : Queries)
    		if (qq.Code == aCode)
    		{
    			ret = qq;
    			break;
    		}
    	return ret;
    }

    @Override
    public String toString() 
    {
    	return Title;
    }
}
