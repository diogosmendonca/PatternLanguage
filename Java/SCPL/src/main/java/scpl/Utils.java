package scpl;

import java.util.List;
import java.util.Map;

import com.sun.source.tree.Tree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

import com.sun.source.tree.ModifiersTree;

public class Utils {
	
	public static boolean isEquals(Node a, Node b) {
		
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		//System.out.println("A -> " +a.getNode());
		//System.out.println("B -> " +b.getNode());
		
		//Verifica se é nó de esboço
		if(b.getParent() != null && b.getNode() != null) {
		
			//Compara se os tipos sao iguais
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				return false;
			}
			
			//Caso seja classe, metodo ou variavel,compara os nomes
			if(!compareName(a, b)) {
				return false;
			}
		
		}
		
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
	
	public static boolean subtree(Node a, Node b) {
		
		if(isEquals(a, b)) {
			return true;
		}
		
		if(searchChildren(a, b)) {
			return true;
		}
		
		for(Node child : a.getChildren()) {
			if(subtree(child, b)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean searchChildren(Node a, Node b) {
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return false;
			}
			
			searching=true;
			
			while(searching && counter<a.getChildren().size() ) {
				if(isEquals(a.getChildren().get(counter), b.getChildren().get(i))) {
					searching=false;
				}
				counter++;
			}
		}
		
		if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
			return false;
		}
		
		return true;
	}
	
	
	public static boolean isParcialSubtree(Node a, Node b) {
				
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		System.out.println("A -> " +a.getNode());
		System.out.println("B -> " +b.getNode());
		

		//Verifica se é nó de esboço
		if(b.getParent() != null && b.getNode() != null) {
			
			//Compara se os tipos sao iguais
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				return false;
			}
			
			//Caso seja classe, metodo ou variavel,compara os nomes
			if(!compareName(a, b)) {
				return false;
			}
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return false;
			}
			
			searching=true;
			
			while(searching && counter<a.getChildren().size() ) {
				if(isParcialSubtree(a.getChildren().get(counter), b.getChildren().get(i))) {
					searching=false;
				}
				counter++;
			}
		}
		
		if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
			return false;
		}
		
		return true;
	}
	
	static boolean compareName(Node node1, Node node2) {
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				return ((ClassTree) node1.getNode()).getSimpleName().toString()
							.equals(((ClassTree) node2.getNode()).getSimpleName().toString());
				
			case METHOD:
				return ((MethodTree) node1.getNode()).getName().toString()
							.equals(((MethodTree) node2.getNode()).getName().toString()); 
				
			case VARIABLE:
				return ((VariableTree) node1.getNode()).getName().toString()
							.equals(((VariableTree) node2.getNode()).getName().toString());
				
			default:
				return true;
		}
		
	}
	
	public static Node getCompilationUnitTree(Map<Tree, List<Node>> nodes) {
		
		return nodes.get(null).iterator().next();
	}
	
	public static Node removeStub(Map<Tree, List<Node>> nodes) {
		
		Node retorno = nodes.get(getCompilationUnitTree(nodes).getNode()).get(0);
		
		if(retorno.getNode().getKind() == Tree.Kind.CLASS) {
			if(!((ClassTree) retorno.getNode()).getSimpleName().toString()
					.equals("StubClass")){
				
				return retorno;
			}
		}
		
		retorno = nodes.get(retorno.getNode()).get(1);
		
		if(retorno.getNode().getKind() == Tree.Kind.METHOD) {
			if(!((MethodTree) retorno.getNode()).getName().toString()
					.equals("stubMethod")){
				
				return retorno;
			}
		}
		
		retorno = nodes.get(retorno.getNode()).get(2);
		
		if(retorno.getNode().getKind() == Tree.Kind.BLOCK) {
			
			switch(retorno.getChildren().size()) {
			
				case 0:
					return null;
					
				case 1:
					return retorno.getChildren().get(0);
					
				default:
					Node fakeNode = new Node(null);
					fakeNode.getChildren().addAll(retorno.getChildren());
					
					return fakeNode;
			}
		}
		
		return getCompilationUnitTree(nodes);
	}
	
}
