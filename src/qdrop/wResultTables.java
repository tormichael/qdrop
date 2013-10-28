package qdrop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLDocument;

import qdrop.ExecuteQuery.ResultTable;
import qdrop.ses.Param;

import JCommonTools.CC;

//import sun.text.resources.FormatData;


/**
 * Show query executed as tables array.
 * @author M.Tor
 * @since 27.05.2010
 */
public class wResultTables extends JFrame 
{
	private WorkSession 	_ws;
	private Thread 			_thr;
	
	//private jqPreferences _prf;
	private ResourceBundle _bnd;
	private JLabel _sbiMain;
	private JTable _tabResult;
	private JEditorPane _txtDebug;
	private JTabbedPane _tpResult;
	private JButton _btnStopExecution;
	private JProgressBar _prbar;
	private JLabel _sbTimer;
	private JComboBox _cboTablesSet;
	private Timer _activityMonitor;
	private Date _processBegin;
	private ExecuteQuery _eq;
	private ResultTableModel _tabModel;
	
	private int _firstDisplayResTabRow;
	private int _lastDisplayResTabRow;
	
	public wResultTables(WorkSession aQP, jqPreferences aPrf)
	{
		_ws = aQP;
		//_prf = aPrf;
		_bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, aPrf.getCurrentLocale());

		_firstDisplayResTabRow = -1;
		_lastDisplayResTabRow = -1;
		
		Dimension szScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(szScreen.width*2/3, szScreen.height*2/3);
		setLocation((int)(szScreen.width/4), (int)(szScreen.height/4));
		
		/**
		 *   T O O L S   B A R
		 */
		JToolBar bar = new JToolBar();
		add(bar, BorderLayout.NORTH);
		
		int szUnit = aPrf.getIconSize(); 
		actSaveXML.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("save.png", szUnit));
		bar.add(actSaveXML);
		actOpenCalcSheet.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("tocalc.png", szUnit));
		bar.add(actOpenCalcSheet);
		bar.addSeparator();
		actStopExecution.putValue(Action.SMALL_ICON, ImageTools.CreateIcon("stop.png", szUnit));
		_btnStopExecution = bar.add(actStopExecution);
		_btnStopExecution.setEnabled(false);
		
		bar.addSeparator(new Dimension(szUnit*5, szUnit));
		JLabel lblTablesSet = new JLabel(_bnd.getString("Label.wResTab.TablesSet"));
		bar.add(lblTablesSet, BorderLayout.EAST);
		bar.addSeparator();
		_cboTablesSet = new JComboBox();
		bar.add(_cboTablesSet, BorderLayout.EAST);
		
		_tpResult = new JTabbedPane();
		add(_tpResult, BorderLayout.CENTER);

		JPanel pnlTable = new JPanel(new BorderLayout());
		_tpResult.addTab(_bnd.getString("TabPanel.wResTab.Result"), ImageTools.CreateIcon("resultab.png", szUnit), pnlTable);
		_tabResult = new JTable(); //_tabModel);
		_tabResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollTabRes = new JScrollPane(_tabResult); 
		pnlTable.add(scrollTabRes, BorderLayout.CENTER);
		
		scrollTabRes.getViewport().addChangeListener(new ChangeListener() 
		{
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				JViewport vp = (JViewport) e.getSource();
				Rectangle vRect = vp.getViewRect();
				_firstDisplayResTabRow = _tabResult.rowAtPoint(new Point(0, vRect.y));
				_lastDisplayResTabRow = _tabResult.rowAtPoint(new Point(0, vRect.y+vRect.height-1));
				ResizeColumnByContent();
			}
		});
		
		
		JPanel pnlDebug = new JPanel(new BorderLayout());
		_tpResult.addTab(_bnd.getString("TabPanel.wResTab.Debug"), ImageTools.CreateIcon("debuginfo.png", szUnit), pnlDebug);
		_txtDebug = new JEditorPane();
		pnlDebug.add(new JScrollPane(_txtDebug), BorderLayout.CENTER);

		
		/**
		 * S T A T U S   B A R
		 */
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(BorderFactory.createRaisedBevelBorder());
		//statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		statusBar.setLayout(new BorderLayout());
		add(statusBar, BorderLayout.SOUTH);
		_sbiMain = new JLabel();
		//_sbiMain.setText();
		_sbiMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(0,5,0,5)));
		statusBar.add(_sbiMain, BorderLayout.WEST);
		
		JPanel sbProcess = new JPanel();
		statusBar.add(sbProcess, BorderLayout.EAST);
		sbProcess.setLayout(new FlowLayout(FlowLayout.LEFT));
		_prbar = new JProgressBar();
		//_prbar.setMinimum(0);
		_prbar.setVisible(false);
		sbProcess.add(_prbar);
		
		_sbTimer = new JLabel();
		_sbTimer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(1,5,1,5)));
		sbProcess.add(_sbTimer);
		_sbTimer.setVisible(false);
		_processBegin = new Date();

		this.setIconImage(ImageTools.CreateIcon("ExecTab.png", szUnit).getImage());
		
		_eq = new ExecuteQuery(_ws);
		_thr = new Thread(_eq);
		
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e) 
			{
				RunQuery();
			}
		});
		
		_activityMonitor = new Timer(500, new ActionListener() 
		{  
			public void actionPerformed(ActionEvent event)
			{  
				// show progress
				//textArea.append(current + "\n");
				_prbar.setStringPainted(!_prbar.isIndeterminate());
				//_prbar.setValue(current);
				_sbTimer.setText("" + ((System.currentTimeMillis()-_processBegin.getTime())/1000) + " sec.");   
				// check if task is completed
				if (_thr.getState() == Thread.State.TERMINATED)
				{  
					_activityMonitor.stop();
					_btnStopExecution.setEnabled(false);
					_prbar.setVisible(false);
					_sbTimer.setVisible(false);
					_sbiMain.setText(_bnd.getString("Text.wResTab.sbFinish"));
					if (_eq.isError())
						AddError2Debug(_eq.get_errSQL());
					else
						ShowResultTables();
				}
			}
		});
	}
	
	private void AddError2Debug(String aErrText)
	{
		String ss = 
			"<table border=\"1\"><tr><td>ERROR</td><td>" 
			+ aErrText.replaceAll("\n", "<br/>") 
			+ "</td></tr></table>";
		try
		{
			//_txtDebug.getDocument().insertString(_txtDebug.getText().indexOf("ResTab")+12, ss, null);
			HTMLDocument html = (HTMLDocument)_txtDebug.getDocument(); 
			html.insertAfterEnd(html.getElement("ResTab"), ss); 
		}
		catch (Exception ex) {}
	}
	
	private void ShowResultTables()
	{
		if (_eq.getTables().size() == 0)
			return;
		
		for(ResultTable rt : _eq.getTables())
			_cboTablesSet.addItem(rt);
		
		_cboTablesSet.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				ShowSelectedTable((ResultTable)_cboTablesSet.getSelectedItem());
			}
		});
		
		_tpResult.setSelectedIndex(0);
		_cboTablesSet.setSelectedIndex(0);
	}
	
	private void ShowSelectedTable(ResultTable aRT)
	{
		_tabModel = new ResultTableModel(aRT);
		_tabResult.setModel(_tabModel);
		
		_tabResult.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() 
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) 
			{
				if(value != null)
					setText(value.toString());
				if (_ws.get_ses().ColumnNumberName.length()>0 && column == 0)
					setBackground(_btnStopExecution.getBackground());
				return this;
			}
		});
		
		//_sbiMain.setText("first = "+_firstDisplayResTabRow+", last = "+_lastDisplayResTabRow);
	}
	
	private void RunQuery()
	{
		_tpResult.setSelectedIndex(1);
		
		try
		{
			String params = CC.STR_EMPTY;
			for (Param prm : _ws.get_currentQuery().Params)
				params += "<tr><td>"+prm.Name+"</td><td>"+prm.Value+"</td></tr>";

			_txtDebug.setContentType("text/html");
			_txtDebug.setText(
					String.format(
							_bnd.getString("Text.wResTab.DebugInfo"), 
							_ws.get_currentQuery().SQL.replaceAll("\n", "<br/>"), 
							params
			));
			
			_prbar.setVisible(true);
			_prbar.setIndeterminate(true);
			//_prbar.setValue(0);
			//_prbar.setMaximum(1000);
			_sbTimer.setVisible(true);
            _processBegin.setTime(System.currentTimeMillis());
			_sbiMain.setText(_bnd.getString("Text.wResTab.sbExecution"));
			_thr.start();
            _activityMonitor.start();
			_btnStopExecution.setEnabled(true);
			
		}
		catch (Exception ex)
		{
			AddError2Debug(ex.getMessage());
		}
	}
	
	private void ResizeColumnByContent()
	{
		if (_firstDisplayResTabRow == -1 || _lastDisplayResTabRow == _firstDisplayResTabRow)
			return;
		
		FontMetrics fm = new FontMetrics(_tabResult.getFont()) {
		};
		
		for (int ci = 0; ci < _tabResult.getColumnCount(); ci++)
		{
			TableColumn col = _tabResult.getColumnModel().getColumn(ci);
			int maxVal = (int)fm.getStringBounds(_tabResult.getModel().getColumnName(ci), null).getWidth(); 
			int currVal;
			for (int ri = _firstDisplayResTabRow; ri <= _lastDisplayResTabRow; ri++)
			{
				Object val = _tabResult.getModel().getValueAt(ri, ci);
				if (val != null)
					currVal = (int)fm.getStringBounds(val.toString(), null).getWidth();
				else
					currVal = 0;
				
				if (currVal>maxVal)
					maxVal= currVal;
			}
			if (maxVal > 0)
				col.setPreferredWidth(maxVal+10);
				
		}
	}
	
	Action actSaveXML = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// TODO Auto-generated method stub
		}
	};
	
	Action actOpenCalcSheet = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	Action actStopExecution = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//if (_thr.getState() == Thread.State.RUNNABLE)
				_thr.interrupt();
		}
	};
	
	class ResultTableModel extends AbstractTableModel
	{
		
		ExecuteQuery.ResultTable _tab;
		
		//public void set_tab(ExecuteQuery.ResultTable tab) 
		//{
		//	_tab = tab;
		//}
		
		public ResultTableModel()
		{
			this(null);
		}
		public ResultTableModel(ExecuteQuery.ResultTable aTab)
		{
			_tab = aTab;
		}

		@Override
		public int getColumnCount() 
		{
			return _tab != null ? _tab.columnsName.length : 0;
		}

		@Override
		public int getRowCount() 
		{
			return  _tab != null ? _tab.rows.size() : 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			Object ret = null;
			
			if (_tab != null)
			{
				//if (_tab.rows.size() > rowIndex && _tab.rows.get(rowIndex).length > columnIndex)
					ret = _tab.rows.get(rowIndex)[columnIndex];
			}	
			
			return  ret ;
		}

		@Override
		public String getColumnName(int column) 
		{
			return _tab != null ? _tab.columnsName[column] : super.getColumnName(column) ;
		}
		
	}
}
