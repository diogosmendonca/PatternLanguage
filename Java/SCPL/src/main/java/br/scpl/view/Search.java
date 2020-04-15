package br.scpl.view;

import java.util.List;

import br.scpl.model.Node;

public class Search implements Command<List<Node>> {
			
	@Override
	public List<Node> execute(String[] args) {
		
		if(args.length!=2){
			System.out.println("Parâmetros incorretos para o comando.");
			return null;
		}
		
		System.out.println("Realizando busca dos padrões " +args[1] +"\nno código-fonte " +args[0]);
		
		return View.searchOcorrences(args[0], args[1]);
	}
}
