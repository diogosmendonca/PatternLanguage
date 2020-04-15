package br.scpl.exception;

public class UnknownCommandException extends Exception{
	
	private String msg;
	
    public UnknownCommandException(String msg){
      super("O comando "+msg +" não foi encontrado.");
      this.msg = "O comando "+msg +" não foi encontrado.";
    }
    public String getMessage(){
      return msg;
    }

}
