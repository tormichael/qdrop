package qdrop.ses;

import JCommonTools.CC;


/**
 * Parameter version 3.
 * @author M.Tor
 * create: 20.01.2011
 */
public class Param3 
{
	/**
	 * ����� �������. ����� �������������� ��� �������������� ����������.
	 */
	public int Number;
    /**
     * ������� (������������) ��� ���������.
     */
	public String Title;
    /**
     *  ���������� (������������ � SQL) ��� ���������.
     */
	public String Name;
    /**
     *  ��� ���������. ��. ������������ eQueryParamType.
     */
	public eQueryParamType Type;
    /**
     * ������ ��������� ���������.
     * False - ����������� ��� OLEDB ������� ���������� ��� SQL �������.
     * True - ������� ������ ������.
     * �������� ��������� ������ ��� SQL, � XSLT ������� ������ ����������� ������� ������. 
     */
	public boolean Inset;
    /**
     * ������� �������� ���������. 
     */
	public String CurrentValue;
    /**
     *  �������� �����������.
     */
	public String DefaultValue;
    /**
     *  SQL ������ ��� ������������ ���������� �������� ���������.
     */
	public String SelectValue;
	
    public Param3()
    {
        Number = 0;
        Title = CC.STR_EMPTY;
        Name = CC.STR_EMPTY;
        Type = eQueryParamType.Undefined;
        Inset = false;
        CurrentValue = CC.STR_EMPTY;
        DefaultValue = CC.STR_EMPTY;
        SelectValue = CC.STR_EMPTY;
    }
}
