package br.scpl.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

	public List<File> getFiles() {
		return files;
	}

	public int size() {
		return files.size() + folders.stream().mapToInt(PatternFolder::size).sum();
	}
	
	public List<File> getAndFiles(){
		
		List<File> retorno = new ArrayList<File>();
		
		for(File file: files) {
			if(Pattern.compile("_AND[0-9]+\\.JAVA").matcher(file.getName().toUpperCase()).find()) {
				retorno.add(file);
			}
		}
		
		return retorno;
	}
	
	public Map<String,List<File>> groupByAndFiles(List<File> andFiles) {
		
		Map<String,List<File>> andGroups = new LinkedHashMap<String, List<File>>();
		
		for(File file: andFiles) {
			String prefix = file.getName().toUpperCase().replaceAll("_AND[0-9]+\\.JAVA", "");
			
			if(!andGroups.containsKey(prefix)) {
				andGroups.put(prefix, new ArrayList<File>());
			}
			
			andGroups.get(prefix).add(file);
		}
		
		return andGroups;		
	}
	
}
