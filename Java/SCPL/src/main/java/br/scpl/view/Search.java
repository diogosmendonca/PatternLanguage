package br.scpl.view;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import br.scpl.model.Node;
import br.scpl.view.converter.CharsetConverter;

public class Search extends JCommander implements Command<List<Node>>{
	
	 @Parameter(names = {"-c","-code","-codigo"}, description = "Caminho relativo ao código", required = true)
	 private String code;
	 
	 @Parameter(names = {"-p","-pattern","-padrao"}, description = "Caminho relativo ao padrão", required = true)
	 private String pattern;
	 
	 @Parameter(names = {"-charset"}, description = "Caminho relativo ao padrão", required = false, converter = CharsetConverter.class)
	 private Charset charset;
	 
	@Override
	public List<Node> execute() {
		
 		return View.searchOcorrences(code,pattern, charset);
	}
}
