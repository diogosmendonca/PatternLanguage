package br.scpl.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.scpl.controller.EqualsController;
import br.scpl.model.Node;

/***
 * Classe com métodos utilitários
 * 
 * @author Denis
 *
 */

public class Utils {
	
	public static boolean verifyNotParent(Node a, Node b, Map<String, String> wildcardsMap) {
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		for(Node notParent: b.getNotParents()) {
			
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
	
	public static List<Node> filterReturnNodes(List<Node> nodes) {
		
		List<Node> retorno = new ArrayList<>();
		
		nodes.forEach( node -> {			
			if(node.getIsToReturn()) {
				retorno.add(node);
			}else {
				retorno.addAll(filterReturnNodes(node.getChildren()));
			}
		});
		
		
		return retorno;
	}
}
