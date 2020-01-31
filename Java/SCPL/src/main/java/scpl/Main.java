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
		
		files.add(new File("./src/main/resources/Teste.java"));
		files.add(new File("./src/main/resources/Teste2.java"));
		files.add(new File("./src/main/resources/CodigoFonte.java"));
		files.add(new File("./src/main/resources/PadraoErro.java"));
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files.toArray(new File[0]));
		JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		
		Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
		
		Iterator<? extends CompilationUnitTree> iter = compilationUnitTrees.iterator();
		
		
		Map<Tree, List<Node>> nodes1 = new LinkedHashMap<>();
		//NodeVisitor.build(iter.next(),nodes1);
		System.out.println(NodeVisitor.build(iter.next(),nodes1));
		addChildren(nodes1);
		
		Map<Tree, List<Node>> nodes2 = new LinkedHashMap<>();
		//NodeVisitor.build(iter.next(),nodes2);
		System.out.println(NodeVisitor.build(iter.next(),nodes2));
		addChildren(nodes2);
		
		//System.out.println(Utils.isEquals(Utils.getCompilationUnitTree(nodes1), Utils.removeStub(nodes2)));
		
		System.out.println(Utils.subtree(Utils.getCompilationUnitTree(nodes1), Utils.removeStub(nodes2)));
		
	}
	
	private static void addChildren(Map<Tree, List<Node>> nodes) {
		  for(Tree key : nodes.keySet()) {
				for(Node node :  nodes.get(key)) {
					Collection<Node> children = nodes.get(node.getNode());
					if(children != null) {
						node.getChildren().addAll(children);
					}
				}
			}
	 }

}
