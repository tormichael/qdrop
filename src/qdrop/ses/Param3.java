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
	 * Ќомер запроса. ћожет использоватьс€ дл€ упор€дочивани€ параметров.
	 */
	public int Number;
    /**
     * ¬нешнее (отображаемое) им€ параметра.
     */
	public String Title;
    /**
     *  ¬нутреннее (используемое в SQL) им€ параметра.
     */
	public String Name;
    /**
     *  “ип параметра. —м. перечисление eQueryParamType.
     */
	public eQueryParamType Type;
    /**
     * —пособ установки параметра.
     * False - стандартна€ дл€ OLEDB встатка параметров дл€ SQL запроса.
     * True - проста€ замена текста.
     * –азличие актуально только дл€ SQL, в XSLT вставка всегда выполн€етс€ заменой текста. 
     */
	public boolean Inset;
    /**
     * “екущее значение параметра. 
     */
	public String CurrentValue;
    /**
     *  «начение поумолчанию.
     */
	public String DefaultValue;
    /**
     *  SQL запрос или перечисление допустимых значений параметра.
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
