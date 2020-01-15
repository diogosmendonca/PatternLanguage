package scpl;

import java.util.List;

public class Utils {
	
	public static boolean isEquals(Node a, Node b) {
		System.out.println("A" +a.getNode()  +"B" +b.getNode());
		if(a.getNode().getKind()!=a.getNode().getKind()) {
			return false;
		}
		
		/* Name ?
		if(a.getNode().getName()!=b.getNode().getName()) {
			return false;
		}*/
		
		if(a.getChildren().size()!=b.getChildren().size()) {
			return false;
		}
		
		for(int i=0; i<a.getChildren().size(); i++) {
			if(!isEquals(a.getChildren().get(i), b.getChildren().get(i))) {
				return false;
			}
		}
		
		return true;
		
	}
	
	public static boolean isSubtree(Node a, Node b) {
		
		if(isEquals(a, b)) {
			return true;
		}
		
		for(Node node : a.getChildren()) {
			return isSubtree(node, b);
		}
		
		return false;
	}
	

}
