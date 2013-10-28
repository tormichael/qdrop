package qdrop;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import JCommonTools.CC;
import JCommonTools.TableTools;
import JCommonTools.TextEditor;

import qdrop.ses.Param;
import qdrop.ses.eQueryParamType;

public class pnlParamEdit extends JPanel 
{
	private ArrayList<Param> 	_params;
	private ParamTableModel 	_tmParam;
	private ResourceBundle 		_bnd;
	private ResourceBundle 		_bndCT;
	private JTable 				_tabParam;
	private JSplitPane 			_sppParam;
	private TextEditor 			_txtParam;
	
	private int					_prevSelectedRow;
	
	private boolean 			_selectValue;
	
	public void set_selectValue(boolean selectValue) {
		_selectValue = selectValue;
	}
	
	public boolean is_selectValue() {
		return _selectValue;
	}
	
	
	public void setParamCollection(ArrayList<Param> aParams)
	{
		_params = aParams;
	}
	
	public String getTabColumnsWidthAsString()
	{
		return TableTools.GetColumnsWidthAsString(_tabParam);
	}
	public void setTabColumnsWidthFromString(String aTxt)
	{
		TableTools.SetColumnsWidthFromString(_tabParam, aTxt);
	}
	
	public pnlParamEdit(ResourceBundle aBndCT, ResourceBundle aBnd)
	{
		_bnd = aBnd;
		_bndCT = aBndCT;

		this.setLayout(new BorderLayout());
		
		_prevSelectedRow = -1;
		_selectValue = true;
		
		_tmParam = new ParamTableModel();
		_tabParam = new JTable(_tmParam);
		_txtParam = new TextEditor();
		_sppParam = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, 
				new JScrollPane(_tabParam), 
				new JScrollPane(_txtParam));
		this.add(_sppParam, BorderLayout.CENTER);
		
		JComboBox cboType = new JComboBox(new DefaultComboBoxModel(eQueryParamType.values()));
		_tabParam.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboType));

		JPopupMenu pppParamTab = new JPopupMenu();
		JMenuItem mnpPTDelete = new JMenuItem(actDeleteRows);
		mnpPTDelete.setText(_bndCT.getString("Menu.Edit.Delete"));
		pppParamTab.add(mnpPTDelete);
		_tabParam.setComponentPopupMenu(pppParamTab);

		_tabParam.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "DeleteTabRows");
		_tabParam.getActionMap().put("DeleteTabRows", actDeleteRows);

		this.addComponentListener(new ComponentAdapter() 
		{
			@Override
			public void componentShown(ComponentEvent e) 
			{
				_sppParam.setDividerLocation(getHeight()/3);
			}
		});

		_tabParam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = _tabParam.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() 
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				if (e.getValueIsAdjusting())
					return;
			
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (!lsm.isSelectionEmpty())
				{
					if (_prevSelectedRow >=0 && _prevSelectedRow < _params.size())
					{
						if (_selectValue)
							_params.get(_prevSelectedRow).SelectText = _txtParam.getText();
						else
							_params.get(_prevSelectedRow).Value = _txtParam.getText();
					}
					_prevSelectedRow = lsm.getMinSelectionIndex();
					if (_prevSelectedRow < _params.size())
					{
						if (_selectValue)
							_txtParam.setText(_params.get(_prevSelectedRow).SelectText);
						else
							_txtParam.setText(_params.get(_prevSelectedRow).Value);
					}
					else
					{
						_txtParam.setText(CC.STR_EMPTY);
					}
				}
			}
		});
		
//		_tmParam.addTableModelListener(new TableModelListener() 
//		{
//			@Override
//			public void tableChanged(TableModelEvent e) 
//			{
//				_txtParam.setText(e.getSource().toString() +" ("+ e.getFirstRow()+", "+ e.getLastRow());
//				
//			}
//		});
	}

	Action actDeleteRows = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			int [] iSelRow = _tabParam.getSelectedRows();
			for (int ii = iSelRow.length-1; ii >= 0 ; ii--)
				_params.remove(iSelRow[ii]);
			
			_tabParam.revalidate();
		}
	};
	class ParamTableModel extends AbstractTableModel
	{
		public ParamTableModel ()
		{
		}

		@Override
		public int getColumnCount() 
		{
			return 6;
		}

		@Override
		public int getRowCount() 
		{
			return _params != null ? _params.size()+1 : 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			Object ret = null;
			
			if (_params != null)
			{
				if (_params.size() > rowIndex)
				{
					switch (columnIndex)
					{
						case 0:
							ret = _params.get(rowIndex).Number;
							break;
						case 1:
							ret = _params.get(rowIndex).Title;
							break;
						case 2:
							ret = _params.get(rowIndex).Name;
							break;
						case 3:
							ret = _params.get(rowIndex).Type;
							break;
						case 4:
							ret = _params.get(rowIndex).IsInsert;
							break;
						case 5:
							ret = _params.get(rowIndex).DefaultValue;
							break;
					}
				}
				else
				{
					if (columnIndex == 4)
						ret = false;
					else
						ret = CC.STR_EMPTY;
				}
					
			}
			
			return ret;
		}
		
		@Override
		public String getColumnName(int columnIndex)
		{
			String ret = super.getColumnName(columnIndex);
			
			switch (columnIndex)
			{
				case 0:
					ret = _bnd.getString("Table.Param.ColName.Num");
					break;
				case 1:
					ret = _bnd.getString("Table.Param.ColName.Title");
					break;
				case 2:
					ret = _bnd.getString("Table.Param.ColName.Name");
					break;
				case 3:
					ret = _bnd.getString("Table.Param.ColName.Type");
					break;
				case 4:
					ret = _bnd.getString("Table.Param.ColName.Insert");
					break;
				case 5:
					ret = _bnd.getString("Table.Param.ColName.Default");
					break;
			}
			
			return ret;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			return columnIndex == 4 ? Boolean.class  : super.getColumnClass(columnIndex);
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			//return columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5;
			return true;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
		{
			if (_params == null)
				return;
			
			if (_params.size() >= rowIndex)
			{
				if (_params.size() == rowIndex)
					_params.add(new Param());
				
				switch (columnIndex)
				{
					case 0:
						_params.get(rowIndex).Number = Integer.parseInt(aValue.toString());
						break;
					case 1:
						_params.get(rowIndex).Title = aValue.toString();
						break;
					case 2:
						_params.get(rowIndex).Name = aValue.toString();
						break;
					case 3:
						_params.get(rowIndex).Type = eQueryParamType.valueOf(aValue.toString());
						break;
					case 4:
						_params.get(rowIndex).IsInsert = Boolean.parseBoolean(aValue.toString());
						break;
					case 5:
						_params.get(rowIndex).DefaultValue = aValue.toString();
						break;
				}
			}
				
			//super.setValueAt(aValue, rowIndex, columnIndex);
		}

		
	
	}
}
