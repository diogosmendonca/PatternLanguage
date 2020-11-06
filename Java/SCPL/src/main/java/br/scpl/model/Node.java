package br.scpl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

import br.scpl.model.sonarqube.Issue;

public class Node {
	
	private Node parent;
	private Tree node;
	private List<Node> children;
	private CompilationUnitTree compilatioUnitTree;
	private Long startPosition;
	private Long endPosition;
	private boolean fullVisited;
	private boolean exists;
	private boolean changeOperator;
	private List<Node> notExistsAsParents;
	private boolean isToReturn;
	private String returnMessage;
	private List<Issue> issues;
	private Node matchingNode;
	private Map<Node, Integer> path = new LinkedHashMap<>();
	private boolean isParcialReturn;
	private static final Map<Tree,Node> nodesMap = new LinkedHashMap<>();
	
	public Node(Tree node, CompilationUnitTree compilatioUnitTree) {
		this.node = node;
		this.children = new ArrayList<>();
		this.compilatioUnitTree = compilatioUnitTree;
		this.fullVisited = false;
		this.exists = true;
		this.changeOperator = false;
		this.isToReturn = false;
		this.notExistsAsParents = new ArrayList<>();
		this.issues = new ArrayList<>();
	}
	
	public Node(Node node, List<Node> ignore) {
				
		this.parent = null;
		this.node = node.getNode();
		
		List<Node> childrenAux = new ArrayList<>();
		
		for(Node child: node.getChildren()) {
			if(!ignore.contains(child)) {
				Node childClone = new Node(child,ignore);
				childClone.setParent(this);
				childrenAux.add(childClone);
			}
		}
		
		this.children = childrenAux;		
		this.compilatioUnitTree = node.getCompilatioUnitTree();
		this.fullVisited = false;
		this.exists = true;
		this.changeOperator = false;
		this.isToReturn = false;
		this.notExistsAsParents = new ArrayList<>();
		this.issues = new ArrayList<>();
	}
	
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public Tree getNode() {
		return node;
	}
	public List<Node> getChildren() {
		return children;
	}
	
	public CompilationUnitTree getCompilatioUnitTree() {
		return compilatioUnitTree;
	}
	
	public void setStartPosition(Long startPosition) {
		this.startPosition = startPosition;
	}

	public void setEndPosition(Long endPosition) {
		this.endPosition = endPosition;
	}

	public LineMap getLineMap() {
		return this.compilatioUnitTree.getLineMap();
	}
	
	public String getFilePath() {
		return this.compilatioUnitTree.getSourceFile().getName();
	}
	
	public long getStartLine() {
		return this.getLineMap().getLineNumber(this.startPosition);
	}
	
	public long getStartColumn() {
		return this.getLineMap().getColumnNumber(this.startPosition);
	}
	
	public long getEndLine() {
		
		if(this.isParcialReturn) {
			if(Arrays.asList(Kind.CLASS,Kind.INTERFACE).contains(this.node.getKind())) {
				return this.getStartLine();
			}
			
			if(this.node.getKind() == Kind.METHOD) {
				return this.getBlockChild().getStartLine();
			}
		}
		
		return this.getLineMap().getLineNumber(this.endPosition);
	}
	
	public long getEndColumn() {
		
		if(this.isParcialReturn) {
			if(Arrays.asList(Kind.CLASS,Kind.INTERFACE).contains(this.node.getKind())) {
				ClassTree classTree = (ClassTree)this.getNode();
				String className = classTree.getSimpleName().toString();
				Node modifier = nodesMap.get(classTree.getModifiers());
				
				int increase = 0;
				
				if(this.node.getKind() == Kind.CLASS) {
					increase = 7;// 7 = 2 spaces + 5 letters(class)
				}
				
				if(this.node.getKind() == Kind.INTERFACE) {
					increase = 11;// 11 = 2 spaces + 9 letters(interface)
				}
				
				return modifier.getEndColumn() + className.length() + increase;
			}
			
			if(this.node.getKind() == Kind.METHOD) {
				return this.getBlockChild().getStartColumn();
			}
		}
		
		return this.getLineMap().getColumnNumber(this.endPosition);
	}
	
	public boolean getFullVisited() {
		return fullVisited;
	}

	public void setFullVisited(boolean fullVisited) {
		if(!this.hasBrother(Tree.Kind.BLOCK)) {
			this.fullVisited = fullVisited;
			
			for(Node child : this.children) {
				child.setFullVisited(exists);
			}
		}
		
	}

	public boolean hasBrother(Kind kind) {
		
		if(this.parent == null) {
			return false;
		}
		
		for(Node brother: this.parent.getChildren()) {
			if(brother.getNode().getKind() == kind) {
				return true;
			}
		}
		return false;
	}

	public boolean getExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
		
		for(Node child : this.children) {
			child.setExists(exists);
		}
	}

	public boolean getChangeOperator() {
		return changeOperator;
	}

	public void setChangeOperator(boolean changeOperator) {
		this.changeOperator = changeOperator;
		
		if(this.parent != null) {
			this.parent.setChangeOperator(changeOperator);
		}
		
	}
	
	public String toString() {
		return this.node.toString();
	}
	
	public List<Node> getNotExistsAsParents() {
		return notExistsAsParents;
	}

	public Node getChildrenbyTree(Tree tree) {
				
		return this.children.stream().filter(x-> x.getNode().equals(tree))
				.findFirst().orElse(null);
	}
	
	public boolean isToReturn() {
		return isToReturn;
	}

	public void setIsToReturn(boolean isToReturn) {
		this.isToReturn = isToReturn;
		
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	
	public List<Issue> getIssues() {
		return issues;
	}

	public Node getMatchingNode() {
		return matchingNode;
	}

	public void setMatchingNode(Node matchingNode) {
		this.matchingNode = matchingNode;
	}

	public static Map<Tree, Node> getNodesMap() {
		return nodesMap;
	}

	public Map<Node, Integer> getPath() {
		return path;
	}
	
	public boolean isParcialReturn() {
		return isParcialReturn;
	}

	public void setParcialReturn(boolean isParcialReturn) {
		this.isParcialReturn = isParcialReturn;
	}

	public Node getBlockChild() {
		return this.children.stream().filter(n -> n.getNode().getKind() == Kind.BLOCK).findFirst().orElse(null);
	}
	
	public List<Node> getChildrenThatExists(){
		
		List<Node> childrenThatExists = new ArrayList<>();
		
		for(Node c: this.children) {
			if(c.getExists()) {
				childrenThatExists.add(c);
			}
		}
		
		return childrenThatExists.isEmpty() ? null : childrenThatExists;
	}
	
	public boolean allChildrenDoNotExist() {
		
		for(Node n: this.children) {
			if(n.getExists()) {
				return false;
			}
		}
		
		return true;
	}
	
	public Node transferAlert() {
		Node node = this.parent;
		
		node.setIsToReturn(true);
		
		if(Arrays.asList(Kind.METHOD, Kind.INTERFACE, Kind.CLASS)
				.contains(node.getNode().getKind())) {
			node.setParcialReturn(true);
		}
		
		if(this.returnMessage != null) {
			if(node.returnMessage == null) {
				node.returnMessage = this.returnMessage;
			}else if(!node.returnMessage.contains(this.returnMessage)){
				node.returnMessage += ". " +this.returnMessage;
			}
		}
		
		node.issues.addAll(this.issues);
		
		node.issues = node.issues.stream()
			     .distinct()
			     .collect(Collectors.toList());
		
		return node;
		
	}
	
	public boolean isDefaultModifierAccess() {
		return this.getNode().getKind() == Kind.MODIFIERS && 
				((ModifiersTree) this.getNode()).getFlags().isEmpty();
	}
	
}
	
