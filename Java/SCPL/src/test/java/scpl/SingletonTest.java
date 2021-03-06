package scpl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import br.scpl.model.Node;
import br.scpl.view.Search;

public class SingletonTest {

	
	@Test
	public void correctSingletonTest() {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/Singleton/Examples/CorrectSingleton.java", 
				"./src/test/resources/Singleton/Patterns/CorrectSingleton.java");
		
		assertEquals(1, retorno.size());
	}
	
	@Test
	public void notPrivateConstructorSingletonTest() {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/Singleton/Examples/NotPrivateConstructorSingleton.java", 
				"./src/test/resources/Singleton/Patterns/NotPrivateConstructorSingletonPattern.java");
		
		assertEquals(1, retorno.size());
	}

	

	@Test
	public void notPrivateInstanceSingletonTest() {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/Singleton/Examples/NotPrivateInstanceSingleton.java", 
				"./src/test/resources/Singleton/Patterns/NotPrivateInstanceSingletonPattern.java");
		
		assertEquals(1, retorno.size());
	}
	
	@Test
	public void singletonTest() {
		
		List<Node> retorno = Search.searchOccurrences("./src/test/resources/Singleton/Examples", 
				"./src/test/resources/Singleton/Patterns/Singleton.java");
		
		assertEquals(8, retorno.size());
		//FIXME Falso positivo
	}

	
}
