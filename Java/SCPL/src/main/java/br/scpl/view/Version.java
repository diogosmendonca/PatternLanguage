package br.scpl.view;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Shows the version of the application")
public class Version extends JCommander implements Command<String>{
	
	@Override
	public String execute(JCommander jc) throws IOException {
		
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
		String version = properties.getProperty("version");
		
		System.out.println(version);
		
		return version;
	}

}