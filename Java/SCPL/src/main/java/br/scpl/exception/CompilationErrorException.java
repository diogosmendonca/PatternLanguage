package br.scpl.exception;

import br.scpl.model.Node;

public class CompilationErrorException extends Exception {
	
private static final long serialVersionUID = 1L;
	
	public CompilationErrorException(String error) {
		super("Compilation error: \r\n"+error);
	}

}
