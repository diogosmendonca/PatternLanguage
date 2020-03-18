package br.scpl.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.source.tree.Tree.Kind;

import br.scpl.model.BlockCodeStruct;
import br.scpl.model.Node;
import scpl.Utils;

public class SearchController {
	
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
		if(EqualsController.isEquals(a, b, new LinkedHashMap<>())) {
			if(!b.getUsingExistsOperator()|| !b.getChangeOperator()) {
				if(b.getFakeNode()) {
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
			childrenNodesAux = searchChildren(a, b, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
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
	

	private static List<Node> searchChildren(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath) {
		
		//Lista de ocorrências do padrão
		List<Node> ocorrences = new ArrayList<>();
		
		//Se as raízes são diferentes, retorna vazio
		if(!EqualsController.basicComparation(a, b, wildcardsMap)) {
			
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
		int counter = path.get(a) != null ? path.get(a) : 0;
		int i = 0;
		int lastOcorrenceIndex = counter;
		int maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		Map<Node, Integer> pathOld = new LinkedHashMap<>();
		pathOld.putAll(path);
		
		Map<Node, Integer> limitPathOld = new LinkedHashMap<>();
		limitPathOld.putAll(limitPath);
		
		for(i =0;i<b.getChildren().size();i++) { 
			
			//Se o que falta > o que resta ou ainda está buscando (ou seja, não achou o filho anterior do padrão no código-fonte)
			if(b.getChildren().size()-i-notChilds > a.getChildren().size()-counter || searching) {
				return ocorrences;
			}
			
			searching=true;
			
			//Enquanto está buscando e contador é menor que número de filhos de do código fonte
			while(searching && counter<=maxIndexA ) {
				//Se o índice atual não está na lista de índices das ocorrências
				if(!a.getChildren().get(counter).getFullVisited()) {
					//Se é igual é adicionado a lista de ocorrencias auxiliar 
					
					subtreeAux = search(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap, path, limitPath);
					
					//FIXME Problema do padrão de bloco vazio
					if(subtreeAux==null) {
						searching=false;
						lastOcorrenceIndex = counter;
						limitPath.clear();
						limitPath.putAll(limitPathOld);
						maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
						continue;
					}
					
					if(subtreeAux.size() > 0) {
						if(b.getChildren().get(i).getExists()) {
							currentOcorrences.addAll(subtreeAux);
							searching=false;
							lastOcorrenceIndex = counter;
							limitPath.clear();
							limitPath.putAll(limitPathOld);
							maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
						}else {
							if(b.getChildren().get(i).getChangeOperator()) {
								currentOcorrences.addAll(subtreeAux);
								searching=false;
								lastOcorrenceIndex = counter;
								limitPath.clear();
								limitPath.putAll(limitPathOld);
								maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
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
				
				if(searching==false) {
					path.remove(a);
					path.put(a, counter);
				}
				
				if(searching==true) {
					counter++;
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
					if(!b.getChildren().get(i).getExists()) {
						ocorrences.addAll(currentOcorrences);
					}else {
						//Se usou wildcards, deve recomeçar a busca mesmo não tendo achado
						if(!wildcardsMap.equals(wildcardsMapBefore)) {
							i =-1;
							searching=false;
							currentOcorrences.clear();
							wildcardsMap.clear();
							wildcardsMap.putAll(wildcardsMapBefore);
							path.clear();
							path.putAll(pathOld);
							counter = path.get(a) != null ? path.get(a) : 0;
							limitPath.clear();
							limitPath.putAll(limitPathOld);
							maxIndexA = limitPath.get(a) != null ? limitPath.get(a) : a.getChildren().size()-1;
						}
					}
					
				}else{
					ocorrences.addAll(currentOcorrences);
				}
			}
		}
					
		return ocorrences;
	}
	
	public static List<Node> subtreeFirstOcorrence(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath){
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		Map<Node, Integer> pathOld = new LinkedHashMap<>();
		pathOld.putAll(path);
		
		Map<Node, Integer> limitPathOld = new LinkedHashMap<>();
		limitPathOld.putAll(limitPath);
		
		List<Node> ocorrences = new ArrayList<>();
			
		//Se os nós são iguais, a sub-árvore é toda a ávore
		if(EqualsController.isEquals(a, b, wildcardsMap)) {
			if(!b.getUsingExistsOperator()|| !b.getChangeOperator()) {
				if(b.getFakeNode()) {
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
		path.clear();
		path.putAll(pathOld);
		limitPath.clear();
		limitPath.putAll(limitPathOld);
		
		ocorrences.addAll(searchChildren(a, b, wildcardsMap, path, limitPath));
		
		if(ocorrences.size() > 0) {
			return ocorrences;
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
				
				ocorrences.addAll(subtreeFirstOcorrence(child, b, wildcardsMap, path, limitPath));
				if(ocorrences.size() > 0) {
					
					if(!b.getChangeOperator()) {
						if(b.getExists()) {
							path.remove(a);
							path.put(a, counter);
						}else {
							limitPath.remove(a);
							limitPath.put(a, counter);
						}
					}
						
					return ocorrences;
				}
			}
			counter++;
		}
		
		return ocorrences;
	}

	public static List<Node> search(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath) {
		
		List<Node> ocorrences = new ArrayList<Node>();
		
		//Se os nós são iguais, a sub-árvore é toda a árvore
		if(EqualsController.isEquals(a, b, wildcardsMap)) {
			if(!b.getUsingExistsOperator() || !b.getChangeOperator()) {
				if(b.getFakeNode()) {
					ocorrences.addAll(a.getChildren());
				}else {
					ocorrences.add(a);
				}
				a.setFullVisited(true);
				return ocorrences;
			}
		}
		
		if(!b.getUsingExistsOperator() || !b.getChangeOperator() ) {
			return subtreeFirstOcorrence(a, b, wildcardsMap, path, limitPath);
		}else {
			return searchSubtree(a, b, wildcardsMap, path, limitPath);
		}
	}

	private static List<Node> searchSubtree(Node a, Node b, Map<String, String> wildcardsMap, Map<Node, Integer> path, Map<Node, Integer> limitPath) {
		
		List<Node> ocorrences = new ArrayList<Node>();
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		if(b.getExists()) {
			
		}else {
			List<BlockCodeStruct> blockWanted = new ArrayList<BlockCodeStruct>();
			Utils.getDiferentOperatorBlock(b, new ArrayList<Node>(),blockWanted);
			
			//FIXME Sempre será apenas um bloco de código ?
			BlockCodeStruct block = blockWanted.get(0);
			Node wanted = block.getNode();
			
			List<Node> ocorrenceWanted = subtreeFirstOcorrence(a, wanted, wildcardsMap, path, limitPath);
			
			if(ocorrenceWanted.size() > 0) {
				//Cópia do padrão para buscar apenas o trecho com operador de existência diferente
				Node clone = new Node(b);
				
				//Lista dos nós com operador de existência diferente para serem removidos
				List<Node> listToRemove = new ArrayList<Node>();
				
				//Se é um nó fake adiciona seus filhos, se não, adiciona o nó
				if(block.getNode().getFakeNode()) {
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
					ocorrencesAux = searchChildren(context,clone, wildcardsMap, new LinkedHashMap<>(), new LinkedHashMap<>());
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

}
