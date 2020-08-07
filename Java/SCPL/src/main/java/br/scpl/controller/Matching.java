package br.scpl.controller;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;

import br.scpl.model.Node;

public class Matching {
	
	private static final int INDENT_SPACES = 2;
	
	private int indentLevel;
	private StringBuilder sb;
	private String result;
	
	public Matching() {
		this.sb = new StringBuilder();
		this.indentLevel = 0;
		this.result = new String();
	}
	
	public void print(Node node) {
		
		indent().append(node.toString()).append("\n");
		
		if(!(node.getNode() instanceof ExpressionTree || node.getNode() instanceof VariableTree || node.getNode() instanceof ExpressionStatementTree)) {
			indentLevel++;
			node.getChildren().forEach(n-> print(n));
			indentLevel--;
		}
		
	}
	
	private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
    }
	
	
	public StringBuilder getSb(){
		return sb;
	}

}
