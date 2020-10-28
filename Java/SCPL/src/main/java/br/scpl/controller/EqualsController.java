package br.scpl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang.StringEscapeUtils;

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
import br.scpl.model.sonarqube.Issue;
import br.scpl.util.ConfigUtils;
import br.scpl.util.StringUtil;

 /**
 * @author Denis
 */

class EqualsController {
	
	private EqualsController() {}
	
	private static final String ANY = ConfigUtils.getProperties().getProperty("any");
	private static final String SOME = ConfigUtils.getProperties().getProperty("some");
	private static final String NOT_ANNOTATION = ConfigUtils.getProperties().getProperty("notAnnotation");
	private static final String ALERT_IF_NOT_ANNOTATION = ConfigUtils.getProperties().getProperty("alertIfNotAnnotation");
	private static final String DEFAULT_ACCESS = "DefaultAccess";
	
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
			
			Boolean equalsModifier = equalsModifier(a,b);
			if(equalsModifier != null) {
				retorno = equalsModifier;
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
					if(b.getChildren().isEmpty()) {
						retorno = true;
						return retorno;
					}
				}
				
				return retorno;				
			}
			
			//Para cada filho chama recursivamente o método isEquals
			for(int i=0; i<a.getChildren().size(); i++) {
				if(a.getChildren().get(i).getFullVisited()) {
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
				a.setMatchingNode(b);
				if(b.isToReturn()) {
					a.setIsToReturn(true);
					a.setReturnMessage(b.getReturnMessage());
					a.getIssues().clear();
					
					b.getIssues().forEach(i -> {
						a.getIssues().add(StringUtil.getIssue(i.getAlertComment()));
					});
					
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
				if(startsWithAnyCaseInsentive(((IdentifierTree) b.getNode()).getName().toString())){
					flagAny = true;
				}
				if(startsWithSomeCaseInsentive(((IdentifierTree) b.getNode()).getName().toString())){
					flagSome = true;
				}
			}
			
			//Compara se os tipos sao iguais.
			if(a.getNode().getKind()!=b.getNode().getKind()) {
				if(a.getNode().getKind() == Kind.MODIFIERS || (!flagAny && !flagSome)) {
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
	/***
	 * 
	 * Verify if a name starts with the wildcard any ignoring case (lower/upper case)
	 * 
	 * @param name Identifier String to analyzed
	 * @return Boolean that indicates if the name starts with the wildcard any.
	 */
	private static boolean startsWithAnyCaseInsentive(String name) {
		
		return name.toUpperCase().startsWith(ANY.toUpperCase());
	}
	
	
	/***
	 * 
	 * Verify if a name starts with the wildcard some ignoring case (lower/upper case)
	 * 
	 * @param name Identifier String to analyzed
	 * @return Boolean that indicates if the name starts with the wildcard some.
	 */
	private static boolean startsWithSomeCaseInsentive(String name) {
		
		return name.toUpperCase().startsWith(SOME.toUpperCase());
	}
	
	/**
	 * 
	 * Get two nodes and verify if the name of both is equals.
	 * Used for name of variables, methods, classes and modifiers.
	 * 
	 * @param node1 Node representing the source code tree.
	 * @param node2 Node representing the pattern tree.
	 * @param flagAny Flag that indicates if the any wildcard is being used.
	 * @param flagSome Flag that indicates if the some wildcard is being used. 
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if the name of nodes are equals.
	 */
	
	private static boolean compareName(Node node1, Node node2, boolean flagAny, boolean flagSome, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
		
		String name1 = "";
		String name2 = flagSome ? ((IdentifierTree)node2.getNode()).toString() : "";
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				if(startsWithAnyCaseInsentive(name2)) {
					return true;
				}
				
				if(startsWithSomeCaseInsentive(name2)) {
					
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
				
				if(startsWithAnyCaseInsentive(name2)) {
					return true;
				}
				
				if(startsWithSomeCaseInsentive(name2)) {
					
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
				
				if(startsWithAnyCaseInsentive(name2)) {
					return true;
				}
				
				if(startsWithSomeCaseInsentive(name2)) {
					
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
				
				if(startsWithAnyCaseInsentive(name2)) {
					return true;
				}
				
				if(startsWithSomeCaseInsentive(name2)) {
					
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
				
				if(startsWithAnyCaseInsentive(name2)) {
					return true;
				}
				
				if(startsWithSomeCaseInsentive(name2)) {
					
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
				
				if(startsWithSomeCaseInsentive(name2)) {
					
					if(wildcardsMap.get(name2)==null) {
						wildcardsMap.put(name2, name1);
						return true;
						
					}else {
						return wildcardsMap.get(name2).equals(name1);
					}
				}
				
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
	protected static Boolean equalsModifier(Node a, Node b) {
		
		if(a.getNode().getKind() == b.getNode().getKind() && b.getNode().getKind() == Kind.MODIFIERS) {
			
			
			ModifiersTree modifierPattern = ((ModifiersTree) b.getNode());
			List<? extends AnnotationTree> annotationsPattern = modifierPattern.getAnnotations();
			Set<Modifier> flagsPattern = modifierPattern.getFlags();
			
			List<String> annotationsToRemove = new ArrayList<>();
					
			List<String> notModifier = new ArrayList<>();
			HashMap<String, String> alertIfNot = new LinkedHashMap<>();
			HashMap<String, Issue> alertIfNotIssues = new LinkedHashMap<>();
			
			boolean defaultAcessNot = false;
			boolean defaultAcessAlertIfNot = false;
			
			for(AnnotationTree annotation: annotationsPattern) {
				
				if(annotation.toString().toUpperCase().startsWith(NOT_ANNOTATION.toUpperCase())) {
					
					String modifier = annotation.toString().toUpperCase().split(NOT_ANNOTATION.toUpperCase())[1].toLowerCase();
					
					if(modifier.equalsIgnoreCase(DEFAULT_ACCESS)) {
						defaultAcessNot = true;
						modifier = DEFAULT_ACCESS;
					}
					
					notModifier.add(modifier);
					annotationsToRemove.add(annotation.toString());
				}else if(annotation.toString().toUpperCase().startsWith(ALERT_IF_NOT_ANNOTATION.toUpperCase())) {
					
					List<? extends ExpressionTree> arguments =  annotation.getArguments();
					
					String message = null;
					Issue issue = null;
					String modifier = annotation.toString().toUpperCase().split(ALERT_IF_NOT_ANNOTATION.toUpperCase())[1].toLowerCase();
					
					message = arguments.toString();
					message = StringEscapeUtils.unescapeJava(message);
					modifier = StringEscapeUtils.unescapeJava(modifier);
					modifier = modifier.replaceAll("(?i)"+message, "");
					modifier = modifier.replaceAll("\\(", "");
					modifier = modifier.replaceAll("\\)", "");
					
					if(modifier.equalsIgnoreCase(DEFAULT_ACCESS)) {
						defaultAcessAlertIfNot = true;
						modifier = DEFAULT_ACCESS;
					}
					
					if(arguments.isEmpty()) {
						message = "The modifier must be " +modifier;
					}else {
						if(message.startsWith("\"") && message.endsWith("\"")) {
							message = message.replaceFirst("\"", "");
							message = message.replaceAll("\"$", "");
							
							if(message.toUpperCase().contains("RULEID")) {
								issue = StringUtil.getIssue(message);
								message = issue.getPrimaryLocation().getMessage();
							}
						}
					}
					
					alertIfNot.put(modifier, message);
					alertIfNotIssues.put(modifier, issue);
					annotationsToRemove.add(annotation.toString());
				}
			}
			
			annotationsPattern = annotationsPattern.stream().filter(x -> !annotationsToRemove.contains(x.toString())).collect(Collectors.toList());
			
			ModifiersTree modifierCode = ((ModifiersTree) a.getNode());
			List<? extends AnnotationTree> annotationsCode = modifierCode.getAnnotations();
			Set<Modifier> flagsCode = modifierCode.getFlags();
			
			Boolean notBoolean = null;
			
			if(!notModifier.isEmpty()) {
				
				notModifier.remove(DEFAULT_ACCESS);
				
				notBoolean = true;
				
				Set<Modifier> notFlags = notModifier.stream().map(i -> Modifier.valueOf(i.toUpperCase())).collect(Collectors.toSet());
				
				if(defaultAcessNot && notDefaultCase(flagsCode)) {
					notBoolean = false;
				}
				
				if(notFlags.stream().anyMatch(flagsCode::contains)) {
					notBoolean = false;
				}
				
			}
			
			if((notBoolean == null || notBoolean) && !alertIfNot.isEmpty()) {
				
				List<String> messages = new ArrayList<>();//a.getReturnMessage();
				List<Issue> issues = new ArrayList<>();
				
				if(defaultAcessAlertIfNot && !notDefaultCase(flagsCode)) {
					messages.add(alertIfNot.get(DEFAULT_ACCESS));						
					issues.add(alertIfNotIssues.get(DEFAULT_ACCESS));
				}
				
				alertIfNot.remove(DEFAULT_ACCESS);
				
				Set<Modifier> alertIfNotFlags = alertIfNot.keySet().stream().map(i -> Modifier.valueOf(i.toUpperCase())).collect(Collectors.toSet());
				
				List<Modifier> matches = alertIfNotFlags.stream().filter(x-> !flagsCode.contains(x)).collect(Collectors.toList());
				
				for(Modifier m : matches) {
					messages.add(alertIfNot.get(m.toString()));
					issues.add(alertIfNotIssues.get(m.toString()));
				}
				
				messages = messages.stream().filter(m -> m != null).collect(Collectors.toList());
				issues = issues.stream().filter(i -> i != null).collect(Collectors.toList());
				
				StringBuilder sb = new StringBuilder();
				
				messages.forEach(m -> sb.append(m+". "));
				
				if(!messages.isEmpty() || !issues.isEmpty()){
					a.setIsToReturn(true);
					a.setReturnMessage(sb.toString());
					a.getIssues().clear();
					a.getIssues().addAll(issues);						

					return true;
				}else {
					a.setIsToReturn(false);
					a.setReturnMessage("");
					a.getIssues().clear();
				}
				
			}
			
			if(notBoolean != null && !notBoolean) {
				return notBoolean;
			}
			
			if(isAnyModifier(b)) {
				return true;
			}
			
			return flagsCode.containsAll(flagsPattern) && annotationsCode.containsAll(annotationsPattern);
			
		}
		return null;
	}
	
	/***
	 * 
	 * Because it is empty, the default modifier access is found by 
	 * the non-occurrence of the other access modifiers.
	 * And it needs special treatment.
	 * 
	 * @param flagsCode Modifiers used in the source code.
	 * @return true if notDefaultAccess is used and modifier access default occurs.
	 */
	private static boolean notDefaultCase(Set<Modifier> flagsCode) {
				
		List<Modifier> acessModifiers = Arrays.asList(Modifier.valueOf("PRIVATE"), 	
				Modifier.valueOf("PUBLIC"), Modifier.valueOf("PROTECTED"));
		
		return flagsCode.stream().noneMatch(acessModifiers::contains);
	}
	
	
	/**
	 * Method used when a method tree equality fails. 
	 * Checks if wildcard is being used in the method and performs equality correctly.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if wildcard is being used and the methods are equals.
	 */
	private static boolean anyMethod(Node a, Node b, Map<String, String> wildcardsMap) {
		
		MethodTree methodPattern = (MethodTree)b.getNode();
		MethodTree methodCode = (MethodTree)a.getNode();
		
		List<Tree> removePattern = new ArrayList<>();
		List<Tree> removeCode = new ArrayList<>();
		
		if(isAnyParameter(b)) {
			removePattern.addAll(methodPattern.getParameters());
				
			removeCode.addAll(methodCode.getParameters());
		}
		
		if(isAnyThrows(b)) {
			removePattern.addAll(methodPattern.getThrows());
			
			removeCode.addAll(methodCode.getThrows());
		}
		
		List<Node> childrenPattern = new ArrayList<>();
		childrenPattern.addAll(b.getChildren());
		
		childrenPattern = childrenPattern.stream().filter(x -> !removePattern.contains(x.getNode())).collect(Collectors.toList());
		
		List<Node> childrenCode = new ArrayList<>();
		childrenCode.addAll(a.getChildren());
		
		childrenCode = childrenCode.stream().filter(x -> !removeCode.contains(x.getNode())).collect(Collectors.toList());
		
		if(childrenPattern.size() == childrenCode.size()) {
			for(int i=0; i<childrenPattern.size(); i++) {
				/*if(childrenCode.get(i).getFullVisited()) {
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
		
		return methodPattern.getParameters().stream().anyMatch(x -> startsWithAnyCaseInsentive(x.toString()));
	}
	
	/**
	 * Verify if the throws clause of the method tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in throws clause of the method tree.
	 */
	
	public static boolean isAnyThrows(Node node) {
		MethodTree methodPattern = (MethodTree)node.getNode();
		
		return methodPattern.getThrows().stream().anyMatch(x -> startsWithAnyCaseInsentive(x.toString()));
	}
	
	/**
	 * Verify if the identifier tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in identifier tree uses any wildcard.
	 */
	private static boolean isAnyExpression(Node node) {
		
		IdentifierTree identifier = (IdentifierTree)node.getNode();
			
		return startsWithAnyCaseInsentive(identifier.getName().toString());
	}
	
	/**
	 * Verify if the modifier tree uses any wildcard.
	 * 
	 * @param node Node representing the source code tree.
	 * @return Boolean that indicates if wildcard is being used in modifier tree uses any wildcard.
	 */
	public static boolean isAnyModifier(Node node) {
		
		boolean retorno = false;
		
		if(node.getNode().getKind() == Kind.CLASS) {
			ClassTree classTree = (ClassTree) node.getNode();
			retorno = isAnyModifier(Node.getNodesMap().get(classTree.getModifiers()));
		}
		
		if(node.getNode().getKind() == Kind.METHOD) {
			MethodTree mehtodTree = (MethodTree) node.getNode();
			retorno = isAnyModifier(Node.getNodesMap().get(mehtodTree.getModifiers()));
		}
		
		if(node.getNode().getKind() == Kind.MODIFIERS) {
			ModifiersTree modifierTree = (ModifiersTree) node.getNode();
			List<? extends AnnotationTree> annotations = modifierTree.getAnnotations();
			retorno = annotations.stream().anyMatch(a -> a.toString().toUpperCase().startsWith("@"+ANY.toUpperCase()));
		}
		
		return retorno;
	}
	
	/**
	 * Verify if the argument of the method invocation tree uses any wildcard.
	 * 
	 * @param a Node representing the source code tree.
	 * @param b Node representing the pattern tree.
	 * @param wildcardsMap Map for wildcard mapping.
	 * @return Boolean that indicates if wildcard is being used in the argument of the method invocation.
	 */
	
	private static boolean anyArgument(Node a, Node b, Map<String, String> wildcardsMap) {
		
		MethodInvocationTree invocantionPattern = (MethodInvocationTree)b.getNode();
		List<? extends ExpressionTree> argumentsPattern = invocantionPattern.getArguments();
		
		if(argumentsPattern.size() == 1) {
			ExpressionTree argument = argumentsPattern.get(0);
			
			if(argument.getKind() == Kind.IDENTIFIER) {
				if(startsWithAnyCaseInsentive(((IdentifierTree) argument).getName().toString())){
					
					MethodInvocationTree invocantionCode = (MethodInvocationTree)a.getNode();
					List<? extends ExpressionTree> argumentsCode = invocantionCode.getArguments();
					
					List<Node> childrenPattern = b.getChildren();
					
					childrenPattern = childrenPattern.stream().filter(x -> !argumentsPattern.contains(x.getNode())).collect(Collectors.toList());
					
					List<Node> childrenCode = a.getChildren();
					
					childrenCode = childrenCode.stream().filter(x -> !argumentsCode.contains(x.getNode())).collect(Collectors.toList());
					
					if(childrenPattern.size() == childrenCode.size()) {
						for(int i=0; i<childrenPattern.size(); i++) {
							/*if(childrenCode.get(i).getFullVisited()) {
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
	 * @param wildcardsMap Map for wildcard mapping.
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
		
		Boolean equalsModifier = equalsModifier(a,b); 
		
		if(equalsModifier != null) {
			return equalsModifier;
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
			
			if(b.getNode().getKind() == Kind.BLOCK && b.getChildren().isEmpty()) {
				return true;
			}
			
		}
		
		if(a.getChildren().size()<b.getChildren().size()) {
			return false;
		}
		
		boolean searching = false;
		int i  = 0;
		int counter = 0;
		
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
