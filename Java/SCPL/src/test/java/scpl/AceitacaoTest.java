package scpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import br.scpl.model.Node;
import br.scpl.util.ConfigUtils;
import br.scpl.view.Search;

public class AceitacaoTest {
	
	@Test
	public void tc01() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC01_Code.java", 
				"./src/test/resources/AceitacaoFiles/TC01_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
 		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(42, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc02() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC02_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC02_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc03() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC03_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC03_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(31, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc04() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC04_Code.java"
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
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC05_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC05_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc06() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC06_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC06_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(20, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc07() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC07_Code.java"
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
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC08_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC08_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc09() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC09_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC09_Pattern.java");

		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(44, retorno.get(0).getEndColumn());
	}
	
	
	@Test
	public void tc10() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC10_Code.java"
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
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC11_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC11_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc12() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC12_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC12_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(20, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc13() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC13_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC13_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc14() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC14_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC14_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc15() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC15_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC15_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test 
	public void tc16() throws IOException {
  
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC16_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC16_Pattern.java");
  
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(10, retorno.get(0).getEndColumn());
	}
 
	@Test
	public void tc17() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC17_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC17_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(15, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc18() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC18_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC18_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc19() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC19_Code.java"
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
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC20_Code.java"
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
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC21_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC21_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc22() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC22_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC22_Pattern.java");
		
		assertEquals(3, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(21, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc23() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC23_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC23_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc24() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC24_Code.java"
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
	public void tc25() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC25_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC25_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(5, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
	}
	
	@Test
	public void tc26() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC26_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC26_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc27() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC27_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC27_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(36, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc28() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC28_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC28_Pattern.java");
		
		assertEquals(0, retorno.size());
		
	}
	
	@Test
	public void tc29() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC29_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC29_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(7, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc30() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC30_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC30_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc31() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC31_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC31_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc32() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC32_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC32_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc33() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC33_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC33_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(42, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc34() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC34_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC34_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc35() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC35_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC35_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc36() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC36_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC36_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(12, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc37() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC37_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC37_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc38() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC38_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC38_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc39() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC39_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC39_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	
    @Test 
    public void tc40() throws IOException {
  
    List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC40_Code.java"
    			,"./src/test/resources/AceitacaoFiles/TC40_Pattern.java");
  
  		assertEquals(1, retorno.size());
  		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(13, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(29, retorno.get(0).getEndColumn());
  		
  	}
	 
	
	@Test
	public void tc41() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC41_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC41_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc42() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/PatternFolder"
				,"./src/test/resources/AceitacaoFiles/TC42_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(6, retorno.get(0).getStartLine());
		assertEquals(17, retorno.get(0).getStartColumn());
		assertEquals(6, retorno.get(0).getEndLine());
		assertEquals(44, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc43() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC43_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC43_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(6, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(6, retorno.get(0).getEndLine());
		assertEquals(22, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc44() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC44_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC44_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc45() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC45_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC45_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(6, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc46() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC46_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC46_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(8, retorno.get(0).getEndLine());
		assertEquals(10, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc47() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC47_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC47_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc48() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC48_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC48_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc49() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC49_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC49_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc50() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC50_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC50_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc51() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC51_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC51_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc52() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC52_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC52_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc53() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC53_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC53_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc54() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC54_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC54_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc55() throws IOException {
		
		ConfigUtils.getProperties().setProperty("debug", "on");
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC55_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC55_Pattern.java");
		
		assertEquals(4, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(9, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(4, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(4, retorno.get(1).getEndLine());
		assertEquals(22, retorno.get(1).getEndColumn());
		assertEquals(6, retorno.get(2).getStartLine());
		assertEquals(9, retorno.get(2).getStartColumn());
		assertEquals(6, retorno.get(2).getEndLine());
		assertEquals(18, retorno.get(2).getEndColumn());
		assertEquals(7, retorno.get(3).getStartLine());
		assertEquals(9, retorno.get(3).getStartColumn());
		assertEquals(7, retorno.get(3).getEndLine());
		assertEquals(22, retorno.get(3).getEndColumn());
		
	}
	
	@Test
	public void tc56() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC56_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC56_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc57() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC57_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC57_Pattern.java");
		
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
	public void tc58() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC58_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC58_Pattern.java");
		
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
	public void tc59() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC59_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC59_Pattern.java");
		
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
	public void tc60() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC60_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC60_Pattern.java");

		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc61() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC61_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC61_Pattern.java");

		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc62() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC62_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC62_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc63() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC63_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC63_Pattern.java");

		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc64() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC64_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC64_Pattern.java");

		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc65() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC65_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC65_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc66() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC66_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC66_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(17, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
	}	
	
	@Test
	public void tc67() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC67_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC67_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc68() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC68_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC68_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc69() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC69_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC69_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc70() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC70_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC70_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(17, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc71() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC71_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC71_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(17, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc72() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC71_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC72_Pattern.java");
		
		assertEquals(null, retorno);
		
	}
	
	@Test
	public void tc73() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC72_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC71_Pattern.java");
		
		assertEquals(null, retorno);
		
	}
	
	@Test
	public void tc74() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC71_Code.java"
				,"./src/test/resources/AceitacaoFiles/PatternFolder/test.txt");
		
		assertEquals(null, retorno);
	}
	
	@Test
	public void tc75() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/PatternFolder/test.txt"
				,"./src/test/resources/AceitacaoFiles/PatternFolder");
		
		assertEquals(null, retorno);
	}
	
	@Test
	public void tc76() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC01_Code.java"
				,"./src/test/resources/AceitacaoFiles/PatternFolder");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc77() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC71_Code.java"
				,"./src/test/resources/AceitacaoFiles/PatternFolder");
		
		assertEquals(0, retorno.size());
	}
	
	
	@Ignore
	@Test
	public void tc78() throws IOException {
		
		final RandomAccessFile raFile = new RandomAccessFile("./src/test/resources/AceitacaoFiles/TC78_Code.java", "rw");
		raFile.getChannel().lock();
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC78_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC78_Pattern.java");
		
		raFile.close();
		
		assertEquals(null, retorno);
	}
	
	@Test
	public void tc79() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC79_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC79_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc80() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC80_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC80_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	@Test
	public void tc81() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC81_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC81_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(24, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc82() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC82_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC82_Pattern.java");
		
		assertEquals(0, retorno.size());
		
	}
	
	@Test
	public void tc83() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC83_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC83_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(24, retorno.get(0).getEndColumn());
	}
	
	@Test
	public void tc84() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC84_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC84_Pattern.java");
		
		assertEquals(0, retorno.size());
				
	}
	
	@Test
	public void tc85() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC85_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC85_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(17, retorno.get(0).getEndColumn());
		assertEquals(6, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(6, retorno.get(1).getEndLine());
		assertEquals(20, retorno.get(1).getEndColumn());
		
	}
	
	@Test
	public void tc86() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC86_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC86_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(9, retorno.get(0).getEndColumn());
		assertEquals(5, retorno.get(1).getStartLine());
		assertEquals(9, retorno.get(1).getStartColumn());
		assertEquals(5, retorno.get(1).getEndLine());
		assertEquals(20, retorno.get(1).getEndColumn());
		
	}
	
	@Test
	public void tc87() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC87_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC87_Pattern.java");
		
		assertEquals(0, retorno.size());
		
	}
	
	@Test
	public void tc88() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC88_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC88_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(17, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc89() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC89_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC89_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(17, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc90() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC90_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC90_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(4, retorno.get(0).getStartLine());
		assertEquals(17, retorno.get(0).getStartColumn());
		assertEquals(4, retorno.get(0).getEndLine());
		assertEquals(28, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc91() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC91_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC91_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc92() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC92_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC92_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(1, retorno.get(0).getEndLine());
		assertEquals(7, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc93() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC93_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC93_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(5, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc94() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC94_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC94_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(1, retorno.get(0).getEndLine());
		assertEquals(8, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc95() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC95_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC95_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(1, retorno.get(0).getStartLine());
		assertEquals(1, retorno.get(0).getStartColumn());
		assertEquals(8, retorno.get(0).getEndLine());
		assertEquals(2, retorno.get(0).getEndColumn());
		
	}
	
	@Test
	public void tc96() throws IOException {
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC96_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC96_Code.java");
		
		assertEquals(null, retorno);
	}
	
	//issue 6
	@Test
	public void tc97() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC97_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC97_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(8, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(8, retorno.get(0).getEndLine());
		assertEquals(44, retorno.get(0).getEndColumn());
  }
  
	//issue 7
	@Test
	public void tc98() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC98_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC98_Pattern.java");

		assertEquals(1, retorno.size());
		assertEquals(3, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(3, retorno.get(0).getEndLine());
		assertEquals(12, retorno.get(0).getEndColumn());		
	}

	//issue 8
	@Test
	public void tc99() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC99_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC99_Pattern.java");
		
		assertEquals(0, retorno.size());
	}
	
	//issue 8
	@Test
		public void tc100() throws IOException {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/AceitacaoFiles/TC100_Code.java"
				,"./src/test/resources/AceitacaoFiles/TC100_Pattern.java");
		
		assertEquals(2, retorno.size());
		assertEquals(7, retorno.get(0).getStartLine());
		assertEquals(5, retorno.get(0).getStartColumn());
		assertEquals(7, retorno.get(0).getEndLine());
		assertEquals(18, retorno.get(0).getEndColumn());
		assertEquals(7, retorno.get(1).getStartLine());
		assertEquals(19, retorno.get(1).getStartColumn());
		assertEquals(7, retorno.get(1).getEndLine());
		assertEquals(30, retorno.get(1).getEndColumn());
		
	}
}
