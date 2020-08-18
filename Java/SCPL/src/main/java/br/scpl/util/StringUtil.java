package br.scpl.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;

import br.scpl.model.sonarqube.Issue;
import br.scpl.model.sonarqube.Location;
import br.scpl.model.sonarqube.TextRange;
import br.scpl.view.FileHandler;

/***
 * 
 * @author Denis
 *
 */

public class StringUtil {
	
	private StringUtil() {}
	
	private final static String NOT = ConfigUtils.getProperties().getProperty("not");
	private final static String EXISTS = ConfigUtils.getProperties().getProperty("exists");
	
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
	    	
	    	if(!alert.equals("")) {
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
	
	public static Map<Integer,Issue> extractIssue(Tree tree) throws IOException {
		Map<Integer,Issue> retorno = new LinkedHashMap<>();
		
		CompilationUnitTree cu = (CompilationUnitTree)tree;
		
		JavaFileObject sourceFile = cu.getSourceFile();
		
		String content = FileHandler.getStringContent(sourceFile);
			
		Pattern pattern = Pattern.compile("(?s)(/\\*((?!\\*/).)*?alert( )*\\((.)*\\)\\*/|//(.)*?alert( )*\\((.)*\\)(\\n|\\r))",
				Pattern.CASE_INSENSITIVE);
		
	    Matcher matcher = pattern.matcher(content);
	    while (matcher.find()) {
	    	String alert = matcher.group();
	    	
	    	Issue issue = getIssue(alert);
	    	
	    	Integer line = getLineComment(content, matcher.end());
	    	
	    	if(issue!=null) {
	    		retorno.put(line+1, issue);
	    	}
	    }
	    
	    return retorno;
	} 
	
	private static Issue getIssue(String alert) {
		Issue issue = null;
		
		alert = alert.replaceAll("\r", "");
		alert = alert.replaceAll("\n", "");
		alert = alert.replaceAll(" +", " ");
		alert = alert.replaceAll("\\*/", "");
		
		Pattern pattern = Pattern.compile("alert(.)*",
				Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(alert);
	    
	    if(matcher.find()) {
	    	alert = matcher.group();
	    	
	    	alert = alert.replaceAll("(?i)alert", "");
	    	
	    	while(alert.startsWith(" ")) {
	    		alert = alert.replaceFirst(" ", "");
	    	}
	    	
	    	alert = alert.replaceFirst("\\(", "");
	    	
	    	alert = alert.replaceAll("\\)$", "");
	    	
	    	List<String> attributes = Arrays.asList(alert.split(","));
	    	
	    	Properties properties = new Properties();
	    	
	    	attributes.forEach( a -> {
	    		
	    		String[] split = a.split("=");
	    		
	    		properties.setProperty(split[0].toLowerCase().trim(), split[1].trim());
	    		
	    	});
	    	
	    	issue = new Issue();
	    	
	    	issue.setEngineId("SCPL");
	    	issue.setRuleId(properties.getProperty("ruleid"));
	    	issue.setPrimaryLocation(new Location());
	    	issue.getPrimaryLocation().setMessage(properties.getProperty("message"));
	    	issue.getPrimaryLocation().setTextRange(new TextRange());
	    	issue.setType(properties.getProperty("type"));
	    	issue.setSeverity(properties.getProperty("severity"));
	    	issue.setEffortMinutes(0);
	    	
	    }	
		
		return issue;
	}
	
	public static Map<Integer,Boolean> extractExistsOperator(Tree tree) throws IOException{
		Map<Integer,Boolean> retorno = new LinkedHashMap<>();
		
		CompilationUnitTree cu = (CompilationUnitTree)tree;
		
		JavaFileObject sourceFile = cu.getSourceFile();
		
		String content = FileHandler.getStringContent(sourceFile);
			
		Pattern pattern = Pattern.compile("(?s)(/\\*("+NOT+"|"+EXISTS+")\\*/|//("+NOT+"|"+EXISTS+")(\\n|\\r))",
				Pattern.CASE_INSENSITIVE);
		
	    Matcher matcher = pattern.matcher(content);
	    while (matcher.find()) {
	    	String existsText = matcher.group();
	    	
	    	Boolean exists = null;
	    	
	    	if(existsText.contains(NOT)) {
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
