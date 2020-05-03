package br.scpl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.Tree.Kind;

import br.scpl.model.Node;

public class EqualsController {
	
	private static final ResourceBundle wildcards = ResourceBundle.getBundle("wildcards");
	private final static String anyClass = wildcards.getString("anyClass");
	private final static String anyMethod = wildcards.getString("anyMethod");
	private final static String someMethod = wildcards.getString("someMethod");
	private final static String anyVariable = wildcards.getString("anyVariable");
	private final static String someVariable = wildcards.getString("someVariable");
	private final static String anyValue = wildcards.getString("anyValue");
	private final static String anyParameter = wildcards.getString("anyParameter");
	private final static String anyArgument = wildcards.getString("anyArgument");
	private final static String anyModifier = wildcards.getString("anyModifier");
	private final static String anyType = wildcards.getString("anyType");
	private final static String anyException = wildcards.getString("anyException");
	private final static String anyExpression = wildcards.getString("anyExpression");
	
	
	/***
	 * Recebe duas árvores e um mapa com os wildcard já utilizados.
	 * Retorna um booleano que indica se as duas árvores são iguais.
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return Se duas árvores são iguais 
	 */
	
	public static boolean isEquals(Node a, Node b, Map<String, String> wildcardsMap) {
		
		boolean retorno = false;
		
		Map<String, String> wildcardsMapBefore = new LinkedHashMap<>();
		wildcardsMapBefore.putAll(wildcardsMap);
		
		try {
			if(equalsModifier(a,b)) {
				retorno = true;
				return retorno;
			}
			
			if(!basicComparation(a, b, wildcardsMap)) {
				//Ignora Expression_Statement na comparação
				if(a.getNode() instanceof ExpressionTree && b.getNode().getKind() == Kind.EXPRESSION_STATEMENT) {
					ExpressionTree treeAux = ((ExpressionStatementTree)b.getNode()).getExpression();
					
					Node nodeAux = Node.getNodesMap().get(treeAux);
					
					wildcardsMap.clear();
					wildcardsMap.putAll(wildcardsMapBefore);
					
					return isEquals(a, nodeAux, wildcardsMap);
				}
				
				return retorno;
			}
			
			//Verifica se as árvores tem o mesmo número de filhos
			if(a.getChildren().size()!=b.getChildren().size()) {
				if(b.getNode().getKind() == Kind.METHOD) {
					retorno = anyMethod(a, b, wildcardsMap);
					return retorno;
				}
				
				if(b.getNode().getKind() == Kind.METHOD_INVOCATION) {
					retorno = anyArgument(a, b, wildcardsMap);
					return retorno;
				}
				
				//FIXME REPENSAR
				if(b.getNode().getKind() == Kind.IDENTIFIER) {
					retorno = isAnyExpression(b);
					return retorno;
				}
				
				if(b.getNode().getKind() == Kind.BLOCK) {
					if(b.getChildren().size() == 0) {
						retorno = true;
						return retorno;
					}
				}
				
				return retorno;				
			}
			
			//Para cada filho chama recursivamente o método isEquals
			for(int i=0; i<a.getChildren().size(); i++) {
				if(a.getChildren().get(i).getFullVisited().booleanValue()) {
					return retorno;
				}
				if(!isEquals(a.getChildren().get(i), b.getChildren().get(i), wildcardsMap)) {
					return retorno;
				}
			}
			
			retorno = true;
			return retorno;
		}
		finally {
			if(retorno) {
				if(b.getIsToReturn()) {
					a.setIsToReturn(true);
					a.setReturnMessage(b.getReturnMessage());
				}
			}
	    }
	}
	
	
	/***
	 * Recebe duas árvores e um mapa com os wildcard já utilizados.
	 * Faz as comparações básicas, verifica se são nulos e se o tipo e nome são iguais.
	 * Retorna booleano que indica se passou em todas as comparações básicas.
	 * 
	 * @param a Árvore do código-fonte alvo
	 * @param b Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return booleano que indica se passou em todas as comparações básicas
	 */
	
	public static boolean basicComparation(Node a, Node b, Map<String, String> wildcardsMap) {		
		//Se é um nó fake não realiza as verificações
			boolean flagAny = false;
			
			if(b.getNode().getKind() == Kind.IDENTIFIER && (
					(((IdentifierTree) b.getNode()).getName().toString()).startsWith(anyValue) || 
						(((IdentifierTree) b.getNode()).getName().toString()).startsWith(anyType) ||
							(((IdentifierTree) b.getNode()).getName().toString()).startsWith(anyExpression))){
				
				flagAny = true;
				
			}
			
			//Compara se os tipos sao iguais.
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				if(!flagAny) {
					return false;
				}
			}
			
			if(!compareValue(a, b, flagAny, wildcardsMap)) {
				return false;
			}
			
			if(!compareName(a, b, flagAny, wildcardsMap)) {
				return false;
			}
		
		return true;		
	}
	
	/**
	 * 
	 * Realiza a comparação do nome dos nós passados, de acordo com o tipo do nó.
	 * 
	 * @param node1 Árvore do código-fonte alvo
	 * @param node2 Árvore do padrão buscado
	 * @param wildcardsMap Mapa de wildcards
	 * @return booleano que indica se os nós possuem o mesmo nome
	 */
	
	private static boolean compareName(Node node1, Node node2, boolean flagAny, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
		
		String name1 = null;
		String name2 = null;
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				if(name2.startsWith(anyClass)) {
					return true;
				}
				
				return name1.equals(name2);
				
			case METHOD:
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.startsWith(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case METHOD_INVOCATION:	
				
				ExpressionTree e1 = ((MethodInvocationTree) node1.getNode()).getMethodSelect();
				ExpressionTree e2 = ((MethodInvocationTree) node2.getNode()).getMethodSelect();
				
				if(e1.getKind() != e2.getKind()) {
					return false;
				}
				
				switch (e1.getKind()) {
				
					case MEMBER_SELECT: 
						
						name1 = ((MemberSelectTree) e1).getIdentifier().toString();
						name2 = ((MemberSelectTree) e2).getIdentifier().toString();
						
						break;
					
					case IDENTIFIER :
						
						name1 = ((IdentifierTree) e1).getName().toString();
						name2 = ((IdentifierTree) e2).getName().toString();
						
						break;
					
				}
				
				if(name2.startsWith(anyMethod)) {
					return true;
				}
				
				if(name2.startsWith(someMethod)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case VARIABLE:
				
				name1 = ((VariableTree) node1.getNode()).getName().toString();
				name2 = ((VariableTree) node2.getNode()).getName().toString();
				
				if(name2.startsWith(anyVariable)) {
					return true;
				}
				
				if(name2.startsWith(someVariable)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
					
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						return false;
					}
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case IDENTIFIER:
				
				name1 = ((IdentifierTree) node1.getNode()).getName().toString();
				name2 = ((IdentifierTree) node2.getNode()).getName().toString();
				
				//TODO rever a necessidade
				if(name2.startsWith(anyVariable)) {
					return true;
				}
				
				if(name2.startsWith(someVariable)) {
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case MODIFIERS:
				name1 = ((ModifiersTree) node1.getNode()).toString();
				
				name2 = ((ModifiersTree) node2.getNode()).toString();
				
				return name1.equals(name2);
				
			case PRIMITIVE_TYPE:
				
				name1 = ((PrimitiveTypeTree) node1.getNode()).toString();
				
				name2 = ((PrimitiveTypeTree) node2.getNode()).toString();
				
				return name1.equals(name2);
				
			default:
				return true;
		}
		
	}
	
	public static boolean equalsModifier(Node a, Node b) {
		
		if(a.getNode().getKind() == b.getNode().getKind() && b.getNode().getKind() == Kind.MODIFIERS) {
			
			if(isAnyModifier(b)) {
				return true;
			}
			
			ModifiersTree modifierPattern = ((ModifiersTree) b.getNode());
			
			List<? extends AnnotationTree> annotations = modifierPattern.getAnnotations();
			
			List<String> notModifier = new ArrayList<String>();
			
			for(AnnotationTree annotation: annotations) {
				if(annotation.toString().startsWith("@not")) {
					notModifier.add(annotation.toString().split("@not")[1].toLowerCase());
				}
			}
			
			if(notModifier.size()>0) {
				ModifiersTree modifierCode = ((ModifiersTree) a.getNode());
				
				if(notModifier.stream().anyMatch(x-> modifierCode.toString().contains(x))) {
					return false;
				}
				
				return true;
			}
		}
		return false;
	}
	
	public static boolean anyMethod(Node a, Node b, Map<String, String> wildcardsMap) {
		
		MethodTree methodPattern = (MethodTree)b.getNode();
		MethodTree methodCode = (MethodTree)a.getNode();
		
		List<Tree> removePattern = new ArrayList<Tree>();
		List<Tree> removeCode = new ArrayList<Tree>();
		
		if(isAnyParameter(b)) {
			removePattern.addAll(methodPattern.getParameters());
				
			removeCode.addAll(methodCode.getParameters());
		}
		
		if(isAnyThrows(b)) {
			removePattern.addAll(methodPattern.getThrows());
			
			removeCode.addAll(methodCode.getThrows());
		}
		
		List<Node> childrenPattern = new ArrayList<Node>();
		childrenPattern.addAll(b.getChildren());
		
		childrenPattern = childrenPattern.stream().filter(x -> !removePattern.contains(x.getNode())).collect(Collectors.toList());
		
		List<Node> childrenCode = new ArrayList<Node>();
		childrenCode.addAll(a.getChildren());
		
		childrenCode = childrenCode.stream().filter(x -> !removeCode.contains(x.getNode())).collect(Collectors.toList());
		
		if(childrenPattern.size() == childrenCode.size()) {
			for(int i=0; i<childrenPattern.size(); i++) {
				if(childrenCode.get(i).getFullVisited().booleanValue()) {
					return false;
				}
				if(!isEquals(childrenCode.get(i), childrenPattern.get(i), wildcardsMap)) {
					return false;
				}
			}
			
			return true;
		}
				
		return false;
	}
	
	public static boolean isAnyParameter(Node node) {
		MethodTree methodPattern = (MethodTree)node.getNode();
		
		return methodPattern.getParameters().stream().anyMatch(x -> x.toString().startsWith(anyParameter));
	}
	
	public static boolean isAnyThrows(Node node) {
		MethodTree methodPattern = (MethodTree)node.getNode();
		
		return methodPattern.getThrows().stream().anyMatch(x -> x.toString().startsWith(anyException));
	}
	
	public static boolean isAnyExpression(Node node) {
		
		IdentifierTree identifier = (IdentifierTree)node.getNode();
			
		return identifier.getName().toString().startsWith(anyExpression);
	}
	
	public static boolean isAnyModifier(Node node) {
		
		switch(node.getNode().getKind()) {
			
			case CLASS:
				ClassTree classTree = (ClassTree) node.getNode();
				return isAnyModifier(Node.getNodesMap().get(classTree.getModifiers()));
			
			case METHOD:
				MethodTree mehtodTree = (MethodTree) node.getNode();
				return isAnyModifier(Node.getNodesMap().get(mehtodTree.getModifiers()));
			
			case MODIFIERS:
				ModifiersTree modifierTree = (ModifiersTree) node.getNode();
				List<? extends AnnotationTree> annotations = modifierTree.getAnnotations();
				
				return annotations.stream().anyMatch(a -> a.toString().startsWith(anyModifier));
				
		}
		
		return false;
	}
	
	private static boolean anyArgument(Node a, Node b, Map<String, String> wildcardsMap) {
		
		MethodInvocationTree invocantionPattern = (MethodInvocationTree)b.getNode();
		List<? extends ExpressionTree> argumentsPattern = invocantionPattern.getArguments();
		
		if(argumentsPattern.size() == 1) {
			ExpressionTree argument = argumentsPattern.get(0);
			
			if(argument.getKind() == Kind.IDENTIFIER) {
				if(((IdentifierTree) argument).getName().toString().startsWith(anyArgument)){
					
					MethodInvocationTree invocantionCode = (MethodInvocationTree)a.getNode();
					List<? extends ExpressionTree> argumentsCode = invocantionCode.getArguments();
					
					List<Node> childrenPattern = b.getChildren();
					
					childrenPattern = childrenPattern.stream().filter(x -> !argumentsPattern.contains(x.getNode())).collect(Collectors.toList());
					
					List<Node> childrenCode = a.getChildren();
					
					childrenCode = childrenCode.stream().filter(x -> !argumentsCode.contains(x.getNode())).collect(Collectors.toList());
					
					if(childrenPattern.size() == childrenCode.size()) {
						for(int i=0; i<childrenPattern.size(); i++) {
							if(childrenCode.get(i).getFullVisited().booleanValue()) {
								return false;
							}
							if(!isEquals(childrenCode.get(i), childrenPattern.get(i), wildcardsMap)) {
								return false;
							}
						}
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static boolean compareValue(Node node1, Node node2, boolean flagAny, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
		
		List<Kind> typesList = Arrays.asList(Kind.INT_LITERAL,Kind.LONG_LITERAL,Kind.FLOAT_LITERAL,Kind.DOUBLE_LITERAL,
				Kind.BOOLEAN_LITERAL,Kind.CHAR_LITERAL,Kind.STRING_LITERAL);
		
		if(typesList.contains(node1.getNode().getKind())) {
			Object value1 = ((LiteralTree)node1.getNode()).getValue();
			Object value2 = ((LiteralTree)node2.getNode()).getValue();
			
			return value1.equals(value2);
		}
		
		return true;
	}
	
	public static boolean partialEquals(Node a, Node b, Map<String, String> wildcardsMap) {
			
		if(equalsModifier(a,b)) {
			return true;
		}
		
		if(!basicComparation(a, b, wildcardsMap)) {
			return false;
		}
		
		//Verifica se as árvores tem o mesmo número de filhos
		if(a.getChildren().size()!=b.getChildren().size()) {
			
			if(b.getNode().getKind() == Kind.METHOD) {
				return anyMethod(a, b, wildcardsMap);
			}
			
			if(b.getNode().getKind() == Kind.METHOD_INVOCATION) {
				return anyArgument(a, b, wildcardsMap);
			}
			
			//FIXME REPENSAR
			if(b.getNode().getKind() == Kind.IDENTIFIER) {
				return isAnyExpression(b);
			}
			
			if(b.getNode().getKind() == Kind.BLOCK && b.getChildren().size() == 0) {
				return true;
			}
			
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		boolean searching = false;
		int i,counter = 0;
		
		for(i =0;i<b.getChildren().size();i++) {
			
			if(searching || b.getChildren().size()-i > a.getChildren().size()-counter) {
				return false;
			}
			
			searching = true;
			
			while(searching && counter<a.getChildren().size()) {
				searching = !partialEquals(a.getChildren().get(counter), b.getChildren().get(i), wildcardsMap);
				counter++;
			}
			
			if(i == b.getChildren().size() - 1) {
				return !searching;
			}
		} 
		
		
		return true;
	}
}
