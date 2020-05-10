package br.scpl.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PatternFolder {
	
	private List<PatternFolder> folders;
	private List<File> files;
	
	public PatternFolder() {
		this.folders = new ArrayList<>();
		this.files = new ArrayList<>();
	}
	
	public List<PatternFolder> getFolders() {
		return folders;
	}
	public void setFolders(List<PatternFolder> folders) {
		this.folders = folders;
	}
	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
	

}
