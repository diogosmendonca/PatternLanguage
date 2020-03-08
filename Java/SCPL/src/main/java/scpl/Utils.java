package scpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import com.sun.source.util.SourcePositions;
import com.sun.source.tree.IdentifierTree;

import com.sun.source.tree.ModifiersTree;

/***
 * Classe com métodos utilitários
 * 
 * @author Denis
 *
 */

public class Utils {
	
	private static final ResourceBundle wildcards = ResourceBundle.getBundle("wildcards");
	private final static String anyLiteralValue = wildcards.getString("anyLiteralValue");
	private final static String anyClass = wildcards.getString("anyClass");
	private final static String anyMethod = wildcards.getString("anyMethod");
	private final static String someMethod = wildcards.getString("someMethod");
	private final static String anyVariable = wildcards.getString("anyVariable");
	private final static String someVariable = wildcards.getString("someVariable");
	
	private static List<Node> globalOcorrences = new ArrayList<>();
	
	
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
		
		//Se é um nó fake não realiza as verificações
		if(!isFakeNode(b)) {
			
			boolean flagAnyLiteralvalue = false;
			
			if(a.getNode() instanceof LiteralTree && b.getNode().getKind() == Kind.STRING_LITERAL &&
					((String)((LiteralTree)b.getNode()).getValue()).equals(anyLiteralValue)) {
				
				flagAnyLiteralvalue = true;
				
			}
			
			//Compara se os tipos sao iguais.
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				if(!flagAnyLiteralvalue) {
					return false;
				}
			}
			
			if(!compareValue(a, b, flagAnyLiteralvalue, wildcardsMap)) {
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
	
	/**
	 * Verifica se uma árvore é sub árvore de outra e retorna todas as ocorrências
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @return Lista das ocorrências do padrão no código-fonte 
	 */
	
	public static List<Node> subtree(Node a, Node b) {
		
		List<Node> ocorrences = new ArrayList<>();
		
		//Se os nós são iguais, a sub-árvore é toda a ávore
		if(isEquals(a, b, new LinkedHashMap<>())) {
			if(!b.getUsingExistsOperator()|| !b.getChangeOperator()) {
				if(isFakeNode(b)) {
					ocorrences.addAll(a.getChildren());
				}else {
					ocorrences.add(a);
				}
				a.setFullVisited(true);
				return ocorrences;
			}
		}
		
		List<Node> childrenNodesAux = new ArrayList<Node>();
		
		do {
			childrenNodesAux = searchChildren(a, b, new LinkedHashMap<>());
			if(childrenNodesAux.size() > 0 && ocorrences.containsAll(childrenNodesAux)) {
				break;
			}
			ocorrences.addAll(childrenNodesAux);
		}while(childrenNodesAux.size() > 0);
		
		//Chama recursivamente para todos os filhos do nó
		for(Node child : a.getChildren()) {
			if(!child.getFullVisited()) {
				ocorrences.addAll(subtree(child, b));
			}
		}
		
		return ocorrences;
	}
	

private static List<Node> searchChildren(Node a, Node b, Map<String, String> wildcardsMap) {
	
	//Lista de ocorrências do padrão
	List<Node> ocorrences = new ArrayList<>();
	
	//Se as raízes são diferentes, retorna vazio
	if(!basicComparation(a, b, wildcardsMap)) {
		
		return ocorrences;
	}
	
	int notChilds = 0;
	
	//FIXME Problema do padrão de bloco vazio
	if(b.getChildren().size() == 0 && b.getNode().getKind() == Kind.BLOCK) {
		return null;
	}
	
	
	//Se o código-fonte alvo possui menos nós que o padrão, não tem como o padrão ser sub-árvore. Logo retorna vazio
	if(a.getChildren().size()<b.getChildren().size()) {
		int diff = a.getChildren().size()-b.getChildren().size();
		
		for(Node child : b.getChildren()) {
			if(!child.getExists()) {
				notChilds++;
			}
		}
		
		diff = diff + notChilds;
		
		if(diff<0) {
			return ocorrences;
		}
	}
	
	//Lista auxiliar que guarda as ocorrências da busca atual
	List<Node> currentOcorrences = new ArrayList<>();
	
	List<Node> subtreeAux;
	
	boolean searching = false;
	int counter = 0;
	int i = 0;
	int lastOcorrenceIndex = 0;
	int maxIndexA = a.getChildren().size();
	
	Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
	wildcardsMapBefore.putAll(wildcardsMap);
	
	for(i =0;i<b.getChildren().size();i++) { 
		
		//Se o que falta > o que resta ou ainda está buscando (ou seja, não achou o filho anterior do padrão no código-fonte)
		if(b.getChildren().size()-i-notChilds > a.getChildren().size()-counter || searching) {
			return ocorrences;
		}
		
		searching=true;
		
		//Enquanto está buscando e contador é menor que número de filhos de do código fonte
		while(searching && counter<maxIndexA ) {
			//Se o índice atual não está na lista de índices das ocorrências
			if(!a.getChildren().get(counter).getFullVisited()) {
				//Se é igual é adicionado a lista de ocorrencias auxiliar 
				
				subtreeAux = search(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap);
				
				//FIXME Problema do padrão de bloco vazio
				if(subtreeAux==null) {
					searching=false;
					lastOcorrenceIndex = counter;
					maxIndexA = a.getChildren().size();
					counter++;
					continue;
				}
				
				if(subtreeAux.size() > 0) {
					if(b.getChildren().get(i).getExists()) {
						currentOcorrences.addAll(subtreeAux);
						searching=false;
						lastOcorrenceIndex = counter;
						maxIndexA = a.getChildren().size();
					}else {
						if(b.getChildren().get(i).getChangeOperator()) {
							currentOcorrences.addAll(subtreeAux);
							searching=false;
							lastOcorrenceIndex = counter;
							maxIndexA = a.getChildren().size();
						}else {
							//FIXME
							if(i == b.getChildren().size() - 1) {
								return ocorrences;
							}
							searching=false;
							maxIndexA = counter;
							counter = lastOcorrenceIndex;							
						}
					}
				}
			}
			counter++;
		}
		
		if(searching && !(i == b.getChildren().size() - 1)) {
			if(!b.getChildren().get(i).getExists()) {
				counter = lastOcorrenceIndex++;
				searching = false;
			}
		}
		
		if(i == b.getChildren().size() - 1) {
			if(searching){
				if(!b.getChildren().get(i).getExists()) {
					ocorrences.addAll(currentOcorrences);
				}else {
					//Se usou wildcards, deve recomeçar a busca mesmo não tendo achado
					if(!wildcardsMap.equals(wildcardsMapBefore)) {
						counter = 0;
						i =-1;
						searching=false;
						currentOcorrences.clear();
						wildcardsMap.clear();
						wildcardsMap.putAll(wildcardsMapBefore);
					}
				}
				
			}else{
				ocorrences.addAll(currentOcorrences);
			}
		}
	}
				
	return ocorrences;
}
	
	public static List<Node> subtreeFirstOcorrence(Node a, Node b, Map<String, String> wildcardsMap){
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		List<Node> ocorrences = new ArrayList<>();
			
		//Se os nós são iguais, a sub-árvore é toda a ávore
		if(isEquals(a, b, wildcardsMap)) {
			if(!b.getUsingExistsOperator()|| !b.getChangeOperator()) {
				if(isFakeNode(b)) {
					ocorrences.addAll(a.getChildren());
				}else {
					ocorrences.add(a);
				}
				a.setFullVisited(true);
				return ocorrences;
			}
		}
		
		wildcardsMap.clear();
		wildcardsMap.putAll(wildcardsMapBefore);
		
		ocorrences.addAll(searchChildren(a, b, wildcardsMap));
		
		if(ocorrences.size() > 0) {
			return ocorrences;
		}
		
		wildcardsMap.clear();
		wildcardsMap.putAll(wildcardsMapBefore);
		
		//Chama recursivamente para todos os filhos do nó
		for(Node child : a.getChildren()) {
			if(!child.getFullVisited()) {
				ocorrences.addAll(subtreeFirstOcorrence(child, b, wildcardsMap));
				if(ocorrences.size() > 0) {
					return ocorrences;
				}
				wildcardsMap.clear();
				wildcardsMap.putAll(wildcardsMapBefore);
			}
		}
		
		return ocorrences;
	}

	public static List<Node> search(Node a, Node b, Map<String, String> wildcardsMap) {
		
		List<Node> ocorrences = new ArrayList<Node>();
		
		//Se os nós são iguais, a sub-árvore é toda a árvore
		if(isEquals(a, b, wildcardsMap)) {
			if(!b.getUsingExistsOperator() || !b.getChangeOperator()) {
				if(isFakeNode(b)) {
					ocorrences.addAll(a.getChildren());
				}else {
					ocorrences.add(a);
				}
				a.setFullVisited(true);
				return ocorrences;
			}
		}
		
		if(!b.getUsingExistsOperator() || !b.getChangeOperator() ) {
			return searchChildren(a, b, wildcardsMap);
		}else {
			return searchSubtree(a, b, wildcardsMap);
		}
	}

	private static List<Node> searchSubtree(Node a, Node b, Map<String, String> wildcardsMap) {
		
		List<Node> ocorrences = new ArrayList<Node>();
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		if(b.getExists()) {
			
		}else {
			List<BlockCodeStruct> blockWanted = new ArrayList<BlockCodeStruct>();
			getDiferentOperatorBlock(b, new ArrayList<Node>(),blockWanted);
			
			//FIXME Sempre será apenas um bloco de código ?
			BlockCodeStruct block = blockWanted.get(0);
			Node wanted = block.getNode();
			
			List<Node> ocorrenceWanted = subtreeFirstOcorrence(a, wanted, wildcardsMap);
			
			if(ocorrenceWanted.size() > 0) {
				//Cópia do padrão para buscar apenas o trecho com operador de existência diferente
				Node clone = new Node(b);
				
				//Lista dos nós com operador de existência diferente para serem removidos
				List<Node> listToRemove = new ArrayList<Node>();
				
				//Se é um nó fake adiciona seus filhos, se não, adiciona o nó
				if(isFakeNode(block.getNode())) {
					listToRemove.addAll(block.getNode().getChildren()); 
				}else {
					listToRemove.add(block.getNode());
				}
				
				//Busca no mapeamento qual é o seu nó clone, dado o nó
				listToRemove = listToRemove.stream()
						.map(m -> Node.getCloneNodeMap().get(m))
						.collect(Collectors.toList());
				
				//Remove
				Node.getCloneNodeMap().get(block.getContext()).getChildren().removeAll(listToRemove);
				
				List<Node> ocorrencesAux;
				
				//FIXME Pensar melhor o contexto de busca
				Node context = ocorrenceWanted.get(0).getParent();
				while (context != null) {
					ocorrencesAux = searchChildren(context,clone, wildcardsMap);
					if(ocorrencesAux.size() > 0) {
						return ocorrences;
					}
					context = context.getParent(); 
				}
				
				ocorrences.addAll(ocorrenceWanted);
				return ocorrences;
			}else {
				wildcardsMap.putAll(wildcardsMapBefore);
			}
		}
		
		// TODO Auto-generated method stub
		return null;
	}
		
	private static void getDiferentOperatorBlock(Node node, List<Node> path , List<BlockCodeStruct> retorno) {
		Boolean exists = node.getExists(); 
		path.add(node);
		List<Node> nodeList = new ArrayList<Node>();
		for(Node child: node.getChildren()) {
			if(child.getExists()!=exists) {
				nodeList.add(child);
				continue;
			}
			
			if(nodeList.size() > 0) {
				retorno.add(new BlockCodeStruct(path, nodeList, node));
				nodeList.clear();
			}
			
			if(child.getChangeOperator()) {
				getDiferentOperatorBlock(child, path, retorno);				
			}
		}
		if(nodeList.size() > 0) {
			retorno.add(new BlockCodeStruct(path, nodeList, node));
			nodeList.clear();
		}
		path.remove(node);
	}


	/**
	 * 
	 * Realiza a comparação do nome dos nós passados, de acordo com o tipo do nó.
	 * 
	 * @param node1 Árvore do código-fonte alvo
	 * @param node2 Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return booleano que indica se os nós possuem o mesmo nome
	 */
	
	private static boolean compareName(Node node1, Node node2, Map<String, String> wildcardsMap) {
		
		String name1 = null;
		String name2 = null;
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				if(name2.equalsIgnoreCase(anyClass)) {
					return true;
				}
				
				return name1.equals(name2);
				
			case METHOD:
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case METHOD_INVOCATION:	
				
				ExpressionTree e1 = ((MethodInvocationTree) node1.getNode()).getMethodSelect();
				ExpressionTree e2 = ((MethodInvocationTree) node2.getNode()).getMethodSelect();
				
				if(e1.getKind() != e2.getKind()) {
					return false;
				}
				
				switch (e1.getKind()) {
				
					case MEMBER_SELECT: 
						
						name1 = ((MemberSelectTree) e1).getIdentifier().toString();
						name2 = ((MemberSelectTree) e2).getIdentifier().toString();
						
						break;
					
					case IDENTIFIER :
						
						name1 = ((IdentifierTree) e1).getName().toString();
						name2 = ((IdentifierTree) e2).getName().toString();
						
						break;
					
				}
				
				if(name2.equalsIgnoreCase(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
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
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case IDENTIFIER:
				
				name1 = ((IdentifierTree) node1.getNode()).getName().toString();
				name2 = ((IdentifierTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyVariable)) {
					return true;
				}
				
				if(name2.startsWith(someVariable)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
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
	
	private static boolean compareValue(Node node1, Node node2, boolean flagAnyLiteralValue, Map<String, String> wildcardsMap) {
		
		if(flagAnyLiteralValue) {
			return true;
		}
		
		Object value1 = null;
		Object value2 = null;
		
		switch(node1.getNode().getKind()) {
		
			case INT_LITERAL:
				value1 = ((Integer)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Integer)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);

				
			case LONG_LITERAL:
				value1 = ((Long)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Long)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
				
			case FLOAT_LITERAL:
				value1 = ((Float)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Float)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case DOUBLE_LITERAL:
				value1 = ((Double)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Double)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case BOOLEAN_LITERAL:
				value1 = ((Boolean)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Boolean)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case CHAR_LITERAL:
				value1 = ((char)((LiteralTree)node1.getNode()).getValue());
				value2 = ((char)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case STRING_LITERAL:
				value1 = ((String)((LiteralTree)node1.getNode()).getValue());
				value2 = ((String)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case NULL_LITERAL:
				//value1 = ((?)((LiteralTree)node1.getNode()).getValue());
				break;
				
		}
		
		
		return true;
	}
	
	/**
	 * Após percorrer a árvore e montar toda a estrutura, dada a lista de todos os nós
	 * o método retorna a raíz (CompilationUnitTree).
	 * 
	 * @param nodes Lista de nós da árvore
	 * @return CompilationUnitTree(raíz)  correspondente a árvore passada
	 */
	public static Node getCompilationUnitTree(Map<Node, List<Node>> nodes) {
		
		return nodes.get(null).iterator().next();
	}
	
	/**
	 * Dada a árvore do padrão, o método retira a estrutura de esboço (método e classe).
	 * Retornando um nó fake pai de todos os nós do padrão
	 *  ou a raíz do próprio padrão (quando se trata de um padrão de bloco).
	 * 
	 * @param nodes Lista de nós da árvore
	 * @return O nó raíz do padrão
	 */
	
	public static Node removeStub(Map<Node, List<Node>> nodes) {
		
		Node retorno = nodes.get(getCompilationUnitTree(nodes)).get(0);
		
		if(retorno.getNode().getKind() != Tree.Kind.CLASS) {
			return retorno;
		}
		
		retorno = nodes.get(retorno).get(nodes.get(retorno).size()-1);
		
		if(retorno.getNode().getKind() != Tree.Kind.METHOD) {
			return retorno;
		}
		
		retorno = nodes.get(retorno).get(nodes.get(retorno).size()-1);
		
		if(retorno.getNode().getKind() == Tree.Kind.BLOCK) {
			
			switch(retorno.getChildren().size()) {
			
				case 0:
					return null;
					
				case 1:
					if(!retorno.getChildren().get(0).getUsingExistsOperator()) {
						return retorno.getChildren().get(0);
					}
					
				default:
					Node fakeNode = new Node();
					fakeNode.getChildren().addAll(retorno.getChildren());
					fakeNode.setUsingExistsOperator(retorno.getUsingExistsOperator());
					fakeNode.setChangeOperator(retorno.getChangeOperator());
					
					return fakeNode;
			}
		}
		
		return getCompilationUnitTree(nodes);
	}
	
	///////////////////////////////////////////////////
	
	/**
	 * Faz o parse dos arquivos java e retorna as ASTs e um objeto com as posições de cada nó.
	 * 
	 * 
	 * @param files array de arquivos de código-fonte java
	 * @return CompilationUnitStruct correspondente aos arquivos passados, 
	 * que contém um iterator de CompilationUnitTree e um onjsto SourcePositions(guarda as posições do nós)
	 */
	
	public static CompilationUnitStruct parserFileToCompilationUnit(File... files) {
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		SourcePositions pos = Trees.instance(javacTask).getSourcePositions();
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees;
		Iterator<? extends CompilationUnitTree> iter = null;
		
		try {
			compilationUnitTrees = javacTask.parse();
			iter = compilationUnitTrees.iterator();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new CompilationUnitStruct(iter,pos);

	}
	
	public static Map<Node, List<Node>> buildTree(Tree tree){
		Map<Node, List<Node>> nodes = new LinkedHashMap<>();
		VisitorNode.build(tree,nodes);
		//System.out.println(NodeVisitor.build(tree,nodes));
		
		return nodes;
	}
	
	
	/**
	 * Recebe o path da pasta do código-fonte alvo e a pasta com os padrões buscados.
	 * Retorna as ocorrências dos padrões em cada arquivo de código-fonte
	 * 
	 * @param pathCode Caminho da pasta com os arquivos de código-fonte alvos da busca
	 * @param pathPattern Caminho da pasta com os arquivos da regras dos padrões buscados
	 * @return
	 */
	
	public static  List<Node> searchOcorrences(String pathCode, String pathPattern){
		
		List<Node> retorno = new ArrayList<>();
		
		File[] filesPatterns = FileHandler.getFiles(pathPattern);
		
		CompilationUnitStruct compilationUnitStructPattern = Utils.parserFileToCompilationUnit(filesPatterns);
		
		Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitStructPattern.getCompilationUnitTree();
		
        List<Tree> listPattern = new ArrayList<>(); 
  
        // Add each element of iterator to the List 
        compilationUnitsPattern.forEachRemaining(listPattern::add); 
		
		File[] filesCode = FileHandler.getFiles(pathCode);
		
		CompilationUnitStruct compilationUnitStructCode = Utils.parserFileToCompilationUnit(filesCode);
		
		Iterator<? extends CompilationUnitTree> compilationUnitsCode = compilationUnitStructCode.getCompilationUnitTree();
		
		SourcePositions posCode = compilationUnitStructCode.getPos();
		
		while(compilationUnitsCode.hasNext()) {
			
			Tree treeCode = compilationUnitsCode.next();
			
			for(Tree treePattern: listPattern) {
				
				retorno.addAll(Utils.subtree(Utils.getCompilationUnitTree(Utils.buildTree(treeCode)), Utils.removeStub(Utils.buildTree(treePattern))));
				globalOcorrences.clear();
			}
			
		}
		
		//TODO editar saida do retorno (Arquivo, linhas e colunas)
		
		String arquivoAtual = "";
		
		for(Node r : retorno) {
			r.setStartPosition(posCode.getStartPosition(r.getCompilatioUnitTree(), r.getNode()));
			r.setEndPosition(posCode.getEndPosition(r.getCompilatioUnitTree(), r.getNode()));
			
			if(!r.getFilePath().equals(arquivoAtual)) {
				arquivoAtual = r.getFilePath();
				System.out.println(arquivoAtual);
			}
			
			System.out.println("Inicio: L: " +r.getStartLine() +" C: " +r.getStartColumn() );
			System.out.println("Fim: L: " +r.getEndLine() +" C: " +r.getEndColumn());
			
		}
		
		System.out.println(retorno.size());
		
		return retorno;
	}
	
	/*public static boolean usingNotOperator(Node node) {
		
		if(node.getNode().getKind() == Kind.LABELED_STATEMENT) {
			if(((LabeledStatementTree) node.getNode()).getLabel().toString().equalsIgnoreCase("not")) {
				return true;
			}
		}
		
		return false;
	}*/
	
	public static boolean changeOperator(Node node){
		
		for(Node child: node.getChildren()) {
			if(node.getExists() != child.getExists()) {
				return true;
			}
			if(changeOperator(child)) {
				return true;
			}
		}
		
		return false;
	}
	
}
