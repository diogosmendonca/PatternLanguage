package br.scpl.model.sonarqube;

import java.util.ArrayList;
import java.util.List;

import br.scpl.exception.NoAlertFoundException;
import br.scpl.model.Node;

public class SonarQubeFormat {
	
	private List<Issue> issues;
	
	public SonarQubeFormat() {
		this.issues = new ArrayList<>();
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public static SonarQubeFormat listNodeToSonarQubeFormat(List<Node> nodes) throws NoAlertFoundException {
		
		SonarQubeFormat sonarQubeFormat = new SonarQubeFormat();
		
		for(Node node: nodes) {
			
			Issue issue = node.getIssue();
			
			if(issue==null) {
				throw new NoAlertFoundException(node.getMatchingNode());
			}
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