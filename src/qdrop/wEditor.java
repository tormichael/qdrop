package qdrop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import edrop.pnlEditor;

import qdrop.ses.Param;
import qdrop.ses.Query;
import qdrop.ses.Query2XML;
import qdrop.ses.Session;
import qdrop.ses.eQueryParamType;

import JCommonTools.CC;
import JCommonTools.ComboTree;
import JCommonTools.GBC;
import JCommonTools.TableTools;
import JCommonTools.TextEditor;

public class wEditor extends JFrame 
{
	private Session 	_ses; // working session
	private Query 		_qq; // current query
	
	private ResourceBundle _bnd;
	private ResourceBundle _bndCT;

	//JMenuBar mnuBar;
	//JMenu mnuFile;
	private JLabel 			_sbiMain;
	private ComboTree 		_ctrParents;
	private JTextField 		_txtName;
	private JTextField 		_txtCode;
	private JTextField 		_txtAuthor;
	//private TextEditor 		_txtSQL;
	private pnlEditor		_txtSQL;
	private TextEditor 		_txtXSLT;
	private JTextArea  		_txtComment;
	private JComboBox 		_cboResult;
	private JComboBox 		_cboType;
	private pnlParamEdit	_pnlParam;
	

	private String _prefPath;
	public void setPreferencePath(String aPath)
	{
		_prefPath = aPath;
	}
	
	public wEditor(Session aSes, jqPreferences aPrf)
	{
		_ses = aSes;
		
		Dimension szScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(szScreen.width/2, szScreen.height/2);
		setLocation((int)(szScreen.width/2*Math.random()), (int)(szScreen.height/3*Math.random()));

		_bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, aPrf.getCurrentLocale());
		_bndCT = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT_CT, aPrf.getCurrentLocale());

		/**
		 * M E N U
		 */
		
		JMenuBar mnuBar = new JMenuBar();
		setJMenuBar(mnuBar);
		
		JMenu mnuQuery = new JMenu(_bnd.getString("Menu.Main.Query"));
		mnuBar.add(mnuQuery);
		
		JMenuItem mnuQuerySaveExit = new JMenuItem(actSaveExit);
		mnuQuerySaveExit.setText( _bnd.getString("Menu.wEditor.QuerySaveExit"));
		mnuQuery.add(mnuQuerySaveExit);
		
		JMenuItem mnuQuerySaveToFile = new JMenuItem(actSaveToFile);
		mnuQuerySaveToFile.setText( _bnd.getString("Menu.wEditor.QuerySave2File"));
		mnuQuery.add(mnuQuerySaveToFile);
		
		JMenuItem mnuQueryLoadFromFile = new JMenuItem(actLoadFromFile);
		mnuQueryLoadFromFile.setText( _bnd.getString("Menu.wEditor.QueryLoadFromFile"));
		mnuQuery.add(mnuQueryLoadFromFile);
		
		mnuQuery.addSeparator();
		
		JMenuItem mnuQueryExit = new JMenuItem(actExit);
		mnuQueryExit.setText( _bnd.getString("Menu.wEditor.QueryExit"));
		mnuQuery.add(mnuQueryExit);
		

		JMenu mnuEdit = new JMenu(_bnd.getString("Menu.General.Edit"));
		mnuBar.add(mnuEdit);
		
		/**
		 *   T O O L S   B A R
		 */
		JToolBar bar = new JToolBar();
		add(bar, BorderLayout.NORTH);

		int szUnit = aPrf.getIconSize(); 
		actSaveExit.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("save_exit.png", szUnit));
		bar.add(actSaveExit);
		
		/**
		 * C O N T E N T S
		 */
		JTabbedPane tpQuery = new JTabbedPane();
		add(tpQuery, BorderLayout.CENTER);
		
		// Tab page 1 (Main)
		JPanel pnlMain = new JPanel();
		tpQuery.addTab(_bnd.getString("TabPanel.wEditor.Main"), pnlMain);
		GridBagLayout gbl = new GridBagLayout();
		GBC gbc = new GBC(0,0);
		gbc.setInsets(2, 2, 2, 2);
		gbc.setFill(GBC.HORIZONTAL);
		pnlMain.setLayout(gbl);
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// first row
		JLabel lblParent = new JLabel(_bnd.getString("Label.wEditor.Parent"));
		gbl.setConstraints(lblParent, gbc);
		pnlMain.add(lblParent);
		_ctrParents = new ComboTree();
		_ctrParents.setTreeModel(new TreeModelSession(aSes));
		gbl.setConstraints(_ctrParents, gbc.setGridXY(1,0).setGridSpan(2, 1).setWeight(1.0, 0.0));
		pnlMain.add(_ctrParents);
		JLabel lblSep = new JLabel("\\", JLabel.CENTER);
		//lblSep.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		gbl.setConstraints(lblSep, gbc.setGridXY(3,0).setGridSpan(1, 1).setWeight(0.0, 0.0));
		pnlMain.add(lblSep);
		JLabel lblName = new JLabel(_bnd.getString("Label.wEditor.Name"));
		gbl.setConstraints(lblName, gbc.setGridXY(4,0));
		pnlMain.add(lblName);
		_txtName = new JTextField();
		gbl.setConstraints(_txtName, gbc.setGridXY(5,0).setWeight(1.0, 0.0));
		pnlMain.add(_txtName);
		// next row
		JLabel lblCode = new JLabel(_bnd.getString("Label.wEditor.Code"));
		gbl.setConstraints(lblCode, gbc.setGridXY(0,1).setGridSpan(1, 1).setWeight(0.0, 0.0));
		pnlMain.add(lblCode);
		_txtCode = new JTextField();
		gbl.setConstraints(_txtCode, gbc.setGridXY(1,1).setWeight(1.0, 0.0));
		pnlMain.add(_txtCode);
		JButton cmdNextCode = new JButton("1,2,3,...");
		gbl.setConstraints(cmdNextCode, gbc.setGridXY(2,1).setWeight(0.0, 0.0));
		pnlMain.add(cmdNextCode);
		JLabel lblAuthor = new JLabel(_bnd.getString("Label.wEditor.Author"));
		gbl.setConstraints(lblAuthor, gbc.setGridXY(4,1));
		pnlMain.add(lblAuthor);
		_txtAuthor = new JTextField();
		gbl.setConstraints(_txtAuthor, gbc.setGridXY(5,1).setWeight(1.0, 0.0));
		pnlMain.add(_txtAuthor);
		//next row 
		JLabel lblResult = new JLabel(_bnd.getString("Label.wEditor.Result"));
		gbl.setConstraints(lblResult, gbc.setGridXY(0,2).setWeight(0.0, 0.0));
		pnlMain.add(lblResult);
		_cboResult = new JComboBox();
		gbl.setConstraints(_cboResult, gbc.setGridXY(1,2).setGridSpan(2,1));
		pnlMain.add(_cboResult);
		JLabel lblType = new JLabel(_bnd.getString("Label.wEditor.Type"));
		gbl.setConstraints(lblType, gbc.setGridXY(4,2).setGridSpan(1,1).setWeight(0.0, 0.0));
		pnlMain.add(lblType);
		_cboType = new JComboBox();
		gbl.setConstraints(_cboType, gbc.setGridXY(5,2));
		pnlMain.add(_cboType);
		// next row
		JLabel lblComment = new JLabel(_bnd.getString("Label.wEditor.Comment"));
		lblComment.setVerticalAlignment(JLabel.NORTH);
		gbl.setConstraints(lblComment, gbc.setGridXY(0,3).setWeight(0.0, 0.0).setFill(GBC.VERTICAL));
		pnlMain.add(lblComment);
		_txtComment = new JTextArea();
		gbl.setConstraints(_txtComment, gbc.setGridXY(1,3).setGridSpan(5, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
		pnlMain.add(_txtComment);

		_pnlParam = new pnlParamEdit(_bndCT, _bnd);
		tpQuery.addTab(_bnd.getString("TabPanel.wEditor.Param"), _pnlParam);
		
		JPanel pnlSQL = new JPanel(new BorderLayout());
		tpQuery.addTab(_bnd.getString("TabPanel.wEditor.SQL"), pnlSQL);
		//_txtSQL = new TextEditor();
		_txtSQL = new pnlEditor();
		pnlSQL.add(new JScrollPane(_txtSQL), BorderLayout.CENTER);
		
		JPanel pnlXSLT = new JPanel(new BorderLayout());
		tpQuery.addTab(_bnd.getString("TabPanel.wEditor.XSLT"), pnlXSLT);
		_txtXSLT = new TextEditor();
		pnlXSLT.add(new JScrollPane(_txtXSLT), BorderLayout.CENTER);
		
		/**
		 * S T A T U S   B A R
		 */
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(BorderFactory.createRaisedBevelBorder());
		statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(statusBar, BorderLayout.SOUTH);
		//statusBar.set
		
		_sbiMain = new JLabel();
		_sbiMain.setText("Welcome!");
		_sbiMain.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.add(_sbiMain);
		
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) 
			{
				SaveProgramPreference();
				super.windowClosing(e);
			}
			@Override
			public void windowOpened(WindowEvent e) 
			{
				LoadProgramPreference();
				super.windowOpened(e);
			}
		});
		
	}

	public void SetQuery(Query aQ)
	{
		_qq = aQ;
		_txtName.setText(_qq.Name);
		_txtCode.setText(_qq.Code+"");
		_txtAuthor.setText(_qq.Author);
		_txtComment.setText(_qq.Note);
		_txtSQL.setText(_qq.SQL);
		_txtSQL.runKeyWordPopUp();
		_txtXSLT.setText(_qq.XSLT);
		_ctrParents.setSelectedPath(((TreeModelSession)_ctrParents.getTreeModel()).getPath(_ses.getParent(aQ)));
		_pnlParam.setParamCollection(aQ.Params);
	}
	
	private void save2Query(Query aQ)
	{
		aQ.Name = _txtName.getText();
		aQ.Code = Integer.parseInt(_txtCode.getText());
		aQ.Author = _txtAuthor.getText();
		aQ.Note = _txtComment.getText();
		aQ.SQL = _txtSQL.getText();
		aQ.XSLT = _txtXSLT.getText();
		//_ctrParents.setSelectedPath(((TreeModelSession)_ctrParents.getTreeModel()).getPath(_ses.getParent(aQ)));
		//_pnlParam.setParamCollection(aQ.Params);
	}
	
	private void LoadProgramPreference()
	{
		if (_prefPath == null || _prefPath.length() == 0)
			return;
		Preferences node = Preferences.userRoot().node(_prefPath);

		_pnlParam.setTabColumnsWidthFromString(node.get("TabParamColWidth", CC.STR_EMPTY));
	}
	
	private void SaveProgramPreference()
	{
		if (_prefPath == null || _prefPath.length() == 0)
			return;
		Preferences node = Preferences.userRoot().node(_prefPath);

		node.put("TabParamColWidth", _pnlParam.getTabColumnsWidthAsString());
		
		//node.putInt("SplitDividerLocation", _splPanel.getDividerLocation());
	}
	
	
	
	Action actSaveExit = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				save2Query(_qq);
				setVisible(false);
			}
			catch (Exception ex)
			{
			}
		}
	};
	
	Action actSaveToFile = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
	    	try
	    	{
	    		Query2XML qq = new Query2XML();
	    		save2Query(qq);
	    		String fileName = qq.Name; 
	    		JFileChooser dlg = new JFileChooser();
	    		dlg.setCurrentDirectory(new File("C:\\ASW\\SQ4\\"));
	    		dlg.setFileFilter(new FileNameExtensionFilter("Query", "q4"));
	    		dlg.setSelectedFile(new File(fileName));
	    		if (dlg.showSaveDialog(wEditor.this) == JFileChooser.APPROVE_OPTION )
	    		{
	    			fileName = dlg.getSelectedFile().getPath();
	    		
		    		JAXBContext context = JAXBContext.newInstance(Query2XML.class);
		    		Marshaller m = context.createMarshaller();
		    		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    		m.marshal(qq, new File(fileName));
	    		}
	    	}
	    	catch (JAXBException ex)
	    	{
	    		//_logger.info(ex.getMessage());
	    		ex.printStackTrace();
	    	}
			
		}
	};
	
	Action actLoadFromFile = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
		}
	};

	Action actExit = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			setVisible(false);
		}
	};

}
