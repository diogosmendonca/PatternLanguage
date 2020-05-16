package br.scpl.exception;

public class UnknownCommandException extends Exception{
	
	private String msg;
	
    public UnknownCommandException(String msg){
      super("The command "+msg +" was not found.");
      this.msg = "The command "+msg +" was not found.";
    }
    public String getMessage(){
      return msg;
    }

}
