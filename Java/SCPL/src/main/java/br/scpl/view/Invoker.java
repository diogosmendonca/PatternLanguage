package br.scpl.view;

import java.util.HashMap;
import java.util.Map;

import br.scpl.exception.UnknownCommandException;

public class Invoker {
	
	public static Map<String, Command> commands = new HashMap<>();
	
	static {
		commands.put("search", new Search());
		commands.put("exit", new Exiter());
	}
	
	public static void invoke(String command, String[] args) throws UnknownCommandException {
		
		if(!commands.containsKey(command)) {
			throw new UnknownCommandException(command);
		}
		commands.get(command).execute(args);
	}

}
