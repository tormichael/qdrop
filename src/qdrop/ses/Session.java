package qdrop.ses;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import qdrop.WorkSession;

import JCommonTools.CC;

/**
 * Сессия - совокупность запросов объединенных, как правило, логикой задачи.
 * Версия 4.0
 * @author M.Tor
 * create: 15.12.2009
 * update: 15.04.2010
*/
@XmlRootElement (name = "Session4")
public class Session {
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
     * Код БД по умолчанию;
     */
    public int DBCodeDefault; 
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
     * Название колонки с номером строки.
     * Если оно не определено, то колонка не добавляется в результирующую таблицу.
     */
    public String ColumnNumberName;
    /**
     *  Коллекция возможных соединений с БД.
     */
    @XmlElementWrapper (name = "ParamDBCol")
    @XmlElement (name = "ParamDB")
    public ArrayList<ParamDB> ParamDBCol;
    /**
     *  Коллекция общих параметров.
     */
    @XmlElementWrapper (name = "Params")
    @XmlElement (name = "Param")
    public ArrayList<Param> Params;
    /**
     * Дерево запросов
     */
    @XmlElementWrapper (name = "Queries4")
    @XmlElement(name="Query4")
    public ArrayList<Query> Queries;
    //public DefaultMutableTreeNode TreeQueries;

    public Session()
    {
    	Init();
        Params = new ArrayList<Param>(4);
        ParamDBCol = new ArrayList<ParamDB>();
        Queries = new ArrayList<Query>();
    }
    
    private void Init()
    {
        Code = 0;
        Title = "New Session (v4)";
        DBCodeDefault = 0;
        Note = CC.STR_EMPTY;
        ParamBegDelim = WorkSession.PARAM_BEG_DELIM_DEFAULT;
        ParamEndDelim = WorkSession.PARAM_END_DELIM_DEFAULT;
        Hash = CC.STR_EMPTY;
        ImagePath = CC.STR_EMPTY;
        ImageName = CC.STR_EMPTY;
        ColumnNumberName = CC.STR_EMPTY; 
    }
    
    public void Clear()
    {
    	Init();
    	Params.clear();
    	ParamDBCol.clear();
    	Queries.clear();
    }
    
    /**
     * Find query in session by code.
     * @param aCode
     * @return found query or null
     */
    public Query getQuery(int aCode)
    {
    	return _findQuery(Queries, aCode);
    }
    
    private Query _findQuery(ArrayList<Query> aQs, int aCode)
    {
    	Query ret = null;
    	for (Query qq : aQs)
    	{
    		if (qq.Children != null && qq.Children.size() > 0)
    		{
    			ret = _findQuery(qq.Children, aCode);
    		         	if (ret != null)
    				break;
    		}
    		if (qq.Code == aCode)
    		{
    			ret = qq;
    			break;
    		}
    	}
    	
    	return ret;
    }

    @Override
    public String toString() 
    {
    	return Title;
    }
    public Query getParent(Query aQ)
    {
    	return _getParent(null, aQ);
    }
    private Query _getParent(Query aQP, Query aQ)
    {
    
    	Query qRet = null;
    	ArrayList<Query> qal;
    	if (aQP == null)
    		qal = Queries;
    	else
    		qal = aQP.Children;
    	
    	for (Query qq : qal)
    	{
    		if (qq.Children != null && qq.Children.size() > 0)
    			qRet = _getParent(qq, aQ);
    		
    		if (qRet == null && qq.equals(aQ))
    			qRet = aQP;
    		
    		if (qRet != null)
    			break;
    	}
    	
    	return qRet;
    }
    
}
