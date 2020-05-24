package br.scpl.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
	
	private static final String separator = ResourceBundle.getBundle("config").getString("separator");
	
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		
		CLIOptions cli = new CLIOptions();
		
		//List<String> test = Arrays.asList("--verbose", "search", "-c", "C:\\opt\\Projects\\sisgee\\sisgee", "-p", "./src/test/resources/AceitacaoFiles/TC66_Pattern.java", "--charset", "UTF-8");
		
		//List<String> test = Arrays.asList("--verbose");
		
		//args = test.toArray(new String[0]);
		
		JCommander jc = JCommander.newBuilder()
				  .addObject(cli)
				  .build();
		
		Command.command.forEach((key,value) ->{
			
			String[] alias = Command.commandAlias.get(key);
			
			if(alias != null) {
				jc.addCommand(key, value, alias);
				
			}else {
				jc.addCommand(key,value);
			}
			
		});
		
		try {
		  
		  jc.parse(args);
		  
		  if(cli.isVerbose()) {
			  ((AppenderSkeleton)Logger.getRootLogger().getAppender("stdout"))
			   .setThreshold(Level.DEBUG);
		  }
		  
		  log.debug("Parameters: " +Arrays.toString(args));
		  
		  if(args.length == 0) {
			  throw new ParameterException("Missing parameters");
		  }
		  
		  if(jc.getParsedCommand() == null) {
			  throw new ParameterException("Expected a command, got none");
		  }
		  
		  Command command = (Command) jc.getCommands()
				  .get(jc.getParsedCommand()).getObjects().get(0);
		  
		  command.execute(jc);
			  
		}catch (ParameterException  e) {
			log.error("Error: " + e.getLocalizedMessage());
		    jc.usage();
		} catch (IOException e) {
			log.error("Error: " + e.getLocalizedMessage());
		}
	}
}
