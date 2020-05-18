package scpl;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.scpl.view.Main;

@Ignore
public class CLITest {
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	
	@Test
	public void tc01() {
		
		String[] args = new String[0];
		
		Main.main(args);
		
		Assert.assertTrue(outContent.toString().contains("Error: Missing parameters"));
		Assert.assertTrue(outContent.toString().contains("Usage:"));
		
	}
	
	@Test
	public void tc02() {
		
		String[] args = { "--help"};
		
		Main.main(args);
		
		Assert.assertTrue(outContent.toString().contains("Usage:"));
	}
	
	@Test
	public void tc03() {
		
		String[] args = { "search", "-c", "./src/test/resources/AceitacaoFiles/TC66_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC66_Pattern.java", "--charset", "UTF-8" };
		
		Main.main(args);
		
		Assert.assertTrue(outContent.toString().contains("Return size:"));
	}
	
	@Test
	public void tc04() {
		
		String[] args = { "search", "-c", "./src/test/a/", "-p", "./src/test/resources/AceitacaoFiles/TC66_Pattern.java", "--charset", "UTF-8" };
		
		Main.main(args);
		
		//Assert.assertTrue(outContent.toString().contains("Return size:"));
	}

}
