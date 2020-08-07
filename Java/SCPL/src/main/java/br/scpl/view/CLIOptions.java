package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.util.ConfigUtils;

/**
 * 
 * @author Denis
 *
 */
public class CLIOptions extends JCommander {
	
	@Parameter(names = {"-V", "--verbose"}, description = "Activate the mode that displays extended information")
	private boolean verbose = false;

	@Parameter(names = {"--debug"}, description = "Activate the debug mode")
	private boolean debug = false;
	
	//Put CLI options here
	
	/**
	 * 
	 * @return Boolean that indicates if the verbose mode is activated.
	 */
	public boolean isVerbose() {
		if(ConfigUtils.getProperties().getProperty("verbose").equals("on")) {
			verbose = true;
		}
		
		return verbose;
	}

	/**
	 * 
	 * @return Boolean that indicates if the debug mode is activated.
	 */
	public boolean isDebug() {
		if(ConfigUtils.getProperties().getProperty("debug").equals("on")) {
			debug = true;
		}
		
		return debug ;
	}
	
}
