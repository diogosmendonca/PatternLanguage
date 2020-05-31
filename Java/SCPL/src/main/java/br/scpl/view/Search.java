package br.scpl.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.ControllerFacade;
import br.scpl.exception.NoFilesFoundException;
import br.scpl.model.CompilationUnit;
import br.scpl.model.Node;
import br.scpl.model.PatternFolder;
import br.scpl.model.sonarqube.SonarQubeFormat;
import br.scpl.view.converter.CharsetConverter;

@Parameters(commandDescription = "Search for patterns in source code")
public class Search extends JCommander implements Command<List<Node>>{
	
	private static final String separator = ResourceBundle.getBundle("config").getString("separator");

	private static Logger log = Logger.getLogger(Search.class);
	
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
		
 		return searchOccurrences(code,pattern, charset,format);
	}
	
	/**
	 * Recebe o path da pasta do código-fonte alvo e a pasta com os padrões buscados.
	 * Retorna as ocorrências dos padrões em cada arquivo de código-fonte
	 * 
	 * @param pathCode Caminho da pasta com os arquivos de código-fonte alvos da busca
	 * @param pathPattern Caminho da pasta com os arquivos da regras dos padrões buscados
	 * @param charset Charset que será utilizado
	 * @param format Formato de saída
	 * @return
	 */	
	public static List<Node> searchOccurrences(String pathCode, String pathPattern, Charset charset, String format) {
		
		List<Node> retorno = new ArrayList<>();
		
		try {
			
			log.debug(separator);
			log.debug("Start of file search.");
			
			PatternFolder patternFolder = FileHandler.getPatternFolder(pathPattern);
	        
			File[] filesCode = FileHandler.getFiles(pathCode);
			
			log.debug(separator);
			log.debug("End of file search.");
			
			CompilationUnit compilationUnitStructCode = FileHandler.parserFileToCompilationUnit(filesCode, charset);
			
			Iterator<? extends CompilationUnitTree> compilationUnitsCode = compilationUnitStructCode.getCompilationUnitTree();
			
			SourcePositions posCode = compilationUnitStructCode.getPos();
			
			while(compilationUnitsCode.hasNext()) {
				
				CompilationUnitTree treeCode = compilationUnitsCode.next();
				
				retorno.addAll(searchOccurrencesFolder(treeCode,patternFolder, charset));
			}
			
			String currentFile = "";
			
			//FIXME Problema de retornar modifiers
			retorno = retorno.stream().filter(x -> x.getNode().getKind() != Kind.MODIFIERS && x.getNode().getKind() != Kind.PRIMITIVE_TYPE).collect(Collectors.toList());
			
			log.debug(separator);
			
			for(Node r : retorno) {
				r.setStartPosition(posCode.getStartPosition(r.getCompilatioUnitTree(), r.getNode()));
				r.setEndPosition(posCode.getEndPosition(r.getCompilatioUnitTree(), r.getNode()));
			}
			
			if(format != null) {
				format = format.toLowerCase();
				
				switch(format) {
				
					case "sonarqube":
						
						SonarQubeFormat sonarQubeFormat = SonarQubeFormat.listNodeToSonarQubeFormat(retorno);
						
						Gson g = new GsonBuilder().setPrettyPrinting().create();
	
						String outputJson = g.toJson(sonarQubeFormat);
						
						log.info(outputJson);
						
						break;
						
					default:
						log.error("Error: Unknow format");
						break;
				}
				
			}else {
				for(Node r : retorno) {
					
					if(!r.getFilePath().equals(currentFile)) {
						currentFile = r.getFilePath();
						log.info("File: " +currentFile);
					}
					
					if(r.getReturnMessage() != null) {
						log.info("Alert Message: " +r.getReturnMessage());
					}
					
					log.info("Start: L: " +r.getStartLine() +" C: " +r.getStartColumn() );
					log.info("End: L: " +r.getEndLine() +" C: " +r.getEndColumn() +System.lineSeparator());
					
				}
				
				log.info("Found patterns: " +retorno.size() +System.lineSeparator());
			}
			return retorno;
			
		}catch(FileNotFoundException e) {
			log.error("Failed to find the file: " +e.getMessage());
		}
		catch(IOException e) {
			log.error("Error: " +e.getLocalizedMessage());
		} catch (NoFilesFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		
		return retorno;
	}
	
	private static List<Node> searchOccurrencesFolder(CompilationUnitTree treeCode, PatternFolder pattern, Charset charset) throws IOException{
		List<Node> retorno = new ArrayList<>();
		
		for(PatternFolder folder : pattern.getFolders()) {
			retorno.addAll(searchOccurrencesFolder(treeCode, folder, charset));
		}
		
		if(pattern.getFiles().size() > 0) {
			
			File[] filesPatterns = pattern.getFiles().toArray(new File[0]);
			
			CompilationUnit compilationUnitStructPattern = FileHandler.parserFileToCompilationUnit(filesPatterns, charset);
			
			Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitStructPattern.getCompilationUnitTree();
			
			SourcePositions posPattern = compilationUnitStructPattern.getPos();
			
			List<Node> listToRemove = new ArrayList<>();
			
			while(compilationUnitsPattern.hasNext()) {
				
				CompilationUnitTree treePattern = compilationUnitsPattern.next();
				
				String fileName = treePattern.getSourceFile().getName().toUpperCase();
				
				if(fileName.endsWith("EXCLUDE.JAVA")){
					listToRemove.addAll(ControllerFacade.searchOccurrences(treeCode, treePattern, posPattern));
				}else {
					retorno.addAll(ControllerFacade.searchOccurrences(treeCode, treePattern, posPattern));					
				}
				
			}
			
			List<Tree> treesToRemove =  listToRemove.stream().map(n -> n.getNode()).collect(Collectors.toList());
			
			retorno = retorno.stream().filter(r -> !(treesToRemove.contains(r.getNode()))).collect(Collectors.toList());
			
		}
		
		return retorno;
	}

	public static List<Node> searchOccurrences(String pathCode, String pathPattern) {
		return searchOccurrences(pathCode, pathPattern, null, null);
	}
}
