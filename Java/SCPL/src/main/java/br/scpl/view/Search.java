package br.scpl.view;

import java.nio.charset.Charset;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import br.scpl.model.Node;
import br.scpl.view.converter.CharsetConverter;

@Parameters(commandDescription = "Search for patterns in source code")
public class Search extends JCommander implements Command<List<Node>>{
	
	 @Parameter(names = {"-c", "--code"}, description = "Source code path", required = true)
	 private String code;
	 
	 @Parameter(names = {"-p", "--pattern"}, description = "Pattern path", required = true)
	 private String pattern;
	 
	 @Parameter(names = {"-C", "--charset"}, description = "Specifies the charset to be used", required = false, converter = CharsetConverter.class)
	 private Charset charset;
	 
	 @Parameter(names = {"-f", "--format"}, description = "Specifies the format of the output")
	 private String format;
	 
	@Override
	public List<Node> execute(JCommander jc) {
		
 		return CLIView.searchOcorrences(code,pattern, charset,format);
	}
}
