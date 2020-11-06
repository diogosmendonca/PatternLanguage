package br.scpl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.source.tree.Tree.Kind;

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
			
			if (node.getNode().getKind() == Kind.MODIFIERS){
				Node parent = node.getParent();
				Node parentMatchingNode = parent.getMatchingNode();
				
				if(parentMatchingNode != null && parentMatchingNode.isToReturn()){
					if( Arrays.asList(Kind.METHOD, Kind.INTERFACE, Kind.CLASS)
								.contains(parent.getNode().getKind())) {
							
						parent.setIsToReturn(true);
						parent.setReturnMessage(parentMatchingNode.getReturnMessage());
						parent.setParcialReturn(true);
						parent.getIssues().clear();
						
						parentMatchingNode.getIssues().forEach(i -> {
							parent.getIssues().add(StringUtil.getIssue(i.getAlertComment()));
						});
	
						retorno.add(parent);
					}
				}
			}
			
			if(node.isToReturn()) {
				retorno.add(node);
			}else {
				retorno.addAll(filterReturnNodes(node.getChildren()));
			}
		});
		
		
		return retorno;
	}
	
	public static List<Node> getReturnNode(List<Node> nodes){
		
		List<Node> retorno = Utils.filterReturnNodes(nodes);
		
		if(!retorno.isEmpty()) {
			nodes = retorno;
		}
		
		return nodes;
	}
		
}
