package scpl;

import java.util.List;

public class BlockCodeStruct {
	
	private List<Node> path;
	private Node node;
	
	public BlockCodeStruct(List<Node> path, List<Node> nodeList) {
		this.path = path;
		
		if(nodeList.size() == 1) {
			this.node = nodeList.get(0);
		}else {
			Node fakeNode = new Node();
			fakeNode.getChildren().addAll(nodeList);
			fakeNode.setUsingExistsOperator(true);
			this.node = fakeNode;
		}
		
		
		
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
	
	
}
