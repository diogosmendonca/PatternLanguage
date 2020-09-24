package br.scpl.model.sonarqube;

public class Location {
	
	private String message;
	
	private String filePath;

    private TextRange textRange;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setTextRange(TextRange textRange) {
		this.textRange = textRange;
	}

}
