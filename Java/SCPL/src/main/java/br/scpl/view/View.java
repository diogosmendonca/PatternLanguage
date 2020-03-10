package br.scpl.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;

import br.scpl.controller.FileHandler;
import br.scpl.controller.SearchController;
import br.scpl.model.CompilationUnitStruct;
import br.scpl.model.Node;
import scpl.Utils;

public class View {
	
	/**
	 * Recebe o path da pasta do código-fonte alvo e a pasta com os padrões buscados.
	 * Retorna as ocorrências dos padrões em cada arquivo de código-fonte
	 * 
	 * @param pathCode Caminho da pasta com os arquivos de código-fonte alvos da busca
	 * @param pathPattern Caminho da pasta com os arquivos da regras dos padrões buscados
	 * @return
	 */
	
	public static  List<Node> searchOcorrences(String pathCode, String pathPattern){
		
		List<Node> retorno = new ArrayList<>();
		
		File[] filesPatterns = FileHandler.getFiles(pathPattern);
		
		CompilationUnitStruct compilationUnitStructPattern = FileHandler.parserFileToCompilationUnit(filesPatterns);
		
		Iterator<? extends CompilationUnitTree> compilationUnitsPattern = compilationUnitStructPattern.getCompilationUnitTree();
		
        List<Tree> listPattern = new ArrayList<>(); 
  
        // Add each element of iterator to the List 
        compilationUnitsPattern.forEachRemaining(listPattern::add); 
		
		File[] filesCode = FileHandler.getFiles(pathCode);
		
		CompilationUnitStruct compilationUnitStructCode = FileHandler.parserFileToCompilationUnit(filesCode);
		
		Iterator<? extends CompilationUnitTree> compilationUnitsCode = compilationUnitStructCode.getCompilationUnitTree();
		
		SourcePositions posCode = compilationUnitStructCode.getPos();
		
		while(compilationUnitsCode.hasNext()) {
			
			Tree treeCode = compilationUnitsCode.next();
			
			for(Tree treePattern: listPattern) {
				
				retorno.addAll(SearchController.subtree(Utils.getCompilationUnitTree(Utils.buildTree(treeCode)), Utils.removeStub(Utils.buildTree(treePattern))));
			}
			
		}
		
		//TODO editar saida do retorno (Arquivo, linhas e colunas)
		
		String arquivoAtual = "";
		
		for(Node r : retorno) {
			r.setStartPosition(posCode.getStartPosition(r.getCompilatioUnitTree(), r.getNode()));
			r.setEndPosition(posCode.getEndPosition(r.getCompilatioUnitTree(), r.getNode()));
			
			if(!r.getFilePath().equals(arquivoAtual)) {
				arquivoAtual = r.getFilePath();
				System.out.println(arquivoAtual);
			}
			
			System.out.println("Inicio: L: " +r.getStartLine() +" C: " +r.getStartColumn() );
			System.out.println("Fim: L: " +r.getEndLine() +" C: " +r.getEndColumn());
			
		}
		
		System.out.println(retorno.size());
		
		return retorno;
	}

}