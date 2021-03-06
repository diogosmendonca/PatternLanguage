package br.scpl.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.ControllerFacade;
import br.scpl.exception.CompilationErrorException;
import br.scpl.exception.NoAlertFoundException;
import br.scpl.exception.NoValidFilesFoundException;
import br.scpl.model.CompilationUnit;
import br.scpl.model.Node;
import br.scpl.model.PatternFolder;
import br.scpl.model.sonarqube.SonarQubeFormat;
import br.scpl.util.Debug;
import br.scpl.util.Utils;
import br.scpl.view.converter.CharsetConverter;

/**
 * 
 * @author Denis
 *
 */

@Parameters(commandDescription = "Search for patterns in source code")
public class Search extends JCommander implements Command<List<Node>>{
	
	private static Logger log = Logger.getLogger(Search.class);
	
	 @Parameter(names = {"-c", "--code"}, description = "Source code path", required = true)
	 private String code;
	 
	 @Parameter(names = {"-p", "--pattern"}, description = "Pattern path", required = true)
	 private String pattern;
	 
	 @Parameter(names = {"-C", "--charset"}, description = "Specifies the charset to be used", required = false, converter = CharsetConverter.class)
	 private Charset charset;
	 
	 @Parameter(names = {"-f", "--format"}, description = "Specifies the format of the output                             Implemented options: sonarqube")
	 private String format;
	 
	@Override
	public List<Node> execute(JCommander jc) {
		
 		return searchOccurrences(code,pattern, charset,format);
	}
	
	/**
	 * Receives the path of the target source code and the folder with the searched patterns.
	 * Returns the occurrences of the patterns in each source code file.
	 * 
	 * @param pathCode Source code path target search.
	 * @param pathPattern Code path of the fetched pattern.
	 * @param charset Specifies the charset tha will be used.
	 * @param format Specifies the output format.
	 * @return List o Node representing the occurrences of the pattern in the source code. Null in case of Exception.
	 */	
	public static List<Node> searchOccurrences(String pathCode, String pathPattern, Charset charset, String format) {
		
		List<Node> retorno = new ArrayList<>();
		
		try {
			
			log.debug("");
			log.debug("Start of file search.");
			
			PatternFolder patternFolder = FileHandler.getPatternFolder(pathPattern);
	        
			File[] filesCode = FileHandler.getFiles(pathCode);
			
			log.debug("");
			log.debug("End of file search.");
			
			CompilationUnit compilationUnitCode = FileHandler.parserFileToCompilationUnit(filesCode, charset);
			
			Iterator<? extends CompilationUnitTree> compilationUnitsCode = compilationUnitCode.getCompilationUnitTree();
			
			SourcePositions posCode = compilationUnitCode.getPos();
			
			while(compilationUnitsCode.hasNext()) {
				
				CompilationUnitTree treeCode = compilationUnitsCode.next();
				
				retorno.addAll(searchOccurrencesFolder(treeCode, posCode, patternFolder, charset));
			}
			
			String currentFile = "";
			
			retorno = retorno.stream().map(r -> {
				//removing empty default acess modifier, has no beginning and end
				if (r.isDefaultModifierAccess()) {
					if(r.isToReturn()){
						return r.transferAlert();
					}else {
						return null;
					}
				}
				return r;
			}).collect(Collectors.toList());
			
			retorno = retorno.stream()
				     .distinct()
				     .filter(Objects::nonNull)
				     .collect(Collectors.toList());	
			
			log.debug("");
			
			if(format != null) {
				format = format.toLowerCase();
				
				if(format.equals("sonarqube")) {
						
					SonarQubeFormat sonarQubeFormat = SonarQubeFormat.listNodeToSonarQubeFormat(retorno);
					
					Gson g = new GsonBuilder().setPrettyPrinting().create();

					String outputJson = g.toJson(sonarQubeFormat);
					
					log.info(outputJson);
				}else {
					log.error("Error: Unknow format");
				}
				
			}else {
				int count = 0;
				for(Node r : retorno) {
					
					if(r.isToReturn() || Debug.isActivated()) {
						count++;
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
					
				}
				
				log.info("Found patterns: " +count +System.lineSeparator());
			}
			return retorno;
			
		}catch(FileNotFoundException e) {
			log.error("Failed to find the file: " +e.getMessage());
			return null;
		} catch (IOException|NoValidFilesFoundException|NoAlertFoundException|CompilationErrorException e) {
			log.error(e.getLocalizedMessage());
			return null;
		} 
		
	}
	
	/**
	 * Receives the CompilationUnitTree of the target source code and PatternFolder containing the searched patterns.
	 * Returns the occurrences of the patterns in source code tree.
	 * 
	 * @param treeCode Source code CompilationUnitTree.
	 * @param posCode SourcePositions object, that stores the node positions. 
	 * @param pattern PatternFolder containing the searched patterns.
	 * @param charset Specifies the charset tha will be used.
	 * @return List o Node representing the occurrences of the pattern in the source code.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 * @throws CompilationErrorException Signals that an error of compilation occurred in the parse of files.
	 */
	private static List<Node> searchOccurrencesFolder(CompilationUnitTree treeCode, SourcePositions posCode, PatternFolder pattern, Charset charset) throws IOException, CompilationErrorException{
		List<Node> retorno = new ArrayList<>();
		
		for(PatternFolder folder : pattern.getFolders()) {
			retorno.addAll(searchOccurrencesFolder(treeCode, posCode, folder, charset));
		}
		
		if(!pattern.getFiles().isEmpty()) {
			
			List<File> andFiles = pattern.getAndFiles();
			List<File> files = pattern.getFiles().stream().filter(f -> !andFiles.contains(f)).collect(Collectors.toList());
			
			if(!files.isEmpty()) {
				
				File[] filesPatterns = files.toArray(new File[0]);
				
				CompilationUnit compilationUnitPattern = FileHandler.parserFileToCompilationUnit(filesPatterns, charset);
				
				Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitPattern.getCompilationUnitTree();
				
				SourcePositions posPattern = compilationUnitPattern.getPos();
				
				List<Node> listToRemove = new ArrayList<>();
				
				while(compilationUnitsPattern.hasNext()) {
					
					CompilationUnitTree treePattern = compilationUnitsPattern.next();
					
					String fileName = treePattern.getSourceFile().getName().toUpperCase();
					
					if(fileName.endsWith("EXCLUDE.JAVA")){
						listToRemove.addAll(ControllerFacade.searchOccurrences(treeCode, treePattern, posCode, posPattern));
					}else {
						retorno.addAll(ControllerFacade.searchOccurrences(treeCode, treePattern, posCode, posPattern));					
					}
					
				}
				
				List<Tree> treesToRemove =  listToRemove.stream().map(Node::getNode).collect(Collectors.toList());
				
				retorno = retorno.stream().filter(r -> !(treesToRemove.contains(r.getNode()))).collect(Collectors.toList());
			}
			
			Map<String, List<File>> andGroups = pattern.groupByAndFiles(andFiles);
			
			for(Entry<String, List<File>> entry : andGroups.entrySet()) {
				
				File[] filesPatterns = entry.getValue().toArray(new File[0]);
				
				CompilationUnit compilationUnitPattern = FileHandler.parserFileToCompilationUnit(filesPatterns, charset);
				
				Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitPattern.getCompilationUnitTree();
				
				SourcePositions posPattern = compilationUnitPattern.getPos();
				
				List<Node> parcialReturn = new ArrayList<Node>();
				
				boolean returning = true;
				
				while(compilationUnitsPattern.hasNext()) {
					
					CompilationUnitTree treePattern = compilationUnitsPattern.next();
					
					List<Node> auxReturn = ControllerFacade.searchOccurrences(treeCode, treePattern, posCode, posPattern);
					
					if(auxReturn.isEmpty()) {
						returning = false;
						break;
					}
					
					parcialReturn.addAll(auxReturn);					
					
				}
				
				if(returning) {
					parcialReturn = parcialReturn.stream().filter(Utils.distinctByKey(Node::getNode)).collect(Collectors.toList());
					retorno.addAll(parcialReturn);
				}
			  
			}
			
		}
		
		return retorno;
	}
	
	/**
	 * Receives the path of the target source code and the folder with the searched patterns.
	 * Returns the occurrences of the patterns in each source code file.
	 * 
	 * @param pathCode Source code path target search.
	 * @param pathPattern Code path of the fetched pattern.
	 * @return List o Node representing the occurrences of the pattern in the source code.
	 */	
	public static List<Node> searchOccurrences(String pathCode, String pathPattern) {
		return searchOccurrences(pathCode, pathPattern, null, null);
	}
}
