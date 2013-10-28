package qdrop;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageTools 
{

	public static ImageIcon CreateIcon(String aFName, int aSize)
	{
		URL url = wSession.class.getResource(Start.FD_RESOURCE_ICONS+aFName);
		ImageIcon ico = new ImageIcon(url);
		return new ImageIcon(ico.getImage().getScaledInstance(aSize, aSize, Image.SCALE_SMOOTH));
	}
	
}
