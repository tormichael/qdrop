package qdrop;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import qdrop.ses.ParamBase;

public class ExecuteQueryParamSelectTree extends ExecuteQueryParam 
{
	private DefaultMutableTreeNode _node;
	private DefaultTreeModel _model;
	
	public void setParent(DefaultMutableTreeNode aParent)
	{
		_node = aParent;
	}
	
	public ExecuteQueryParamSelectTree(WorkSession aWS, DefaultTreeModel aModel)
	{
		super (aWS);
		_model = aModel;
	}

	@Override
	protected void readRow(ResultSet aRS) throws SQLException 
	{
		super.readRow(aRS);
		ParamBaseArrayList pa = (ParamBaseArrayList)((ParamBaseArrayList)_node.getUserObject()).clone();
		int ii = 1;
		for (ParamBase prm : pa)
		{
			switch (prm.Type)
			{
			case Integer:
				prm.valSetInt(aRS.getInt(ii));
				break;
			case String:
				prm.valSetString(aRS.getString(ii));
				break;
			case Date:
				prm.valSetDate(aRS.getDate(ii));
				break;
			}
			ii++;
		}
		DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(pa); 
		_node.add(nNode);
		if (pa.isFolder())
			nNode.add(new DefaultMutableTreeNode());
	}
	
	@Override
	protected void finishedRun() 
	{
		super.finishedRun();
		_model.reload(_node);
	}
}
