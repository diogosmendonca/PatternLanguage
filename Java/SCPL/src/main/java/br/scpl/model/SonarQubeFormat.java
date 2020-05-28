package br.scpl.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SonarQubeFormat {
	
	private List<Issue> issues;
	
	public SonarQubeFormat() {
		this.issues = new ArrayList<>();
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
	
	public static SonarQubeFormat listNodeToSonarQubeFormat(List<Node> nodes) {
		
		SonarQubeFormat sonarQubeFormat = new SonarQubeFormat();
		
		for(Node node: nodes) {
			
			Issue issue = node.getIssue();
			issue.getPrimaryLocation().setFilePath(node.getFilePath().replaceAll("(\\\\)+", "/"));
			
			TextRange textRange = issue.getPrimaryLocation().getTextRange();
			textRange.setStartLine((int) node.getStartLine());
			textRange.setEndLine((int) node.getEndLine());
			textRange.setStartColumn((int) node.getStartColumn());
			textRange.setEndColumn((int) node.getEndColumn());
			
			sonarQubeFormat.getIssues().add(issue);
		}
		
		return sonarQubeFormat;
	}
	
}