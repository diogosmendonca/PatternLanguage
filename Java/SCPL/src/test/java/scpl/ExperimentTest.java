package scpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.scpl.model.Node;
import br.scpl.view.View;

public class ExperimentTest {
	
	
	@Test
	public void tc01() throws IOException {
		List<Node> retorno = View.searchOcorrences("C:\\opt\\Projects\\sisgee\\sisgee", 
				"./src/test/resources/ExperimentFiles/TC01_Pattern.java");
		
		assertEquals(16, retorno.size());
	}
	
	@Test
	public void tc02() throws IOException {
		List<Node> retorno = View.searchOcorrences("C:\\opt\\Projects\\sisgee\\sisgee", 
				"./src/test/resources/ExperimentFiles/TC02_Pattern.java");
		
		assertEquals(13, retorno.size());
	}
	
	@Test
	public void tc03() throws IOException {
		List<Node> retorno = View.searchOcorrences("C:\\opt\\Projects\\sisgee\\sisgee", 
				"./src/test/resources/ExperimentFiles/TC03_Pattern.java");
		
		assertEquals(13, retorno.size());
	}
	
	//C:\\opt\\Projects\\sisgee\\Documents\\NetBeansProjects\\sisgee\\sisgee\\src\\main\\java\\br\\cefetrj\\sisgee\\view\\termoaditivo\\VerTermoAditivoServlet.java
	
	@Test
	public void pdm() throws IOException {
		
		List<Node> retorno = View.searchOcorrences("./src/test/resources/AceitacaoFiles/ParseIntCheck.java", 
				"./src/test/resources/AceitacaoFiles/ParseInt_Pattern.java");
		
		assertEquals(1, retorno.size());
		assertEquals(6, retorno.get(0).getStartLine());
 		assertEquals(28, retorno.get(0).getStartColumn());
		assertEquals(6, retorno.get(0).getEndLine());
		assertEquals(54, retorno.get(0).getEndColumn());
	}
}
