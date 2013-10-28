package qdrop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import qdrop.ses.Query;

import JCommonTools.CC;

/**
 * Main window/form.
 * @author M.Tor
 *
 */
public class wSession extends JFrame 
{
	public final static String PREFERENCE_PATH = "/drop";
	
	private WorkSession _qp;
	private jqPreferences _prf;
	
	private JMenuBar mnuBar;
	private JMenu mnuSession;
	private JMenuItem mnuSessionNew;
	private JMenuItem mnuSessionOpen;
	private JMenuItem mnuSessionSave;
	private JMenuItem mnuSessionSaveAs;
	private JMenuItem mnuSessionParam;
	private JCheckBoxMenuItem mnuSessionDesign;
	private JMenuItem mnuSessionExit;
	private JMenu mnuQuery;
	private JMenu mnuQueryExecute;
	private JMenuItem mnuQueryExecuteTab;
	private JMenuItem mnuQueryExecuteHTML;
	private JMenuItem mnuQueryNew;
	private JMenuItem mnuQueryEdit;
	private JMenuItem mnuQueryDelete;
	private JMenuItem mnuQueryExport;
	private JMenuItem mnuQueryImport;
	private JMenu mnuOption;
	private JMenuItem mnuOptionPref;

	private TreeViewSession _tvSes;
	private JSplitPane _splPanel;
	private JLabel _sbiMain;
	private String _currSesFileName;
	private PanelParam _pnlParam;
	private ResourceBundle _bnd;

	private int _lastSelectedQueryCode;

	public wSession() throws HeadlessException {

		_prf = new jqPreferences();
		_qp = new WorkSession(_prf.getCurrentLocale());
		_currSesFileName = CC.STR_EMPTY;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/**
		 * M E N U
		 */
		
		mnuBar = new JMenuBar();
		setJMenuBar(mnuBar);
		
		mnuSession = new JMenu();
		mnuBar.add(mnuSession);
		
		mnuSessionNew = mnuSession.add(actSessionNew);
		
		mnuSessionOpen = new JMenuItem();
		mnuSession.add(mnuSessionOpen);
		mnuSessionOpen.addActionListener(new OpenSessionAction());
		mnuSessionOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		actSessionSave  = new ActionSessionSave(); 
		mnuSessionSave = mnuSession.add(actSessionSave);
		mnuSessionSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		actSessionSaveAs = new ActionSessionSaveAs();
		mnuSessionSaveAs = mnuSession.add(actSessionSaveAs);
		
		mnuSession.addSeparator();
		
		mnuSessionParam = new JMenuItem();
		mnuSession.add(mnuSessionParam);
		mnuSessionParam.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				wSesParams dlg = new wSesParams(_qp.get_ses(), _prf);
				dlg.setPreferencePath(PREFERENCE_PATH);
				dlg.setVisible(true);
			}
		});
		
		mnuSession.addSeparator();
		
		mnuSessionDesign = new JCheckBoxMenuItem();
		mnuSessionDesign.setSelected(true);
		mnuSession.add(mnuSessionDesign);
		
		mnuSession.addSeparator();
		
		mnuSessionExit = new JMenuItem();
		mnuSession.add(mnuSessionExit);
		mnuSessionExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				SaveProgramPreference();
				System.exit(0);
			}
		});

		mnuQuery = new JMenu();
		mnuBar.add(mnuQuery);

		mnuQuery.add(mnuQueryExecute = new JMenu());
		
		mnuQueryExecuteTab = mnuQueryExecute.add(actQueryExecTab);
		mnuQueryExecuteHTML = mnuQueryExecute.add(actQueryExecHTML);
		
		mnuQuery.addSeparator();
		
		mnuQueryNew = mnuQuery.add(actQueryNew);
		mnuQueryEdit = mnuQuery.add(actQueryEdit);
		mnuQueryDelete = mnuQuery.add(actQueryDelete);
		
		mnuQuery.addSeparator();
		
		mnuQueryExport = new JMenuItem();
		mnuQuery.add(mnuQueryExport);
		mnuQueryImport = new JMenuItem();
		mnuQuery.add(mnuQueryImport);

		mnuOption = new JMenu();
		mnuBar.add(mnuOption);
		
		mnuOptionPref = new JMenuItem();
		mnuOption.add(mnuOptionPref);
		mnuOptionPref.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgPreference dlg = new dlgPreference(wSession.this, _prf);
				dlg.setVisible(true);
			}
		});

		/**
		 *   T O O L S   B A R
		 */
		JToolBar bar = new JToolBar();
		bar.add(actQueryExecTab);
		actQueryExecTab.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("ExecTab.png", _prf.getIconSize()));
		bar.add(actQueryExecHTML);
		actQueryExecHTML.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("ExecHTML.png", _prf.getIconSize()));
		bar.addSeparator();
		bar.add(actSessionSave);
		bar.addSeparator();
		bar.add(actQueryNew);
		actQueryNew.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("new.png", _prf.getIconSize()));
		bar.add(actQueryEdit);
		actQueryEdit.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("kedit.png", _prf.getIconSize()));
		bar.add(actQueryDelete);
		actQueryDelete.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("trashcan_empty.png", _prf.getIconSize()));
		add(bar, BorderLayout.NORTH);

		/**
		 * C O N T E N T S
		 */
		JPanel pnlSession = new JPanel(new BorderLayout());
		_tvSes = new TreeViewSession(_qp.get_ses());
		pnlSession.add(new JScrollPane(_tvSes), BorderLayout.CENTER);
		_pnlParam = new PanelParam();
		_pnlParam.set_qp(_qp);
		_splPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlSession, _pnlParam);
		add(_splPanel, BorderLayout.CENTER);

		_tvSes.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{
				if (e.getPath().getLastPathComponent() instanceof Query)
				{
					Query qq = (Query) e.getPath().getLastPathComponent(); 
					_qp.set_currentQuery(qq);
					_pnlParam.Display();
				}
			}
		});
		
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
		
		_pnlParam.set_statusBar(_sbiMain);
		
		UpdateLanguage();
		
		this.setIconImage(ImageTools.CreateIcon("drop3a.png", _prf.getIconSize()).getImage());
		
		LoadProgramPreference();
		
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) 
			{
				SaveProgramPreference();
				super.windowClosing(e);
			}
			
			@Override
			public void windowOpened(WindowEvent e) 
			{
				LoadSession(_currSesFileName);
				if (_lastSelectedQueryCode >0)
				{
					if (_qp.get_ses() != null)
					{
						Query qq = _qp.get_ses().getQuery(_lastSelectedQueryCode);
						if (qq != null)
							_tvSes.setSelectionPath(_tvSes.getPath(qq));
					}
				}
				super.windowOpened(e);
			}
		});
	}
	
	private void UpdateLanguage()
	{
		_bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, _prf.getCurrentLocale());

		setTitle(_bnd.getString("Titles.Main"));
		
		mnuSession.setText(_bnd.getString("Menu.Main.Session"));
		mnuSessionNew.setText(_bnd.getString("Menu.General.New"));
		mnuSessionOpen.setText(_bnd.getString("Menu.General.Open"));
		mnuSessionSave.setText(_bnd.getString("Menu.General.Save"));
		mnuSessionSaveAs.setText(_bnd.getString("Menu.General.SaveAs"));
		mnuSessionParam.setText(_bnd.getString("Menu.General.Parameters"));
		mnuSessionDesign.setText(_bnd.getString("Menu.Main.Designing"));
		mnuSessionExit.setText(_bnd.getString("Menu.General.Exit"));
		mnuQuery.setText(_bnd.getString("Menu.Main.Query"));
		mnuQueryExecute.setText(_bnd.getString("Menu.General.Execute"));
		mnuQueryExecuteTab.setText(_bnd.getString("Menu.Main.Table"));
		mnuQueryExecuteHTML.setText(_bnd.getString("Menu.Main.HTML"));
		mnuQueryNew.setText(_bnd.getString("Menu.General.New"));
		mnuQueryEdit.setText(_bnd.getString("Menu.General.Edit"));
		mnuQueryDelete.setText(_bnd.getString("Menu.General.Delete"));
		mnuQueryExport.setText(_bnd.getString("Menu.General.Export"));
		mnuQueryImport.setText(_bnd.getString("Menu.General.Import"));
		mnuOption.setText(_bnd.getString("Menu.General.Options"));
		mnuOptionPref.setText(_bnd.getString("Menu.General.Preferences"));
		
		actSessionSave.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.Save"));
		actQueryExecTab.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.ExecTab"));
		actQueryExecHTML.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.ExecHTML"));
		actQueryNew.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.New"));
		actQueryEdit.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.Edit"));
		actQueryDelete.putValue(Action.SHORT_DESCRIPTION, _bnd.getString("ToolTip.Action.Delete"));
		
		/*Properties prop = null;
		InputStream stream = null;
		InputStreamReader reader = null;
		try
		{
			//C:\ASW\EclipseWS\JQuery\src\JQuery
			stream = new FileInputStream("/ASW/EclipseWS/JQuery/src/JQuery/Rsc/JQMenu_ru_RU.properties");
			reader = new InputStreamReader(stream, "UTF-8");
			prop = new Properties();
			prop.load(reader);
		}
		catch (FileNotFoundException ex){System.err.println(ex.getMessage());}
		catch (UnsupportedEncodingException ex){}
		catch (IOException e) {}
		finally
		{
			try{
			if (reader != null)
				reader.close();
			}
			catch (IOException e){}
		}
		
		if (prop == null)
			return;
		
		mnuSessionLoad.setText(prop.getProperty("Load"));*/
	}

	Action actSessionNew = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(wSession.this, "Do it later");
			
		}
		
		
	};
	Action actSessionSave;
	private class ActionSessionSave extends AbstractAction
	{
		public ActionSessionSave()
		{
			putValue(Action.SMALL_ICON, ImageTools.CreateIcon("save.png", _prf.getIconSize()));
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			if (_currSesFileName.length() == 0 || _qp.isLoadSQ3())
				_currSesFileName = SaveSessionAs();
			
			if (_currSesFileName.length() > 0)
				_qp.Save(_currSesFileName);
		}
	}
	Action actSessionSaveAs;
	private class ActionSessionSaveAs extends AbstractAction
	{
		public ActionSessionSaveAs()
		{
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			String fn = SaveSessionAs();
			if (fn.length() > 0)
			{
				_currSesFileName = fn;
				_qp.Save(_currSesFileName);
			}
		}
	}
	
	Action actQueryExecTab = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (_qp.get_currentQuery()!= null)
			{
				wResultTables wRes = new wResultTables(_qp, _prf);
				wRes.setVisible(true);
			}
			else
				_sbiMain.setText(_bnd.getString("Error.CurrentQueryNull"));
			
		}
	};
	Action actQueryExecHTML = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(wSession.this, "Do it later");
		}
	};	
	Action actQueryNew = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			wEditor qe = new wEditor(_qp.get_ses(), _prf);
			qe.setPreferencePath(PREFERENCE_PATH);
			qe.setVisible(true);
		}
	};
	Action actQueryEdit = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (_qp.get_currentQuery()!= null)
			{
				wEditor qe = new wEditor(_qp.get_ses(), _prf);
				qe.setPreferencePath(PREFERENCE_PATH);
				qe.SetQuery(_qp.get_currentQuery());
				qe.setVisible(true);
			}
		}
	};
	Action actQueryDelete = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(wSession.this, "Do it later");
		}
	};



	private class OpenSessionAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			JFileChooser dlg = new JFileChooser();
			dlg.setCurrentDirectory(new File("C:\\ASW\\SQ4\\"));
			//dlg.setFileFilter(new FileNameExtensionFilter("Session", "sq3", "sq4"));
			if (dlg.showOpenDialog(wSession.this) == JFileChooser.APPROVE_OPTION)
				LoadSession(dlg.getSelectedFile().getPath());
		}
	}
	
	private void LoadSession(String aFileName)
	{
		if (aFileName.length() > 0 && _qp.Load(aFileName))
		{
			_currSesFileName = aFileName;
		}
		else
		{
			_currSesFileName = CC.STR_EMPTY;
		}
		_tvSes.setModel(new TreeModelSession(_qp.get_ses()));
		_tvSes.expandRow(0);
	}
	
	private String SaveSessionAs()
	{
		String ret = CC.STR_EMPTY;
		JFileChooser dlg = new JFileChooser();
		//dlg.setCurrentDirectory(new File("C:\\ASW\\SQ3\\"));
		dlg.setFileFilter(new FileNameExtensionFilter("Session", "sq3", "sq4"));
		if (_currSesFileName.length() > 0)
			dlg.setSelectedFile(new File(_currSesFileName));
		if (dlg.showSaveDialog(wSession.this) == JFileChooser.APPROVE_OPTION )
			ret = dlg.getSelectedFile().getPath();
		return ret;
	}

	private void LoadProgramPreference()
	{
		Preferences node = Preferences.userRoot().node(PREFERENCE_PATH);
		
		int frameState = node.getInt("FormState", Frame.NORMAL);
		setState(frameState);
		if  (frameState != Frame.MAXIMIZED_BOTH)
		{
			Dimension szScreen = Toolkit.getDefaultToolkit().getScreenSize();
			String st = node.get("FormSize", CC.STR_EMPTY);
			if (st.length() > 0)
			{
				String [] ss = st.split(";");
				setSize(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
			}
			else
			{
				setSize(szScreen.width/2, szScreen.height/2);
			}
			st = node.get("FormLocation", CC.STR_EMPTY);
			if (st.length() > 0)
			{
				String [] ss = st.split(";");
				setLocation(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
			}
			else
			{
				setLocation(szScreen.width/4, szScreen.height/4);
			}
		}
		_splPanel.setDividerLocation(node.getInt("SplitDividerLocation", 100));
		
		_currSesFileName = node.get("LastLoadedSession", CC.STR_EMPTY);
		_lastSelectedQueryCode = node.getInt("LastSelectedQuery", 0);
	}
	
	private void SaveProgramPreference()
	{
		Preferences node = Preferences.userRoot().node(PREFERENCE_PATH);
		
		int frameState = this.getState();
		node.putInt("FormState", frameState == Frame.ICONIFIED ? Frame.NORMAL : frameState);
		if (frameState == Frame.NORMAL)
		{
			Dimension sz = 	getSize();
			node.put("FormSize", sz.width + ";" + sz.height);
			Point pt = getLocation();
			node.put("FormLocation", pt.x + ";" + pt.y);
		}
		node.putInt("SplitDividerLocation", _splPanel.getDividerLocation());
		node.put("LastLoadedSession", _currSesFileName);
		if (_qp.get_currentQuery()!= null)
			node.putInt("LastSelectedQuery", _qp.get_currentQuery().Code);

	}
}

