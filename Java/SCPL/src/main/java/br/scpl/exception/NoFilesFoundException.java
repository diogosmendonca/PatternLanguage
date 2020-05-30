package br.scpl.exception;

public class NoFilesFoundException extends Exception{
	
	public NoFilesFoundException(String rootPath) {
		super("No valid files found for the path: "+rootPath);
	}

}
