package scpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.BlockTree;				
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;

import br.scpl.controller.EqualsController;
import br.scpl.controller.FileHandler;
import br.scpl.controller.NodeVisitor;
import br.scpl.model.CompilationUnitStruct;
import br.scpl.model.Node;

import com.sun.source.util.SourcePositions;
import com.sun.source.tree.IdentifierTree;

import com.sun.source.tree.ModifiersTree;

/***
 * Classe com métodos utilitários
 * 
 * @author Denis
 *
 */

public class Utils {
	
	public static Node getDiferentOperatatorNode(Node node) {
		
		Node retorno = null;
		
		if(node.getNodeOfDifferentOperator() != null) {
			return node.getNodeOfDifferentOperator();
		}else {
			for(Node child: node.getChildren()) {
				retorno = child.getNodeOfDifferentOperator();
				
				if(retorno != null) {
					return retorno;
				}
			}
			
		}
		
		return retorno;
	}
	
	public static boolean verifyNotParent(Node a, Node b, Map<String, String> wildcardsMap) {
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		if(b.getNotParent() != null) {
			Node parentAux = a.getParent();
			
			while(parentAux!=null) {
				Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
				wildcardsMapAux.putAll(wildcardsMapBefore);
				if(EqualsController.partialEquals(parentAux,b.getNotParent(), wildcardsMapAux)) {
					return false;
				}
				parentAux = parentAux.getParent();
			}
		}
		
		return true;
	}
}
