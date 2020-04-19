package br.scpl.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.exception.UnknownCommandException;

public class Action implements Command<Object> {
	
	@Parameter(names = {"-a","-action","-acao"}, description = "Ação que será executada", required = true, converter = CommandConverter.class)
	private Command command;
	
	@Parameter(required = true)
	private List<String> parameters = new ArrayList<String>();

	@Override
	public Object execute() {
		String[] argv = parameters.toArray(new String[0]);
			
		JCommander.newBuilder()
		  .addObject(command)
		  .build()
		  .parse(argv);
			
		return command.execute();
	}
}
