package br.scpl.model;

import java.util.Iterator;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.SourcePositions;

public class CompilationUnitStruct {
	
	private Iterator<? extends CompilationUnitTree> compilationUnitTree;
	private SourcePositions pos;

	public CompilationUnitStruct(Iterator<? extends CompilationUnitTree> compilationUnitTree, SourcePositions pos) {
		this.compilationUnitTree = compilationUnitTree;
		this.pos = pos;
	}

	public Iterator<? extends CompilationUnitTree> getCompilationUnitTree() {
		return compilationUnitTree;
	}

	public SourcePositions getPos() {
		return pos;
	}

}
