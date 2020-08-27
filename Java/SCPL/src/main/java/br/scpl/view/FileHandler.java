package br.scpl.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

import br.scpl.exception.CompilationErrorException;
import br.scpl.exception.NoValidFilesFoundException;
import br.scpl.model.CompilationUnit;
import br.scpl.model.PatternFolder;

/**
 * 
 * @author Denis
 *
 */
public class FileHandler {
	
	private static Logger log = Logger.getLogger(FileHandler.class);
	
	private static final String BEGIN_OR = "//#BEGIN";
	private static final String END_OR = "//#END";
	private static final String OR = "//#OR";
	
	private FileHandler() {}
	
	/**
	 * Browse the specified file and insert the Java files into the list.
	 * 
	 * @param file File to be browsed
	 * @param files List that stores all the Java files already browsed.
	 */
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
	
	/**
	 * Browse the specified file and insert the Java files into the PatternFolder object, 
	 * respecting the folder structure.
	 * 
	 * @param file File to be browsed
	 * @param folder PatternFolder object that stores all the Java files already browsed, respecting the folder structure.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static void browseFiles(File file, PatternFolder folder) throws IOException {
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
			
			folder.getFiles().addAll(preProcessing(file));
		}
	}
	
	public static List<File> preProcessing(File file) throws IOException{
		List<File> files = new ArrayList<>();
		
		String name = file.getName();
		
		String content = getStringContent(file);
		
		if(content.contains("//InAnyMethod")) {
			
			String anyMeThod = "@anyModifier\r\n" + 
					"class anyClass {\r\n" + 
					"    @anyModifier\r\n" + 
					"    anyReturn anyMethod(anyType anyParameter) {";
			String anyMethodClose = "}\r\n" +"}";
			
			content = content.replaceAll("(\r\n)+", "\r\n        ");
			
			content = content.replace("//InAnyMethod", anyMeThod);
			
			content = content.replaceAll("\\s+$", "");
			
			content = content.concat(anyMethodClose);
		}
		
		if(content.contains(BEGIN_OR) && content.contains(END_OR)) {
			
			String begin = content.substring (0, content.indexOf(BEGIN_OR));
			
			String end = content.substring(content.indexOf(END_OR), content.length());
			
			content = content.replace(begin, "");
			content = content.replace(end, "");
			content = content.replace(BEGIN_OR, "");
			end = end.replace(END_OR, "");
			
			List<String> options = Arrays.asList(content.split(OR));
			
			int n = 1;
			
			for(String o : options) {
				
				o = concatBeginAndEnd(o,begin,end);
				
				String currentName = String.format("_Option%d.java", n);
				
				File currentFile = new File(file.getAbsolutePath().replace(name, name.replace(".java", currentName)));
				currentFile.deleteOnExit();
				
				try(FileWriter writer = new FileWriter(currentFile)){
					writer.write(o);
				}
				
				files.add(currentFile);
				
				n++;
			}
			
		}
		
		if(files.isEmpty()) {
			files.add(file);
		}
				
		return files;
	}
	
	private static String concatBeginAndEnd(String str, String begin, String end) {
		return begin.concat(str).concat(end);
	}
	
	/**
	 * Groups all Java patterns files by folders using the object PatternFolder.
	 * 
	 * @param rootPath Path of pattern. 
	 * @return PatternFolder object that contains all patterns.
	 * @throws NoValidFilesFoundException Signals that none valid file has found (.java).
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static PatternFolder getPatternFolder(String rootPath) throws NoValidFilesFoundException, IOException{
		PatternFolder folder = new PatternFolder();
		log.debug("");
		log.debug("Searching patterns files.");
		if(!(new File(rootPath)).exists()){
			throw new FileNotFoundException(rootPath);
		}
		browseFiles(new File(rootPath),folder);
		log.debug("");
		log.debug("Total files: " +folder.size());
		if(folder.size()==0) {
			throw new NoValidFilesFoundException(rootPath);
		}
		return folder;
	} 
	
	/**
	 * Gets all Java files of the specified path.
	 * 
	 * @param rootPath Path of source code.
	 * @return files array containing all Java files in the specified path.
	 * @throws FileNotFoundException Signals that an attempt to open the file denoted by a specified pathname has failed.
	 * @throws NoValidFilesFoundException Signals that none valid file has found (.java).
	 */
	public static File[] getFiles(String rootPath) throws FileNotFoundException, NoValidFilesFoundException {
		List<File> files = new ArrayList<>();
		log.debug("");
		log.debug("Searching source code files.");
		if(!(new File(rootPath)).exists()){
			throw new FileNotFoundException(rootPath);
		}
		browseFiles(new File(rootPath),files);
		log.debug("");
		log.debug("Total files: " +files.size());
		if(files.size()==0) {
			throw new NoValidFilesFoundException(rootPath);
		}
		return files.toArray(new File[0]);
	}
	
	/**
	 * Parse the java files and return the ASTs and an object with the positions of each node.
	 * 
	 * @param files array of Java code files.
	 * @param charset Specifies the charset tha will be used.
	 * @return The compilationUnit object corresponding to the passed files, which contains a 
	 * CompilationUnitTree iterator and a SourcePositions object (stores the node positions)  
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 * @throws CompilationErrorException Signals that an error of compilation occurred in the parse of files.
	 */
	public static CompilationUnit parserFileToCompilationUnit(File[] files, Charset charset) throws IOException, CompilationErrorException{
		
		if(charset==null) {
			
			UniversalDetector detector = new UniversalDetector(null);
			
			try(FileInputStream fis = new FileInputStream(files[0].getAbsolutePath())){
				byte[] buf = new byte[4096];
				
				int nread;
				while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
					detector.handleData(buf, 0, nread);
				}
				detector.dataEnd();
				
				if(detector.getDetectedCharset() != null) {
					charset = Charset.forName(detector.getDetectedCharset());
				}
			}			
			
		}
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, charset);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		
		Writer errorWriter = new StringWriter();
		JavacTask javacTask = (JavacTask) compiler.getTask(errorWriter, fileManager, null, null, null, compilationUnits);
		SourcePositions pos = Trees.instance(javacTask).getSourcePositions();
		
		DocTrees docTrees = DocTrees.instance(javacTask);
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
		
		if(!errorWriter.toString().equals("")) {
			throw new CompilationErrorException(errorWriter.toString());
		}
		Iterator<? extends CompilationUnitTree> iter = compilationUnitTrees.iterator();
			
		return new CompilationUnit(iter,pos,docTrees);
	}
	
	/**
	 * Return the string content of the object specified.	 
	 * 
	 * @param sourceFile JavaFileObject
	 * @return String content of the object specified.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static String getStringContent(JavaFileObject sourceFile) throws IOException {
		return new String(Files.readAllBytes(Paths.get(sourceFile.getName())));
	}
	
	public static String getStringContent(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
	}
}
