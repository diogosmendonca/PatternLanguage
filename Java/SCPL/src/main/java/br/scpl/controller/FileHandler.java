package br.scpl.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.mozilla.universalchardet.UniversalDetector;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

import br.scpl.model.CompilationUnitStruct;
import br.scpl.model.PatternFolder;

public class FileHandler {
	
	private static final String separator = ResourceBundle.getBundle("config").getString("separator");

	private static Logger log = Logger.getLogger(FileHandler.class);
	
	private FileHandler() {}

	public static void browseFiles(File file, List<File> files) {
		// Testa se o arquivo é uma pasta
		if (file.isDirectory()) {
			// lista os arquivos da pasta
			String[] subDirectory = file.list();

			// Verifica se a lista de arquivos não é nula(Vazia)
			if (subDirectory != null) {
				// lista os arquivos desse subdiretório e pra cada arquivo, chama recursivamente
				// o mesmo método
				for (String dir : subDirectory) {
					browseFiles(new File(file + File.separator + dir), files);
				}
			}
			// Testa se o arquivo é um código fonte Java (termina com .java) e adiciona na
			// lista de arquivos da classe
		} else if (file.getName().endsWith(".java")) {
			files.add(file);
		}
	}
	
	public static void browseFiles(File file, PatternFolder folder) {
		// Testa se o arquivo é uma pasta
		if (file.isDirectory()) {
			// lista os arquivos da pasta
			String[] subDirectory = file.list();

			// Verifica se a lista de arquivos não é nula(Vazia)
			if (subDirectory != null) {
				// lista os arquivos desse subdiretório e pra cada arquivo, chama recursivamente
				// o mesmo método
				for (String dir : subDirectory) {
					
					File auxFile = new File(file + File.separator + dir);
					
					if(auxFile.isDirectory()) {
						
						PatternFolder auxFolder = new PatternFolder();
						
						folder.getFolders().add(auxFolder);
						
						browseFiles(auxFile, auxFolder);
					}else {
						browseFiles(auxFile, folder);
					}
				}
			}
			// Testa se o arquivo é um código fonte Java (termina com .java) e adiciona na
			// lista de arquivos da classe
		} else if (file.getName().endsWith(".java")) {
			folder.getFiles().add(file);
		}
	}
	
	public static PatternFolder getPatternFolder(String rootPath) throws FileNotFoundException{
		PatternFolder folder = new PatternFolder();
		log.info(separator);
		log.info("Searching patterns files.");
		if(!(new File(rootPath)).exists()){
			throw new FileNotFoundException(rootPath);
		}
		browseFiles(new File(rootPath),folder);
		log.info(separator);
		log.info("Total files: " +folder.size());
		return folder;
	} 

	public static File[] getFiles(String rootPath) throws FileNotFoundException {
		List<File> files = new ArrayList<>();
		log.info(separator);
		log.info("Searching source code files.");
		if(!(new File(rootPath)).exists()){
			throw new FileNotFoundException(rootPath);
		}
		browseFiles(new File(rootPath),files);
		log.info(separator);
		log.info("Total files: " +files.size());
		return files.isEmpty() ? null : files.toArray(new File[0]);
	}
	
	/**
	 * Faz o parse dos arquivos java e retorna as ASTs e um objeto com as posições de cada nó.
	 * 
	 * 
	 * @param files array de arquivos de código-fonte java
	 * @return CompilationUnitStruct correspondente aos arquivos passados, 
	 * que contém um iterator de CompilationUnitTree e um onjsto SourcePositions(guarda as posições do nós)
	 * @throws  
	 */
	

	
	public static CompilationUnitStruct parserFileToCompilationUnit(File[] files, Charset charset) throws IOException{
		
		if(charset==null) {
			
			FileInputStream fis = new FileInputStream(files[0].getAbsolutePath());				
			
			byte[] buf = new byte[4096];
			
			int nread;
			
			do {
				nread = fis.read(buf);
			}while (nread > 0); 
			
			CharsetDetector cd = new CharsetDetector();
			cd.setText(buf);
		    CharsetMatch match = cd.detect();
			
			if(match.getName() != null && match.getConfidence() == 100) {
				charset = Charset.forName(match.getName());
			}
		}
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, charset);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		SourcePositions pos = Trees.instance(javacTask).getSourcePositions();
		
		DocTrees docTrees = DocTrees.instance(javacTask);
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
		Iterator<? extends CompilationUnitTree> iter = compilationUnitTrees.iterator();
			
		return new CompilationUnitStruct(iter,pos,docTrees);
	}
	
	public static String getStringContent(JavaFileObject sourceFile) throws IOException {
		return new String(Files.readAllBytes(Paths.get(sourceFile.getName())));
	}
	
}
