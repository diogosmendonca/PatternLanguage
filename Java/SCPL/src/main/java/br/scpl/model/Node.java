package br.scpl.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
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
	private Boolean fullVisited;
	private Boolean exists;
	private Boolean changeOperator;
	private List<Node> notParents;
	private Boolean isToReturn;
	private String returnMessage;
	private Issue issue;
	private Map<Node, Integer> path = new LinkedHashMap<>();
	private static final Map<Node,Node> cloneNodeMap = new LinkedHashMap<>();
	private static final Map<Tree,Node> nodesMap = new LinkedHashMap<Tree, Node>();
	
	public Node(Tree node, CompilationUnitTree compilatioUnitTree) {
		this.node = node;
		this.children = new ArrayList<Node>();
		this.compilatioUnitTree = compilatioUnitTree;
		this.fullVisited = false;
		this.exists = true;
		this.changeOperator = false;
		this.isToReturn = false;
		this.notParents = new ArrayList<Node>();
	}
	
	public Node(Node node, List<Node> ignore) {
				
		this.parent = null;
		this.node = node.getNode();
		
		List<Node> children = new ArrayList<Node>();
		
		for(Node child: node.getChildren()) {
			if(!ignore.contains(child)) {
				Node childClone = new Node(child,ignore);
				childClone.setParent(this);
				children.add(childClone);
			}
		}
		
		this.children = children;		
		this.compilatioUnitTree = node.getCompilatioUnitTree();
		this.fullVisited = false;
		this.exists = true;
		this.changeOperator = false;
		this.isToReturn = false;
		this.notParents = new ArrayList<Node>();
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
		return this.getLineMap().getLineNumber(this.endPosition);
	}
	
	public long getEndColumn() {
		return this.getLineMap().getColumnNumber(this.endPosition);
	}
	
	public Boolean getFullVisited() {
		return fullVisited;
	}

	public void setFullVisited(Boolean fullVisited) {
		if(!this.hasBrother(Tree.Kind.BLOCK)) {
			this.fullVisited = fullVisited;
			
			for(Node child : this.children) {
				child.setFullVisited(exists);
			}
		}
		
	}

	public Boolean hasBrother(Kind kind) {
		
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

	public Boolean getExists() {
		return exists;
	}

	public void setExists(Boolean exists) {
		this.exists = exists;
		
		for(Node child : this.children) {
			child.setExists(exists);
		}
	}

	public Boolean getChangeOperator() {
		return changeOperator;
	}

	public void setChangeOperator(Boolean changeOperator) {
		this.changeOperator = changeOperator;
		
		if(this.parent != null) {
			this.parent.setChangeOperator(changeOperator);
		}
		
	}
	
	public String toString() {
		return this.node.toString();
	}
	
	public List<Node> getNotParents() {
		return notParents;
	}

	public Node getChildrenbyTree(Tree tree) {
				
		return this.children.stream().filter(x-> x.getNode().equals(tree))
				.findFirst().orElse(null);
	}
	
	public Boolean getIsToReturn() {
		return isToReturn;
	}

	public void setIsToReturn(Boolean isToReturn) {
		this.isToReturn = isToReturn;
		
		this.children.forEach(n -> n.setIsToReturn(isToReturn));
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	
	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	public static Map<Tree, Node> getNodesMap() {
		return nodesMap;
	}

	public Map<Node, Integer> getPath() {
		return path;
	}
	
	public Node getBlockChild() {
		return this.children.stream().filter(n -> n.getNode().getKind() == Kind.BLOCK).findFirst().get();
	}
	
}
	
