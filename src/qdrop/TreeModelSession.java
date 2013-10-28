package qdrop;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import qdrop.ses.Query;
import qdrop.ses.Session;

public class TreeModelSession implements TreeModel {

	private Session _ses;
	
//	public void set_ses(Session ses) {
//		_ses = ses;
//	}
	
	public TreeModelSession (Session aSes)
	{
		_ses = aSes;
	}
	
	@Override
	public Object getChild(Object parent, int index) 
	{
		Query ret = null;
		
		if (parent instanceof Session)
		{
			ret = ((Session)parent).Queries.get(index);
		}
		else 	
		{
			Query q = (Query)parent;
			if (q.Children != null)
				ret = q.Children.get(index);
		}
		return ret;
	}

	@Override
	public int getChildCount(Object parent) 
	{
		int ret = 0;
		if (parent instanceof Session)
		{
			ret = ((Session)parent).Queries.size();
		}
		else
		{
			Query q = (Query)parent;
			if (q.Children != null)
				ret = q.Children.size();
		}
		return ret;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) 
	{
		int ret = 0;
		if (parent instanceof Session)
		{
			ret = ((Session)parent).Queries.indexOf(child);
		}
		else
		{
			Query q = (Query)parent;
			if (q.Children != null)
				ret = q.Children.indexOf(child);
		}
		return ret;
	}

	@Override
	public Object getRoot() 
	{
		return _ses;
	}

	@Override
	public boolean isLeaf(Object node) 
	{
		if (node instanceof Session)
		{
			return false;
		}
		else
		{
			Query q = (Query)node;
			return (q.Children == null);
		}
	}

	private EventListenerList listenerList = new EventListenerList(); 
	
	@Override
	public void addTreeModelListener(TreeModelListener l) 
	{
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) 
	{
		listenerList.remove(TreeModelListener.class, l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
	}

	public TreePath getPath(Object node)
	{
		TreePath ret = new TreePath(_ses);
		if (node instanceof Query)
		{
			ArrayList<Object> ql = new ArrayList<Object>();
			ql.add(node);
			Query qParent = _ses.getParent((Query)node);
			while (qParent != null)
			{
				ql.add(qParent);
				qParent = _ses.getParent(qParent);
			}
			for (int ii = ql.size()-1; ii >= 0; ii--)
				ret = ret.pathByAddingChild(ql.get(ii));
		}
		return ret;
	}
}
