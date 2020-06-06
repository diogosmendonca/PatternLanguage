package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * 
 * @author Denis
 *
 */
public class CLIOptions extends JCommander {
	
	@Parameter(names = {"-V", "--verbose"}, description = "Activate the mode that displays extended information")
	 private boolean verbose = false;

	//Put CLI options here
	
	/**
	 * 
	 * @return Boolean that indicates if the verbose mode is activated.
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
}
