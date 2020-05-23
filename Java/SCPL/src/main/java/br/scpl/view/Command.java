package br.scpl.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;

public interface Command<T> {
	
	public static final Map<String, Command> command = new HashMap<>(){
        {
            put("help", new Help());
            put("search", new Search());
            put("version", new Version());
        }
    };
    
    public static final Map<String, String[]> commandAlias = new HashMap<>(){
        {
        	put("help", new String [] {"-h", "--help"});
        	put("search", new String [] {"-s", "--search"});
        	put("version", new String [] {"-v", "--version"});
        }
    };
	
	T execute(JCommander jc) throws IOException;
}
