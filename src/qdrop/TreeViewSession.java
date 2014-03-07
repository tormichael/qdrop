package qdrop;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import qdrop.ses.Session;

public class TreeViewSession extends JTree 
{
	//private TreeModelSession _model;
	
	public TreeViewSession(Session aSes)
	{
		super(new TreeModelSession(aSes));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		//this.
	}

	
	public TreePath getPath(Object node)
	{
		return ((TreeModelSession)this.getModel()).getPath(node);
	}
//	public void setNewSession(Session aSes)
//	{
//		TreeModelSession model = (TreeModelSession)this.getModel();
//		model.set_ses(aSes);
//	}
}
