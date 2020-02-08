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

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.source.tree.Tree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
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
	
	public static List<Node> searchAux(Node a, Node b, Map<String, String> wildcardsMap) {
		
		List<Node> ocorrences = new ArrayList<>();
		
		//Se os nós são iguais, a sub-árvore é toda a ávore
		if(isEquals(a, b, wildcardsMap)) {
			if(isFakeNode(b)) {
				ocorrences.addAll(a.getChildren());
			}else {
				ocorrences.add(a);
			}
			a.setFullVisited(true);
			return ocorrences;
		}
		
		return searchChildren3(a, b, wildcardsMap);
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
			if(isFakeNode(b)) {
				ocorrences.addAll(a.getChildren());
			}else {
				ocorrences.add(a);
			}
			a.setFullVisited(true);
			return ocorrences;
		}
		
		List<Node> childrenNodesAux = new ArrayList<Node>();
		
		do {
			childrenNodesAux = searchChildren3(a, b, new LinkedHashMap<>());
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
	
	
	/**
	 * 
	 * Quando as árvores não são idênticas, é necessário verificar se o nó raíz é igual e todos os 
	 * filhos do padrão estáo presentes no código-fonte.
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return 
	 */
	
	private static List<Node> searchChildren(Node a, Node b, Map<String, String> wildcardsMap) {
		
		//Lista de ocorrências do padrão
		List<Node> ocorrences = new ArrayList<>();
		
		//Se as raízes são diferentes, retorna vazio
		if(!basicComparation(a, b, wildcardsMap)) {
			return ocorrences;
		}
		
		//Se o código-fonte alvo possui menos nós que o padrão, não tem como o padrão ser sub-árvore. Logo retorna vazio
		if(a.getChildren().size()<b.getChildren().size()) {
			return ocorrences;
		}
		
		//Lista auxiliar que guarda as ocorrências da busca atual
		List<Node> ocorrencesAux = new ArrayList<>();
		//Lista que contém os índices das ocorrências já encontradas, para não serem comparadas novamente quando recomeçar a busca
		List<Integer> ocorrencesIndex = new ArrayList<>();
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			//Se o que falta > o que resta ou ainda está buscando (ou seja, não achou o filho anterior do padrão no código-fonte)
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return ocorrences;
			}
			
			searching=true;
			
			//Enquanto está buscando e contador é menor que número de filhos de do código fonte
			while(searching && counter<a.getChildren().size() ) {
				//Se o índice atual não está na lista de índices das ocorrências
				if(!ocorrencesIndex.contains(counter)) {
					//TODO Sub árvores parciais
					//Se é igual é adicionado a lista de ocorrencias auxiliar
					if(isEquals(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap)) {
						ocorrencesAux.add(a.getChildren().get(counter));
						ocorrencesIndex.add(counter);
						searching=false;
					}
				}
				counter++;
			}
			
			//Se for o último filho do padrão
			if(i == b.getChildren().size() - 1) {
				// E ainda estiver buscando
				if(searching) {
					//Se usou wildcards, deve recomeçar a busca mesmo não tendo achado
					if(!wildcardsMap.isEmpty()) {
						ocorrencesAux.clear();
						wildcardsMap.clear();
						counter = 0;
						i =-1;
						searching=false;
					}
				}else {
					//Recomeça a busca para achar outras ocorrências
					ocorrences.addAll(ocorrencesAux);
					ocorrencesAux.clear();
					wildcardsMap.clear();
					counter = 0;
					i =-1;
				}
			}
		}
					
		return ocorrences;
	}
	
private static List<Node> searchChildren2(Node a, Node b, Map<String, String> wildcardsMap) {
		
		//Lista de ocorrências do padrão
		List<Node> ocorrences = new ArrayList<>();
		
		//Se as raízes são diferentes, retorna vazio
		if(!basicComparation(a, b, wildcardsMap)) {
			return ocorrences;
		}
		
		//Se o código-fonte alvo possui menos nós que o padrão, não tem como o padrão ser sub-árvore. Logo retorna vazio
		if(a.getChildren().size()<b.getChildren().size()) {
			return ocorrences;
		}
		
		//Lista auxiliar que guarda as ocorrências da busca atual
		List<Node> currentOcorrences = new ArrayList<>();
		
		//Lista que contém os índices das ocorrências já encontradas, para não serem comparadas novamente quando recomeçar a busca
		List<Integer> ocorrencesIndex = new ArrayList<>();
		
		List<Node> subtreeAux;
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			//Se o que falta > o que resta ou ainda está buscando (ou seja, não achou o filho anterior do padrão no código-fonte)
			if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
				return ocorrences;
			}
			
			searching=true;
			
			//Enquanto está buscando e contador é menor que número de filhos de do código fonte
			while(searching && counter<a.getChildren().size() ) {
				//Se o índice atual não está na lista de índices das ocorrências
				if(!a.getChildren().get(counter).getFullVisited()) {
					//Se é igual é adicionado a lista de ocorrencias auxiliar
					subtreeAux = searchAux(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap);
					if(subtreeAux.size() > 0) {
						currentOcorrences.addAll(subtreeAux);
						ocorrencesIndex.add(counter);
						searching=false;
					}
				}
				counter++;
			}
			
			//Se for o último filho do padrão
			if(i == b.getChildren().size() - 1) {
				// E ainda estiver buscando
				if(searching) {
					//Se usou wildcards, deve recomeçar a busca mesmo não tendo achado
					if(!wildcardsMap.isEmpty()) {
						counter = 0;
						i =-1;
						searching=false;
					}
				}else {
					//Recomeça a busca para achar outras ocorrências
					ocorrences.addAll(currentOcorrences);
					counter = 0;
					i =-1;
				}
				globalOcorrences.addAll(currentOcorrences);
				currentOcorrences.clear();
				wildcardsMap.clear();
				
			}
		}
					
		return ocorrences;
	}


private static List<Node> searchChildren3(Node a, Node b, Map<String, String> wildcardsMap) {
	
	//Lista de ocorrências do padrão
	List<Node> ocorrences = new ArrayList<>();
	
	//Se as raízes são diferentes, retorna vazio
	if(!basicComparation(a, b, wildcardsMap)) {
		return ocorrences;
	}
	
	//Se o código-fonte alvo possui menos nós que o padrão, não tem como o padrão ser sub-árvore. Logo retorna vazio
	if(a.getChildren().size()<b.getChildren().size()) {
		return ocorrences;
	}
	
	//Lista auxiliar que guarda as ocorrências da busca atual
	List<Node> currentOcorrences = new ArrayList<>();
	
	List<Node> subtreeAux;
	
	boolean searching = false;
	int counter = 0;
	int i = 0;
	
	for(i =0;i<b.getChildren().size();i++) { 
		
		//Se o que falta > o que resta ou ainda está buscando (ou seja, não achou o filho anterior do padrão no código-fonte)
		if(b.getChildren().size()-i > a.getChildren().size()-counter || searching) {
			return ocorrences;
		}
		
		searching=true;
		
		//Enquanto está buscando e contador é menor que número de filhos de do código fonte
		while(searching && counter<a.getChildren().size() ) {
			//Se o índice atual não está na lista de índices das ocorrências
			if(!a.getChildren().get(counter).getFullVisited()) {
				//Se é igual é adicionado a lista de ocorrencias auxiliar
				subtreeAux = searchAux(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap);
				if(subtreeAux.size() > 0) {
					currentOcorrences.addAll(subtreeAux);
					searching=false;
				}
			}
			counter++;
		}
		
		if(i == b.getChildren().size() - 1) {
			if(!searching) {
				ocorrences.addAll(currentOcorrences);
			}	
		}
		
	}
				
	return ocorrences;
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
	
	static boolean compareName(Node node1, Node node2, Map<String, String> wildcardsMap) {
		
		String name1;
		String name2;
		
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
				
			case IDENTIFIER:
				
				name1 = ((IdentifierTree) node1.getNode()).getName().toString();
				name2 = ((IdentifierTree) node2.getNode()).getName().toString();
				
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
	
	/**
	 * Após percorrer a árvore e montar toda a estrutura, dada a lista de todos os nós
	 * o método retorna a raíz (CompilationUnitTree).
	 * 
	 * @param nodes Lista de nós da árvore
	 * @return CompilationUnitTree(raíz)  correspondente a árvore passada
	 */
	public static Node getCompilationUnitTree(Map<Tree, List<Node>> nodes) {
		
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
					Node fakeNode = new Node();
					fakeNode.getChildren().addAll(retorno.getChildren());
					
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
	
	public static Map<Tree, List<Node>> buildTree(Tree tree){
		Map<Tree, List<Node>> nodes = new LinkedHashMap<>();
		NodeVisitor.build(tree,nodes);
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
	
}
