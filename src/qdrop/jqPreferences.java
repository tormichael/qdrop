package qdrop;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;

/*
 * JQuery preferences class
 * 19.08.2009
 * by M.Tor
 */
public class jqPreferences {

	private Locale _lcl;
	
	public Locale getCurrentLocale()
	{
		return _lcl; 
	}
	public void setCurrentLocale(Locale aLcl)
	{
		_lcl = aLcl;
	}
	
	private int _iconSize;
	public int getIconSize()
	{
		return _iconSize;
	}
	
	public jqPreferences ()
	{
		Load();
		
	}
	
	private void Load()
	{
		Properties prop = new Properties();
		try
		{
			FileInputStream fin = new FileInputStream("JQuery.properties");
			prop.load(fin);
		}
		catch (IOException ex)
		{
		}

		String lang = prop.getProperty("Language", "");
		if (lang.length()> 0)
			_lcl = new Locale(lang);
		else
			_lcl = Locale.getDefault();

		try
		{
		_iconSize = Integer.parseInt(prop.getProperty("IconSize"));
		}
		catch (Exception ex)
		{
			_iconSize = 24;
		}

	}
	
	public void Save()
	{
		Properties prop = new Properties();
		try
		{
			FileOutputStream fout = new FileOutputStream("JQuery.properties");
			prop.put("Language", _lcl.getDisplayName());
			prop.put("IconSize", ((Integer)_iconSize).toString());
			prop.store(fout, "JQuery user defined prperties");
		}
		catch (IOException ex)
		{
		}
	}
}
