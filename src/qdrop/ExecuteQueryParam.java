package qdrop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import qdrop.ses.Param;
import qdrop.ses.ParamBase;
import qdrop.ses.Query;
import qdrop.ses.eQueryParamType;

import JCommonTools.ItemValDisp;
import JCommonTools.PlaceCode;
import JCommonTools.StringHandler;
import JCommonTools.DB.DBWork;

public class ExecuteQueryParam implements Runnable 
{
	protected WorkSession 			mWS;
	protected String				mParamSQL;
	protected eQueryParamType 		mParamType;
	protected String 				mErrSQL;
	protected ArrayList<ParamBase>	mParamsOptional;
	
	public boolean isErrSQL()
	{
		return mErrSQL != null && mErrSQL.length() > 0;
	}
	
	public String getErrSQL() 
	{
		return mErrSQL;
	}
	
	
	public void setParamSql(String aSQLString) 
	{
		mParamSQL = aSQLString;
	}
	
	public void setParamsOptional(ArrayList<ParamBase> aParamsOptional)
	{
		mParamsOptional = aParamsOptional;
	}
	
	public ExecuteQueryParam(WorkSession aWS)
	{
		mWS = aWS;
		mParamSQL = null;
		mParamType = eQueryParamType.Undefined;
		mParamsOptional = null;
	}
	
	@Override
	public void run() 
	{
		mErrSQL = null;
		try
		{
			if (mWS.get_currentQuery() != null && mParamSQL != null)
			{
	    		Query qq = mWS.get_currentQuery();
	        	ArrayList<ParamBase> arrPrm = mWS.getParameters(qq);
	        	if (mParamsOptional != null)
	        	{
	            	for (ParamBase prm : mParamsOptional)
	            		arrPrm.add(prm);
	        	}
	        	String sql = mWS.TranslateSQLReplaceParam (mParamSQL, arrPrm,mWS.get_ses().ParamBegDelim,mWS.get_ses().ParamEndDelim); 
	        	ArrayList<Object> psParam = new ArrayList<Object>();
	        	sql = mWS.TranslateSQL2PreparedStatementSQL(sql,arrPrm,mWS.get_ses().ParamBegDelim,mWS.get_ses().ParamEndDelim, psParam);
	    		
        		DBWork wdb = mWS.getWorkDB(qq);
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
        			if (obj instanceof Boolean)
        				ps.setBoolean(ii, (Boolean)obj);
        			else if (obj instanceof java.util.Date)
        				ps.setDate(ii, new java.sql.Date(((java.util.Date)obj).getTime()));
        			else if (obj instanceof Integer)
        				ps.setInt(ii, (Integer)obj);
        			else if (obj instanceof String)
        				ps.setString(ii, (String)obj);
        			ii++;
        		}
        		//boolean isResult = 
        		ps.execute();
        		ResultSet rs = ps.getResultSet();
        		while(rs.next() && !Thread.currentThread().isInterrupted())
        		{
        			readRow(rs);
        		}
			}
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch (SQLException ex)
		{
			mErrSQL = "[" + ex.getErrorCode() + "] " + ex.getMessage();
		}
		catch (NullPointerException ex)
		{
			StringWriter out = new StringWriter(); 
			ex.printStackTrace(new PrintWriter(out));
			mErrSQL = out.toString();
		}
		catch (Exception ex)
		{
			mErrSQL = ex.getMessage();
		}
		
		finishedRun ();
	}

	protected void readRow (ResultSet aRS) throws SQLException 
	{
		
	}

	protected void finishedRun () 
	{
		
	}
}
