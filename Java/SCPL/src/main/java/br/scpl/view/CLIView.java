package br.scpl.view;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import br.scpl.util.ConfigUtils;
import br.scpl.util.Debug;

/**
 * 
 * @author Denis
 *
 */
public class CLIView {
	
	private static Logger log = Logger.getLogger(CLIView.class);
	
	private CLIView() {}
	
	/**
	 * Do the parser of the CLI parameters and executes the respective command.
	 * 
	 * @param args CLI parameters
	 */
	public static void parserCLI(String[] args) {
		
		CLIOptions cli = new CLIOptions();
		
		Debug debug = new Debug();
		
		JCommander jc = JCommander.newBuilder()
				  .addObject(cli)
				  .addObject(debug)
				  .build();
		
		Command.commands.forEach((key,value) ->{
			
			String[] alias = Command.commandAlias.get(key);
			
			jc.addCommand(key, value, alias);
			
		});
		
		try {
		  if(args==null) {
			  args = new String[0];
		  }
		  
		  jc.parse(args);
		  		  
		  if(cli.isVerbose()) {
			  ((AppenderSkeleton)Logger.getRootLogger().getAppender("stdout"))
			   .setThreshold(Level.DEBUG);
			  
			  ConfigUtils.getProperties().setProperty("verbose", "on");
		  }
		  
		  log.debug("Parameters: " +Arrays.toString(args));
		  
		  if(args.length == 0) {
			  throw new ParameterException("Missing parameters");
		  }
		  
		  if(jc.getParsedCommand() == null) {
			  throw new ParameterException("Expected a command, got none");
		  }
		  
		  Command<?> command = (Command<?>) jc.getCommands()
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
