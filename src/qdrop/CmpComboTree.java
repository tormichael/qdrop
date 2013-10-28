package qdrop;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import qdrop.ses.Param;
import qdrop.ses.ParamBase;
import qdrop.ses.eQueryParamType;

import JCommonTools.CC;
import JCommonTools.ComboTree;
import JCommonTools.ItemValDisp;
import JCommonTools.PlaceCode;
import JCommonTools.PlaceCodeText;
import JCommonTools.StructuredQueryLanguage;

/**
 * Component select from tree structure.
 * 
 * @author Tor
 * create: 20.01.2011
 * 
 * Field "SQLText" for parameter type [xxxTree] is followed syntax:
 * -------------------------------------------------
 * | Variables region
 * | ;
 * | SQL or strings set region (level 1 or next to end)
 * | ;
 * | SQL or strings set region (level 2 or next to end)
 * | ;
 * | .......
 * | ;
 * | SQL or strings set region (level N or next to end)
 * -------------------------------------------------
 * where each range delimiter symbol ;
 * 
 * Variables region
 * ================
 * 1) SYSTEM (not displayed in region)
 * 		_code	- first field(column) for types IntXXXTree or PCXXXTree
 * 		_place	- second field(column) for types PCXXXTree
 * 		_text	- first field(column) for types StrXXXTree
 * 		_title	- second or third field(column) for all types
 * 2) User definition variable followed syntax:
 * -----------------------------------------------------
 * | [variable1-name] [variable1-type] [variable1-value]
 * | [variable2-name] [variable2-type] [variable2-value]
 * | ....
 * | [variableN-name] [variableN-type] [variableN-value]
 * -----------------------------------------------------
 * where [variable-type] is
 *  i - Integer (if I insert variable value)
 *  s - String (if S insert variable value)
 *  d - Date (if D insert variable value)
 * 
 * SQL or strings set region
 * =========================
 * SQL sentence followed syntax:
 * -----------------------------------------------------
 * | SELECT [code], [title], [isFolder], .....
 * or 
 * | SELECT [code], [place], [title], [isFolder], .....
 * or 
 * | SELECT [text], [title], [isFolder], ..... 
 * -----------------------------------------------------
 *  or strings set appearance:
 * -----------------------------------------------------
 * | [code1]~[title1]~[isFolder]~... 
 * | [code2]~[title2]~[isFolder]~...
 * | ....
 * | [codeN]~[titleN]~[isFolder]~...
 * or 
 * | [code1]~[place1]~[title1]~[isFolder]~...
 * | [code2]~[place2]~[title2]~[isFolder]~...
 * | ....
 * | [codeN]~[placeN]~[titleN]~[isFolder]~...
 * or 
 * | [text1]~[title1]~[isFolder]~...
 * | [text2]~[title2]~[isFolder]~...
 * | ....
 * | [textN]~[titleN]~[isFolder]~...
 * -----------------------------------------------------
 * where new line is delimiter strings set,
 * isFolder != 0 then this item have children, 
 * otherwise if isFolder == 0 this item don't have children.
 * 
 */
public class CmpComboTree extends ComboTree implements iCmpQueryParam 
{

	private WorkSession	_ws;
	private Param		_param;
	private String[]	_SQLOrStr;
	private String		_err;
	private ParamBaseArrayList _vars;
	private ExecuteQueryParam _eqParam;
	private DefaultMutableTreeNode _root;
	private Thread 		_thr;
	
	public boolean isError()
	{
		return (_err != null && _err.length() > 0);
	}
	
	public CmpComboTree(WorkSession aWS, Param aParam)
	{
		_ws = aWS;
		_param = aParam;
		
		_SQLOrStr = null;
		_err = null;
		_vars = new ParamBaseArrayList(_param.Type);
		_vars.setTitle("root");
		
		_root = new DefaultMutableTreeNode(_vars);
		DefaultTreeModel model = new DefaultTreeModel(_root); 
		mTree.setModel(model);
		mTree.setRootVisible(false); // hide root
		_eqParam = new ExecuteQueryParamSelectTree(aWS, model);
		
		mTree.addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) 
			{
				_loadLevel((DefaultMutableTreeNode)event.getPath().getLastPathComponent());
			}
			@Override
			public void treeCollapsed(TreeExpansionEvent event) 
			{
				//((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).removeAllChildren();
			}
		});
		
	}

	@Override
	public void Load() 
	{
		_parserVarSQLAndSet();
		_loadLevel(_root);
	}
	@Override
	public void Reload() 
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getState() 
	{
		ResourceBundle bnd = _ws.getResourceBundle();
		
		String ret = bnd.getString("Text.Ok"); //CC.STR_EMPTY;
		if (_thr != null)
		{
			if (_thr.getState() == State.RUNNABLE)
				ret = bnd.getString("Text.ExecQueryParam.sbExecution");
			else if (_eqParam != null && _eqParam.isErrSQL())
				ret = _eqParam.getErrSQL();
			//else
				//ret = ;
		}
		
		return ret;
	}
	
	/**
	 * Parser "SQLText" field.
	 */
	private void _parserVarSQLAndSet()
	{
		ResourceBundle bnd = _ws.getResourceBundle();
		_SQLOrStr = _param.SelectText.split(";");

		if (_SQLOrStr == null || _SQLOrStr.length < 2)
		{
			_err = bnd.getString("Error.TreeSQLTextParser");
			return;
		}
		
		//int offsetUV = (_param.Type == eQueryParamType.PCSelectTree || _param.Type == eQueryParamType.PCTree)? 3 : 2;  
		// parser variable region:
		String ss[] = _SQLOrStr[0].split(CC.NEW_LINE);//System.getProperty("line.separator"));
		// user definition variables:
		for (int ii = 0; ii < ss.length; ii++)
		{
			if (ss[ii].trim().length()==0)
				continue; // empty space string (may be for nice)
			
			String vv[] = ss[ii].split(" ");
			// if (vv.length < 2) may be, but in future !!!!!
			if (vv.length != 3)
			{
				_err = bnd.getString("Error.TreeSQLTextParser.WrongVar");
				return;
			}
			ParamBase prm = new ParamBase();
			prm.Name = vv[0];
			if (vv[1].equalsIgnoreCase("i"))
			{
				prm.Type = eQueryParamType.Integer;
				prm.IsInsert = vv[1].equals("I");
			}
			else if (vv[1].equalsIgnoreCase("s"))
			{
				prm.Type = eQueryParamType.String;
				prm.IsInsert = vv[1].equals("S");
			}
			else if (vv[1].equalsIgnoreCase("d"))
			{
				prm.Type = eQueryParamType.Date;
				prm.IsInsert = vv[1].equals("D");
			}
			else
			{
				_err = bnd.getString("Error.TreeSQLTextParser.UndefinedVarType");
				return;
			}
			prm.Value = vv[2];
			_vars.add(prm);
		}
	}
	
	/**
	 * Load level tree for parent.
	 * @param aParent
	 */
	private void _loadLevel (DefaultMutableTreeNode aParent)
	{
		// first remove fake child node item 
		if (aParent.getChildCount() == 1)
		{
			if (((DefaultMutableTreeNode)aParent.getFirstChild()).getUserObject() == null)
			{
				aParent.remove(0);
			}
		}
		
		if (aParent.getChildCount() > 0)
		{
			return;
		}
		
		int level = aParent.getLevel()+1;
		
		String sql = _SQLOrStr.length <= level ? _SQLOrStr[_SQLOrStr.length-1] : _SQLOrStr[level];   

		if (StructuredQueryLanguage.isSelectClause(sql))
		{
			_eqParam.setParamSql(sql);
			_eqParam.setParamsOptional((ArrayList<ParamBase>)aParent.getUserObject());
			((ExecuteQueryParamSelectTree)_eqParam).setParent(aParent);
			_thr = new Thread(_eqParam);
			_thr.start();
		}
		else
		{
			String ss[] = sql.split(CC.NEW_LINE); 
			for(int ii=0; ii < ss.length; ii++)
			{
				String cc[] = ss[ii].split("~");
				ParamBaseArrayList pa = (ParamBaseArrayList)((ParamBaseArrayList)_root.getUserObject()).clone();
				if (cc.length != pa.size())
				{
					_err = _ws.getResourceBundle().getString("Error.TreeSQLTextParser.NotEqualVV");
					break;
				}
				int jj = 1;
				for (ParamBase prm : pa)
				{
					prm.Value = cc[jj];
					jj++;
				}
				DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(pa); 
				nNode.setAllowsChildren(pa.isFolder());
				_root.add(nNode);
			}
		}
	}
	
}
