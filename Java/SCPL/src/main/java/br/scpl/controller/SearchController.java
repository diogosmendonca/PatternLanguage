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
						//counter++;
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
			return subtreeFirstOcorrence(a, b, wildcardsMap);
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
			Utils.getDiferentOperatorBlock(b, new ArrayList<Node>(),blockWanted);
			
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

}
