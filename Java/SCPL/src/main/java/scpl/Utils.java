package scpl;

import java.util.List;

import com.sun.source.tree.Tree;
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
		
		//Compara se os tipos sao iguais
		if(a.getNode().getKind()!=b.getNode().getKind()) {
			return false;
		}
		
		//Caso seja classe, metodo ou variavel,compara os nomes
		if(!compareName(a, b)) {
			return false;
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
	
	public static boolean isEquals2(Node a, Node b) {
		
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		//System.out.println("A -> " +a.getNode());
		//System.out.println("B -> " +b.getNode());
		
		//Compara se os tipos sao iguais
		if(a.getNode().getKind()!=b.getNode().getKind()) {
			return false;
		}
		
		//Caso seja classe, metodo ou variavel,compara os nomes
		if(!compareName(a, b)) {
			return false;
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		int cont = 0;
		for(int i=0; i<b.getChildren().size(); i++) {
			for(int j=0; j<a.getChildren().size(); j++) {

				if(!isEquals2(a.getChildren().get(j), b.getChildren().get(i))) {
					continue;
				}else {
					cont++;
					break;
				}
			}
			
		}
		
		if(cont == b.getChildren().size()) {
			return true;
		}else {

			return false;
		}
		
		
	}
	
	
	public static boolean isSubtree(Node a, Node b) {
				
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		System.out.println("A -> " +a.getNode());
		System.out.println("B -> " +b.getNode());
		
		//Compara se os tipos sao iguais
		if(a.getNode().getKind()!=b.getNode().getKind()) {
			return false;
		}
		
		//Caso seja classe, metodo ou variavel,compara os nomes
		if(!compareName(a, b)) {
			return false;
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		boolean searching = false;
		int counter = 0;
		
		for(int i =0;i<b.getChildren().size();i++) { 
			
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return false;
			}
			
			searching=true;
			
			while(searching && counter<a.getChildren().size() ) {
				if(isSubtree(a.getChildren().get(counter), b.getChildren().get(i))) {
					searching=false;
				}
				counter++;
			}
		}
		
		return true;
	}
	
	//Problema Aqui
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
	
}
