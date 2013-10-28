package qdrop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import qdrop.ses.ParamDB;

public class WorkDB 
{
	private Connection 		_cn;
	private ParamDB			_pdb;

	private Logger 		_logger;
	
	public Logger get_logger() {
		return _logger;
	}

	public Connection  getConn()
	{
		try
		{
			if ((_cn == null || _cn.isClosed()) && _pdb != null)
			{
				if (_pdb.UserName != null && _pdb.UserName.length() > 0)
					_cn = DriverManager.getConnection(_pdb.Host, _pdb.UserName, _pdb.Pwd);
				else
					_cn = DriverManager.getConnection(_pdb.Host);
			}
		}
		catch (SQLException ex)
		{
			_logger.log(Level.WARNING, "", ex);
		}
		return _cn;
	}
	
	public ParamDB getParamDB()
	{
		return _pdb;
	}
	public void setParamDB(ParamDB aPDB) throws ClassNotFoundException  
	{
		_pdb = aPDB;
		if (_pdb != null)
			Class.forName(_pdb.Driver);
	}
	
	public WorkDB() throws ClassNotFoundException
	{
		this(null);
	}
	
	public WorkDB(ParamDB aPDB) throws ClassNotFoundException
	{
		_cn = null;
		setParamDB(aPDB);
		
		_logger = Logger.getLogger("qDrop", Start.FN_RESOURCE_TEXT);
	}
}
