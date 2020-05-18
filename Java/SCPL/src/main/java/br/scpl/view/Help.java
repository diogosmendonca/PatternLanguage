package br.scpl.view;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import br.scpl.model.Node;

@Parameters(commandDescription = "Shows the help menu")
public class Help extends JCommander implements Command<Void>{
	
	@Override
	public Void execute(JCommander jc) {
		jc.usage();
	    return null;
	}

}
