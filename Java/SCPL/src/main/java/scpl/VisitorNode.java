package scpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

public class VisitorNode extends TreePathScanner<Void, Map<Node, List<Node>>> {
	
	
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final CompilationUnitTree compilatioUnitTree;
	  private Node root;
	  private Map<Tree,Node> nodesMap;

	  public VisitorNode(Tree tree) {
		sb = new StringBuilder();
	    indentLevel = 0;
	    compilatioUnitTree = (CompilationUnitTree) tree;
	    root = new Node();
	    nodesMap = new LinkedHashMap<Tree, Node>();
	  }

	  public static String build(Tree tree, Map<Node, List<Node>> nodes) {
	    VisitorNode nv = new VisitorNode(tree);
	    nv.scan(tree, nodes);
	    addInfos(nodes);
	    System.out.println(nv.sb.toString());
	    return nv.sb.toString();
	  }

	  private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
	  }	  

	  @Override
	  public Void scan(Tree tree, Map<Node, List<Node>> nodes) {
	      if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
	        String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
	        indent().append(nodeName).append("\n");
	        
	        Tree parent = null;
	        
	        if(getCurrentPath()!= null) {
	        	parent = getCurrentPath().getLeaf();
	        }
	        
	        Node nodeParent = nodesMap.get(parent);
	        Node node;
	        
	        if(nodesMap.get(tree) == null) {
	        	node = new Node(tree,compilatioUnitTree, true);
	        	nodesMap.put(tree,node);
	        }else {
	        	node = nodesMap.get(tree);
	        }
	        
	        if (nodes.get(nodeParent) == null) {
				List<Node> l = new ArrayList<>();
				
				//Criando nó raiz com sua Tree equivalente e add no mapeamento.
				l.add(node);
				nodes.put(nodeParent, l);
			}else {
				nodes.get(nodeParent).add(node);
			}
	        
     	  }
	      indentLevel++;
	      super.scan(tree, nodes);
	      indentLevel--;
		 return null;
	  }
	  
	  private static void addInfos(Map<Node, List<Node>> nodes) {
		  List<Node> listToRemove = new ArrayList<Node>();
		  for(Node key : nodes.keySet()) {
				for(Node node :  nodes.get(key)) {
					Collection<Node> children = nodes.get(node);
					if(children != null) {
						node.getChildren().addAll(children);
						for(Node child: children) {
							child.setParent(node);
						}
						
					}
				}
				
			  Boolean exists = null;
			  Boolean parent = false;
			  if(key != null) {
				  /*
				  if(key.getNode().getKind() == Kind.LABELED_STATEMENT) {
					  if(((LabeledStatementTree) key.getNode()).getLabel().toString().equalsIgnoreCase("not")) {
						  exists = false;
						  parent = true;
					  }
					  
					  if(((LabeledStatementTree) key.getNode()).getLabel().toString().equalsIgnoreCase("exist")) {
						  exists = true;
						  parent = true;
					  }
				  }*/
				  
				  if(key.getNode().getKind() == Kind.BLOCK && key.getParent().getNode().getKind() == Kind.LABELED_STATEMENT) {
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("not")) {
						  exists = false;
					  }
					  
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("exists")) {
						  exists = true;
					  }
				  }
				  
				  if(exists != null) {
					  
					  listToRemove.add(key);
					  listToRemove.add(key.getParent());
					  
					  for(Node node :  nodes.get(key)) {
						  	Node parentAux;
						  	
						  	if(parent) {
						  		parentAux = node.getParent();
						  	}else {
						  		parentAux = node.getParent().getParent().getParent();
						  	}
						  	
						  	nodes.get(parentAux).remove(key.getParent());
						  	parentAux.getChildren().remove(key.getParent());
						  	nodes.get(parentAux).add(node);
							parentAux.getChildren().add(node);
							node.setParent(parentAux);
							
							node.setExists(exists);
							for(Node child : node.getChildren()) {
								child.setExists(exists);
							}
								
					  }
				  }
				  
				  
		  }
		}
		  
		for(Node n : listToRemove) {
			nodes.remove(n);
		}
	 }
	  
}

