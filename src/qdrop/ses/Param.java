package qdrop.ses;

//import com.sun.org.apache.xpath.internal.operations.Variable;

import JCommonTools.CC;

/**
 * ѕараметр запроса.
 * @author U1168
 * create: 13.10.2009
 * update: 24.12.2009
 */
public class Param extends ParamBase  
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
     *  «начение поумолчанию.
     */
	public String DefaultValue;
	/**
	 *  оманда дл€ отображени€ содержани€ дл€ текущей позиции параметра
	 */
	public String CommandShowContent;
    /**
     *  SQL запрос или перечисление допустимых значений параметра.
     */
	public String SelectText;
	
    public Param()
    {
    	super();
        Number = 0;
        Title = CC.STR_EMPTY;
        DefaultValue = CC.STR_EMPTY;
        CommandShowContent = CC.STR_EMPTY;
        SelectText = CC.STR_EMPTY;
    }
    
    @Override
	public Object clone()  throws CloneNotSupportedException 
    {
    	Param prm = (Param) super.clone();
    	
    	prm.Number = Number;
    	prm.Title = new String(Title);
    	prm.DefaultValue = new String(DefaultValue);
    	prm.CommandShowContent = new String(CommandShowContent);
    	prm.SelectText = new String(SelectText);
    	
    	return prm;
    }
    
}
