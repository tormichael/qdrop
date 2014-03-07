package qdrop.ses;

import JCommonTools.CC;
import JCommonTools.DB.DBConnectionParam;

public class ParamDB extends DBConnectionParam 
{
	/**
	 * Код соединения
	 */
	public int Code;

    public ParamDB()
    {
    	super();
    	
    	Code = 0;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
    	ParamDB pdb = (ParamDB) super.clone();

    	pdb.Code = Code;
    	
    	return pdb;
    }
}
