package qdrop.ses;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import qdrop.WorkSession;

import JCommonTools.CC;

/**
 * ������ - ������������ �������� ������������, ��� �������, ��� ����� ����������� � ��
 * ��� ������� ������.
 * ������ 3.0
 * @author M.Tor
 * create: 13.10.2009
 * update: 15.12.2009
*/
@XmlRootElement (name = "Session")
public class Session3 
{
    /**
     *  ���������� ������������� ������.
     */
    public int Code;
    /**
     * �������� ������.
     */
    public String Title;
    /**
     * ����������� � ������.
     */
    public String Note;
    /**
     * ������ ���������� � ��.
     */
    public String DBConnection;
    /**
     *  ��������� ������� ����������.
     */
    public String ParamBegDelim;
    /**
     *  �������� ������� ����������.
     */
    public String ParamEndDelim;
    /**
     *  ��� ����� ������ ��� ����� � ����� ��������������/�������������� ������.
     */
    public String Hash;
    /**
     *  ��������������� ����������� ������������ � ������. 
     */
    public String ImagePath;
    /**
     *  ��� ����� � ������������ (*.ico, *.jpg � �.�.) ��� ������.
     */
    public String ImageName;
    /**
     *  ��������� ����� ����������.
     */
    @XmlElementWrapper (name = "Params")
    @XmlElement (name = "Param")
    public ArrayList<Param3> Params;
    /**
     * ��������� ��������
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
