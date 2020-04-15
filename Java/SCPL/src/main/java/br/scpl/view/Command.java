package br.scpl.view;

import java.util.List;

import br.scpl.model.Node;

public interface Command<T> {
	
	T execute(String[] args);
}
