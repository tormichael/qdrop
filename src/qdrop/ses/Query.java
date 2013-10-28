package qdrop.ses;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;



import JCommonTools.CC;

public class Query {
	
	public enum Type {query, folder, link};
	
    /**
     *  ���������� ������������� �������.
     */
    public int Code;
    /**
     *  �������� �������.
     */
    public String Name;
    /**
     * ��� �������
     */
    public Type TypeItem;
    /**
     *  Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE 
     *  statement or an SQL statement that returns nothing.
     */
    public boolean IsExecuteUpdate;
    /**
     *  ���� �������� �������. 
     */
    public Date DateCreate;
    /**
     *  ���� ���������� ��������� �������.
     */
    public Date DateLastModified;
    /**
     *  ���� ��������� �������.
     *  true - ����� � ���������������� ������
     *  false - ������� � ���������������� ������ 
     */
    public boolean Hidden;    
    /**
     *  ����� �������.
     */
	public String Author;
    /**
     *  ��������� � �������.
     */
	public String Note;
	/**
	 * ��� �� ��� ������� ������ ������.
	 * ���� ��� = 0, �� ��� DBCode "������", ���� � ������ = 0, �� � ������ DBCodeDefault.
	 */
	public int DBCode;
    /**
     *  ����� �������. SQL ���������.
     */
	public String SQL;
    /**
     *  ����� XSLT ��������������.
     */
	public String XSLT;
    /**
     *  ��� ����� � ������������ (*.ico, *.jpg � �.�.) ��� �������.
     */
	public String ImageName;
    /** 
     * ��������� ����������.
     */
    @XmlElementWrapper (name = "Params")
    @XmlElement (name = "Param")
    public ArrayList<Param> Params;

    /**
     * ������.
     */
    //public Query Parent;
    /**
     * ��������� "�����", �����������
     */
    @XmlElementWrapper (name = "Children4")
    @XmlElement(name="Query4")
    public ArrayList<Query> Children;
    
    /**
     *  "������" �� ������� - ���.
     */
    public int Link;
    
    
    public Query()
    {
        Code = 0;
        DBCode = 0;
        Name = "New Query (v4)";
        TypeItem = Type.query;
        IsExecuteUpdate = false;
        DateCreate = new Date();
        DateLastModified = new Date();
        Hidden = false;
        Author = CC.STR_EMPTY;
        Note = CC.STR_EMPTY;
        SQL = null; //CC.STR_EMPTY;
        XSLT = null; //CC.STR_EMPTY;
        ImageName = CC.STR_EMPTY;
        Link = 0;

        Params = new ArrayList<Param>();
        //Parent = null;
        Children = null;
    }

    @Override
    public String toString() 
    {
    	return Name;
    }
}
