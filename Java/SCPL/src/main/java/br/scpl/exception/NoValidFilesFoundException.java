package br.scpl.exception;

public class NoValidFilesFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public NoValidFilesFoundException(String rootPath) {
		super("No valid files found for the path: "+rootPath);
	}

}
