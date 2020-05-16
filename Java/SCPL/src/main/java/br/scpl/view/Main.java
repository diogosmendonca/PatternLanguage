package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import br.scpl.exception.UnknownCommandException;

public class Main {

	public static void main(String[] args) {
		
		Action action = new Action();
		
		//String[] args = { "-a", "search", "-c", "C:\\opt\\Projects\\sisgee\\sisgee", "-p", "./src/test/resources/ExperimentFiles/Template", "-charset", "CP1252" };
		
		try {
			JCommander.newBuilder()
			  .addObject(action)
			  .build()
			  .parse(args);
			
			action.execute();
			
		}catch (ParameterException  e) {
			e.printStackTrace();
		}catch (UnknownCommandException e) {
			e.printStackTrace();
		}
	}
}
