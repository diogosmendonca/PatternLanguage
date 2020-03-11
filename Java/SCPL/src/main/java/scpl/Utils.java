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
import br.scpl.model.BlockCodeStruct;
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
	
	public static void getDiferentOperatorBlock(Node node, List<Node> path , List<BlockCodeStruct> retorno) {
		Boolean exists = node.getExists(); 
		path.add(node);
		List<Node> nodeList = new ArrayList<Node>();
		for(Node child: node.getChildren()) {
			if(child.getExists()!=exists) {
				nodeList.add(child);
				continue;
			}
			
			if(nodeList.size() > 0) {
				retorno.add(new BlockCodeStruct(path, nodeList, node));
				nodeList.clear();
			}
			
			if(child.getChangeOperator()) {
				getDiferentOperatorBlock(child, path, retorno);				
			}
		}
		if(nodeList.size() > 0) {
			retorno.add(new BlockCodeStruct(path, nodeList, node));
			nodeList.clear();
		}
		path.remove(node);
	}
	
	/**
	 * Após percorrer a árvore e montar toda a estrutura, dada a lista de todos os nós
	 * o método retorna a raíz (CompilationUnitTree).
	 * 
	 * @param nodes Lista de nós da árvore
	 * @return CompilationUnitTree(raíz)  correspondente a árvore passada
	 */
	public static Node getCompilationUnitTree(Map<Node, List<Node>> nodes) {
		
		return nodes.get(null).iterator().next();
	}
	
	/**
	 * Dada a árvore do padrão, o método retira a estrutura de esboço (método e classe).
	 * Retornando um nó fake pai de todos os nós do padrão
	 *  ou a raíz do próprio padrão (quando se trata de um padrão de bloco).
	 * 
	 * @param nodes Lista de nós da árvore
	 * @return O nó raíz do padrão
	 */
	
	public static Node removeStub(Map<Node, List<Node>> nodes) {
		
		Node retorno = nodes.get(getCompilationUnitTree(nodes)).get(0);
		
		if(retorno.getNode().getKind() != Tree.Kind.CLASS) {
			return retorno;
		}
		
		retorno = nodes.get(retorno).get(nodes.get(retorno).size()-1);
		
		if(retorno.getNode().getKind() != Tree.Kind.METHOD) {
			return retorno;
		}
		
		retorno = nodes.get(retorno).get(nodes.get(retorno).size()-1);
		
		if(retorno.getNode().getKind() == Tree.Kind.BLOCK) {
			
			switch(retorno.getChildren().size()) {
			
				case 0:
					return null;
					
				case 1:
					if(!retorno.getChildren().get(0).getUsingExistsOperator()) {
						return retorno.getChildren().get(0);
					}
					
				default:
					Node fakeNode = new Node();
					fakeNode.getChildren().addAll(retorno.getChildren());
					fakeNode.setUsingExistsOperator(retorno.getUsingExistsOperator());
					fakeNode.setChangeOperator(retorno.getChangeOperator());
					fakeNode.setFakeNode(true);
					
					return fakeNode;
			}
		}
		
		return getCompilationUnitTree(nodes);                                           
	}
	
	public static Map<Node, List<Node>> buildTree(Tree tree){
		Map<Node, List<Node>> nodes = new LinkedHashMap<>();
		NodeVisitor.build(tree,nodes);
		
		return nodes;
	}
	
}
