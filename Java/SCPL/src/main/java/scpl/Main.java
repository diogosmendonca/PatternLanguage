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
import com.sun.source.util.TreePath;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;

public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException {
		
		List<File> files = new ArrayList<>();
				
		files.add(new File("./arquivos/CodigoFonte.java"));
		files.add(new File("./arquivos/PadraoErro.java"));
		
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
		
		ComparationVisitor cv = new ComparationVisitor(); 
		
		Integer c = 0;
		Integer c2 =0;
		
		Tree l = (cv.scan(lista.get(0), c));
		System.out.println(cv.atr.getKind());
		System.out.println("Kind do resultado " + l.getKind());
		
		System.out.println(cv.scan(lista.get(0),c2));
		
		/*
		if(isEquals(lista.get(0),lista.get(1))) {
			System.out.println("São iguais");
		}*/
		
	}
	
	public static boolean isEquals(Tree CT1,Tree CT2) {
		
	  	PrinterVisitor pv1 = new PrinterVisitor();
		PrinterVisitor pv2 = new PrinterVisitor();

		if (CT1 == null) {
			
			System.out.println("Nulo1");
            return false;

		} 
	    if (CT2 == null) {
	    	
	    	System.out.println("Nulo2");
        	return false;

	    }
	    
		//TreePath tp1 = new TreePath(CT1);
		//TreePath tp2 = new TreePath(CT2);
		System.out.println();
		System.out.println(CT1.getClass().getInterfaces()[0].getSimpleName());
		if(CT1.getKind() == CT2.getKind())
		{		
			return true;
			
		}else {
			return false;
		}
		
	}
	
	
	
	

}
