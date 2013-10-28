package qdrop.ses;

import java.util.Date;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;



import JCommonTools.CC;

/**
 * ������, ������ ��� ����� �� ������/������
 * @author U1168
 * create: 13.10.2009
 * update: 22.10.2009
 */
public class Query3 
{
    /**
     *  ���������� ������������� �������.
     */
    public int Code;
    /**
     *  �������� �������.
     */
    public String Name;
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
     *  ����� �������. SQL ���������.
     */
	public String Text;
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
    public ArrayList<Param3> Params;
    
    public Query3()
    {
	    Code = 0;
	    Name = "New Query (v4)";
	    DateCreate = new Date();
	    DateLastModified = new Date();
	    Hidden = false;
	    Author = CC.STR_EMPTY;
	    Note = CC.STR_EMPTY;
	    Text = null; //CC.STR_EMPTY;
	    XSLT = null; //CC.STR_EMPTY;
	    ImageName = CC.STR_EMPTY;
	
	    Params = new ArrayList<Param3>();
    }

    @Override
    public String toString() 
    {
    	return Name;
    }
}
