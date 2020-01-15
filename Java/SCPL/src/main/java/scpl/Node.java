package scpl;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.Tree;

public class Node {
	
	private Tree parent;
	private Tree node;
	private List<Node> children;
	
	public Node(Tree node) {
		this.node = node;
		this.children = new ArrayList<Node>();
	}
	
	public Node(Tree parent, Tree node) {
		this.parent = parent;
		this.node = node;
		this.children = new ArrayList<Node>();
	}
	
	
	public Tree getParent() {
		return parent;
	}
	public void setParent(Tree parent) {
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
	
	
	

}
