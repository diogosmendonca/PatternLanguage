package br.scpl.model.sonarqube;

import java.io.IOException;
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

	public static SonarQubeFormat listNodeToSonarQubeFormat(List<Node> nodes) throws NoAlertFoundException, IOException {
		
		SonarQubeFormat sonarQubeFormat = new SonarQubeFormat();
		
		for(Node node: nodes) {
			if(node.isToReturn()) {
				if(node.getIssues().isEmpty()) {
					throw new NoAlertFoundException(node.getMatchingNode());
				}
				for(Issue issue : node.getIssues()) {
					issue.getPrimaryLocation().setFilePath(node.getFilePath().replaceAll("(\\\\)+", "/"));
					
					issue.getPrimaryLocation().setTextRange(TextRange.nodeToTextRange(node));
					
					sonarQubeFormat.getIssues().add(issue);
				}
				
			}
		}
		
		return sonarQubeFormat;
	}
	
}