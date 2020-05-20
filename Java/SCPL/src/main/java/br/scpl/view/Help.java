package br.scpl.view;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Shows the help menu")
public class Help extends JCommander implements Command<Void>{
	
	@Override
	public Void execute(JCommander jc) {
		jc.usage();
	    return null;
	}

}
