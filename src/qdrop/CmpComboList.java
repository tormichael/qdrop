package qdrop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import JCommonTools.CC;
import JCommonTools.ItemValDisp;
import JCommonTools.PlaceCode;
import JCommonTools.PlaceCodeText;
import JCommonTools.StructuredQueryLanguage;

import qdrop.ses.Param;
import qdrop.ses.eQueryParamType;

public class CmpComboList extends JComboBox implements iCmpQueryParam
{
	private Thread 		_thr;
	private ExecuteQueryParam _eqParam;
	private WorkSession	_ws;
	private Param		_param;

	public CmpComboList(WorkSession aWS, Param aParam)
	{
		_ws = aWS;
		_param = aParam;
		
		_thr = null;
		_eqParam = null;
		
		addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (e.getActionCommand().equals("comboBoxChanged"))
				{
					ItemValDisp item = (ItemValDisp) CmpComboList.this.getSelectedItem();
					if (item != null)
					{
						switch (_param.Type)
						{
							case PCList:
							case PCSelectList:
								if (item.getValue() instanceof PlaceCodeText)
									_param.Value = ((PlaceCodeText)item.getValue()).getPCText();
								else if (item.getValue() instanceof PlaceCode)
									_param.Value = ((PlaceCode)item.getValue()).getPlaceDelimCode();
								else
									_param.Value = CC.STR_EMPTY;
								break;
							case PlaceCode:
								if (item.getValue() instanceof PlaceCode)
									_param.Value = ((PlaceCode)item.getValue()).getPlaceDelimCode();
								else
									_param.Value = CC.STR_EMPTY;
								break;
							default:
								_param.Value = item.getValue().toString();
								break;
						}
						
					}
				}
			}
		});
	}
	
	@Override
	public void Load ()
	{
		DefaultComboBoxModel cbm = new DefaultComboBoxModel();  
		_eqParam = new ExecuteQueryParamComboList(_ws, cbm, _param.Type);
		
		this.setModel(cbm);
		 _load(cbm);
	}
	@Override
	public void Reload()
	{
		DefaultComboBoxModel cbm = (DefaultComboBoxModel)this.getModel();
		cbm.removeAllElements();
		_load(cbm);
	}
	
	@Override
	public String getState() 
	{
		ResourceBundle bnd = _ws.getResourceBundle();
		
		String ret = bnd.getString("Text.Ok"); //CC.STR_EMPTY;
		if (_thr != null)
		{
			if (_thr.getState() == State.RUNNABLE)
				ret = bnd.getString("Text.ExecQueryParam.sbExecution");
			else if (_eqParam != null && _eqParam.isErrSQL())
				ret = _eqParam.getErrSQL();
			//else
				//ret = ;
		}
		
		return ret;
	}
	
	private void _load(DefaultComboBoxModel aCbm)
	{
		String ss[] = _param.SelectText.split(";");
		
		for (int ii=0; ii < ss.length; ii++)
		{
			if (StructuredQueryLanguage.isSelectClause(ss[ii]))
			{
				_eqParam.setParamSql(ss[ii]);
				_thr = new Thread(_eqParam);
				_thr.start();
			}
			else
			{
				String ss2[] = ss[ii].split(CC.NEW_LINE); 
				for(int jj=0; jj < ss2.length; jj++)
				{
					String ss3[] = ss2[jj].split("~");
	    			if (_param.Type == eQueryParamType.PCSelectList)
	    				aCbm.addElement(new ItemValDisp(PlaceCode.PDC2PlaceCode(ss3[0]), ss3[1]));
   					else
   						aCbm.addElement(new ItemValDisp(ss3[0], ss3[1]));
					
				}
			}
		}
	}
}
