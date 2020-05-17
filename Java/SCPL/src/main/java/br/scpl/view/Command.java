package br.scpl.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.scpl.model.Node;

public interface Command<T> {
	
	public static final Map<String, Command> command = new HashMap<>(){
        {
            put("search", new Search());
        }
    };
	
	T execute();
}
