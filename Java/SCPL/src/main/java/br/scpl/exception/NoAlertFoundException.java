package br.scpl.exception;

import br.scpl.model.Node;

public class NoAlertFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public NoAlertFoundException(Node node) {
		super("An alert comment was not found for the node '"+node +"'\n"
				+ "In the file " +node.getFilePath() +" Line " +node.getStartLine()+"\n"
				+ "For the sonarqube format, you must specify an alert comment like: "
				+ "'//Alert(ruleid=<ruleid>, type=<type>, severity=<severity>, message=<message>)'");
	}

}
