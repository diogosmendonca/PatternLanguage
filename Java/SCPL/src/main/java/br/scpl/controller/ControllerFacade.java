package br.scpl.controller;

import java.io.IOException;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.SourcePositions;

import br.scpl.model.Node;

public class ControllerFacade {
	
	private ControllerFacade() {}
	
	/**
	 * Searches for all occurrences of the pattern present in the source code.
	 * 
	 * @param code CompilationUnitTree corresponding the source code tree.
	 * @param pattern CompilationUnitTree corresponding the pattern tree.
	 * @param sourcePositionsCode SourcePositions corresponding the positions of the within the source code file.
	 * @param sourcePositionsPattern SourcePositions corresponding the positions of the within the source code file of the pattern.
	 * @return List of all occurrences of the pattern present in the source code.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static List<Node> searchOccurrences(CompilationUnitTree code, CompilationUnitTree pattern, SourcePositions sourcePositionsCode, SourcePositions sourcePositionsPattern) throws IOException {
		 
		Node rootCode = NodeVisitor.build(code, sourcePositionsCode, false);
		
		Node rootPattern = NodeVisitor.build(pattern, sourcePositionsPattern, true);
		
		return SearchController.subtree(rootCode, rootPattern);
	}

}
