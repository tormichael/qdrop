package qdrop;

import java.util.ArrayList;

import qdrop.ses.Param;
import qdrop.ses.ParamBase;
import qdrop.ses.eQueryParamType;

/**
 * Array ParamBase item for each tree node.
 * This sequence:
 * for type IntTree, IntSelectTree -
 * 		[CODE],[TITLE],[IS_FOLDER],[PARAM1],[PARAM2],...[PARAMn]
 * for type StrTree, StrSelectTree -
 * 		[TEXT],[TITLE],[IS_FOLDER],[PARAM1],[PARAM2],...[PARAMn]
 * for type PCTree, PCSelectTree -
 * 		[CODE],[PLACE],[IS_FOLDER],[TITLE],[PARAM1],[PARAM2],...[PARAMn]
 * 
 * @author M.Tor
 * create: 25.01.2011
 */
public class ParamBaseArrayList extends ArrayList<ParamBase> 
{
	public final static String VAR_NAME_CODE = "_code";
	public final static String VAR_NAME_PLACE = "_place";
	public final static String VAR_NAME_TEXT = "_text";
	public final static String VAR_NAME_TITLE = "_title";
	public final static String VAR_NAME_IS_FOLDER = "_isfolder";
	
	public String getTitle()
	{
		return this.get(_colTitleIndex).Value;
	}
	public void setTitle(String aVal)
	{
		this.get(_colTitleIndex).Value = aVal;
	}

	public String getText()
	{
		return this.get(0).Value;
	}
	public void setText(String aVal)
	{
		this.get(0).Value = aVal;
	}
	
	public Integer getCode()
	{
		return this.get(0).valGetInt();
	}
	public void setCode(Integer aVal)
	{
		this.get(0).valSetInt(aVal);
	}

	public Integer getPlace()
	{
		return this.get(1).valGetInt();
	}
	public void setPlace(Integer aVal)
	{
		this.get(1).valSetInt(aVal);
	}
	
	public boolean isFolder()
	{
		return this.get(_colTitleIndex+1).valGetInt()!= 0;
	}
	
	private eQueryParamType _type;
	private int	_colTitleIndex;
	
	public ParamBaseArrayList (eQueryParamType aType)
	{
		_type = aType;
		_colTitleIndex = _type == eQueryParamType.PCSelectTree || _type == eQueryParamType.PCTree ? 2 : 1; 
		// define system variables:
		switch (aType)
		{
		case IntTree:
		case IntSelectTree:
			add(new ParamBase(VAR_NAME_CODE, eQueryParamType.Integer));
			break;
		case StrTree:
		case StrSelectTree:
			add(new ParamBase(VAR_NAME_TEXT, eQueryParamType.String));
			break;
		case PCTree:
		case PCSelectTree:
			add(new ParamBase(VAR_NAME_CODE, eQueryParamType.Integer));
			add(new ParamBase(VAR_NAME_PLACE, eQueryParamType.Integer));
			break;
		}
		add(new ParamBase(VAR_NAME_TITLE, eQueryParamType.String));
		add(new ParamBase(VAR_NAME_IS_FOLDER, eQueryParamType.Integer));
	}
	
	@Override
	public String toString() 
	{
		return this.get(_colTitleIndex).Value;
	}
	
	@Override
	public Object clone()
	{
    	ParamBaseArrayList pba = (ParamBaseArrayList) super.clone();

    	try
    	{
	    	int ii = 0;
	    	for (ParamBase pb : this)
	    		pba.set(ii++, (ParamBase)pb.clone());
	    	pba._type = _type;
	    	pba._colTitleIndex = _colTitleIndex;
    	}
    	catch (CloneNotSupportedException ex)
    	{
    		
    	}
    	
		return pba;
	}
}
