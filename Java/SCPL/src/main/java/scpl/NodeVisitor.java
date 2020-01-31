package scpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

public class NodeVisitor extends TreePathScanner<Void, Map<Tree, List<Node>>> {
	
	
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final Node root;

	  public NodeVisitor(Tree tree) {
		sb = new StringBuilder();
	    indentLevel = 0;
	    root = new Node(tree);
	  }

	  public static String build(Tree tree, Map<Tree, List<Node>> nodes) {
	    NodeVisitor pv = new NodeVisitor(tree);
	    pv.scan(tree, nodes);
	    addChildren(nodes);
	    return pv.sb.toString();
	  }

	  private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
	  }	  

	  @Override
	  public Void scan(Tree tree, Map<Tree, List<Node>> nodes) {
	      if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
	        String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
	        indent().append(nodeName).append("\n");
	        
	        Tree parent = null;
	        
	        if(getCurrentPath()!= null) {
	        	parent = getCurrentPath().getLeaf();
	        }
			        
	        if (nodes.get(parent) == null) {
				List<Node> l = new ArrayList<>();
				
				//Criando nó raiz com sua Tree equivalente e add no mapeamento.
				l.add(new Node(parent,tree));
				nodes.put(parent, l);
			}else {
				nodes.get(parent).add(new Node(parent,tree));
			}
	        
     	  }
	      indentLevel++;
	      super.scan(tree, nodes);
	      indentLevel--;
		 return null;
	  }
	  
	  private static void addChildren(Map<Tree, List<Node>> nodes) {
		  for(Tree key : nodes.keySet()) {
				for(Node node :  nodes.get(key)) {
					Collection<Node> children = nodes.get(node.getNode());
					if(children != null) {
						node.getChildren().addAll(children);
					}
				}
			}
	 }
	  
}

