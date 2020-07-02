package br.scpl.util;

import java.util.ArrayList;
import java.util.List;

import br.scpl.model.Node;

/***
 * 
 * @author Denis
 *
 */

public class Utils {
	
	private Utils() {}
	
	private static List<Node> filterReturnNodes(List<Node> nodes) {
		
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
	
	public static List<Node> getReturnNode(List<Node> nodes){
		
		List<Node> retorno = Utils.filterReturnNodes(nodes);
		
		if(retorno.size()>0) {
			nodes = retorno;
		}
		
		return nodes;
	}
		
}
