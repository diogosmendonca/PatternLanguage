package br.scpl.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.source.tree.Tree.Kind;

import br.scpl.model.Node;

@Parameters(separators = "=", commandDescription = "Debug mode")
public class Debug {
	
	private static Logger log = Logger.getLogger(Debug.class);
	
	private static final String INDENT_STRING = "";
	private static final int INDENT_SPACES = 2;
	
	private int indentLevel;
	private StringBuilder sb;
	
	@Parameter(names = {"-d", "--debug"}, description = "Activate the debug mode", order = 0)
	private static boolean activated = false;
	
	@Parameter(names = {"-l", "--showLocation"}, description = "Flag that indicates if debug will show the location informations", arity=1, order = 1)
	private static boolean showLocation = true;
	
	@Parameter(names = {"-b", "--beginLine"}, description = "Debug start line in source code", arity=1, order = 2)
	private static Integer startLine;
	
	@Parameter(names = {"-e", "--endLine"}, description = "Final debug line in source code", arity=1, order = 3)
	private static Integer endLine;
	
	public Debug() {
		this.sb = new StringBuilder();
		this.indentLevel = 0;
	}
	
	public void  run(Node a, Node b) {
		if(isActivated()) {
			print(b, false);
			
			this.sb.append("\n\n\n");
			this.indentLevel = 0;
			
			print(a, true);
			
			log.info(sb.toString());
		}
	}
	
	private void print(Node node, boolean isSourceCode) {
		
		String toString = node.toString();
		String close = null;
		String position = "";
		
		
		if(node.getBlockChild() != null) {
			if(toString.contains("{")) {
				toString = toString.substring(0, toString.indexOf("{"));					
			}
			if(toString.startsWith("\r\n")) {
				toString = toString.replaceFirst("\r\n", "");
			}
			position = getDefaultPosition(node);
			
		}else {
			
			switch(node.getNode().getKind()) {
			
			case COMPILATION_UNIT:
				toString = node.getFilePath();
				break;
				
			case INTERFACE:	
				
			case CLASS:
				int index = toString.indexOf("{");
				toString = toString.substring(0, index+1);
				toString = toString.replaceFirst("\r\n", "");
				
				position = getDefaultPosition(node);
				close = "} L: "+node.getEndLine() +" C: " +node.getEndColumn();
				break;
				
				
			case BLOCK:
				toString = "{";
				position = "L: " +node.getStartLine() +" C: " +node.getStartColumn() ;
				close = "} L: "+node.getEndLine() +" C: " +node.getEndColumn();
				break;
				
			default:
				toString = node.toString();
				if(!toString.equals("")) {
					position = getDefaultPosition(node) ;			
				}
				break;
			}
		}
		
		toString = toString.replaceAll("\r\n", " ");
		
		String matching = node.getNode().getKind() == Kind.COMPILATION_UNIT || node.getMatchingNode() == null ? "" : "##" ;
		
		if(close!=null) {
			close = matching + close;
		}
		
		if(!showLocation) {
			position = "";
		}
		
		String line = matching +toString +" (" +getSimpleName(node) +") " +position +"\n"; 
		
		boolean print = true;
		
		if(isSourceCode && node.getNode().getKind() != Kind.COMPILATION_UNIT) {
			if(!toString.equals("")) {
				print = isToPrint(node.getStartLine());
			}else {
				print = isToPrint(node.getParent().getStartLine());
			}
		}
		
		if(print) {
			indent().append(line);			
			indentLevel++;
		}
		
		node.getChildren().forEach(n-> print(n,isSourceCode));
		if(print) {
			indentLevel--;
		}
		if(close!=null && (!isSourceCode || isToPrint(node.getEndLine()))) {
			indent().append(close).append("\n");
		}
		
	}
	
	private String getDefaultPosition(Node node) {
		return "L: " +node.getStartLine() +" C: " +node.getStartColumn() +" -> L: " +node.getEndLine() +" C: " +node.getEndColumn();
	}
	
	private boolean isToPrint(long line) {
		boolean start = true;
		boolean end = true;
		
		if(startLine != null) { 
			start = false;
			if(line >= startLine) {
				start = true;
			}
		}

		if(endLine != null) { 
			end = false;
			if(line <= endLine) {
				end = true;
			}
		}
		
		return start&&end;
	}
	
	private StringBuilder indent() {
		int indent = INDENT_SPACES * indentLevel;
	    return sb.append(StringUtils.leftPad(StringUtils.repeat(INDENT_STRING, indent), indent));
    }
	
	private String getSimpleName(Node node) {
		return node.getNode().getClass().getInterfaces()[0].getSimpleName();
	}
	
	/**
	 * 
	 * @return Boolean that indicates if the debug mode is activated.
	 */
	public static boolean isActivated() {
		if(!activated && ConfigUtils.getProperties().getProperty("debug").equals("on")) {
			activated = true;
		}
		return activated;
	}

}
