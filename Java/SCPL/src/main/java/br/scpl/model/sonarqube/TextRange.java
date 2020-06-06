package br.scpl.model.sonarqube;

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
	
}