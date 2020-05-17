package br.scpl.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {

	public static void main(String[] args) {
		
		CLIOptions cli = new CLIOptions();
		
		//List<String> test = Arrays.asList("search", "-c", "C:\\opt\\Projects\\sisgee\\sisgee", "-p", "./src/test/resources/ExperimentFiles/Template", "-charset", "UTF-8" );  
		
		//List<String> test = Arrays.asList("-h");
		
		//args = test.toArray(new String[0]);
		
		try {
			JCommander jct = JCommander.newBuilder()
			  .addObject(cli)
			  .build();
			
			  Command.command.forEach((key,value) ->{
				  jct.addCommand(key,value);
			  });	
				
			  jct.parse(args);
			  
			  if(cli.isHelp()) {
				  jct.usage();
				  return;
			  }
			  
			  Command command = (Command) jct.getCommands()
					  .get(jct.getParsedCommand()).getObjects().get(0);
			  
			  command.execute();
			  
		}catch (ParameterException  e) {
			e.printStackTrace();
		}
	}
}
