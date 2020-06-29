package br.scpl.controller;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;

import br.scpl.model.Node;
import br.scpl.util.Utils;

/**
 * @author Denis
 */

class SearchController {
	
	private static Logger log = Logger.getLogger(SearchController.class);
	
	private static final Map<Integer,Map<Node,Node>> returnedNode = new LinkedHashMap<>();
	private static Integer round = 0;
	
	/**
	 * Checks whether a tree is subtree of another and returns all occurrences.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @return List of pattern occurrences in the source code 
	 */
	
	public static List<Node> subtree(Node a, Node b) {
		
		log.debug("");
		log.debug("Searching the pattern " +(b.getCompilatioUnitTree()).getSourceFile().getName());
		log.debug(" in source code file " +(a.getCompilatioUnitTree()).getSourceFile().getName());
		
		List<Node> occurrences = new ArrayList<>();
		
			//Se os nós são iguais, a sub-árvore é toda a ávore
			if(EqualsController.isEquals(a, b, new LinkedHashMap<>())) {
				if(!b.getChangeOperator()) {
					occurrences.add(a);
					a.setFullVisited(true);
					if(returnedNode.get(round) == null) {
						Map<Node,Node> aux = new LinkedHashMap<Node, Node>();
						aux.put(b, a);
						returnedNode.put(round, aux);
					}else {
						returnedNode.get(round).put(b, a);
					}
					
					occurrences = Utils.getReturnNode(occurrences);
					return occurrences;
				}
			}
			
			List<Node> childrenNodesAux = new ArrayList<Node>();
			
			do {
				childrenNodesAux = search(a, b, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
				if(occurrences.containsAll(childrenNodesAux)) {
					occurrences = Utils.getReturnNode(occurrences);
					break;
				}else {
					occurrences.addAll(childrenNodesAux);
					round++;
				}
			}while(childrenNodesAux.size() > 0);
		log.debug("Occurrences found in the file: " +occurrences.size());
		return occurrences;
	}
	
	/**
	 * Manages the search for patterns by calling the subtreeFirstOcorrence function for each child node 
	 * and get an occurrence of an entire pattern.  
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @param path Map that stores the current path in the tree, through the child list indexes.
	 * @param limitPath Map that stores the limit path in current search, through the child list indexes.
	 * @return All nodes that make up the occurrence of a pattern.
	 */

	private static List<Node> search(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath) {
		
		//Lista de ocorrências do padrão
		List<Node> occurrences = new ArrayList<>();
		
		//Se as raízes são diferentes, retorna vazio
		if(!EqualsController.basicComparation(a, b, wildcardsMap)) {
			
			return occurrences;
		}
		
		List<Integer> anyIndex = new ArrayList<Integer>(); 
		
		int notChilds = 0;
		
		if(b.getNode() instanceof ExpressionTree || b.getNode() instanceof VariableTree) {
			return occurrences;
		}
		
		if(b.getNode().getKind()==Kind.METHOD) {
			
			if(EqualsController.isAnyParameter(b)) {
				List<Integer> aux =  ((MethodTree)b.getNode()).getParameters().stream()
										.map(x -> b.getChildrenbyTree(x))
											.map(y -> b.getChildren().indexOf(y)).collect(Collectors.toList());
				anyIndex.addAll(aux);
			}
			
			if(EqualsController.isAnyThrows(b)) {
				List<Integer> aux =  ((MethodTree)b.getNode()).getThrows().stream()
						.map(x -> b.getChildrenbyTree(x))
							.map(y -> b.getChildren().indexOf(y)).collect(Collectors.toList());
				anyIndex.addAll(aux);
			}
			
			ModifiersTree modifierCodeTree = ((MethodTree)a.getNode()).getModifiers();
			Node modifierCode = a.getChildrenbyTree(modifierCodeTree);
			
			ModifiersTree modifierPatternTree = ((MethodTree)b.getNode()).getModifiers();
			Node modifierPattern = b.getChildrenbyTree(modifierPatternTree);
			
			if(!EqualsController.isEquals(modifierCode,modifierPattern, new LinkedHashMap<>())) {
				return occurrences;
			}
			
			if(EqualsController.isAnyModifier(b)) {
				
				anyIndex.add(b.getChildren().indexOf(modifierPattern));
			}
		}
		
		if(b.getNode().getKind() == Kind.CLASS) {
			ModifiersTree modifierCodeTree = ((ClassTree)a.getNode()).getModifiers();
			Node modifierCode = a.getChildrenbyTree(modifierCodeTree);
			
			ModifiersTree modifierPatternTree = ((ClassTree)b.getNode()).getModifiers();
			Node modifierPattern = b.getChildrenbyTree(modifierPatternTree);
			
			if(!EqualsController.isEquals(modifierCode,modifierPattern, new LinkedHashMap<>())) {
				return occurrences;
			}
			
			if(EqualsController.isAnyModifier(b)) {
				
				anyIndex.add(b.getChildren().indexOf(modifierPattern));
			}
		}
		
		if(b.getNode().getKind() == Kind.BLOCK) {
			if(!a.getParent().equals(returnedNode.get(round).get(b.getParent()))){
				return occurrences;
			}
		}
		
		
		if(!verifyNotParent(a, b, wildcardsMap)) {
			return occurrences;
		}
		
		if(returnedNode.get(round) == null) {
			Map<Node,Node> aux = new LinkedHashMap<Node, Node>();
			aux.put(b, a);
			returnedNode.put(round, aux);
		}else {
			returnedNode.get(round).put(b, a);
		}
		
		//Lista auxiliar que guarda as ocorrências da busca atual
		List<Node> currentOccurrences = new ArrayList<>();
		
		List<Node> subtreeAux;
		
		boolean searching = false;
		int counter = 0;
		int i = 0;
		int lastOcorrenceIndex = counter;
		int maxIndexA = a.getChildren().size()-1;
		
		Map<String, String> wildcardsMapLastFail = new LinkedHashMap<>();
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		Map<Node, Integer> pathOld = new LinkedHashMap<>();
		pathOld.putAll(path);
		
		Map<Node, Integer> limitPathOld = new LinkedHashMap<>();
		limitPathOld.putAll(limitPath);
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			if(searching) {
				return occurrences;
			}
			
			if(!anyIndex.contains(i)) {
			
			searching=true;
			
			//Enquanto está buscando e contador é menor que número de filhos de do código fonte
				while(searching && counter<=maxIndexA ) {
					//Se o índice atual não está na lista de índices das ocorrências
					if(!a.getChildren().get(counter).getFullVisited()) {
						//Se é igual é adicionado a lista de ocorrencias auxiliar 
						
						subtreeAux = subtreeFirstOcorrence(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap, path, limitPath);
						
						
						if(subtreeAux.size() > 0) {
							if(b.getChildren().get(i).getExists()) {
								currentOccurrences.addAll(subtreeAux);
								searching=false;
								lastOcorrenceIndex = counter;
								limitPath.clear();
								limitPath.putAll(limitPathOld);
								maxIndexA = a.getChildren().size()-1;
							}else {
 								if(i == b.getChildren().size() - 1) {
									return occurrences;
								}
								searching=false;
								maxIndexA = counter;
								counter = lastOcorrenceIndex;							
							}
						}
					}
					
					if(searching==false) {
						path.put(a, counter);
					}
					
					if(searching==true || currentOccurrences.contains(a.getChildren().get(counter)) ) {
						counter++;
					}
				}
			}
			if(searching && !(i == b.getChildren().size() - 1)) {
				if(!b.getChildren().get(i).getExists()) {
					counter = lastOcorrenceIndex++;
					searching = false;
				}
			}
			
			if(i == b.getChildren().size() - 1) {
				if(searching){
					if(!b.getChildren().get(i).getExists() && !b.getChildren().get(i).getChangeOperator()) {
						occurrences.addAll(currentOccurrences);
					}else {
						//Se usou wildcards, deve recomeçar a busca mesmo não tendo achado
						if(!wildcardsMap.equals(wildcardsMapBefore) && (wildcardsMapLastFail.isEmpty() || !wildcardsMap.equals(wildcardsMapLastFail))) {
							i =-1;
							searching=false;
							currentOccurrences.clear();
							wildcardsMapLastFail.clear();
							wildcardsMapLastFail.putAll(wildcardsMap);
							wildcardsMap.clear();
							wildcardsMap.putAll(wildcardsMapBefore);
							path.clear();
							path.putAll(pathOld);
							counter = 0;
							limitPath.clear();
							limitPath.putAll(limitPathOld);
							maxIndexA = a.getChildren().size()-1;
						}
					}
					
				}else{
					occurrences.addAll(currentOccurrences);
				}
			}
		}
					
		return occurrences;
	}
	
	/***
	 * Do a subtree search, but only return the first ocorrence.
	 * Go through the entire tree recursively looking for an equal tree.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @param path Map that stores the current path in the tree, through the child list indexes.
	 * @param limitPath Map that stores the limit path in current search, through the child list indexes.
	 * @return Returns only the first occurrence of the subtree.
	 */
	
	private static List<Node> subtreeFirstOcorrence(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath){
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		Map<Node, Integer> pathOld = new LinkedHashMap<>();
		pathOld.putAll(path);
		
		Map<Node, Integer> limitPathOld = new LinkedHashMap<>();
		limitPathOld.putAll(limitPath);
		
		List<Node> occurrences = new ArrayList<>();
			
		//Se os nós são iguais, a sub-árvore é toda a ávore
		if(EqualsController.isEquals(a, b, wildcardsMap)) {
			if(!b.getChangeOperator()) {
				if(verifyNotParent(a, b, wildcardsMap)){
					occurrences.add(a);
					if(b.getExists()) {
						a.setFullVisited(true);
						returnedNode.get(round).put(b, a);
					}
				}else {
					wildcardsMap.clear();
					wildcardsMap.putAll(wildcardsMapBefore);
				}
				return occurrences;
			}
		}
		
		wildcardsMap.clear();
		wildcardsMap.putAll(wildcardsMapBefore);
		path.clear();
		path.putAll(pathOld);
		limitPath.clear();
		limitPath.putAll(limitPathOld);
		
		occurrences.addAll(search(a, b, wildcardsMap, path, limitPath));
		
		if(occurrences.size() > 0) {
			return occurrences;
		}
		
		
		//Chama recursivamente para todos os filhos do nó
		int counter = path.get(a) != null ? path.get(a) : 0;
		int maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
		while(counter<=maxIndexA ) {
			Node child = a.getChildren().get(counter);
			if(!child.getFullVisited()) {
				
				wildcardsMap.clear();
				wildcardsMap.putAll(wildcardsMapBefore);
				path.clear();
				path.putAll(pathOld);
				limitPath.clear();
				limitPath.putAll(limitPathOld);
				
				occurrences.addAll(subtreeFirstOcorrence(child, b, wildcardsMap, path, limitPath));
				if(occurrences.size() > 0) {
					
					if(b.getExists()) {
						path.put(a, counter);
					}else {
						limitPath.put(a, counter);
					}
						
					return occurrences;
				}
			}
			counter++;
		}
		
		return occurrences;
	}

	/**
	 * Checks whether the corresponding node meets the 
	 * scope conditions that the node cannot be.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return  boolean that indicating whether it satisfies or not.
	 */
	
	private static boolean verifyNotParent(Node a, Node b, Map<String, String> wildcardsMap) {
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		for(Node notParent: b.getNotExistsAsParents()) {
			
			Node parentAux = a.getParent();
			
			while(parentAux!=null) {
				Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
				wildcardsMapAux.putAll(wildcardsMapBefore);
				if(EqualsController.partialEquals(parentAux,notParent, wildcardsMapAux)) {
					return false;
				}
				parentAux = parentAux.getParent();
			}
		}
		
		return true;
	}
}
