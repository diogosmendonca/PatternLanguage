package br.scpl.controller;

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

import br.scpl.model.Node;

public class NodeVisitor extends TreePathScanner<Void, Map<Node, List<Node>>> {
	
	
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final CompilationUnitTree compilatioUnitTree;
	  private Node root;
	  private Map<Tree,Node> nodesMap;

	  public NodeVisitor(Tree tree) {
		sb = new StringBuilder();
	    indentLevel = 0;
	    compilatioUnitTree = (CompilationUnitTree) tree;
	    root = new Node();
	    nodesMap = new LinkedHashMap<Tree, Node>();
	  }

	  public static String build(Tree tree, Map<Node, List<Node>> nodes) {
	    NodeVisitor nv = new NodeVisitor(tree);
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
	        	node = new Node(tree,compilatioUnitTree);
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
		  }
		  
		  for(Node key : nodes.keySet()) {
			  Boolean exists = null;
			  if(key != null) {
				  
				  if(key.getNode().getKind() != Kind.BLOCK && key.getParent() != null && key.getParent().getNode().getKind() == Kind.LABELED_STATEMENT) {
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("not")) {
						  exists = false;
					  }
					  
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("exists")) {
						  exists = true;
					  }
					  
					  if(exists != null) {
						  
						  listToRemove.add(key.getParent());
						  
						  int nodesIndexAux = 0;
						  int parenIndexAux = 0;
						  
						  boolean booleanIndexAux = true; 
						  
					  	  Node parentAux = key.getParent().getParent();
					  	
					  	  if(booleanIndexAux) {
					  		  nodesIndexAux = nodes.get(parentAux).indexOf(key.getParent());
					  		  nodes.get(parentAux).remove(key.getParent());
					  	  }
					  	  nodes.get(parentAux).add(nodesIndexAux,key);
					  	  nodesIndexAux++;
					  	
					  	  if(booleanIndexAux) {
					  		  parenIndexAux = parentAux.getChildren().indexOf(key.getParent());
					  		  parentAux.getChildren().remove(key.getParent());
					  	  }
						  parentAux.getChildren().add(parenIndexAux,key);
						  parenIndexAux++;
						
						  booleanIndexAux = false;
						
						  key.setParent(parentAux);
						
						  while(parentAux != null) {
							  parentAux.setUsingExistsOperator(true);
							  parentAux = parentAux.getParent();
						  }
						
						  key.setExists(exists);
						  if(key.getParent().getExists()!=exists) {
							  key.getParent().setChangeOperator(true);
						  }
					  }
					  
				  } 
				  
				  if(key.getNode().getKind() == Kind.BLOCK && key.getParent().getNode().getKind() == Kind.LABELED_STATEMENT) {
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("not")) {
						  exists = false;
					  }
					  
					  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase("exists")) {
						  exists = true;
					  }
					  
					  if(exists != null) {
						  
						  listToRemove.add(key);
						  listToRemove.add(key.getParent());
						  
						  int nodesIndexAux = 0;
						  int parenIndexAux = 0;
						  
						  boolean booleanIndexAux = true; 
						  
						  for(Node node :  nodes.get(key)) {
							  	Node parentAux = node.getParent().getParent().getParent();
							  	
							  	if(booleanIndexAux) {
							  		nodesIndexAux = nodes.get(parentAux).indexOf(key.getParent());
							  		nodes.get(parentAux).remove(key.getParent());
							  	}
							  	nodes.get(parentAux).add(nodesIndexAux,node);
							  	nodesIndexAux++;
							  	
							  	if(booleanIndexAux) {
							  		parenIndexAux = parentAux.getChildren().indexOf(key.getParent());
							  		parentAux.getChildren().remove(key.getParent());
							  	}
								parentAux.getChildren().add(parenIndexAux,node);
								parenIndexAux++;
								
								booleanIndexAux = false;
								
								node.setParent(parentAux);
								
								while(parentAux != null) {
									parentAux.setUsingExistsOperator(true);
									parentAux = parentAux.getParent();
								}
								
								node.setExists(exists);
								if(node.getParent().getExists()!=exists) {
									node.getParent().setChangeOperator(true);
								}
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

