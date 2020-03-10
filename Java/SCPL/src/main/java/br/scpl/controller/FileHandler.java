package br.scpl.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

import br.scpl.model.CompilationUnitStruct;

public class FileHandler {
	
	private static String separator = "###---###---###---###---###---###---###";

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

	public static File[] getFiles(String rootPath) {
		List<File> files = new ArrayList<>();
		log.info(separator);
		log.info("Buscando arquivos e pastas");
		browseFiles(new File(rootPath),files);
		log.info(separator);
		log.info("Total de arquivos: " +files.size());
		log.info("Fim da Busca de arquivos.");
		return files.isEmpty() ? null : files.toArray(new File[0]);
	}
	
	/**
	 * Faz o parse dos arquivos java e retorna as ASTs e um objeto com as posições de cada nó.
	 * 
	 * 
	 * @param files array de arquivos de código-fonte java
	 * @return CompilationUnitStruct correspondente aos arquivos passados, 
	 * que contém um iterator de CompilationUnitTree e um onjsto SourcePositions(guarda as posições do nós)
	 */
	
	public static CompilationUnitStruct parserFileToCompilationUnit(File... files) {
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		SourcePositions pos = Trees.instance(javacTask).getSourcePositions();
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees;
		Iterator<? extends CompilationUnitTree> iter = null;
		
		try {
			compilationUnitTrees = javacTask.parse();
			iter = compilationUnitTrees.iterator();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new CompilationUnitStruct(iter,pos);

	}

}
