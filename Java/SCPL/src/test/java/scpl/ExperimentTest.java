package scpl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

import br.scpl.model.Node;
import br.scpl.view.View;

public class ExperimentTest {
	
	private static final ResourceBundle config = ResourceBundle.getBundle("config");
	private final static String pathSisgee = config.getString("pathSisgee");
	
	//Scope
	@Test
	public void tc01() throws IOException {
		List<Node> retorno = View.searchOcorrences(pathSisgee, 
				"./src/test/resources/ExperimentFiles/TC01_Pattern.java");
		
		assertEquals(16, retorno.size());
	}
	
	//Ctx01
	@Test
	public void tc02() throws IOException {
		List<Node> retorno = View.searchOcorrences(pathSisgee, 
				"./src/test/resources/ExperimentFiles/TC02_Pattern.java");
		
		assertEquals(3, retorno.size());
	}
	
	//Ctx02
	@Test
	public void tc03() throws IOException {
		List<Node> retorno = View.searchOcorrences(pathSisgee,
				"./src/test/resources/ExperimentFiles/TC03_Pattern.java");
		assertEquals(9, retorno.size());
	}
	
	//Template
	@Test
	public void tc05() throws IOException {
		List<Node> retorno = View.searchOcorrences(pathSisgee,
				"./src/test/resources/ExperimentFiles/Template");
		assertEquals(4, retorno.size());
	}
	
}
