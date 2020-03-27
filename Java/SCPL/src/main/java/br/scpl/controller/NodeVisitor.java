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
	  private Tree root;
	  

	  public NodeVisitor(Tree tree) {
		sb = new StringBuilder();
	    indentLevel = 0;
	    compilatioUnitTree = (CompilationUnitTree) tree;
	  }

	  public static Node build(Tree tree) {
	    NodeVisitor nv = new NodeVisitor(tree);
	    Map<Node, List<Node>> nodes = new LinkedHashMap<>();
	    nv.scan(tree, nodes);
	    addInfos(nodes);
	    System.out.println(nv.sb.toString());
	    return Node.getNodesMap().get(nv.root);
	  }

	  private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
	  }	  

	  @Override
	  public Void scan(Tree tree, Map<Node, List<Node>> nodes) {
	      if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
	        String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
	        indent().append(nodeName).append("\n");
	        
	        if(root == null && tree.getKind()==Kind.COMPILATION_UNIT) {
	        	root = tree;
	        }       
	        
	        Tree parent = null;
	        
	        if(getCurrentPath()!= null) {
	        	parent = getCurrentPath().getLeaf();
	        }
	        
	        Node nodeParent = Node.getNodesMap().get(parent);
	        Node node;
	        
	        if(Node.getNodesMap().get(tree) == null) {
	        	node = new Node(tree,compilatioUnitTree);
	        	Node.getNodesMap().put(tree,node);
	        }else {
	        	node = Node.getNodesMap().get(tree);
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
					  
					  Node parentAux = null;
					  
					  if(exists != null) {
						  
						  listToRemove.add(key.getParent());
						  
						  int nodesIndexAux = 0;
						  int parenIndexAux = 0;
						  
						  boolean booleanIndexAux = true; 
						  
					  	  parentAux = key.getParent().getParent();
					  	
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
						
						  parentAux.setUsingExistsOperator(true);
						
						  key.setExists(exists);
						  if(key.getParent().getExists()!=exists) {
							  key.getParent().setChangeOperator(true);
							  key.getParent().setChangePoint(true);
						  }
						  
						  if(parentAux != null) {
							  parentAux.setNodeOfDifferentOperator(key);
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
						  
						  Node parentAux = null;
						  
						  for(Node node :  nodes.get(key)) {
							  	parentAux = node.getParent().getParent().getParent();
							  	
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
								
								parentAux.setUsingExistsOperator(true);
								
								node.setExists(exists);
								if(node.getParent().getExists()!=exists) {
									node.getParent().setChangeOperator(true);
									node.getParent().setChangePoint(true);
								}
						  }
						  if(parentAux != null) {
							  parentAux.setNodeOfDifferentOperator(nodes.get(key));
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

