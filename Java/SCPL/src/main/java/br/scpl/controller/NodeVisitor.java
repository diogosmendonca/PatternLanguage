package br.scpl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

import br.scpl.model.Node;
import br.scpl.model.sonarqube.Issue;
import br.scpl.util.ConfigUtils;
import br.scpl.util.StringUtil;

/**
 * 
 * @author Denis
 *
 */
class NodeVisitor extends TreePathScanner<Void, Map<Node, List<Node>>> {

	  private static final String NOT = ConfigUtils.getProperties().getProperty("not");
	  private static final String EXISTS = ConfigUtils.getProperties().getProperty("exists");  	
	  
	  private static final int INDENT_SPACES = 2;
		
	  private final StringBuilder sb;
	  private int indentLevel;
	  private final CompilationUnitTree compilatioUnitTree;
	  private Tree root;
	  private SourcePositions sourcePos;
	  private boolean isPattern;
	  private final Map<Integer,String> alertMessagesMap;
	  private final Map<Integer,Boolean> existsModifierMap;
	  private final Map<Integer, Issue> issuesMap;
	  private final Map<Node, Boolean> commentExistsMap;

	  private NodeVisitor(CompilationUnitTree tree, SourcePositions sourcePos, Map<Integer,String> alertMessagesMap, Map<Integer,Boolean> existsModifierMap, Map<Integer,Issue> issuesMap, boolean isPattern) {
		  this.sb = new StringBuilder();
		this.indentLevel = 0;
	    this.compilatioUnitTree = tree;
	    this.sourcePos = sourcePos;
	    this.alertMessagesMap = alertMessagesMap;
	    this.existsModifierMap = existsModifierMap;
	    this.issuesMap = issuesMap;
	    this.isPattern = isPattern;
	    this.commentExistsMap = new LinkedHashMap<>();
	  }
	  
	  /**
	   * 
	   * Build a new tree through CompilationUnitTree with more information that will be used in comparing trees.
	   * 
	   * @param tree CompilationUnitTree corresponding the CompilationUnit of the Java file source code. 
	   * @param sourcePos SourcePositions corresponding the positions of the trees.
	   * @param isPattern Boolean that indicates if is a pattern tree or source code tree.
	   * @return root node of the new tree.
	   * @throws IOException Signals that an I/O exception of some sort has occurred.
	   */
	  public static Node build(CompilationUnitTree tree, SourcePositions sourcePos, boolean isPattern) throws IOException {
		Map<Integer,String> alertMessagesMap = new LinkedHashMap<>();
		Map<Integer,Boolean> existsModifierMap = new LinkedHashMap<>();
		Map<Integer,Issue> issuesMap = new LinkedHashMap<>();
		
		if(isPattern && sourcePos != null) {
			alertMessagesMap = StringUtil.extractAlertMessages(tree);
			existsModifierMap = StringUtil.extractExistsOperator(tree);
			issuesMap = StringUtil.extractIssue(tree);
		}
		
	    NodeVisitor nv = new NodeVisitor(tree, sourcePos, alertMessagesMap, existsModifierMap, issuesMap, isPattern);
	    Map<Node, List<Node>> nodes = new LinkedHashMap<>();
	    nv.scan(tree, nodes);
    	addInfos(nodes, isPattern, nv.commentExistsMap);	    	

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
	        
	        Node nodeParent = Node.getNodesMap().get(parent);
	        
	        Node node;
	        
        	node = new Node(tree,compilatioUnitTree);
        	
        	node.setParent(nodeParent);
        	if(nodeParent != null) {
        		nodeParent.getChildren().add(node);
        		if(nodeParent.getNode().getKind() == Kind.EXPRESSION_STATEMENT) {
        			node.setIsToReturn(nodeParent.isToReturn());
            		node.setReturnMessage(nodeParent.getReturnMessage());
            		node.getIssues().addAll(nodeParent.getIssues());
        		}
        	}
        	Node.getNodesMap().put(tree,node);
	        
	        if (nodes.get(nodeParent) == null) {
				List<Node> l = new ArrayList<>();
				
				//Criando nó raiz com sua Tree equivalente e add no mapeamento.
				l.add(node);
				nodes.put(nodeParent, l);
			}else {
				nodes.get(nodeParent).add(node);
			}
	        
        	node.setStartPosition(sourcePos.getStartPosition(node.getCompilatioUnitTree(), node.getNode()));
        	node.setEndPosition(sourcePos.getEndPosition(node.getCompilatioUnitTree(), node.getNode()));
	        
	        	
	        if(node.getNode().getKind() != Kind.COMPILATION_UNIT && isPattern && (!alertMessagesMap.isEmpty() || !existsModifierMap.isEmpty() || !issuesMap.isEmpty())) {
				
				int line = (int) node.getStartLine();
				
				if(!alertMessagesMap.isEmpty()) {
					String msg = alertMessagesMap.remove(line);
					
					if(msg != null) {
						node.setIsToReturn(true);
						node.setReturnMessage(msg);
						commentExistsMap.put(node, true);
					}
				}
				
				if(!existsModifierMap.isEmpty()) {
					Boolean booleanExists = existsModifierMap.remove(line);
					
					if(booleanExists!=null) {
						//extraido para depois do processamento dos labels
						commentExistsMap.put(node, booleanExists);
					}
				}
				
				if(!issuesMap.isEmpty()) {
					Issue issue = issuesMap.remove(line);
					
					if(issue != null) {
						node.setIsToReturn(true);
						node.setReturnMessage(issue.getPrimaryLocation().getMessage());
						node.getIssues().add(issue);
						commentExistsMap.put(node, true);
					}
				}
	        }
	        
     	  }
	      indentLevel++;
	      super.scan(tree, nodes);
	      indentLevel--;
		 return null;
	  }
	  
	  /**
	   * 
	   * After visiting all nodes adds additional information on the nodes,
	   * information needed for comparison trees.
	   * 
	   * @param nodes Map that stores all the children of a node.
	   * @param isPattern Boolean that indicates if is a pattern tree.
	   * @param commentExistsMap Map that keeps its existence modifier comment for each node.
	   */
	  private static void addInfos(Map<Node, List<Node>> nodes, boolean isPattern, Map<Node, Boolean> commentExistsMap) {
		  List<Node> listToRemove = new ArrayList<>();
		  List<Node> listChangePoints = new ArrayList<>();
		  for(Map.Entry<Node,List<Node>> entry : nodes.entrySet()) {
			  	Node key = entry.getKey();
			  	List<Node> value = entry.getValue();
			  
				for(Node node :  value) {
					if(key!=null) {
						node.getPath().putAll(key.getPath());
						node.getPath().put(key,key.getChildren().indexOf(node));						
					}
				}
		  }
		  if(isPattern) {
			  for(Map.Entry<Node,List<Node>> entry : nodes.entrySet()) {
				  Node key = entry.getKey();
				  
				  Boolean booleanExists = null;
				  if(key != null) {
					  
					  if(key.getNode().getKind() != Kind.BLOCK && key.getParent() != null && key.getParent().getNode().getKind() == Kind.LABELED_STATEMENT) {
						  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase(NOT)) {
							  booleanExists = false;
						  }
						  
						  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase(EXISTS)) {
							  booleanExists = true;
						  }
						  
						  Node parentAux = null;
						  
						  if(booleanExists != null) {
							  
							  listToRemove.add(key.getParent());
							  
							  int nodesIndexAux = 0;
							  int parenIndexAux = 0;
							  
						  	  parentAux = key.getParent().getParent();
						  	
					  		  nodesIndexAux = nodes.get(parentAux).indexOf(key.getParent());
					  		  nodes.get(parentAux).remove(key.getParent());
					  		  
						  	  nodes.get(parentAux).add(nodesIndexAux,key);
						  	
					  		  parenIndexAux = parentAux.getChildren().indexOf(key.getParent());
					  		  parentAux.getChildren().remove(key.getParent());

					  		  parentAux.getChildren().add(parenIndexAux,key);
							
							  key.setParent(parentAux);
							
							  key.setExists(booleanExists);
							  if(key.getParent().getExists()!=booleanExists.booleanValue()) {
								  key.getParent().setChangeOperator(true);
							  }
							  
							  if(parentAux != null) {
								  listChangePoints.add(key);
							  }
						  }
						  
					  } 
					  
					  if(key.getNode().getKind() == Kind.BLOCK && key.getParent().getNode().getKind() == Kind.LABELED_STATEMENT) {
						  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase(NOT)) {
							  booleanExists = false;
						  }
						  
						  if(((LabeledStatementTree) key.getParent().getNode()).getLabel().toString().equalsIgnoreCase(EXISTS)) {
							  booleanExists = true;
						  }
						  
						  if(booleanExists != null) {
							  
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
									
									node.setExists(booleanExists);
									if(node.getParent().getExists()!=booleanExists.booleanValue()) {
										node.getParent().setChangeOperator(true);
									}
							  }
							  if(parentAux != null) {
								  listChangePoints.addAll(nodes.get(key));
							  }
						  }
					  }
				  }
			  }
			  
			for(Map.Entry<Node, Boolean> entry: commentExistsMap.entrySet()) {
				Node node = entry.getKey();
				boolean booleanExists = entry.getValue();
				
			  	Node nodeParent = node.getParent();
			  	node.setExists(booleanExists);
				if(nodeParent.getExists()!=booleanExists) {
					nodeParent.setChangeOperator(true);
				}
				listChangePoints.add(node);
			}
			  
			for(Node n : listToRemove) {
				nodes.remove(n);
			}
			
			for(Node node : listChangePoints) {
				if(!node.getExists() && node.getChangeOperator()) {
					notInfos(node, listChangePoints);
				}
			}
		
	  	}
	 }
	 
	 /**
	  *  Adds non-existent information, scopes that the node cannot be present.
	  *  
	  * @param node Node that will be evaluated and added non-existent scope information.
	  * @param listChangePoints List of change points of existence.
	  */
	 private static  void notInfos(Node node, List<Node> listChangePoints) {
		 
		 int index = node.getParent().getChildren().indexOf(node);
		 List<Node> toCallRecursive = new ArrayList<>();
		 
		 Node block = node.getBlockChild();
		 
		 List<Node> blockChildren;
		 blockChildren = block !=null ? block.getChildren(): node.getChildrenThatExists();
		 
		 List<Node> ignoreList = new ArrayList<>();
		 
		 if(blockChildren != null) {
			 ignoreList.addAll(blockChildren);
			 
			 blockChildren.forEach(i -> {
				 if(!i.getExists() && i.getChangeOperator() && !listChangePoints.contains(i)) {
					 toCallRecursive.add(i);
				 }
			 });
			 
			 Node clone = new Node(node,ignoreList);
			 
			 blockChildren.forEach(x -> {
				 	x.getNotExistsAsParents().addAll(node.getNotExistsAsParents());
				 	x.getNotExistsAsParents().add(clone);
				 	x.setParent(node.getParent());
			 });
			 			 
			 node.getParent().getChildren().addAll(index, blockChildren);
			 
			 node.getParent().getChildren().remove(node);
			 
		 }
		 
		 toCallRecursive.forEach(i -> notInfos(i, listChangePoints));
		 
	 }
		 	 	  
}

