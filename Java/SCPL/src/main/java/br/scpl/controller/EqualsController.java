package br.scpl.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
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
		
		if(!basicComparation(a, b, wildcardsMap)) {
			return false;
		}
		
		//Verifica se as árvores tem o mesmo número de filhos
		if(a.getChildren().size()!=b.getChildren().size()) {
			return false;
		}
		
		//Para cada filho chama recursivamente o método isEquals
		for(int i=0; i<a.getChildren().size(); i++) {
			if(!isEquals(a.getChildren().get(i), b.getChildren().get(i), wildcardsMap)) {
				return false;
			}
		}
		
		return true;
		
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
		if(a==null) {
			return false;
		}
		
		if(b==null) {
			return false;
		}
		
		//System.out.println("A -> " +a.getNode());
		//System.out.println("B -> " +b.getNode());
		
		//Se é um nó fake não realiza as verificações
		if(!b.getFakeNode()) {
						
			boolean flagAny = false;
			
			if(b.getNode().getKind() == Kind.IDENTIFIER &&
					(((IdentifierTree) b.getNode()).getName().toString()).equals(anyValue)) {
				
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
			
			if(!compareName(a, b, wildcardsMap)) {
				return false;
			}
		
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
	
	private static boolean compareName(Node node1, Node node2, Map<String, String> wildcardsMap) {
		
		String name1 = null;
		String name2 = null;
		
		switch(node1.getNode().getKind()) {
		
			case CLASS:
				
				name1 = ((ClassTree) node1.getNode()).getSimpleName().toString();
				name2 = ((ClassTree) node2.getNode()).getSimpleName().toString();
				
				if(name2.equalsIgnoreCase(anyClass)) {
					return true;
				}
				
				return name1.equals(name2);
				
			case METHOD:
				
				name1 = ((MethodTree) node1.getNode()).getName().toString();
				name2 = ((MethodTree) node2.getNode()).getName().toString();
				
				if(name2.equalsIgnoreCase(anyMethod)) {
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
				
				if(name2.equalsIgnoreCase(anyMethod)) {
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
				
				if(name2.equalsIgnoreCase(anyVariable)) {
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
				
				if(name2.equalsIgnoreCase(anyVariable)) {
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
				
			default:
				return true;
		}
		
	}
	
	private static boolean compareValue(Node node1, Node node2, boolean flagAny, Map<String, String> wildcardsMap) {
		
		if(flagAny) {
			return true;
		}
		
		Object value1 = null;
		Object value2 = null;
		
		switch(node1.getNode().getKind()) {
		
			case INT_LITERAL:
				value1 = ((Integer)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Integer)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);

				
			case LONG_LITERAL:
				value1 = ((Long)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Long)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
				
			case FLOAT_LITERAL:
				value1 = ((Float)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Float)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case DOUBLE_LITERAL:
				value1 = ((Double)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Double)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case BOOLEAN_LITERAL:
				value1 = ((Boolean)((LiteralTree)node1.getNode()).getValue());
				value2 = ((Boolean)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case CHAR_LITERAL:
				value1 = ((char)((LiteralTree)node1.getNode()).getValue());
				value2 = ((char)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case STRING_LITERAL:
				value1 = ((String)((LiteralTree)node1.getNode()).getValue());
				value2 = ((String)((LiteralTree)node2.getNode()).getValue());
				return value1.equals(value2);
				
			case NULL_LITERAL:
				//value1 = ((?)((LiteralTree)node1.getNode()).getValue());
				break;
				
		}
		
		return true;
	}
}
