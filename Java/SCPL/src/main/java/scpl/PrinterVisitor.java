package scpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

public class PrinterVisitor extends TreePathScanner<Void, Map<Tree, Collection<Tree>>> {
	
	
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final Node root;

	  public PrinterVisitor(Tree tree) {
		sb = new StringBuilder();
	    indentLevel = 0;
	    root = new Node(tree);
	  }

	  public static String print(Tree tree, Map<Tree, Collection<Tree>> nodes) {
	    PrinterVisitor pv = new PrinterVisitor(tree);
	    pv.scan(tree, nodes);
	    return pv.sb.toString();
	  }

	  private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
	  }	  

	  @Override
	  public Void scan(Tree tree, Map<Tree, Collection<Tree>> nodes) {
	      if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
	        String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
	        indent().append(nodeName).append("\n");
	        
	        if(getCurrentPath()!= null) {
		        Tree parent = getCurrentPath().getLeaf();
			        
		        if (nodes.get(parent) == null) {
					Collection<Tree> l = new HashSet<>();
					l.add(tree);
					nodes.put(parent, l);
				} else {
					nodes.get(parent).add(tree);
				}
	        }
     	  }
	      indentLevel++;
	      super.scan(tree, nodes);
	      indentLevel--;
		 return null;
	  }	  
	  
}
