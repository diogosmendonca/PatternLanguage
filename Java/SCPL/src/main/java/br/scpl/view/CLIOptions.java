package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIOptions extends JCommander {
	
	@Parameter(names = {"-V", "--verbose"}, description = "Activate the mode that displays extended information")
	 private boolean verbose = false;

	//Put CLI options here
	
	public boolean isVerbose() {
		return verbose;
	}
	
}
