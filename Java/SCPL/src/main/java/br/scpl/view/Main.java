package br.scpl.view;

import com.beust.jcommander.JCommander;

public class Main {

	public static void main(String[] args) {
		
		Action action = new Action();
		
		String[] argv = { "-f", "search", "-c", "./src/test/resources/AceitacaoFiles/ParseIntCheck.java", "-p", "./src/test/resources/AceitacaoFiles/ParseInt_Pattern.java" };
		
		JCommander.newBuilder()
		  .addObject(action)
		  .build()
		  .parse(argv);
		
		action.execute();
	}
}
