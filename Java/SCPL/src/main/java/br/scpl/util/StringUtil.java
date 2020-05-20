package br.scpl.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;

import br.scpl.controller.FileHandler;

public class StringUtil {
	
	public static Map<Integer,String> extractAlertMessages(Tree tree) throws IOException{
		Map<Integer,String> retorno = new LinkedHashMap<>();
		
		CompilationUnitTree cu = (CompilationUnitTree)tree;
		
		JavaFileObject sourceFile = cu.getSourceFile();
		
		String content = FileHandler.getStringContent(sourceFile);
			
		Pattern pattern = Pattern.compile("(?s)(/\\*((?!\\*/).)*?alert( )*:(.)*?\\*/|//(.)*?alert( )*:(.)*?(\\n|\\r))",
				Pattern.CASE_INSENSITIVE);
		
	    Matcher matcher = pattern.matcher(content);
	    while (matcher.find()) {
	    	String alert = matcher.group();
	    	alert = getAlertMessage(alert);
	    	
	    	Integer line = getLineComment(content, matcher.end());
	    	
	    	if(alert!="") {
	    		retorno.put(line+1, alert);
	    	}
	    }
	    
	    return retorno;
	}
	
	private static int getLineComment(String data, int end) {
	    int line = 1;
	    Pattern pattern = Pattern.compile("\n");
	    Matcher matcher = pattern.matcher(data);
	    matcher.region(0,end);
	    while(matcher.find()) {
	        line++;
	    }
	    return line;
	}
	
	private static String getAlertMessage(String comment) {
		String msg = null;
		comment = comment.replaceAll("\r", "");
		comment = comment.replaceAll("\n", "");
		comment = comment.replaceAll(" +", " ");

		Pattern pattern = Pattern.compile("alert( )*:((?!(\\*)+/).)*",
				Pattern.CASE_INSENSITIVE);
		
	    Matcher matcher = pattern.matcher(comment);
	    
	    if(matcher.find()) {
	    	msg = matcher.group();
	    	
	    	msg = msg.replaceAll("(?i)alert( )*:", "").trim();
	    }
	    return msg;
	}
	
	public static Map<Integer,Boolean> extractExistsOperator(Tree tree) throws IOException{
		Map<Integer,Boolean> retorno = new LinkedHashMap<>();
		
		CompilationUnitTree cu = (CompilationUnitTree)tree;
		
		JavaFileObject sourceFile = cu.getSourceFile();
		
		String content = FileHandler.getStringContent(sourceFile);
			
		Pattern pattern = Pattern.compile("(?s)(/\\*(not-exists|exists)\\*/|//(not-exists|exists)(\\n|\\r))",
				Pattern.CASE_INSENSITIVE);
		
	    Matcher matcher = pattern.matcher(content);
	    while (matcher.find()) {
	    	String existsText = matcher.group();
	    	
	    	Boolean exists = null;
	    	
	    	if(existsText.contains("not")) {
	    		exists = false;
	    	}else {
	    		exists = true;
	    	}
	    	
	    	Integer line = getLineComment(content, matcher.end());
	    	
	    	retorno.put(line+1, exists);
	    	
	    }
	    
	    return retorno;
	}

}
