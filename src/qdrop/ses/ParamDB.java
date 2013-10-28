package qdrop.ses;

import JCommonTools.CC;

public class ParamDB 
{
	/**
	 * Код соединения
	 */
	public int Code;
	/**
	 * Драйвер JDBC 
	 * для регистрации: Class.forName("org.postgresql.Driver")
	 */
	public String Driver;
    /**
     * Строка соединения с БД.
     */
    public String Host;
    /**
     * Имя пользователя БД.
     */
    public String UserName;
    /**
     * Пароль пользователя БД.
     */
    public String Pwd;

    public ParamDB()
    {
    	Code = 0;
    	Driver = CC.STR_EMPTY;
    	Host = CC.STR_EMPTY;
    	UserName = null;
    	Pwd = null;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
    	ParamDB pdb = (ParamDB) super.clone();

    	pdb.Code = Code;
    	pdb.Driver = new String(Driver);
    	pdb.Host = new String(Host);
    	pdb.UserName = new String(UserName);
    	pdb.Pwd = new String(Pwd);
    	
    	return pdb;
    }
}
