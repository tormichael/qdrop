package qdrop;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;

import qdrop.ses.eQueryParamType;

import JCommonTools.ItemValDisp;
import JCommonTools.PlaceCode;

public class ExecuteQueryParamComboList extends ExecuteQueryParam  
{
	//private ArrayList<ItemValDisp> 	_items;
	private DefaultComboBoxModel	_cbModel;
	
//	public ArrayList<ItemValDisp> getItems() 
//	{
//		return _items;
//	}
	
//	public void setItems(ArrayList<ItemValDisp> items) 
//	{
//		_items = items;
//	}
	
	public ExecuteQueryParamComboList(WorkSession aWS, DefaultComboBoxModel aCBModel, eQueryParamType aType)
	{
		super (aWS);
		
		_cbModel = aCBModel;
		mParamType = aType;
	}
	
	@Override
	protected void readRow(ResultSet aRS) throws SQLException
	{
		super.readRow(aRS);
		if (mParamType == eQueryParamType.PCSelectList)
			_cbModel.addElement(new ItemValDisp(new PlaceCode(aRS.getInt(1), aRS.getInt(2)), aRS.getString(3)));
		else if (mParamType == eQueryParamType.IntSelectList)
			_cbModel.addElement(new ItemValDisp(aRS.getInt(1), aRS.getString(2)));
		else
			_cbModel.addElement(new ItemValDisp(aRS.getString(1), aRS.getString(2)));
	}
}
