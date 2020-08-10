package br.scpl.controller;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.Tree.Kind;

import br.scpl.model.Node;

public class Debug {
	
	private static final int INDENT_SPACES = 2;
	
	private int indentLevel;
	private StringBuilder sb;
	private String result;
	
	public Debug() {
		this.sb = new StringBuilder();
		this.indentLevel = 0;
		this.result = new String();
	}
	
	public void print(Node node) {
		
		String toString = node.toString();
		String close = null;
		String position = "";
		
		switch(node.getNode().getKind()) {
		
			case COMPILATION_UNIT:
				toString = node.getFilePath();
				break;
				
			case CLASS:
				toString = toString.substring(0, toString.indexOf("{")+1);
				if(toString.startsWith("\r\n")) {
					toString = toString.replaceFirst("\r\n", "");
				}
				position = "L: " +node.getStartLine() +" C: " +node.getStartColumn() +" -> L: " +node.getEndLine() +" C: " +node.getEndColumn() ;
				close = "} L: "+node.getEndLine() +" C: " +node.getEndColumn();
				break;
				
			case METHOD:
				if(toString.contains("{")) {
					toString = toString.substring(0, toString.indexOf("{"));					
				}
				if(toString.startsWith("\r\n")) {
					toString = toString.replaceFirst("\r\n", "");
				}
				break;
				
			case BLOCK:
				toString = "{";
				position = "L: " +node.getStartLine() +" C: " +node.getStartColumn() ;
				close = "} L: "+node.getEndLine() +" C: " +node.getEndColumn();
				break;
				
			default:
				toString = node.toString();
				if(!toString.equals("")) {
					position = "L: " +node.getStartLine() +" C: " +node.getStartColumn() +" -> L: " +node.getEndLine() +" C: " +node.getEndColumn() ;			
				}
				break;
		}
		
		toString = toString.replaceAll("\r\n", " ");
		
		String matching = node.getNode().getKind() == Kind.COMPILATION_UNIT || node.getMatchingNode() == null ? "" : "##" ;
		
		
		String line = matching +toString +" (" +getSimpleName(node) +") " +position +"\n"; 
		
		indent().append(line);
		
		if(node.getChildren().size()==0) {
			return;
		}
		
		indentLevel++;
		node.getChildren().forEach(n-> print(n));
		indentLevel--;
		if(close!=null) {
			indent().append(close).append("\n");
		}
		
	}
	
	private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
    }
	
	private String getSimpleName(Node node) {
		return node.getNode().getClass().getInterfaces()[0].getSimpleName();
	}
	
	public StringBuilder getSb(){
		return sb;
	}

}
