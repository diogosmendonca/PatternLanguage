package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIOptions extends JCommander {
	
	@Parameter(names = {"-V", "--verbose"}, description = "Activate the mode that displays extended information")
	 private static boolean verbose = false;

	//Put CLI options here
	
	public static boolean isVerbose() {
		return verbose;
	}
	
}
