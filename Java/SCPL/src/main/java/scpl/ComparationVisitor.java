package scpl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

public class ComparationVisitor extends TreePathScanner<Tree, Integer> {

	Tree atr;

	  @Override
	  public Tree scan(Tree tree, Integer cont) {
	      
		  //System.out.println(cont);
		  if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
			  //System.out.println("Nó " + cont);  
			  String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
			  //System.out.println(nodeName);
	 	  }
		  
		  if(cont==0) {
			  cont++;
			  super.scan(tree,cont);
		  }
		  atr=tree;
		  return tree;
	      
	  }
	  
	  public boolean isEquals(Tree tree, Tree tree2) {
		  
		  Integer cont = 0;
		  Integer cont2 =0 ;
		  
		  scan(tree,cont);
		  scan(tree2,cont2);
		  
		  
		  return true;
	  }
	  
	  
}
