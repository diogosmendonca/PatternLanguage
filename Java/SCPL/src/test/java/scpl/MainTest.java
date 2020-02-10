package scpl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;

import org.junit.Test;

public class MainTest {
	
	final String toolsJarFileName = "tools.jar";
	final String javaHome = System.getProperty("java.home");

	@Test
	public void tc01() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       System.out.println(\"Hello Nico\"); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       System.out.println(\"Hello Nico\"); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(41, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc02() throws IOException {
		
		String code = "public class " + "SourceCode" + " {" +
				"public static void run() {\n" +
				"       System.out.println(\"Hello Nico\"); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {" +
				"void anyMethod() {\n" +
				"       System.out.println(\"Hello\"); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc03() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 10; \n" +
				"       System.out.println(a); \n" +
				"       int c = a + b; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {" +
				"void anyMethod() {\n" +
				"       System.out.println(a); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(1, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(30, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc04() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 10; \n" +
				"       System.out.println(a); \n" +
				"       int c = a + b; \n" +
				"       System.out.println(a); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       System.out.println(a); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(2, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(30, retorno.get(0).getEndColumn());
		assertEquals(7, retorno.get(1).getStartLine());
		assertEquals(8, retorno.get(1).getStartColumn());
		assertEquals(7, retorno.get(1).getEndLine());
		assertEquals(30, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc05() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 10; \n" +
				"       int c = a + b; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       System.out.println(a); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc06() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 20; \n" +
				"       int c = a + b; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       int anyVariable = 10; \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(19, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc07() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 20; \n" +
				"       System.out.println(b); \n" +
				"       System.out.println(a); \n" +
				"       int c = a + b; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       int someVariable = 10; \n" +
				"       System.out.println(someVariable); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(19, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(8, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(30, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc08() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"       int b = 20; \n" +
				"       System.out.println(b); \n" +
				"       int c = a + b; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       int someVariable = 10; \n" +
				"       System.out.println(someVariable); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc09() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       Integer inteiro = new Integer(5); \n" +
				"       String string = inteiro.toString(); \n" +
				"       System.out.println(string); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       String string = inteiro.anyMethod(); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(43, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc11() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       Integer inteiro = new Integer(5); \n" +
				"       String string = inteiro.toString(); \n" +
				"       System.out.println(string.hashCode()); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       String string = inteiro.someMethod(); \n" +
				"       System.out.println(string.someMethod()); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc10() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       Integer inteiro = new Integer(5); \n" +
				"       String string = inteiro.toString(); \n" +
				"       System.out.println(string.hashCode()); \n" +
				"       System.out.println(string.toString()); \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       String string = inteiro.someMethod(); \n" +
				"       System.out.println(string.someMethod()); \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(2, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(43, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(8, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(46, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc12() throws IOException {
		
		String code = "public class " + "SourceCode" + " {\n" +
				"public static void run() {\n" +
				"       int a = 10; \n" +
				"    }" +
				"}";
		
		Path sourceCodePath = createJavaFile("SourceCode", code);
		
		String patternCode = "public class " + "AnyClass" + " {\n" +
				"void anyMethod() {\n" +
				"       int a = \"anyLiteralValue\"; \n" +
				"    }" +
				"}";
		
		Path patternPath = createJavaFile("AnyClass", patternCode);
		
		List<Node> retorno = Utils.searchOcorrences(sourceCodePath.toString(),patternPath.toString());
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(8, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(19, retorno.get(0).getEndColumn());
	}
	
	private Path createJavaFile(String className, String code) throws IOException {
		
		Path temp = Paths.get(System.getProperty("java.io.tmpdir"), className);
		Files.createDirectories(temp);
		
		Path javaSourceFile = Paths.get(temp.normalize().toAbsolutePath().toString(), className + ".java");
		//System.out.println("The java source file is loacted at "+javaSourceFile);
		
		Files.write(javaSourceFile, code.getBytes());
		
		Path toolsJarFilePath = Paths.get(javaHome, "lib", toolsJarFileName);
		if (!Files.exists(toolsJarFilePath)){
			//System.out.println("The tools jar file ("+toolsJarFileName+") could not be found at ("+toolsJarFilePath+").");
		}
		
		//System.out.println(javaSourceFile);
		
		
		
		return javaSourceFile;
	}

}
