package br.scpl.model;

import java.util.ArrayList;
import java.util.List;

public class BlockCodeStruct {
	
	private List<Node> path;
	private Node node;
	private Node context;
	
	public BlockCodeStruct(List<Node> path, List<Node> nodeList, Node context) {
		List<Node> pathAux = new ArrayList<Node>();
		pathAux.addAll(path);
		this.path = pathAux;
		
		if(nodeList.size() == 1) {
			this.node = nodeList.get(0);
		}else {
			Node fakeNode = new Node();
			fakeNode.getChildren().addAll(nodeList);
			fakeNode.setUsingExistsOperator(true);
			this.node = fakeNode;
		}
		
		this.context = context;
	}
	
	public List<Node> getPath() {
		return path;
	}
	public void setPath(List<Node> path) {
		this.path = path;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public Node getContext() {
		return context;
	}

	public void setContext(Node context) {
		this.context = context;
	}
	
}
