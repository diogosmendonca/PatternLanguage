package br.scpl.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;

import br.scpl.model.Node;

public interface Command<T> {
	
	public static final Map<String, Command> command = new HashMap<>(){
        {
            put("search", new Search());
            put("help", new Help());
        }
    };
    
    public static final Map<String, String[]> commandAlias = new HashMap<>(){
        {
            put("help", new String [] {"-h","--help"});
        }
    };
	
	T execute(JCommander jc);
}
