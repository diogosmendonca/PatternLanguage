package br.scpl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

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
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;

import br.scpl.model.Node;

 /**
 * @author Denis
 */

class EqualsController {
	
	private EqualsController() {}
	
	private static final ResourceBundle config = ResourceBundle.getBundle("config");
	private final static String any = config.getString("any");
	private final static String some = config.getString("some");
	
	/***
	 * Gets two trees and says if the both are equals.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if the trees are equals. 
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
					a.setIssue(b.getIssue());
					a.setMatchingNode(b);
				}
			}
	    }
	}
	
	
	/***
	 * 
	 * Gets two trees and says if the both roots informations are equals (kind, name and value).
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if the trees roots are equals.
	 */
	
	public static boolean basicComparation(Node a, Node b, Map<String, String> wildcardsMap) {
		
			boolean flagAny = false;
			boolean flagSome = false;
			
			if(b.getNode().getKind() == Kind.IDENTIFIER) {
				if((((IdentifierTree) b.getNode()).getName().toString()).startsWith(any)){
					flagAny = true;
				}
				if((((IdentifierTree) b.getNode()).getName().toString()).startsWith(some)){
					flagSome = true;
				}
			}
			
			//Compara se os tipos sao iguais.
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				if(!flagAny && !flagSome) {
					return false;
				}
			}
			
			if(!compareValue(a, b, flagAny, flagSome, wildcardsMap)) {
				return false;
			}
			
			if(!compareName(a, b, flagAny, flagSome, wildcardsMap)) {
				return false;
			}
		
		return true;		
	}
	
	/**
	 * 
	 * Get two nodes and verify if the name of both is equals.
	 * Used for name of variables, methods, classes and modifiers.
	 * 
	 * @param node1 Node representing the source code tree.
	 * @param node2 Node representing the pattern tree.
	 * @param flagAny Flag that indicates if the any wildcard is being used. 
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if the name of nodes are equals.
	 */
	
	private static boolean compareName(Node node1, Node node2, boolean flagAny, boolean flagSome, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
		
		String name1 = null;
		String name2 = flagSome ? ((IdentifierTree)node2.getNode()).toString() : null ;
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				if(name2.startsWith(any)) {
					return true;
				}
				
				if(name2.startsWith(some)) {
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case METHOD:
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.startsWith(any)) {
					return true;
				}
				
				if(name2.startsWith(some)) {
					
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
				
				if(e1.getKind() == Kind.MEMBER_SELECT) {
					name1 = ((MemberSelectTree) e1).getIdentifier().toString();
					name2 = ((MemberSelectTree) e2).getIdentifier().toString();
				}
				
				if(e1.getKind() == Kind.IDENTIFIER) {
					name1 = ((IdentifierTree) e1).getName().toString();
					name2 = ((IdentifierTree) e2).getName().toString();
				}
				
				if(name2.startsWith(any)) {
					return true;
				}
				
				if(name2.startsWith(some)) {
					
					Map<String, String> wildcardsMapAux = new LinkedHashMap<>();
					wildcardsMapAux.putAll(wildcardsMap);
									
					if(node1.getChildren().size()==node2.getChildren().size()) {
						for(int i = 0;i<node1.getChildren().size();i++) {
							if(!isEquals(node1.getChildren().get(i), node2.getChildren().get(i), wildcardsMapAux)) {
								return false;
							}
						}
					}else {
						if(!anyArgument(node1,node2,wildcardsMapAux)) {
							return false;							
						}
					}
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
				return name1.equals(name2);
				
			case MEMBER_SELECT: 
				
				name1 = ((MemberSelectTree) node1.getNode()).getIdentifier().toString();
				
				if(!flagSome) {
					name2 = ((MemberSelectTree) node2.getNode()).getIdentifier().toString();					
				}
				
				if(name2.startsWith(any)) {
					return true;
				}
				
				if(name2.startsWith(some)) {
					
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
				
				if(name2.startsWith(any)) {
					return true;
				}
				
				if(name2.startsWith(some)) {
					
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
				
				if(name2.startsWith(some)) {
					
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
				
				if(flagSome) {
					name2 = ((IdentifierTree)node2.getNode()).toString();
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}else {
					name2 = ((PrimitiveTypeTree) node2.getNode()).toString();					
				}
				
				return name1.equals(name2);
				
			default:
				return true;
		}
		
	}
	
	
	/**
	 * Special equals method for equality of nodes of modifier kind.  
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @return Boolean that indicates if two modifier nodes are equals.
	 */
	protected static boolean equalsModifier(Node a, Node b) {
		
		if(a.getNode().getKind() == b.getNode().getKind() && b.getNode().getKind() == Kind.MODIFIERS) {
			
			if(isAnyModifier(b)) {
				return true;
			}
			
			ModifiersTree modifierPattern = ((ModifiersTree) b.getNode());
			
			List<? extends AnnotationTree> annotations = modifierPattern.getAnnotations();
			
			List<String> notModifier = new ArrayList<String>();
			
			for(AnnotationTree annotation: annotations) {
				if(annotation.toString().toUpperCase().startsWith("@NOT")) {
					notModifier.add(annotation.toString().toUpperCase().split("@NOT")[1].toLowerCase());
				}
			}
			
			if(notModifier.size()>0) {
				Set<Modifier> notFlags = notModifier.stream().map(i -> Modifier.valueOf(i.toUpperCase())).collect(Collectors.toSet());
				
				ModifiersTree modifierCode = ((ModifiersTree) a.getNode());
				
				Set<Modifier> flagsCode = modifierCode.getFlags();
				
				//FIXME The default modifier is empty and needs special treatment
				if(notDefaultCase(flagsCode, notFlags)) {
					return false;
				}
				
				if(notFlags.stream().anyMatch(x-> flagsCode.contains(x))) {
					return false;
				}
				
				return true;
			}
		}
		return false;
	}
	
	/***
	 * 
	 * Because it is empty, the default modifier is found by 
	 * the non-occurrence of the other access modifiers.
	 * And it needs special treatment.
	 * 
	 * @param flagsCode Modifiers used in the source code.
	 * @param notFlags Modifiers used in the pattern.
	 * @return true if notDefault is used and modifier access default occurs.
	 */
	private static boolean notDefaultCase(Set<Modifier> flagsCode, Set<Modifier> notFlags) {
		
		if(notFlags.stream().anyMatch(x-> x.equals(Modifier.valueOf("DEFAULT")))) {
					
			List<Modifier> acessModifiers = Arrays.asList(Modifier.valueOf("PRIVATE"), 	
					Modifier.valueOf("PUBLIC"), Modifier.valueOf("PROTECTED"));
			
			if(!flagsCode.stream().anyMatch(x-> acessModifiers.contains(x))) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Method used when a method tree equality fails. 
	 * Checks if wildcard is being used in the method and performs equality correctly.
	 * 
	 * @param node1 Node representing the source code tree.
	 * @param node2 Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if wildcard is being used and the methods are equals.
	 */
	private static boolean anyMethod(Node a, Node b, Map<String, String> wildcardsMap) {
		
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
				/*if(childrenCode.get(i).getFullVisited().booleanValue()) {
					return false;
				}*/
				if(!isEquals(childrenCode.get(i), childrenPattern.get(i), wildcardsMap)) {
					return false;
				}
			}
			
			return true;
		}
				
		return false;
	}
	
	/**
	 * Verify if the parameter of the method tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in the parameter of the method tree.
	 */
	
	public static boolean isAnyParameter(Node node) {
		MethodTree methodPattern = (MethodTree)node.getNode();
		
		return methodPattern.getParameters().stream().anyMatch(x -> x.toString().startsWith(any));
	}
	
	/**
	 * Verify if the throws clause of the method tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in throws clause of the method tree.
	 */
	
	public static boolean isAnyThrows(Node node) {
		MethodTree methodPattern = (MethodTree)node.getNode();
		
		return methodPattern.getThrows().stream().anyMatch(x -> x.toString().startsWith(any));
	}
	
	/**
	 * Verify if the identifier tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in identifier tree uses any wildcard.
	 */
	private static boolean isAnyExpression(Node node) {
		
		IdentifierTree identifier = (IdentifierTree)node.getNode();
			
		return identifier.getName().toString().startsWith(any);
	}
	
	/**
	 * Verify if the modifier tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in modifier tree uses any wildcard.
	 */
	public static boolean isAnyModifier(Node node) {
		
		boolean retorno = false;
		
		switch(node.getNode().getKind()) {
			
			case CLASS:
				ClassTree classTree = (ClassTree) node.getNode();
				retorno = isAnyModifier(Node.getNodesMap().get(classTree.getModifiers()));
				break;
			
			case METHOD:
				MethodTree mehtodTree = (MethodTree) node.getNode();
				retorno = isAnyModifier(Node.getNodesMap().get(mehtodTree.getModifiers()));
				break;
			
			case MODIFIERS:
				ModifiersTree modifierTree = (ModifiersTree) node.getNode();
				List<? extends AnnotationTree> annotations = modifierTree.getAnnotations();
				
				retorno = annotations.stream().anyMatch(a -> a.toString().startsWith("@"+any));
				break;
				
		}
		
		return retorno;
	}
	
	/**
	 * Verify if the argument of the method invocation tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in the argument of the method invocation.
	 */
	
	private static boolean anyArgument(Node a, Node b, Map<String, String> wildcardsMap) {
		
		MethodInvocationTree invocantionPattern = (MethodInvocationTree)b.getNode();
		List<? extends ExpressionTree> argumentsPattern = invocantionPattern.getArguments();
		
		if(argumentsPattern.size() == 1) {
			ExpressionTree argument = argumentsPattern.get(0);
			
			if(argument.getKind() == Kind.IDENTIFIER) {
				if(((IdentifierTree) argument).getName().toString().startsWith(any)){
					
					MethodInvocationTree invocantionCode = (MethodInvocationTree)a.getNode();
					List<? extends ExpressionTree> argumentsCode = invocantionCode.getArguments();
					
					List<Node> childrenPattern = b.getChildren();
					
					childrenPattern = childrenPattern.stream().filter(x -> !argumentsPattern.contains(x.getNode())).collect(Collectors.toList());
					
					List<Node> childrenCode = a.getChildren();
					
					childrenCode = childrenCode.stream().filter(x -> !argumentsCode.contains(x.getNode())).collect(Collectors.toList());
					
					if(childrenPattern.size() == childrenCode.size()) {
						for(int i=0; i<childrenPattern.size(); i++) {
							/*if(childrenCode.get(i).getFullVisited().booleanValue()) {
								return false;
							}*/
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
	
	/**
	 * 
	 * Get two nodes and verify if the value of both is equals.
	 * Used for value of literal kinds (int, float, double, boolean, char and string).
	 * 
	 * @param node1 Node representing the source code tree.
	 * @param node2 Node representing the pattern tree.
	 * @param flagAny Flag that indicates if the any wildcard is being used.
	 * @param flagSome Flag that indicates if the some wildcard is being used. 
	 * @param wildcardsMap Map for wildcar mapping.
	 * @return Boolean that indicates if the value of nodes are equals.
	 */
	
	private static boolean compareValue(Node node1, Node node2, boolean flagAny, boolean flagSome, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
			
		List<Kind> typesList = Arrays.asList(Kind.INT_LITERAL,Kind.LONG_LITERAL,Kind.FLOAT_LITERAL,Kind.DOUBLE_LITERAL,
				Kind.BOOLEAN_LITERAL,Kind.CHAR_LITERAL,Kind.STRING_LITERAL);
		
		if(typesList.contains(node1.getNode().getKind())) {
			Object value1 = ((LiteralTree)node1.getNode()).getValue();
			
			if(flagSome) {
				String value2 = ((IdentifierTree)node2.getNode()).toString();
				
				if(wildcardsMap.get(value2)==null) {
					wildcardsMap.put(value2, value1.toString());
					return true;
					
				}else {
					return wildcardsMap.get(value2).equals(value1.toString());
				}
			}else {
				Object value2 = ((LiteralTree)node2.getNode()).getValue();
				
				return value1.equals(value2);				
			}
			
		}
		
		return true;
	}
	
	/**
	 * Gets two trees and says if the both are partially equals. If is subtree, but the root have be equals.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if the trees are partially equals.
	 */
	
	public static boolean partialEquals(Node a, Node b, Map<String, String> wildcardsMap) {
			
		if(equalsModifier(a,b)) {
			return true;
		}
		
		if(!basicComparation(a, b, wildcardsMap)) {
			return false;
		}
		
		if(a.getChildren().size()!=b.getChildren().size()) {
			
			if(b.getNode().getKind() == Kind.METHOD) {
				return anyMethod(a, b, wildcardsMap);
			}
			
			/*
			if(b.getNode().getKind() == Kind.METHOD_INVOCATION) {
				return anyArgument(a, b, wildcardsMap);
			}
			
			if(b.getNode().getKind() == Kind.IDENTIFIER) {
				return isAnyExpression(b);
			}
			*/
			
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
			
			if(searching) {
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
