package br.scpl.model;

import java.util.Iterator;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SourcePositions;

public class CompilationUnit {
	
	private Iterator<? extends CompilationUnitTree> compilationUnitTree;
	private SourcePositions pos;
	private DocTrees doctrees;

	public CompilationUnit(Iterator<? extends CompilationUnitTree> compilationUnitTree, SourcePositions pos, DocTrees doctrees) {
		this.compilationUnitTree = compilationUnitTree;
		this.pos = pos;
		this.doctrees = doctrees;
	}

	public Iterator<? extends CompilationUnitTree> getCompilationUnitTree() {
		return compilationUnitTree;
	}

	public SourcePositions getPos() {
		return pos;
	}

}
