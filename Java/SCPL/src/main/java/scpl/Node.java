package scpl;

import java.lang.management.CompilationMXBean;
import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public class Node {
	
	private Node parent;
	private Tree node;
	private List<Node> children;
	private CompilationUnitTree compilatioUnitTree;
	private Long startPosition;
	private Long endPosition;
	private Boolean fullVisited;
	
	public Node() {
		this.children = new ArrayList<Node>();
	}
	
	public Node(Tree node, CompilationUnitTree compilatioUnitTree) {
		this.node = node;
		this.children = new ArrayList<Node>();
		this.compilatioUnitTree = compilatioUnitTree;
		this.fullVisited = false;
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
	public void setNode(Tree node) {
		this.node = node;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public CompilationUnitTree getCompilatioUnitTree() {
		return compilatioUnitTree;
	}

	public void setCompilatioUnitTree(CompilationUnitTree compilatioUnitTree) {
		this.compilatioUnitTree = compilatioUnitTree;
	}
	
	public Long getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Long startPosition) {
		this.startPosition = startPosition;
	}

	public Long getEndPosition() {
		return endPosition;
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
		}
	}

	public Boolean hasBrother(Kind kind) {
		for(Node brother: ((Node)this.getParent()).getChildren()) {
			if(brother.getNode().getKind() == kind) {
				return true;
			}
		}
		return false;
	}
}
