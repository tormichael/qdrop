package qdrop;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//import com.sun.rowset.CachedRowSetImpl;

import qdrop.ses.Param;
import qdrop.ses.Param3;
import qdrop.ses.ParamBase;
import qdrop.ses.ParamDB;
import qdrop.ses.Query;
import qdrop.ses.Query3;
import qdrop.ses.Session;
import qdrop.ses.Session3;

import JCommonTools.CC;
import JCommonTools.PlaceCode;
import JCommonTools.ShowMessageHandler;
/**
 * Working session.
 * 
 * @author M.Tor
 * @since 27.04.2010
 */
public class WorkSession 
{
	public final static String PARAM_BEG_DELIM_DEFAULT = "#?";
	public final static String PARAM_END_DELIM_DEFAULT = "?#";
	public final static String PATH_SEPARATOR = "\\\\";
	public final static String EXECUTE_NO_QUERY_MARK = "ExecuteNonQuery";

	
	private Logger _logger;
	
	private ResourceBundle _bnd;
	public ResourceBundle getResourceBundle()
	{
		return _bnd;
	}
	/**
	 * ����������� ������:
	 */
	private Session _ses;
	public Session get_ses() {
		return _ses;
	}
	/**
	 * �������(�������) ������:
	 */
	private Query _currentQuery;
	public Query get_currentQuery() {
		return _currentQuery;
	}
	public void set_currentQuery(Query currentQuery) {
		_currentQuery = currentQuery;
	}
	public void set_currentQuery(int aCode) {
		//_currentQuery = _ses.getQuery(aCode);
	}
	
	private WorkDB[] _wdb;
	
	private boolean _isLoadSQ3;
	public boolean isLoadSQ3() {
		return _isLoadSQ3;
	}

	public WorkSession(Locale aLoc)
	{
		_bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, aLoc);
		
		_ses = new Session();
    	_logger = Logger.getLogger("qdrop");
		ShowMessageHandler smh  = new ShowMessageHandler();
		smh.setLevel(Level.INFO);
		_logger.addHandler(smh);
		_isLoadSQ3 = false;
		_wdb = null;
		
	}

	/**
	 * Get work data base for defined query.
	 * @param aQ - query
	 * @return WorkDB class
	 */
	public WorkDB getWorkDB(Query aQ)
	{
		Query pq = aQ;
		int dbCode = pq.DBCode;
		while (pq != null && dbCode == 0)
		{
			pq = _ses.getParent(pq);
			if (pq != null)
				dbCode = pq.DBCode;
		}
		
		if (dbCode == 0)
			dbCode = _ses.DBCodeDefault;
		
		if (dbCode > 0)
			for (WorkDB wdb : _wdb)
				if (wdb.getParamDB().Code == dbCode)
					return wdb;
		
		return null;
	}
	
	/**
	 * Load session from file.
	 * @param aFileName - file name
	 * @return result: if okay - true, else - false
	 */
    public boolean Load(String aFileName)
    {
    	boolean ret = true;
    	
		_ses.Clear();
    	try
    	{
    		JAXBContext context = JAXBContext.newInstance(Session.class);
    		Unmarshaller um = context.createUnmarshaller();
    		Object obj = um.unmarshal(new File(aFileName));
    		_ses = (Session) obj;
    	}
    	catch (JAXBException ex)
    	{
    		//_logger.info(ex.getMessage());
    		ex.printStackTrace();
    		ret = false;
    	}
    	
    	_isLoadSQ3 = false;
    	if (!ret)
    	{
    		ret = Load_SQ3(aFileName);
    		_isLoadSQ3 = ret;
    	}
    
    	if (ret)
    	{
    		try
    		{
	    		_wdb = new WorkDB[_ses.ParamDBCol.size()];
	    		for (int ii = 0; ii < _wdb.length; ii++)
	    			_wdb[ii] = new WorkDB(_ses.ParamDBCol.get(ii));
    		}
    		catch (ClassNotFoundException ex)
    		{
    			ex.printStackTrace();
    			ret = false;
    		}
    	}
    	
    	return ret;
    }
    
    private boolean Load_SQ3(String aFileName)
    {
    	boolean ret = true;
    	
		_ses.Clear();
    	try
    	{
    		JAXBContext context = JAXBContext.newInstance(Session3.class);
    		Unmarshaller um = context.createUnmarshaller();
    		Object obj = um.unmarshal(new File(aFileName));
    		Session3 ses3 = (Session3) obj;
    		ConvertSQ3ToSQ4(ses3);
    	}
    	catch (JAXBException ex)
    	{
    		//_logger.info(ex.getMessage());
    		ex.printStackTrace();
    		ret = false;
    	}
    	
    	return ret;
    }
    
    public void Save (String aFileName)
    {
    	try
    	{
    		JAXBContext context = JAXBContext.newInstance(Session.class);
    		Marshaller m = context.createMarshaller();
    		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    		m.marshal(_ses, new File(aFileName));
    	}
    	catch (JAXBException ex)
    	{
    		//_logger.info(ex.getMessage());
    		ex.printStackTrace();
    	}
    }
    
    /**
     * Convert session from format SQ3 to SQ4
     * @param aSes3
     */
    public void ConvertSQ3ToSQ4(Session3 aSes3)
    {
    	try
    	{
    		//1. Clone session header:
	    	_ses.Code = aSes3.Code;
	    	_ses.Title = new String(aSes3.Title);
	    	_ses.Note = new String(aSes3.Note);
	    	_ses.DBCodeDefault = 1;
	    	ParamDB pdb = new ParamDB();
	    	pdb.Code = 1;
	    	pdb.Driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    	pdb.Host = ConvertOLEDBConnection2JDBCHost(aSes3.DBConnection);
	    	_ses.ParamDBCol.add(pdb);
	    	_ses.ParamBegDelim = new String(aSes3.ParamBegDelim);
	    	_ses.ParamEndDelim = new String(aSes3.ParamEndDelim);
	    	_ses.Hash = new String(aSes3.Hash);
	    	_ses.ImagePath = new String(aSes3.ImagePath);
	    	_ses.ImageName = new String(aSes3.ImageName);
	    	//2. Convert session parameters:
	    	for (Param3 prm3 : aSes3.Params)
	    	{
	    		Param prm = new Param();
	    		prm.Number = prm3.Number;
	    		prm.Name = prm3.Name;
	    		prm.Title = prm3.Title;
	    		prm.Type = prm3.Type;
	    		prm.IsInsert = prm3.Inset;
	    		prm.Value = prm3.CurrentValue;
	    		prm.DefaultValue = prm3.DefaultValue;
	    		prm.SelectText = prm3.SelectValue;
	    		_ses.Params.add(prm);
	    	}
	    	//3. Clone session queries:
	    	String [] ss = null;
	    	String [] prevSS = new String [0];
			Stack<Query> ownStack = new Stack<Query>();
			Query q4 = null;
	    	for (Query3 q3 : aSes3.Queries)
	    	{
	    		ss = q3.Name.split(PATH_SEPARATOR);
				int ii = 0;
				for (; ii < prevSS.length && ii < ss.length && prevSS[ii].equals(ss[ii]); ii++)
				{
					continue;
				}
				for(int jj = prevSS.length-ii; jj>0 && ownStack.size()>0; jj--)
					ownStack.pop();
				
				for (; ii < ss.length; ii++)
				{
					boolean folder = (ii != ss.length -1);
					q4 = new Query();
					q4.Code = folder ? 0 : q3.Code;
					q4.Name = ss[ii];
					q4.TypeItem = folder ? Query.Type.folder : Query.Type.query;
					if (!folder)
					{
						if (q3.Text.indexOf(_ses.ParamBegDelim + EXECUTE_NO_QUERY_MARK + _ses.ParamEndDelim) >= 0)
						{
							q4.IsExecuteUpdate = true;
							q4.SQL = q3.Text.replace(_ses.ParamBegDelim + EXECUTE_NO_QUERY_MARK + _ses.ParamEndDelim, CC.STR_EMPTY);
						}
						else
							q4.SQL = new String(q3.Text);
					}	
					q4.DateCreate = q3.DateCreate;
					q4.DateLastModified = q3.DateLastModified;
					q4.Hidden = folder ? false : q3.Hidden;    
					q4.Author = new String(q3.Author);
					q4.Note = folder ? CC.STR_EMPTY : new String(q3.Note);
					q4.XSLT = folder ? CC.STR_EMPTY : new String(q3.XSLT);
					q4.ImageName = folder ? CC.STR_EMPTY : new String(q3.ImageName);
					if (!folder)
				    	for (Param3 prm3 : q3.Params)
				    	{
				    		Param prm = new Param();
				    		prm.Number = prm3.Number;
				    		prm.Name = prm3.Name;
				    		prm.Title = prm3.Title;
				    		prm.Type = prm3.Type;
				    		prm.IsInsert = prm3.Inset;
				    		prm.Value = prm3.CurrentValue;
				    		prm.DefaultValue = prm3.DefaultValue;
				    		prm.SelectText = prm3.SelectValue;
				    		q4.Params.add(prm);
				    	}
					
					if (ownStack.size() > 0)
					{
						Query qq = ownStack.peek();
						if (qq.Children == null)
							qq.Children = new ArrayList<Query>();
						//q4.Parent = qq;
						qq.Children.add(q4);
						//if (q3.Hidden)
						//	node.ForeColor = SystemColors.GrayText;
					}
					else
					{
						_ses.Queries.add(q4);
					}
					ownStack.push(q4);
				}
				prevSS = ss;
	    	}
    	}
    	catch (Exception ex)
    	{
    		_logger.info(ex.getMessage());
    	}
    }
    
    /**
     * Convert OLE DB connection string to JDBC connection string.
     * @param aOLEDBCon - OLE DB connection
     * @return JDBC connection string
     */
    public static String ConvertOLEDBConnection2JDBCHost(String aOLEDBCon)
    {
    	//Provider=SQLOLEDB;Server=CSQL-S.IMD.RU;DataBase=InfoBook3;
    	//Connect TimeOut=60;Integrated Security=SSPI;Persist Security Info=false;
    	
    	String ret =  null;
    	String server = null;
    	String database = null;
    	
    	String ss[] = aOLEDBCon.split(";");
    	for (String si : ss)
    	{
    		if (si.toUpperCase().indexOf("SERVER") >= 0)
    			server = si.substring(si.indexOf("=")+1).trim();
    		else if (si.toUpperCase().indexOf("DATABASE") >= 0)
    			database = si.substring(si.indexOf("=")+1).trim();
    	}
    	
    	if (database != null && server != null)
    		ret = "jdbc:sqlserver://"+server+":1433;databaseName="+database+";integratedSecurity=true";
    	// ;user=MyUserName;password=*****;";

    	
    	return ret;
    }

    /**
     * Generate work array of parameters for current query
     * @since 21.01.2011
     * @return array of parameters
     */
    public ArrayList<ParamBase> getParameters()
    {
    	return getParameters(get_currentQuery());
    }
    
    /**
     * Generate work array of parameters
     * @param aQ - query
     * @since 27.05.2010
     * @return array of parameters
     */
    public ArrayList<ParamBase> getParameters(Query aQ)
    {
    	ArrayList<ParamBase> ret = new ArrayList<ParamBase>();
    	
    	for (Param prm : aQ.Params)
    		ret.add(prm);
    	
    	for (Param prm : _ses.Params)
    		ret.add(prm);
    	
    	return ret;
    }
    
    /**
     * Replace in the SQL statement parameter to their current value.
     * @param aSrcSQL - source SQL statement
     * @param aPrmArr - parameters array
     * @param aPBegDelim - left parameter delimiter
     * @param aPEndDelim - right parameter delimiter
     * @return translated SQL statement
     */
    public String TranslateSQLReplaceParam (
    		String aSrcSQL,
    		ArrayList<? extends ParamBase> aPrmArr,
    		String aPBegDelim, 
    		String aPEndDelim
	)
    {
    	StringBuffer ret = new StringBuffer(aSrcSQL);
    	
    	int ii;
		for (ParamBase prm : aPrmArr)
		{
			if (prm.IsInsert)
			{
				ii = ret.indexOf(aPBegDelim+prm.Name+aPEndDelim);
				if (ii >=0 )
					ret = ret.replace(ii, ii + aPBegDelim.length() + prm.Name.length() + aPEndDelim.length(), prm.Value);
			}
		}    	
    	
    	return ret.toString();
    }
    
    /**
     * Replace to the SQL text parameter #?ppp?# to ? and create array for PreparedStatement
     * parameters.
     * @param aSrcSQL - source SQL statement
     * @param aPrmArr - parameters array
     * @param aPBegDelim - left parameter delimiter
     * @param aPEndDelim - right parameter delimiter
     * @return translated SQL statement
     */
    public String TranslateSQL2PreparedStatementSQL( 
    		String aSrcSQL,
    		ArrayList<? extends ParamBase> aPrmArr,
    		String aPBegDelim, 
    		String aPEndDelim,
    		ArrayList<Object> aPSParam
    )
    {
    	StringBuffer ret = new StringBuffer(aSrcSQL);
    	boolean isFound;
    	int ib;
    	int ie = 0;
    	int ibc1, iec1=-1, ibc2, iec2=-1;
    	String eol = System.getProperty("line.separator");
    	String prmName;
    	
    	//StringReader sr = new StringReader(aSrcSQL);
    	try
    	{
	    	while (true) //isContinue)
	    	{
	    		ibc1 = ret.indexOf("/*", ie);
	    		if (ibc1 != -1)
	    			iec1 = ret.indexOf("*/", ie);
	    		ibc2 = ret.indexOf("--", ie);
	    		if (ibc2 != -1)
	    			iec2 = ret.indexOf(eol, ie);
	    		
	    		ib = ret.indexOf(aPBegDelim, ie);
	    		if (ib == -1)
	    		{
	    			break;
	    		}
	    		ie = ret.indexOf(aPEndDelim, ib+aPBegDelim.length());
	    		if (ie == -1)
	    		{
	    			// ERROR! Not found end delimiter
	    			break;
	    		}
	    		// check if parameter within first type /* ... */ comments
	    		if (ibc1 != -1 && ibc1 < ib && ie < iec1)
	    		{
	    			ie = iec1+2;
	    			continue;
	    		}
	    		
	    		// check if parameter within second type -- ... eol comments
	    		if (ibc2 != -1 && ibc2 < ib && ie < iec2)
	    		{
	    			ie = iec2+2;
	    			continue;
	    		}
	    		prmName = ret.substring(ib + aPBegDelim.length(), ie).toUpperCase();

	    		isFound = false;
	    		////////////////////////////////////////////
	    		for (ParamBase prm : aPrmArr)
	    		{
    				switch (prm.Type)
    				{
    					case Boolean:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
	    						aPSParam.add(prm.valGetBool());
	    	    				isFound = true;
    						}
    						break;
    					case Date:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
	    						aPSParam.add(prm.valGetDate());
	    	    				isFound = true;
    						}
    						break;
    					case Integer:
    					case IntSelectList:
    					case IntSelectTree:
    					case IntList:
    					case IntTree:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
    							aPSParam.add(prm.valGetInt());
	    	    				isFound = true;
    						}
    						break;
    					case PlaceCode:
    					case PCSelectList:
    					case PCSelectTree:
    					case PCList:
    					case PCTree:
    						String ss[] = prm.Name.split(":");
    						PlaceCode pc = prm.valGetPC();
			                if (ss[0].toUpperCase().equals(prmName))
			                {
			                	aPSParam.add((Integer)pc.place);
	    	    				isFound = true;
			                }
			                else if (ss[1].toUpperCase().equals(prmName))
			                {
			                	aPSParam.add((Integer)pc.code);
	    	    				isFound = true;
			                }
    						break;
    					case String:
    					case StrSelectList:
    					case StrSelectTree:
    					case StrList:
    					case StrTree:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
	    						aPSParam.add(prm.valGetString());
	    	    				isFound = true;
    						}
    						break;
    				}
	    		}
	    		////////////////////////////////////////////

	    		if (isFound)
	    		{
	    			ret.replace(ib, ie+aPEndDelim.length(), "?");
	    			ie = ib - aPEndDelim.length() + 1;
	    		}
	    		else
	    		{
	    			// ERROR!!! Parameter not found
	    		}
	    	}
    	}
    	catch (Exception ex)
    	{
    		_logger.info(ex.getMessage());
    	}
    	
    	return ret.toString();
    }
    
    /**
     * Replace to the SQL text parameter #?ppp?# to ? and set parameters value to the
     * CachedRowSet.setXXX(i, value).
     * @param aSrcSQL - source SQL statement
     * @param aPrmArr - parameters array
     * @param aPBegDelim - left parameter delimiter
     * @param aPEndDelim - right parameter delimiter
     * @return translated SQL statement
     */
    public String TranslateSQL2CachedRowSet( 
    		String aSrcSQL,
    		ArrayList<? extends ParamBase> aPrmArr,
    		String aPBegDelim, 
    		String aPEndDelim,
    		CachedRowSet aCRowSet
    )
    {
    	StringBuffer ret = new StringBuffer(aSrcSQL);
    	boolean isFound;
    	int ib;
    	int ie = 0;
    	int ibc1, iec1=-1, ibc2, iec2=-1;
    	String eol = System.getProperty("line.separator");
    	String prmName;
    	
    	try
    	{
    		int ipc = 0;
	    	while (true) //isContinue)
	    	{
	    		ibc1 = ret.indexOf("/*", ie);
	    		if (ibc1 != -1)
	    			iec1 = ret.indexOf("*/", ie);
	    		ibc2 = ret.indexOf("--", ie);
	    		if (ibc2 != -1)
	    			iec2 = ret.indexOf(eol, ie);
	    		
	    		ib = ret.indexOf(aPBegDelim, ie);
	    		if (ib == -1)
	    		{
	    			break;
	    		}
	    		ie = ret.indexOf(aPEndDelim, ib+aPBegDelim.length());
	    		if (ie == -1)
	    		{
	    			// ERROR! Not found end delimiter
	    			break;
	    		}
	    		// check if parameter within first type /* ... */ comments
	    		if (ibc1 != -1 && ibc1 < ib && ie < iec1)
	    		{
	    			ie = iec1+2;
	    			continue;
	    		}
	    		
	    		// check if parameter within second type -- ... eol comments
	    		if (ibc2 != -1 && ibc2 < ib && ie < iec2)
	    		{
	    			ie = iec2+2;
	    			continue;
	    		}
	    		prmName = ret.substring(ib + aPBegDelim.length(), ie - ib - aPBegDelim.length()).toUpperCase();

	    		isFound = false;
	    		////////////////////////////////////////////
	    		for (ParamBase prm : aPrmArr)
	    		{
    				switch (prm.Type)
    				{
    					case Boolean:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
    							aCRowSet.setBoolean(ipc, prm.valGetBool());
	    	    				isFound = true;
    						}
    						break;
    					case Date:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
    							aCRowSet.setDate(ipc, new java.sql.Date(prm.valGetDate().getTime()));
	    	    				isFound = true;
    						}
    						break;
    					case Integer:
    					case IntSelectList:
    					case IntSelectTree:
    					case IntList:
    					case IntTree:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
    							aCRowSet.setInt(ipc, prm.valGetInt());
	    	    				isFound = true;
    						}
    						break;
    					case PlaceCode:
    					case PCSelectList:
    					case PCSelectTree:
    					case PCList:
    					case PCTree:
    						String ss[] = prm.Name.split(":");
    						PlaceCode pc = prm.valGetPC();
			                if (ss[0].toUpperCase().equals(prmName))
			                {
    							aCRowSet.setInt(ipc, (Integer)pc.place);
	    	    				isFound = true;
			                }
			                else if (ss[1].toUpperCase().equals(prmName))
			                {
    							aCRowSet.setInt(ipc, (Integer)pc.code);
	    	    				isFound = true;
			                }
    						break;
    					case String:
    					case StrSelectList:
    					case StrSelectTree:
    					case StrList:
    					case StrTree:
    						if (prm.Name.toUpperCase().equals(prmName))
    						{
    							aCRowSet.setString(ipc, prm.valGetString());
	    	    				isFound = true;
    						}
    						break;
    				}
	    		}
	    		////////////////////////////////////////////
	    		ipc++;

	    		if (isFound)
	    		{
	    			ret.replace(ib, ie+aPEndDelim.length(), "?");
	    			ie = ib - aPEndDelim.length() + 1;
	    		}
	    		else
	    		{
	    			// ERROR!!! Parameter not found
	    		}
	    	}
    	}
    	catch (Exception ex)
    	{
    		_logger.info(ex.getMessage());
    	}
    	
    	return ret.toString();
    }

}
