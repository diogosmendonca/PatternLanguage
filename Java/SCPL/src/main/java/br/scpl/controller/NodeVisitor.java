package br.scpl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

import br.scpl.model.Node;
import br.scpl.util.StringUtil;
import br.scpl.util.Utils;

public class NodeVisitor extends TreePathScanner<Void, Map<Node, List<Node>>> {
	
	
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final CompilationUnitTree compilatioUnitTree;
	  private Tree root;
	  private DocTrees docTrees;
	  private SourcePositions sourcePos;
	  private Boolean isPattern;
	  private final Map<Integer,String> alertMessagesMap;
	  private final Map<Integer,Boolean> existsModifierMap;

	  public NodeVisitor(Tree tree, DocTrees docTrees, SourcePositions sourcePos, Map<Integer,String> alertMessagesMap, Map<Integer,Boolean> existsModifierMap, Boolean isPattern) {
		  this.sb = new StringBuilder();
		this.indentLevel = 0;
	    this.compilatioUnitTree = (CompilationUnitTree) tree;
	    this.docTrees = docTrees;
	    this.sourcePos = sourcePos;
	    this.alertMessagesMap = alertMessagesMap;
	    this.existsModifierMap = existsModifierMap;
	    this.isPattern = isPattern;
	    
	  }

	  public static Node build(Tree tree, DocTrees docTrees, SourcePositions sourcePos, Boolean isPattern) throws IOException {
		Map<Integer,String> alertMessagesMap = new LinkedHashMap<>();
		Map<Integer,Boolean> existsModifierMap = new LinkedHashMap<>();
		
		if(sourcePos != null) {
			alertMessagesMap = StringUtil.extractAlertMessages(tree);
			existsModifierMap = StringUtil.extractExistsOperator(tree);
		}
		  
	    NodeVisitor nv = new NodeVisitor(tree, docTrees, sourcePos, alertMessagesMap, existsModifierMap, isPattern);
	    Map<Node, List<Node>> nodes = new LinkedHashMap<>();
	    nv.scan(tree, nodes);
    	addInfos(nodes, isPattern);	    	
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
	        
	        TreePath path = getCurrentPath();
	        
	        Tree parent = path == null? null: path.getLeaf();
	        DocCommentTree dc = null;
	        
	        
	        boolean isToReturn = false;
	        
	        Node nodeParent = Node.getNodesMap().get(parent);
	        
	        Node node;
	        
        	node = new Node(tree,compilatioUnitTree);
        	Node.getNodesMap().put(tree,node);
	        
	        if (nodes.get(nodeParent) == null) {
				List<Node> l = new ArrayList<>();
				
				//Criando nó raiz com sua Tree equivalente e add no mapeamento.
				l.add(node);
				nodes.put(nodeParent, l);
			}else {
				nodes.get(nodeParent).add(node);
			}
	        
	        if(isPattern) {
	        
		        if(!alertMessagesMap.isEmpty() || !existsModifierMap.isEmpty()) {
		        	node.setStartPosition(sourcePos.getStartPosition(node.getCompilatioUnitTree(), node.getNode()));
					node.setEndPosition(sourcePos.getEndPosition(node.getCompilatioUnitTree(), node.getNode()));
					
					int line = (int) node.getStartLine();
					
					if(!alertMessagesMap.isEmpty()) {
						String msg = alertMessagesMap.remove(line);
						
						if(msg != null) {
							node.setIsToReturn(true);
							node.setReturnMessage(msg);
						}
					}
					
					if(!existsModifierMap.isEmpty()) {
						Boolean exists = existsModifierMap.remove(line);
						
						if(exists!=null) {
							node.setExists(exists);
							if(nodeParent.getExists()!=exists) {
								nodeParent.setChangeOperator(true);
								nodeParent.setChangePoint(true);
							}
						}
					}
		        }
	        }
	        
     	  }
	      indentLevel++;
	      super.scan(tree, nodes);
	      indentLevel--;
		 return null;
	  }
	  
	  private static void addInfos(Map<Node, List<Node>> nodes, Boolean isPattern) {
		  List<Node> listToRemove = new ArrayList<Node>();
		  List<Node> listChangePoints = new ArrayList<Node>();
		  for(Node key : nodes.keySet()) {
				for(Node node :  nodes.get(key)) {
					Collection<Node> children = nodes.get(node);
					if(children != null) {
						node.getChildren().addAll(children);
						for(Node child: children) {
							child.setParent(node);
						}
						
					}
					if(key!=null) {
						node.getPath().putAll(key.getPath());
						node.getPath().put(key,key.getChildren().indexOf(node));						
					}
				}
		  }
		  if(isPattern) {
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
								  listChangePoints.add(key);
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
								  listChangePoints.addAll(nodes.get(key));
							  }
						  }
					  }
				  }
			  }
			  
			for(Node n : listToRemove) {
				nodes.remove(n);
			}
			
			for(Node node : listChangePoints) {
				if(!node.getExists()&&node.getChangeOperator()) {
					notInfos(node);
				}
			}
		
	  	}
	 }
	  
	 public static  void notInfos(Node node) {
		 int index = node.getParent().getChildren().indexOf(node);
		 Node nodeWanted = Utils.getDiferentOperatatorNode(node);
			
		//Extraindo nó que não existe
		List<Node> ignoreList = new ArrayList<Node>();
		
		if(nodeWanted.getFakeNode()) {
			ignoreList.addAll(nodeWanted.getChildren());
		}else {
			ignoreList.add(nodeWanted);
		}
		
		//Cópia do padrão para buscar apenas o trecho com operador de existência diferente
		Node clone = new Node(node,ignoreList);
		
		if(nodeWanted.getFakeNode()) {
			nodeWanted.getChildren().forEach(x -> x.setNotParent(clone));
		}else {
			nodeWanted.setNotParent(clone);
		}
		
		if(nodeWanted.getFakeNode()) {
			node.getParent().getChildren().addAll(index, nodeWanted.getChildren());
		}else {
			node.getParent().getChildren().add(index, nodeWanted);
		}
		
		node.getParent().getChildren().remove(node);
		
	 }
		 	 	  
}

