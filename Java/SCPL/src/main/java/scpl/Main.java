package scpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
		
		//Main print 
		
		List<File> files = new ArrayList<>();
		
		files.add(new File("./arquivos/Teste.java"));
		files.add(new File("./arquivos/Teste2.java"));
		files.add(new File("./arquivos/CodigoFonte.java"));
		files.add(new File("./arquivos/PadraoErro.java"));
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files.toArray(new File[0]));
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
		
		Iterator<? extends CompilationUnitTree> iter = compilationUnitTrees.iterator();
		
		
		Map<Tree, List<Node>> nodes1 = new LinkedHashMap<>();
		System.out.println(AstVisitor.build(iter.next(),nodes1));
		addChildren(nodes1);
		
		Map<Tree, List<Node>> nodes2 = new LinkedHashMap<>();
		System.out.println(AstVisitor.build(iter.next(),nodes2));
		addChildren(nodes2);
		
		
		System.out.println(Utils.isSubtree(nodes1.get(null).iterator().next(), nodes2.get(null).iterator().next()));
		
		
		System.out.println("A");
		/*
		
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
	
	private static void addChildren(Map<Tree, List<Node>> nodes) {
		  System.out.println("Tentou");
		  for(Tree key : nodes.keySet()) {
				for(Node node :  nodes.get(key)) {
					Collection<Node> children = nodes.get(node.getNode());
					if(children != null) {
						node.getChildren().addAll(children);
					}
				}
			}
	 }
	
	/*
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
		
	}*/
	
	

}
