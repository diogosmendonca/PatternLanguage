package br.scpl.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.exception.UnknownCommandException;

public class Action {
	
	public static Map<String, Command> commands = new HashMap<>();
	
	static {
		commands.put("search", new Search());
		commands.put("busca", new Search());
	}
	
	@Parameter(names = {"-a","-action","-acao"}, description = "Ação que será executada", required = true)
	private String command;
	
	@Parameter(required = true)
	private List<String> parameters = new ArrayList<String>();

	public Object execute() throws UnknownCommandException {
		
		Command c = commands.get(command);
		
		if(c==null) {
			throw new UnknownCommandException(command);
		}
		
		String[] argv = parameters.toArray(new String[0]);
			
		JCommander.newBuilder()
		  .addObject(c)
		  .build()
		  .parse(argv);
			
		return c.execute();
	}
}
