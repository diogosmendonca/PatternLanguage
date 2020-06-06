package scpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.scpl.view.Main;

public class CLITest {
	
	private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	
	@Before
	public void setUpStreams() {
		outContent = new ByteArrayOutputStream();
		errContent = new ByteArrayOutputStream();
		//true, autoFlush
	    System.setOut(new PrintStream(outContent,true));
	    System.setErr(new PrintStream(errContent,true));
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	
	@Test
	public void tc01() throws IOException {
		
        String[] args = new String[0];
		
		Main.main(args);
		
        Assert.assertTrue(outContent.toString().contains("Error: Missing parameters"));
        Assert.assertTrue(outContent.toString().contains("Usage:"));
	}
	
	@Test
	public void tc02() throws IOException {
		
        String[] args = { "--help"};
		
		Main.main(args);
		
        Assert.assertTrue(outContent.toString().contains("Usage:"));
	}
	
	@Test
	public void tc03() throws IOException {
		
        String[] args = { "--version"};
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("1.0"));
	}
	
	@Test
	public void tc04() throws IOException {
		
        String[] args = { "--verbose"};
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("Parameters: [--verbose]"));
	}
	
	
	@Test
	public void tc05() throws IOException {
		
        String[] args = { "--verbose","search", "-c", "./src/test/resources/AceitacaoFiles/TC40_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC40_Pattern.java", "--charset", "UTF-8", "-f", "eclipse" };
		
		Main.main(args);
        
        Assert.assertTrue(outContent.toString().contains("Error: Unknow format"));
	}
	
	
	
	@Test
	public void tc06() throws IOException {
        
        String[] args = { "search", "-c", "./src/test/resources/AceitacaoFiles/TC40_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC40_Pattern.java", "--charset", "UTF-8", "-f", "sonarqube" };
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("issues"));
	}
	

}
