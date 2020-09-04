package br.scpl.view;

import java.io.IOException;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Denis
 *
 * @param <T> T is the type of the object returned by the execute method
 */
public interface Command<T> {
	
	static final Map<String, JCommander> commands = 
		ImmutableMap.of(
			"help", new Help(), 
			"search", new Search(), 
			"version", new Version());
    
 	static final Map<String, String[]> commandAlias = 
		ImmutableMap.of(
			"help", new String [] {"-h", "--help"}, 
			"search", new String [] {"-s", "--search"}, 
			"version", new String [] {"-v", "--version"});
	
	T execute(JCommander jc) throws IOException;
}
