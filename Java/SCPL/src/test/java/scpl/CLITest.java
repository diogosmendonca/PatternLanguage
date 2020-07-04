package scpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import br.scpl.view.Main;
public class CLITest {
	
	private static ByteArrayOutputStream outContent;
	private static ByteArrayOutputStream errContent;
	private static PrintStream originalOut;
	private static PrintStream originalErr;
	
	@BeforeClass
	public static void setUpStreams() {
		originalOut = System.out;
		originalErr = System.err;
		
		outContent = new ByteArrayOutputStream();
		errContent = new ByteArrayOutputStream();
		//true, autoFlush
	    System.setOut(new PrintStream(outContent,false));
	    System.setErr(new PrintStream(errContent,false));
	    
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
        
        System.out.flush();
		System.err.flush();
		
		Main.main(args);
		
        Assert.assertTrue(outContent.toString().contains("Usage:"));
	}
	
	@Test
	public void tc03() throws IOException {
		
        String[] args = { "--version"};
		
        System.out.flush();
		System.err.flush();
        
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("Version:"));
	}

	@Test
	public void tc04() throws IOException {
		
        String[] args = { "--verbose"};
		
		Main.main(args);
		
        Assert.assertTrue(outContent.toString().contains("Parameters: [--verbose]"));
	}
	
	@Test
	public void tc05() throws IOException {
		
        String[] args = { "--verbose","search", "-c", "./src/test/resources/AceitacaoFiles/TC01_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC01_Pattern.java", "--charset", "UTF-8", "-f", "eclipse" };
		
		Main.main(args);
        
        Assert.assertTrue(outContent.toString().contains("Error: Unknow format"));
	}
	
	@Test
	public void tc06() throws IOException {
        
        String[] args = { "search", "-c", "./src/test/resources/AceitacaoFiles/TC40_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC40_Pattern.java", "--charset", "UTF-8", "-f", "sonarqube" };
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("issues"));
	}
	
	@Test
	public void tc07() throws IOException {
        
        String[] args = { "search", "-c", "./src/test/resources/AceitacaoFiles/TC01_Code.java", "-p", "./src/test/resources/AceitacaoFiles/TC01_Pattern.java", "-f", "sonarqube" };
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("An alert comment was not found for the node"));
	}
	
	@Test
	public void tc08() throws IOException {
        
		final RandomAccessFile raFile = new RandomAccessFile("./target/classes/application.properties", "rw");
		raFile.getChannel().lock();
		
        String[] args = { "--version"};
		
		Main.main(args);

        Assert.assertTrue(outContent.toString().contains("Error:"));
	}
	
	@Test
	public void tc09() throws IOException {
		
        String[] args = null;
		
		Main.main(args);
		
		Assert.assertTrue(outContent.toString().contains("Error: Missing parameters"));
        Assert.assertTrue(outContent.toString().contains("Usage:"));
	}

}
