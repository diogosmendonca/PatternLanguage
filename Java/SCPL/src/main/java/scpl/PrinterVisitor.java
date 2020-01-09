package scpl;

import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;

public class PrinterVisitor extends TreePathScanner<Void, List<String>> {
	
	private static final int INDENT_SPACES = 2;

	  private final StringBuilder sb;
	  private int indentLevel;

	  public PrinterVisitor() {
	    sb = new StringBuilder();
	    indentLevel = 0;
	  }

	  public static String print(Tree tree, List<String> list) {
	    PrinterVisitor pv = new PrinterVisitor();
	    pv.scan(tree, list);
	    return pv.sb.toString();
	  }

	  private StringBuilder indent() {
	    return sb.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
	  }	  

	  @Override
	  public Void scan(Tree tree, List<String> list) {
	      if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
	        String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
	        indent().append(nodeName).append("\n");
     	  }
	      indentLevel++;
	      super.scan(tree, list);
	      indentLevel--;
		 return null;
	  }
}
