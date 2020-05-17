package br.scpl.view;

import java.nio.charset.Charset;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.view.converter.CharsetConverter;

public class CLIOptions extends JCommander {
	
	@Parameter(names = {"-h", "--help"}, description = "Shows help menu", help = true)
	private boolean help = false;
	
	public boolean isHelp() {
	    return help;
	}

}
