package br.scpl.view;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.model.Node;

public class Search extends JCommander implements Command<List<Node>>{
	
	 @Parameter(names = {"-c","-code","-codigo"}, description = "Caminho relativo ao código", required = true)
	 private String code;
	 
	 @Parameter(names = {"-p","-pattern","-padrao"}, description = "Caminho relativo ao padrão", required = true)
	 private String pattern;
	 
	@Override
	public List<Node> execute() {
		
		return View.searchOcorrences(code,pattern);
	}
}
