package scpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;

public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException {
		
		List<File> files = new ArrayList<>();
				
		files.add(new File("C:\\Teste.java"));
		files.add(new File("C:\\Teste2.java"));
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files.toArray(new File[0]));
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
		
		List<CompilationUnitTree> lista = new ArrayList<CompilationUnitTree>();
		
		
		
		for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
			//System.out.println(PrinterVisitor.print(compilationUnitTree, null));
			lista.add(compilationUnitTree);
		}
		
	}
	
	

}
