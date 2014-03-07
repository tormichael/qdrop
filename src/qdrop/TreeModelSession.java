package qdrop;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import qdrop.ses.Query;
import qdrop.ses.Session;

public class TreeModelSession implements TreeModel 
{

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
	public void valueForPathChanged(TreePath path, Object newValue) 
	{
		Query tn = (Query) path.getLastPathComponent();
		if (tn != null && newValue.getClass() == String.class)
		{
			tn.Name = (String) newValue;
		}
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
	
	
	public void InsertAt (Query aQ, Object aParent, int aIndex)
	{
		_ses.Queries.add(aIndex, aQ);
		
        int[]           newIndexs = new int[1];

        newIndexs[0] = aIndex;
        
        Object[]          newChildren = new Object[1];
        newChildren[0] = getChild(aParent, aIndex);
        //fireTreeNodesInserted(this, getPathToRoot(node), newIndexs, newChildren);
	}

    /**
     * Invoke this method if you've modified the {@code TreeNode}s upon which
     * this model depends. The model will notify all of its listeners that the
     * model has changed below the given node.
     *
     * @param node the node below which the model has changed
     */
    public void reload(Query node) 
    {
        if(node != null) 
        {
            //fireTreeStructureChanged(this, getPath(node), null, null);
            fireTreeStructureChanged(this, getPath(node));
        }
    }

	
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent of the nodes that changed; use
     *             {@code null} to identify the root has changed
     * @param childIndices the indices of the changed elements
     * @param children the changed elements
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent the nodes were added to
     * @param childIndices the indices of the new elements
     * @param children the new elements
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent the nodes were removed from
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent of the structure that has changed;
     *             use {@code null} to identify the root has changed
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent of the structure that has changed;
     *             use {@code null} to identify the root has changed
     */
    private void fireTreeStructureChanged(Object source, TreePath path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path);
                //((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }
	
}
