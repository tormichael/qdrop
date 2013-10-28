package qdrop;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import qdrop.ses.Param;
import JCommonTools.CC;
import JCommonTools.PlaceCode;


public class PanelParam extends JPanel 
{
	
	private JLabel _statusBar;
	public void set_statusBar(JLabel statusBar) 
	{
		_statusBar = statusBar;
		if (_statusBar != null && _tm == null)
		{
			_tm = new Timer(500, new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if (_curComp != null)
						_statusBar.setText(_curComp.getState());
					else
						_statusBar.setText(CC.STR_EMPTY);
				}
			});
			_tm.start();
		}
	}
	
	private WorkSession _qp;
	public WorkSession get_qp() {
		return _qp;
	}
	public void set_qp(WorkSession perf) {
		_qp = perf;
	}
	
	private GridBagLayout _gbl;
	private Timer _tm;
	private iCmpQueryParam _curComp;
	
	public PanelParam()
	{
		_statusBar = null;
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		_gbl = new GridBagLayout();
		setLayout(_gbl);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createMatteBorder(10, 10, 10, 10, getBackground())));
		//setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		_tm = null;
		_curComp = null;
	}
	
	
	
	public void Display()
	{
		this.removeAll();
		
		if (_qp == null || _qp.get_currentQuery() == null)
		{
			validate();
			return;
		}
		
		//JPanel pPnl;
		JLabel pLbl;
		JComponent pCmp;
		int row = 0;
		boolean isGrowFinifh = true;
		//int maxLabelWidth = 0;
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = 0;
		gbcLabel.gridwidth = 1;
		gbcLabel.gridheight = 1;
		gbcLabel.anchor = GridBagConstraints.NORTHWEST;
		gbcLabel.insets.right = 10;
		gbcLabel.insets.bottom = 5;
		GridBagConstraints gbcCmp = new GridBagConstraints();
		gbcCmp.gridx = 1;
		gbcCmp.gridwidth = 1;
		gbcCmp.gridheight = 1;
		gbcCmp.anchor = GridBagConstraints.EAST;
		gbcCmp.insets.bottom = 5;
		gbcCmp.weightx = 1.0;
		
		for (Param prm : _qp.get_currentQuery().Params)
		{
			pCmp = null;
			gbcCmp.fill = GridBagConstraints.HORIZONTAL;
			gbcCmp.weighty = 0.0;
			switch (prm.Type)
			{
				case Integer:
				case PlaceCode:
				case String:
				{
					JTextField txt = new JTextField();
					//txt.setText(arg0);
					pCmp = txt; 
				}
				break;
				case Boolean:
				{
					JCheckBox chk = new JCheckBox();
					pCmp = chk;
				}
				break;
				case Date:
				{
					DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
					JFormattedTextField dt = new JFormattedTextField(df);
					pCmp = dt;
				}
				break;
				case StrSelectList:
				case IntSelectList:
				case PCSelectList:
				{
					CmpComboList cbo = new CmpComboList(_qp, prm);
					pCmp = cbo;
				}
				break;
				case StrList:
				case IntList:
				case PCList:
				{
					CmpList lst = new CmpList();
					pCmp = lst;
				}
				break;
				case StrSelectTree:
				case IntSelectTree:
				case PCSelectTree:
				{
		
					CmpComboTree stv = new CmpComboTree(_qp, prm);
					pCmp = stv;
//					gbcCmp.weighty = 1.0;
//					gbcCmp.fill = GridBagConstraints.BOTH;
//					isGrowFinifh = false;
				}
				break;
				case StrTree:
				case IntTree:
				case PCTree:
				{
					CmpTree tv = new CmpTree();
					pCmp = tv;
					gbcCmp.weighty = 1.0;
					gbcCmp.fill = GridBagConstraints.BOTH;
					isGrowFinifh = false;
				}
				break;
			}
			
			if (pCmp != null)
			{
				pLbl = new JLabel(prm.Title);
				gbcLabel.gridy = row++;
				_gbl.setConstraints(pLbl, gbcLabel);
				add(pLbl);
				
				gbcCmp.gridy = gbcLabel.gridy;
				_gbl.setConstraints(pCmp, gbcCmp);
				add(pCmp);
				
				// define maximum labels width:
				//if (pLbl.getWidth() > maxLabelWidth)
				//	maxLabelWidth = pLbl.getWidth();
				
				if (pCmp instanceof iCmpQueryParam)
					((iCmpQueryParam)pCmp).Load();
				
				if (_statusBar != null)
				{
					pCmp.addFocusListener(new FocusListener() {
						
						@Override
						public void focusLost(FocusEvent e) {
							// TODO Auto-generated method stub
						}
						
						@Override
						public void focusGained(FocusEvent e) 
						{
							if (e.getSource() instanceof iCmpQueryParam)
								_curComp = (iCmpQueryParam)e.getSource();
//							else if (e.getSource() instanceof JComponent && ((JComponent)e.getSource()).getParent() instanceof iCmpQueryParam)
//								_curComp = (iCmpQueryParam)((JComponent)e.getSource()).getParent();
							else
								_curComp = null;
						}
					});
				}
			}
		}
		
		if (isGrowFinifh)
		{
			JPanel pnl = new JPanel();
			gbcCmp.gridy = row;
			gbcCmp.weighty = 1.0;
			gbcCmp.fill = GridBagConstraints.BOTH;
			_gbl.setConstraints(pnl, gbcCmp);
			add(pnl);
		}

		_statusBar.setText(_qp.get_currentQuery().Note);
		
		repaint(); 
		validate();
	}

	public boolean IsDependence(Param aMainParam, Param aPrm) 
	{
		boolean ret;
		switch (aPrm.Type)
		{
			case PCList:
			case PCSelectList:
			case PCSelectTree:
			case PCTree:
			case PlaceCode:
				String ss[] = aPrm.Name.split(Character.toString(PlaceCode.DELIM));
				ret = aMainParam.SelectText.contentEquals(new StringBuffer(_qp.get_ses().ParamBegDelim+ss[0]+_qp.get_ses().ParamEndDelim));
				if (!ret)
					ret = aMainParam.SelectText.contentEquals(new StringBuffer(_qp.get_ses().ParamBegDelim+ss[1]+_qp.get_ses().ParamEndDelim));
			break;
			default:
				ret = aMainParam.SelectText.contentEquals(new StringBuffer(_qp.get_ses().ParamBegDelim+aPrm.Name+_qp.get_ses().ParamEndDelim));
			break;
		}
		return ret;
	} 
	
}
