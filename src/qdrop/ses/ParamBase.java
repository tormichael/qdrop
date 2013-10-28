package qdrop.ses;

import java.text.DateFormat;
import java.util.Date;

import JCommonTools.CC;
import JCommonTools.Convert;
import JCommonTools.ItemValDisp;
import JCommonTools.PlaceCode;
import JCommonTools.PlaceCodeText;

/***
 * Set base parameters fields.
 * @author Tor
 * 20.01.2011
 */
public class ParamBase implements Cloneable
{
    /**
     *  ¬нутреннее (используемое в SQL) им€ параметра.
     */
	public String Name;
    /**
     *  “ип параметра. —м. перечисление eQueryParamType.
     */
	public eQueryParamType Type;
    /**
     * “екущее значение параметра. 
     */
	public String Value;
    /**
     * —пособ установки параметра.
     * False - стандартна€ дл€ OLEDB встатка параметров дл€ SQL запроса.
     * True - проста€ замена текста.
     * –азличие актуально только дл€ SQL, в XSLT вставка всегда выполн€етс€ заменой текста. 
     */
	public boolean IsInsert;

    public ParamBase()
    {
        Name = CC.STR_EMPTY;
        Type = eQueryParamType.Undefined;
        Value = CC.STR_EMPTY;
        IsInsert = false;
    }
	
    public ParamBase(String aName, eQueryParamType aType)
    {
        Name = aName;
        Type = aType;
        Value = CC.STR_EMPTY;
        IsInsert = false;
    }
	
    @Override
    public Object clone() throws CloneNotSupportedException 
    {
    	ParamBase prm = (ParamBase) super.clone();
    	prm.Name = new String(Name);
    	prm.Type = Type;
    	prm.IsInsert = IsInsert;
    	prm.Value = new String(Value);
    	return prm;
    }
    
    /// CurrentValue
    // boolean
    public Boolean valGetBool()
    {
    	boolean ret = false;
    	try { ret = Boolean.parseBoolean(Value); }
    	catch (Exception ex) {}
    	return ret;
    }
    public void valSetBool(Boolean aVal)
    {
    	Value = String.valueOf(aVal);
    }
    // Date
    public Date valGetDate ()
    {
    	Date ret = new Date();
    	try { ret = DateFormat.getDateInstance().parse(Value); }
    	catch (Exception ex) {}
    	return ret;
    }
    public void valSetDate(Date aVal)
    {
    	//
    	// Need know, what use date format string ...
    	//
    	//if (SelectValue.length() == 0)
    	//	CurrentValue = DateFormat.getDateInstance().format(aVal);
    	//else
    		Value = DateFormat.getDateInstance().format(aVal);
    }
    // String
    public String valGetString()
    {
    	String ret = CC.STR_EMPTY;
    	switch (Type)
    	{
    	case String:
    		ret = Value;
    		break;
    	case StrSelectList:
    	case StrSelectTree:
    	case StrList:
    	case StrTree:
    		ItemValDisp ivd = new ItemValDisp();
    		ivd.setValueDisplay(Value, String.class.getName());
    		ret = (String)ivd.getValue(); 
    		break;
    	}
    	return ret;
    }
    public void valSetString(String aVal)
    {
    	Value = aVal;
    }
    // Integer
    public Integer valGetInt()
    {
    	Integer ret = 0;
    	switch (Type)
    	{
    	case Integer:
    		ret = Convert.ToIntegerOrZero(Value);
    		break;
    	case IntSelectList:
    	case IntSelectTree:
    	case IntList:
    	case IntTree:
    		ItemValDisp ivd = new ItemValDisp();
    		ivd.setValueDisplay(Value, Integer.class.getName());
    		ret = (Integer)ivd.getValue();
    		break;
    	}
    	return ret == null ? 0 : ret;
    }
    public void valSetInt(Integer aVal)
    {
    	Value = aVal.toString();
    }
    // PlaceCode
    public PlaceCode valGetPC()
    {
    	PlaceCode ret = new PlaceCode();
    	switch (Type)
    	{
    	case PlaceCode:
    		ret = PlaceCode.PDC2PlaceCode(Value);
    		break;
    	case PCSelectList:
    	case PCSelectTree:
    	case PCList:
    	case PCTree:
    		PlaceCodeText pct = new PlaceCodeText(Value);
    		ret = pct;
    		break;
    	}
    	return ret;
    }
    
	
}
