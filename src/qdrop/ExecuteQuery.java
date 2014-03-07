package qdrop;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.lang.model.element.ExecutableElement;
import javax.sql.rowset.CachedRowSet;

import JCommonTools.StringHandler;
import JCommonTools.DB.DBWork;

//import com.sun.rowset.CachedRowSetImpl;
//import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;

import qdrop.ses.Param;
import qdrop.ses.ParamBase;
import qdrop.ses.Query;

/**
 * Execute session's queries used multi-thread
 * @author M.Tor
 * @since 10.06.2010
 * @version 1.0.0	
 */
public class ExecuteQuery implements Runnable 
{
	/**
	 * Result table
	 * @author M.Tor
	 *
	 */
    public class ResultTable
    {
    	public String Name;
    	/* ���� ����� ���� ��� ����������� �� �������� � ����� Vector ������������ ArrayList */
    	//public Vector<String> columnsName;
    	//public Vector<Vector<Object>> rows;
    	public String[] columnsName;
    	public ArrayList<Object[]> rows;
    	
    	public ResultTable(String aName)
    	{
    		Name = aName;
    		columnsName = null; // new Vector<String>(10);
    		rows = new ArrayList<Object[]>(); // new Vector<Vector<Object>>();
    	}
    	
    	@Override
    	public String toString() 
    	{
    		return Name;
    	}
    }

	private WorkSession _ws; 
	private String _errSQL;
    private ArrayList<ResultTable> _tabs;
    
    /**
     * Return array of tables - query executed result. 
     * @return - array of tables.
     */
    public ArrayList<ResultTable> getTables ()
    {
    	return _tabs;
    }
	
	public String get_errSQL() 
	{
		return _errSQL;
	}
	public boolean isError()
	{
		return (_errSQL != null && _errSQL.length() > 0);
	}
    
	
	public ExecuteQuery(WorkSession aWS)
	{
		_ws = aWS;
		_tabs = null;
		_errSQL = null;
	}

    /**
     * Execute current query.
     */
	@Override
	public void run() 
	{
		_errSQL = null;

		try
		{
			// DEBUG:
//			for (int ii=1; ii < 60; ii++)
//				Thread.sleep(1000);
	    	if (_ws.get_currentQuery() != null)
	    		_execute(_ws.get_currentQuery());
	    	//else
	    	//	_logger.info(_bnd.getString("Error.CurrentQueryNull"));
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}

	}

//    public void Execute()
//    {
//    	
//    }
    
//    public void Execute_CachedRowSet(Query aQ)
//    {
//    	ArrayList<Param> arrPrm = _ws.getParameters(aQ); 
//    	String sql = _ws.TranslateSQLReplaceParam (aQ.SQL,arrPrm,_ws.get_ses().ParamBegDelim,_ws.get_ses().ParamEndDelim);
//    	
//    	try
//    	{
//    		CachedRowSet crs = new CachedRowSetImpl();
//    		sql = _ws.TranslateSQL2CachedRowSet (sql,arrPrm,_ws.get_ses().ParamBegDelim,_ws.get_ses().ParamEndDelim, crs);
//    		crs.setCommand(sql);
//    		crs.execute(_ws.getWorkDB(aQ).getConn());
//    	}
//    	catch (SQLException ex)
//    	{
//    		
//    	}
//    }
    
    /**
     * Execute query using PreparedStatement technology. 
     * @param aQ - query
     * @since 27.05.2010
     */
    private void _execute(Query aQ) throws InterruptedException
    {
    	ArrayList<ParamBase> arrPrm = _ws.getParameters(aQ); 
    	String sql = _ws.TranslateSQLReplaceParam (aQ.SQL,arrPrm,_ws.get_ses().ParamBegDelim,_ws.get_ses().ParamEndDelim); 
    	ArrayList<Object> psParam = new ArrayList<Object>();
        sql = _ws.TranslateSQL2PreparedStatementSQL(sql,arrPrm,_ws.get_ses().ParamBegDelim,_ws.get_ses().ParamEndDelim, psParam);
    	
    	try
    	{
    		DBWork wdb = _ws.getWorkDB(aQ);
    		StringHandler sHnd = new StringHandler();
    		sHnd.setLevel(Level.INFO);
    		wdb.getLogger().setUseParentHandlers(false); // don't show in stout 
    		wdb.getLogger().addHandler(sHnd);
    		Connection cn = wdb.getConn();
    		if (cn == null)
    			throw new Exception(sHnd.getString());
    		
    		PreparedStatement ps = cn.prepareStatement(sql);
    		int ii = 1;
    		for(Object obj : psParam)
    		{
    			if (obj.getClass() == Boolean.class)
    				ps.setBoolean(ii, (Boolean)obj);
    			else if (obj.getClass() == java.util.Date.class)
    				ps.setDate(ii, new java.sql.Date(((java.util.Date)obj).getTime()));
    			else if (obj.getClass() == Integer.class)
    				ps.setInt(ii, (Integer)obj);
    			else if (obj.getClass() == String.class)
    				ps.setString(ii, (String)obj);
    			ii++;
    		}
    		boolean isResult = ps.execute();
    		boolean done = false;
    		ResultTable tab;
    		_tabs = new ArrayList<ResultTable>();

    		// Cashed result to the mTabs 
    		while (!done && !Thread.currentThread().isInterrupted())
    		{
    			if (isResult)
    			{
    	    		ResultSet rs = ps.getResultSet();
    	    		ResultSetMetaData rsmd = rs.getMetaData();
    	    		tab = new ResultTable("Table"+((Integer)(_tabs.size()+1)).toString());
    	    		tab.columnsName = new String[rsmd.getColumnCount()];
    	    		int jj = 0;
    	    		int jjd = 1;
    	    		if (_ws.get_ses().ColumnNumberName.length() > 0)
    	    		{
    	    			tab.columnsName[jj] = _ws.get_ses().ColumnNumberName;
    	    			jj++; jjd --;
    	    		}
    	    		for (; jj < tab.columnsName.length; jj++)
    	    			tab.columnsName[jj] = rsmd.getColumnName(jj+jjd);

    	    		int rCount = 1;
    	    		while(rs.next() && !Thread.currentThread().isInterrupted())
    	    		{
    	    			jj = 0;
    	    			Object [] row = new Object[rsmd.getColumnCount() + (jjd==0?1:0)];
    	    			if (jjd == 0)
    	    				row[jj++] = rCount++; 
    	    			for (; jj < row.length; jj++)
    	    				row[jj] = rs.getObject(jj+jjd);
    	    			tab.rows.add(row);
    	    		}
    	    		_tabs.add(tab);
    			}
    			else
    			{
    				int updateCount = ps.getUpdateCount();
    				if (updateCount == -1)
    					done = true;
    				//else
    				// 	����� ������� ���-������ ��������� ���-�� ���������� �������
    			}
    			isResult = ps.getMoreResults();
    		}
    	}
    	catch (SQLException ex)
    	{
    		_errSQL = "[" + ex.getErrorCode() + "] " + ex.getMessage();
    	}
    	catch (NullPointerException ex)
    	{
    		StringWriter out = new StringWriter(); 
    		ex.printStackTrace(new PrintWriter(out));
    		_errSQL = out.toString();
    	}
    	catch (Exception ex)
    	{
    		_errSQL = ex.getMessage();
    	}
    	
    }
	
	
}
