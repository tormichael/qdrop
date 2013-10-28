package qdrop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import JCommonTools.*;

public class dlgPreference extends JDialog {

	private jqPreferences _prf;
	
	public dlgPreference(JFrame aParent, jqPreferences aPrf)
	{
		super(aParent, true);
		_prf = aPrf;

		//setSize(300, 200);
		setLocation(300, 200);
		//setUndecorated(true);
		
		ResourceBundle bnd = ResourceBundle.getBundle(Start.FN_RESOURCE_TEXT, _prf.getCurrentLocale());
		//Locale.getAvailableLocales();

		setTitle(bnd.getString("Titles.Preferences"));
		
		JTabbedPane tap = new JTabbedPane();
		JPanel pnlMain = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		pnlMain.setLayout(gbl);

		JLabel _lblLanguage = new JLabel(bnd.getString("Label.Pref.Language"));
		pnlMain.add(_lblLanguage, new GBC(0, 0, 1, 1).setAnchor(GBC.EAST).setIns(5));
		JComboBox _cboLanguage = new JComboBox();
		_cboLanguage.addItem(Locale.getDefault());
		_cboLanguage.addItem(Locale.ROOT);
		_cboLanguage.addItem(new Locale("ru"));
		pnlMain.add(_cboLanguage, new GBC(1, 0, 1, 1).setWeight(100, 0).setFill(GBC.HORIZONTAL));
		
		
		tap.addTab(bnd.getString("TabPanel.Pref.Main"), pnlMain);

		JButton cmdOk = new JButton(bnd.getString("Button.General.Ok"));
		JButton cmdApply = new JButton(bnd.getString("Button.General.Apply"));
		JButton cmdCancel = new JButton(bnd.getString("Button.General.Cancel"));
		
		JPanel pnlButton = new JPanel();
		pnlButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButton.add(cmdOk);
		pnlButton.add(cmdApply);
		pnlButton.add(cmdCancel);
		
		setLayout(new BorderLayout());
		add(tap, BorderLayout.CENTER);
		add(pnlButton, BorderLayout.SOUTH);

		cmdOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Apply();
				setVisible(false);
			}
		});
		cmdApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Apply();
			}
		});
		cmdCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		getRootPane().setDefaultButton(cmdCancel);
		
		pack();
	}
	
	private void Apply()
	{
		_prf.Save();
	}
}
