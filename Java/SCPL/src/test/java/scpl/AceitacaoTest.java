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

import org.junit.Ignore;
import org.junit.Test;

import br.scpl.model.Node;
import br.scpl.view.View;

public class AceitacaoTest {
	
	final String toolsJarFileName = "tools.jar";
	final String javaHome = System.getProperty("java.home");

	@Test
	public void tc01() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC01_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC01_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(42, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc02() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC02_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC02_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc03() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC03_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC03_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(31, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc04() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC04_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC04_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(31, retorno.get(0).getEndColumn());
		assertEquals(7, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(7, retorno.get(1).getEndLine());
		assertEquals(31, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc05() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC05_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC05_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc06() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC06_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC06_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(20, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc07() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC07_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC07_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(20, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(31, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc08() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC08_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC08_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc09() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC09_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC09_Pattern.java");

		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(44, retorno.get(0).getEndColumn());
	}
	
	
	@Test
	public void tc10() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC10_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC10_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(44, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(47, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc11() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC11_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC11_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc12() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC12_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC12_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(20, retorno.get(0).getEndColumn());
	}
	
	@Ignore
	@Test
	public void tc13() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC13_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC13_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Ignore
	@Test
	public void tc14() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC14_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC14_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Ignore
	@Test
	public void tc15() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC15_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC15_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Ignore
	@Test
	public void tc16() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC16_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC16_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc17() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC17_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC17_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(15, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc18() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC18_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC18_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc19() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC19_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC19_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc20() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC20_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC20_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc21() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC21_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC21_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
	}
	
	@Ignore
	@Test
	public void tc22() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC22_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC22_Pattern.java");
		
		assertEquals(3, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Ignore
	@Test
	public void tc23() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC23_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC23_Pattern.java");
		
		assertEquals(3, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc24() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC24_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC24_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(5, retorno.get(1).getStartLine());
		assertEquals(13, retorno.get(1).getStartColumn());
		assertEquals(5, retorno.get(1).getEndLine());
		assertEquals(26, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc26() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/TC26_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC26_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
	}
	
}
