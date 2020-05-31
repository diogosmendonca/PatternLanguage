package br.scpl.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.ControllerFacade;
import br.scpl.exception.NoFilesFoundException;
import br.scpl.model.CompilationUnit;
import br.scpl.model.Node;
import br.scpl.model.PatternFolder;
import br.scpl.model.sonarqube.SonarQubeFormat;

public class CLIView {
	
	private static final String separator = ResourceBundle.getBundle("config").getString("separator");

	private static Logger log = Logger.getLogger(CLIView.class);
	
	
	public static void parserCLI(String[] args) {
		
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
