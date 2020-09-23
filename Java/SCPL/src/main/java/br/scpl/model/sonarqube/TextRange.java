package br.scpl.model.sonarqube;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;

import br.scpl.model.Node;

public class TextRange {
	
	private Integer startLine;

	private Integer endLine;

	private Integer startColumn;

	private Integer endColumn;

	public void setStartLine(Integer startLine) {
		this.startLine = startLine;
	}

	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}

	public void setStartColumn(Integer startColumn) {
		this.startColumn = startColumn;
	}

	public void setEndColumn(Integer endColumn) {
		this.endColumn = endColumn;
	}
	
	public static TextRange nodeToTextRange(Node node) throws IOException {
		CompilationUnitTree compilationUnitTree = node.getCompilatioUnitTree();
		
		JavaFileObject javaFileObject = compilationUnitTree.getSourceFile();
		
		List<String> lines = Files.readAllLines(Paths.get(javaFileObject.getName()));
		String file = new String(Files.readAllBytes(Paths.get(javaFileObject.getName())));
		
		long startL = node.getStartLine();
		long endL = node.getEndLine();
		long startC = node.getStartColumn();
		long endC = node.getEndColumn();		
		
		String currentLine = lines.get((int)startL-1);
		
		String lineWithOutIdent = currentLine.replaceAll("^\t+", "");
		
		int startDiscount = currentLine.length()-lineWithOutIdent.length();
		
		int endDiscount = startDiscount;
		
		if(startL!=endL) {
			
			String lastLine = lines.get((int)endL-1);;
			
			endDiscount = lastLine.length()-lastLine.replaceAll("^\t+", "").length();
		}
		
		startDiscount = startDiscount*7+1;
		endDiscount = endDiscount*7+1;
		
		startC -= startDiscount;
		endC -= endDiscount;
		
		TextRange textRange = new TextRange();
		
		textRange.setStartLine((int) startL);
		textRange.setEndLine((int) endL);
		textRange.setStartColumn((int) startC);
		textRange.setEndColumn((int) endC);
		
		return textRange;
	}
	
}