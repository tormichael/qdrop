package qdrop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import qdrop.ses.*;

import JCommonTools.CC;
import JCommonTools.GBC;
import JCommonTools.TableTools;
import JCommonTools.DB.DBWork;
import JCommonTools.DB.dDBConnection;

public class wSesParams extends JDialog 
{
	private Session _ses; 

	private ResourceBundle _bnd;
	private ResourceBundle _bndCT;

	private JTextField _txtCode;
	private JTextField _txtName;
	private JTextArea _txtNote;
	private JTextField _txtDelimLeft;
	private JTextField _txtDelimRight;
	private JTextField _txtDBDefault;
	private JTable _tabDBCollection;
	private JTextArea _txtDBConnPreview;

	private DBCollectionTabModel _dbColl;
	
	private String _prefPath;

	public void setPreferencePath(String aPath)
	{
		_prefPath = aPath + "/wsesparam";
	}
	
	/**
	 *		C	O	N	S	T	R	U	C	T	O	R 
	 */
	public wSesParams(Session aSes, jqPreferences aPrf)
	{
		_ses = aSes;

		Dimension szScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(szScreen.width/2, szScreen.height/2);
		setLocation((int)(szScreen.width/2*Math.random()), (int)(szScreen.height/3*Math.random()));
		setModalityType(ModalityType.TOOLKIT_MODAL);
		
		_bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, aPrf.getCurrentLocale());
		_bndCT = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT_CT, aPrf.getCurrentLocale());

		setTitle(_bnd.getString("Titles.wSesParams"));
		
		// Tabs
		JTabbedPane tpParam = new JTabbedPane();
		add(tpParam, BorderLayout.CENTER);
		// Tab page 1 (Main)
		JPanel pnlMain = new JPanel();
		tpParam.addTab(_bnd.getString("TabPanel.SesParam.Main"), pnlMain);
		GridBagLayout gbl = new GridBagLayout();
		pnlMain.setLayout(gbl);
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			// first row
			JLabel lblCode = new JLabel(_bnd.getString("Label.wSesParams.Code"));
			gbl.setConstraints(lblCode, new GBC(0,0).setIns(2).setAnchor(GBC.EAST));
			pnlMain.add(lblCode);
			_txtCode = new JTextField(5);
			gbl.setConstraints(_txtCode, new GBC(1,0).setIns(2).setAnchor(GBC.WEST).setWeight(0.1, 0.0).setFill(GBC.HORIZONTAL));
			pnlMain.add(_txtCode);
			JLabel lblName = new JLabel(_bnd.getString("Label.wSesParams.Name"));
			gbl.setConstraints(lblName, new GBC(2,0).setIns(2).setAnchor(GBC.EAST).setFill(GBC.HORIZONTAL));
			pnlMain.add(lblName);
			_txtName = new JTextField();
			gbl.setConstraints(_txtName, new GBC(3,0).setIns(2).setGridSpan(2, 1).setFill(GBC.HORIZONTAL).setWeight(0.7, 0.0));
			pnlMain.add(_txtName);
			// second row
			JLabel lblNote = new JLabel(_bnd.getString("Label.wSesParams.Comment"));
			gbl.setConstraints(lblNote, new GBC(0,1).setIns(2).setAnchor(GBC.EAST));
			pnlMain.add(lblNote);
			_txtNote = new JTextArea();
			//txtNote.setRows(3);
			JScrollPane spNote = new JScrollPane(_txtNote); 
			gbl.setConstraints(spNote, new GBC(1,1).setIns(2).setGridSpan(4, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
			pnlMain.add(spNote);
			// third row
			JLabel lblDelimLeft = new JLabel(_bnd.getString("Label.wSesParams.DelimLeft"));
			gbl.setConstraints(lblDelimLeft, new GBC(0,2).setIns(2).setAnchor(GBC.EAST));
			pnlMain.add(lblDelimLeft);
			_txtDelimLeft = new JTextField(10);
			gbl.setConstraints(_txtDelimLeft, new GBC(1,2).setIns(2).setAnchor(GBC.WEST).setWeight(0.1, 0.0).setFill(GBC.HORIZONTAL));
			pnlMain.add(_txtDelimLeft);
			JLabel lblDelimRight = new JLabel(_bnd.getString("Label.wSesParams.DelimRight"));
			gbl.setConstraints(lblDelimRight, new GBC(2,2).setIns(2).setAnchor(GBC.EAST));
			pnlMain.add(lblDelimRight);
			_txtDelimRight = new JTextField(10);
			gbl.setConstraints(_txtDelimRight, new GBC(3,2).setIns(2).setAnchor(GBC.WEST).setWeight(0.1, 0.0).setFill(GBC.HORIZONTAL));
			pnlMain.add(_txtDelimRight);

			JPanel pnlEmpty = new JPanel();
			gbl.setConstraints(pnlEmpty, new GBC(4,2).setWeight(0.3, 0.0));
			pnlMain.add(pnlEmpty);
			
			// next row
			GridBagLayout gblDB = new GridBagLayout();
			JPanel pnlDB = new JPanel(gblDB);
			pnlDB.setBorder(BorderFactory.createTitledBorder(_bnd.getString("BorderText.SesParam.DBs")));
			gbl.setConstraints(pnlDB, new GBC(0,3).setIns(2).setGridSpan(5, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
			
				JButton cmdAdd = new JButton(actAddDBConnection);
				cmdAdd.setText(_bnd.getString("Button.DBConnection.Add"));
				gblDB.setConstraints(cmdAdd, new GBC(0,0).setIns(2).setAnchor(GBC.WEST));
				pnlDB.add(cmdAdd);
				JButton cmdSet = new JButton(actSetDBConnection);
				cmdSet.setText(_bnd.getString("Button.DBConnection.Set"));
				gblDB.setConstraints(cmdSet, new GBC(1,0).setIns(2).setAnchor(GBC.WEST));
				pnlDB.add(cmdSet);
				JButton cmdDel = new JButton();
				cmdDel.setText(_bnd.getString("Button.DBConnection.Delete"));
				gblDB.setConstraints(cmdDel, new GBC(2,0).setIns(2).setAnchor(GBC.WEST));
				pnlDB.add(cmdDel);
				
				pnlEmpty = new JPanel();
				gblDB.setConstraints(pnlEmpty, new GBC(3,0).setGridSpan(1, 3).setWeight(0.07, 0.0));
				pnlDB.add(pnlEmpty);
				
				JLabel lblDBDefault = new JLabel(_bnd.getString("Label.wSesParams.DBDefault"));
				gblDB.setConstraints(lblDBDefault, new GBC(4,0).setIns(2).setAnchor(GBC.EAST));
				pnlDB.add(lblDBDefault);
				_txtDBDefault = new JTextField(10);
				gblDB.setConstraints(_txtDBDefault, new GBC(5,0).setIns(2).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL));
				pnlDB.add(_txtDBDefault);
				// next row
				JLabel lblDBCollection = new JLabel(_bnd.getString("Label.wSesParams.DBCollection"));
				gblDB.setConstraints(lblDBCollection, new GBC(0,1).setGridSpan(3, 1).setIns(2).setAnchor(GBC.WEST));
				pnlDB.add(lblDBCollection);
				// next row
				_dbColl = new DBCollectionTabModel(_ses.ParamDBCol); 
				_tabDBCollection = new JTable(_dbColl);
				_tabDBCollection.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				JScrollPane spDBCollection = new JScrollPane(_tabDBCollection);
				gblDB.setConstraints(spDBCollection, new GBC(0,2).setGridSpan(3, 1).setWeight(1.0, 1.0).setAnchor(GBC.WEST).setFill(GBC.BOTH));
				pnlDB.add(spDBCollection);
				_txtDBConnPreview = new JTextArea();
				JScrollPane spDBConnPreview = new JScrollPane(_txtDBConnPreview);
				gblDB.setConstraints(spDBConnPreview, new GBC(4,2).setGridSpan(2, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
				pnlDB.add(spDBConnPreview);
				
				
				
			pnlMain.add(pnlDB);
			//spDBCollection.add(_tabDBCollection);
		
		// Tab page 2 (Optional)
		pnlParamEdit pnlParams = new pnlParamEdit(_bndCT, _bnd);
		pnlParams.set_selectValue(false);
		pnlParams.setParamCollection(_ses.Params);
		tpParam.addTab(_bnd.getString("TabPanel.SesParam.Optional"), pnlParams);

		// bottom bar
		JPanel pnlBottom = new JPanel(); 
		add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new BorderLayout());
		JTextField stsBar = new JTextField();
		pnlBottom.add(stsBar, BorderLayout.WEST);
		JPanel pnlBtnOkCancel = new JPanel();
		pnlBtnOkCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton cmdOk = new JButton(actOk);
		cmdOk.setText(_bndCT.getString("Button.Ok"));
		pnlBtnOkCancel.add(cmdOk);
		JButton cmdCancel = new JButton(_bndCT.getString("Button.Cancel"));
		pnlBtnOkCancel.add(cmdCancel);
		pnlBottom.add(pnlBtnOkCancel, BorderLayout.EAST);
		cmdCancel.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
		});
		
		_tabDBCollection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = _tabDBCollection.getSelectionModel();
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
					int si = lsm.getMinSelectionIndex();
					if (si < _ses.ParamDBCol.size())
					{
						_txtDBConnPreview.setText(DBWork.getConnectDescription(_ses.ParamDBCol.get(si)));
					}
					else
					{
						_txtDBConnPreview.setText(CC.STR_EMPTY);
					}
				}
			}
		});
		
		/// LOAD from _ses
		_txtCode.setText(Integer.toString(_ses.Code));
		_txtName.setText(_ses.Title);
		_txtNote.setText(_ses.Note);
		_txtDelimLeft.setText(_ses.ParamBegDelim);
		_txtDelimRight.setText(_ses.ParamEndDelim);
		_txtDBDefault.setText(Integer.toString(_ses.DBCodeDefault));
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowOpened(WindowEvent e) 
			{
				LoadProgramPreference();
				super.windowOpened(e);
			}
		});
		
	}

	Action actOk = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_ses.Code = Integer.parseInt(_txtCode.getText());
			_ses.Title = _txtName.getText();
			_ses.Note = _txtNote.getText();
			_ses.ParamBegDelim = _txtDelimLeft.getText();
			_ses.ParamEndDelim = _txtDelimRight.getText();
			_ses.DBCodeDefault = Integer.parseInt(_txtDBDefault.getText());
			
			SaveProgramPreference();
			setVisible(false);
		}
	};

	Action actAddDBConnection = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
		}
	};
	Action actDelDBConnection = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
		}
	};
	
	Action actSetDBConnection = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			DBWork wdb = new DBWork(_ses.ParamDBCol.get(_tabDBCollection.getSelectedRow()));
			dDBConnection dlg = new dDBConnection(wdb);
			dlg.setPreferencesPath(_prefPath);
			dlg.setModal(true);
			//dlg.setIconImage(_wld.getImage("dbconnection.png"));
			dlg.setVisible(true);
			if (dlg.isResultOk())
			{
				//_showCurrentConnectionURL();
			}
		}
	};


	private void LoadProgramPreference()
	{
		if (_prefPath == null || _prefPath.length() == 0)
			return;
		Preferences node = Preferences.userRoot().node(_prefPath);

		TableTools.SetColumnsWidthFromString(_tabDBCollection, node.get("tabcolwidth_dbcoll", CC.STR_EMPTY));
	}
	
	private void SaveProgramPreference()
	{
		if (_prefPath == null || _prefPath.length() == 0)
			return;
		Preferences node = Preferences.userRoot().node(_prefPath);

		node.put("tabcolwidth_dbcoll", TableTools.GetColumnsWidthAsString(_tabDBCollection));
		
		//node.putInt("SplitDividerLocation", _splPanel.getDividerLocation());
	}


	
	class DBCollectionTabModel extends AbstractTableModel
	{
	    private ArrayList<ParamDB> _dbCol;
		
		public DBCollectionTabModel(ArrayList<ParamDB> aDBCol)
		{
			_dbCol = aDBCol;
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public String getColumnName(int column) 
		{
			String ret = super.getColumnName(column);
			
			switch (column)
			{
				case 0:
					ret = _bnd.getString("Table.DBColl.ColName.Code");
					break;
				//case 1:
				//	ret = _bnd.getString("Table.DBColl.ColName.Driver");
				//	break;
				case 1:
					ret = _bnd.getString("Table.DBColl.ColName.Host");
					break;
				//case 3:
				//	ret = _bnd.getString("Table.DBColl.ColName.User");
				//	break;
				//case 4:
				//	ret = _bnd.getString("Table.DBColl.ColName.Pwd");
				//	break;
			}
			
			return ret;
		}
		
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return _dbCol != null ? _dbCol.size()+1 : 1;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			Object ret = null;
			
			if (_dbCol.size() > rowIndex)
			{
				switch (columnIndex)
				{
					case 0:
						ret = _dbCol.get(rowIndex).Code;
						break;
					case 1:
						ret = DBWork.getConnectionURL(_dbCol.get(rowIndex));
						break;
//					case 2:
//						ret = _dbCol.get(rowIndex).Host;
//						break;
//					case 3:
//						ret = _dbCol.get(rowIndex).UserName;
//						break;
//					case 4:
//						ret = _dbCol.get(rowIndex).Pwd;
//						break;
				}
			}
			else
			{
				ret = CC.STR_EMPTY;
			}
			
			return ret;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			return true; //super.isCellEditable(rowIndex, columnIndex);
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
		{
			if (_dbCol == null)
				return;
			
			if (_dbCol.size() >= rowIndex)
			{
				if (_dbCol.size() == rowIndex)
					_dbCol.add(new ParamDB());
				
				
				switch (columnIndex)
				{
					case 0:
						_dbCol.get(rowIndex).Code = Integer.parseInt(aValue.toString());
						break;
//					case 1:
//						_dbCol.get(rowIndex).Driver.Path = aValue.toString();
//						break;
//					case 2:
//						_dbCol.get(rowIndex).Host = aValue.toString();
//						break;
//					case 3:
//						_dbCol.get(rowIndex).UserName = aValue.toString();
//						break;
//					case 4:
//						_dbCol.get(rowIndex).Pwd = aValue.toString();
//						break;
				}
			}
		}
		
	}
	
}
