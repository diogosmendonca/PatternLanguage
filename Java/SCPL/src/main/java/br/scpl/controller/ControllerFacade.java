package br.scpl.controller;

import java.io.IOException;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.SourcePositions;

import br.scpl.model.Node;

public class ControllerFacade {
	
	public static List<Node> searchOcorrences(CompilationUnitTree code, CompilationUnitTree pattern, SourcePositions sourcePositionsPattern) throws IOException {
		 
		Node rootCode = NodeVisitor.build(code);
		
		Node rootPattern = NodeVisitor.build(pattern, sourcePositionsPattern, true);
		
		return SearchController.subtree(rootCode, rootPattern);
	}

}
