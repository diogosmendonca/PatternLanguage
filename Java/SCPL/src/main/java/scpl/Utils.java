package scpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.source.tree.Tree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

import com.sun.source.tree.ModifiersTree;

/***
 * Classe com métodos utilitários
 * 
 * @author Denis
 *
 */

public class Utils {
	
	/**
	 * 
	 */
	private static final ResourceBundle wildcards = ResourceBundle.getBundle("wildcards");
	private final static String anyVariable = wildcards.getString("anyVariable");
	private final static String someVariable = wildcards.getString("someVariable");
	private final static String anyMethod = wildcards.getString("anyMethod");
	private final static String someMethod = wildcards.getString("someMethod");
	
	
	/***
	 * Recebe duas árvores e um mapa com os wildcard já utilizados.
	 * Retorna um booleano que indica se as duas árvores são iguais.
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return Se duas árvores são iguais 
	 */
	
	public static boolean isEquals(Node a, Node b, Map<String, String> wildcardsMap) {
		
		if(!basicComparation(a, b, wildcardsMap)) {
			return false;
		}
		
		//Verifica se as árvores tem o mesmo número de filhos
		if(a.getChildren().size()!=b.getChildren().size()) {
			return false;
		}
		
		//Para cada filho chama recursivamente o método isEquals
		for(int i=0; i<a.getChildren().size(); i++) {
			if(!isEquals(a.getChildren().get(i), b.getChildren().get(i), wildcardsMap)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	
	/***
	 * Recebe duas árvores e um mapa com os wildcard já utilizados.
	 * Faz as comparações básicas, verifica se são nulos e se o tipo e nome são iguais.
	 * Retorna booleano que indica se passou em todas as comparações básicas.
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return booleano que indica se passou em todas as comparações básicas
	 */
	
	private static boolean basicComparation(Node a, Node b, Map<String, String> wildcardsMap) {
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		//System.out.println("A -> " +a.getNode());
		//System.out.println("B -> " +b.getNode());
		
		if(!isFakeNode(b)) {
		
			//Compara se os tipos sao iguais.
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				return false;
			}
			
			if(!compareName(a, b, wildcardsMap)) {
				return false;
			}
		
		}
		
		return true;
	}
	
	/***
	 * Verifica se o nó pai da árvore recebida é um nó fake(esboço).
	 * 
	 * @param node Árvore do padrão buscado
	 * @return Se o nó pai da árvore é um nó fake
	 */
	
	private static boolean isFakeNode(Node node) {
		//Se a árvore não tem pai e o nó raiz é null é um nó fake.
		if(node.getParent() != null && node.getNode() != null) {
			return false;
		}
		
		return true;
	}
	
	public static List<Node> subtree(Node a, Node b) {
		
		List<Node> ocorrences = new ArrayList<>();
		
		if(isEquals(a, b, new LinkedHashMap<>())) {
			if(isFakeNode(b)) {
				ocorrences.addAll(a.getChildren());
			}else {
				ocorrences.add(a);
			}
			
			return ocorrences;
		}
		
		List<Node> childrenNodesAux = searchChildren(a, b, new LinkedHashMap<>());
		ocorrences.addAll(childrenNodesAux);
		
		List<Node> aux = new ArrayList<>();
		aux.addAll(a.getChildren());		
		aux.removeAll(childrenNodesAux);
		
		for(Node child : aux) {
			ocorrences.addAll(subtree(child, b));
		}
		
		return ocorrences;
	}
	
	
	
	private static List<Node> searchChildren(Node a, Node b, Map<String, String> wildcardsMap) {
		
		List<Node> ocorrences = new ArrayList<>();
		
		if(!basicComparation(a, b, wildcardsMap)) {
			return ocorrences;
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return ocorrences;
		}
		
		List<Node> ocorrencesAux = new ArrayList<>();
		List<Integer> ocorrencesIndex = new ArrayList<>();
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return ocorrences;
			}
			
			searching=true;
			
			while(searching && counter<a.getChildren().size() ) {
				System.out.println(ocorrencesIndex);
				if(!ocorrencesIndex.contains(counter)) {
					if(isEquals(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap)) {
						ocorrencesAux.add(a.getChildren().get(counter));
						ocorrencesIndex.add(counter);
						searching=false;
					}
				}
				counter++;
			}
			
			if(!searching && i == b.getChildren().size() - 1) {
				ocorrences.addAll(ocorrencesAux);
				ocorrencesAux.clear();
				wildcardsMap.clear();
				counter = 0;
				i =-1;
			}
		}
					
		return ocorrences;
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
			if(!compareName(a, b, null)) {
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
	
	static boolean compareName(Node node1, Node node2, Map<String, String> wildcardsMap) {
		
		String name1;
		String name2;
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				return name1.equals(name2);
				
			case METHOD:
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case METHOD_INVOCATION:	
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case VARIABLE:
				
				name1 = ((VariableTree) node1.getNode()).getName().toString();
				name2 = ((VariableTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyVariable)) {
					return true;
				}
				
				if(name2.startsWith(someVariable)) {
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			default:
				return true;
		}
		
	}
	
	public static Node getCompilationUnitTree(Map<Tree, List<Node>> nodes) {
		
		return nodes.get(null).iterator().next();
	}
	
	public static Node removeStub(Map<Tree, List<Node>> nodes) {
		
		Node retorno = nodes.get(getCompilationUnitTree(nodes).getNode()).get(0);
		
		if(retorno.getNode().getKind() != Tree.Kind.CLASS) {
			return retorno;
		}
		
		retorno = nodes.get(retorno.getNode()).get(nodes.get(retorno.getNode()).size()-1);
		
		if(retorno.getNode().getKind() != Tree.Kind.METHOD) {
			return retorno;
		}
		
		retorno = nodes.get(retorno.getNode()).get(nodes.get(retorno.getNode()).size()-1);
		
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
