package br.scpl.model;

import java.lang.management.CompilationMXBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import org.apache.commons.lang3.SerializationUtils;

public class Node {
	
	private Node parent;
	private Tree node;
	private List<Node> children;
	private CompilationUnitTree compilatioUnitTree;
	private Long startPosition;
	private Long endPosition;
	private Boolean fakeNode;
	private Boolean fullVisited;
	private Boolean exists;
	private Boolean usingExistsOperator;
	private Boolean changeOperator;
	private Boolean changePoint;
	private Node nodeOfDifferentOperator;
	private Node notParent;
	private Boolean isToReturn;
	private String returnMessage;
	private static final Map<Node,Node> cloneNodeMap = new LinkedHashMap<>();
	private static final Map<Tree,Node> nodesMap = new LinkedHashMap<Tree, Node>();
	
	public Node() {
		this.children = new ArrayList<Node>();
		this.fakeNode = false;
		this.fullVisited = false;
		this.exists = true;
		this.usingExistsOperator = false;
		this.changeOperator = false;
		this.changePoint = false;
		this.isToReturn = false;
	}
	
	public Node(Tree node, CompilationUnitTree compilatioUnitTree) {
		this.node = node;
		this.children = new ArrayList<Node>();
		this.compilatioUnitTree = compilatioUnitTree;
		this.fakeNode = false;
		this.fullVisited = false;
		this.exists = true;
		this.usingExistsOperator = false;
		this.changeOperator = false;
		this.changePoint = false;
		this.isToReturn = false;
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
		this.fakeNode = false;
		this.fullVisited = false;
		this.exists = true;
		this.usingExistsOperator = false;
		this.changeOperator = false;
		this.changePoint = false;
		this.isToReturn = false;
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
	
	public Boolean getFakeNode() {
		return fakeNode;
	}

	public void setFakeNode(Boolean fakeNode) {
		this.fakeNode = fakeNode;
	}

	public Boolean getFullVisited() {
		return fullVisited;
	}

	public void setFullVisited(Boolean fullVisited) {
		if(!this.hasBrother(Tree.Kind.BLOCK)) {
			this.fullVisited = fullVisited;
		}
		
		for(Node child : this.children) {
			child.setFullVisited(exists);
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
			child.setUsingExistsOperator(true);
		}
	}

	public Boolean getUsingExistsOperator() {
		return usingExistsOperator;
	}

	public void setUsingExistsOperator(Boolean usingExistsOperator) {
		this.usingExistsOperator = usingExistsOperator;
		
		if(this.parent != null) {
			this.parent.setUsingExistsOperator(usingExistsOperator);
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

	public void setChangePoint(Boolean changePoint) {
		this.changePoint = changePoint;
	}

	public String toString() {
		return this.node.toString();
	}
	
	public Node getNodeOfDifferentOperator() {
		return nodeOfDifferentOperator;
	}
	
	public void setNodeOfDifferentOperator(Node nodeOfDifferentOperator) {
		this.nodeOfDifferentOperator = nodeOfDifferentOperator;
	}
	
	public void setNodeOfDifferentOperator(List<Node> nodeList) {
		if(nodeList.size() == 1) {
			this.nodeOfDifferentOperator = nodeList.get(0);
		}else {
			Node fakeNode = new Node();
			fakeNode.getChildren().addAll(nodeList);
			fakeNode.setUsingExistsOperator(true);
			fakeNode.setFakeNode(true);
			this.nodeOfDifferentOperator = fakeNode;
		}
	}
	
	public Node getNotParent() {
		return notParent;
	}

	public void setNotParent(Node notParent) {
		this.notParent = notParent;
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
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public static Map<Tree, Node> getNodesMap() {
		return nodesMap;
	}	
	
}
	
