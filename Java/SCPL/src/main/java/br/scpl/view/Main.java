package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import br.scpl.exception.UnknownCommandException;

public class Main {

	public static void main(String[] args) {
		
		Action action = new Action();
		
		String[] argv = { "-a", "search", "-c", "./src/test/resources/AceitacaoFiles/ParseIntCheck.java", "-p", "./src/test/resources/AceitacaoFiles/ParseInt_Pattern.java" };
		
		try {
			JCommander.newBuilder()
			  .addObject(action)
			  .build()
			  .parse(argv);
			
			action.execute();
			
		}catch (ParameterException  e) {
			e.printStackTrace();
			System.exit(1);
		}catch (UnknownCommandException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
