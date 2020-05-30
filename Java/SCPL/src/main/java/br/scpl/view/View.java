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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.FileHandler;
import br.scpl.controller.NodeVisitor;
import br.scpl.controller.SearchController;
import br.scpl.exception.NoFilesFoundException;
import br.scpl.model.CompilationUnit;
import br.scpl.model.Node;
import br.scpl.model.PatternFolder;
import br.scpl.model.sonarqube.SonarQubeFormat;

public class View {
	
	private static final String separator = ResourceBundle.getBundle("config").getString("separator");

	private static Logger log = Logger.getLogger(View.class);
	
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
	public static List<Node> searchOcorrences(String pathCode, String pathPattern, Charset charset, String format) {
		
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
				
				Tree treeCode = compilationUnitsCode.next();
				
				retorno.addAll(searchOcorrencesFolder(treeCode,patternFolder));
			}
			
			//TODO editar saida do retorno (Arquivo, linhas e colunas)
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
	
	public static List<Node> searchOcorrences(String pathCode, String pathPattern) {
		return searchOcorrences(pathCode, pathPattern, null, null);
	}
	
	public static List<Node> searchOcorrences(String pathCode, String pathPattern, Charset charset) {
		return searchOcorrences(pathCode, pathPattern, charset, null);
	}
	
	public static List<Node> searchOcorrences(String pathCode, String pathPattern, String format) {
		return searchOcorrences(pathCode, pathPattern, null, format);
	}
	
	public static List<Node> searchOcorrencesFolder(Tree treeCode, PatternFolder pattern, Charset charset) throws IOException{
		List<Node> retorno = new ArrayList<>();
		
		for(PatternFolder folder : pattern.getFolders()) {
			retorno.addAll(searchOcorrencesFolder(treeCode, folder, charset));
		}
		
		if(pattern.getFiles().size() > 0) {
			
			File[] filesPatterns = pattern.getFiles().toArray(new File[0]);
			
			CompilationUnit compilationUnitStructPattern = FileHandler.parserFileToCompilationUnit(filesPatterns, charset);
			
			Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitStructPattern.getCompilationUnitTree();
			
			SourcePositions posPattern = compilationUnitStructPattern.getPos();
			
			List<Node> listToRemove = new ArrayList<>();
			
			while(compilationUnitsPattern.hasNext()) {
				
				Tree treePattern = compilationUnitsPattern.next();
				
				Node rootCode = NodeVisitor.build(treeCode, null, false);
				
				Node rootPattern = NodeVisitor.build(treePattern, posPattern, true);
				
				String fileName = ((CompilationUnitTree) treePattern).getSourceFile().getName().toUpperCase();
				
				if(fileName.endsWith("EXCLUDE.JAVA")){
					listToRemove.addAll(SearchController.subtree(rootCode, rootPattern));
				}else {
					retorno.addAll(SearchController.subtree(rootCode, rootPattern));//;					
				}
				
			}
			
			List<Tree> treesToRemove =  listToRemove.stream().map(n -> n.getNode()).collect(Collectors.toList());
			
			retorno = retorno.stream().filter(r -> !(treesToRemove.contains(r.getNode()))).collect(Collectors.toList());
			
		}
		
		return retorno;
	}
	
	public static List<Node> searchOcorrencesFolder(Tree treeCode, PatternFolder pattern) throws IOException{
		return searchOcorrencesFolder(treeCode, pattern, null);
	}
}
