package br.scpl.view;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.IStringConverter;

public class CommandConverter implements IStringConverter<Command> {
	  
	public static Map<String, Command> commands = new HashMap<>();
	
	static {
		commands.put("search", new Search());
		commands.put("exit", new Exiter());
	}	
	
	  @Override
	  public Command convert(String value) {
		return commands.get(value); 
	  }
}

