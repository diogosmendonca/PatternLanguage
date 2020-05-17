package br.scpl.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.FileHandler;
import br.scpl.controller.NodeVisitor;
import br.scpl.controller.SearchController;
import br.scpl.model.CompilationUnitStruct;
import br.scpl.model.Node;
import br.scpl.model.PatternFolder;
import br.scpl.util.StringUtil;
import br.scpl.util.Utils;

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
	 * @return
	 */	
	public static List<Node> searchOcorrences(String pathCode, String pathPattern, Charset charset) {
		try {
			
			List<Node> retorno = new ArrayList<>();
			
			log.info(separator);
			log.info("Start of file search.");
			
			PatternFolder patternFolder = FileHandler.getPatternFolder(pathPattern);
	        
			File[] filesCode = FileHandler.getFiles(pathCode);
			
			log.info(separator);
			log.info("End of file search.");
			log.info(separator);
			
			CompilationUnitStruct compilationUnitStructCode = FileHandler.parserFileToCompilationUnit(filesCode, charset);
			
			Iterator<? extends CompilationUnitTree> compilationUnitsCode = compilationUnitStructCode.getCompilationUnitTree();
			
			SourcePositions posCode = compilationUnitStructCode.getPos();
			
			while(compilationUnitsCode.hasNext()) {
				
				Tree treeCode = compilationUnitsCode.next();
				
				retorno.addAll(searchOcorrencesFolder(treeCode,patternFolder));
			}
			
			//TODO editar saida do retorno (Arquivo, linhas e colunas)
			String arquivoAtual = "";
			
			//FIXME Problema de retornar modifiers
			retorno = retorno.stream().filter(x -> x.getNode().getKind() != Kind.MODIFIERS && x.getNode().getKind() != Kind.PRIMITIVE_TYPE).collect(Collectors.toList());
			
			for(Node r : retorno) {
				r.setStartPosition(posCode.getStartPosition(r.getCompilatioUnitTree(), r.getNode()));
				r.setEndPosition(posCode.getEndPosition(r.getCompilatioUnitTree(), r.getNode()));
				
				if(!r.getFilePath().equals(arquivoAtual)) {
					arquivoAtual = r.getFilePath();
					System.out.println("\nFile: " +arquivoAtual);
				}
				
				if(r.getReturnMessage() != null) {
					System.out.println("Alert Message: " +r.getReturnMessage());
				}
				
				System.out.println("Start: L: " +r.getStartLine() +" C: " +r.getStartColumn() );
				System.out.println("End: L: " +r.getEndLine() +" C: " +r.getEndColumn());
				
			}
			
			System.out.println();
			System.out.println("Return size: " +retorno.size());
			return retorno;
			
		}catch(FileNotFoundException e) {
			log.error("Failed to find the file: " +e.getMessage());
		}
		catch(IOException e) {
			System.out.println(e);
		}
		
		return new ArrayList<>();
	}
	
	public static List<Node> searchOcorrences(String pathCode, String pathPattern) {
		return searchOcorrences(pathCode, pathPattern, null);
	}
	
	public static List<Node> searchOcorrencesFolder(Tree treeCode, PatternFolder pattern, Charset charset) throws IOException{
		List<Node> retorno = new ArrayList<>();
		
		for(PatternFolder folder : pattern.getFolders()) {
			retorno.addAll(searchOcorrencesFolder(treeCode, folder, charset));
		}
		
		if(pattern.getFiles().size() > 0) {
			
			File[] filesPatterns = pattern.getFiles().toArray(new File[0]);
			
			CompilationUnitStruct compilationUnitStructPattern = FileHandler.parserFileToCompilationUnit(filesPatterns, charset);
			
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
			
			List<Node> returnFilter = Utils.filterReturnNodes(retorno);
			
			List<Node> listToRemoveFilter = Utils.filterReturnNodes(listToRemove);
			
			
			if(returnFilter.size()>0) {
				retorno = returnFilter;
			}
			
			if(listToRemoveFilter.size()>0) {
				listToRemove = listToRemoveFilter;
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
