package br.scpl.view;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

import br.scpl.controller.SearchController;

@Parameters(commandDescription = "Shows the version of the application")
public class Version extends JCommander implements Command<String>{
	
	private static Logger log = Logger.getLogger(Version.class);
	
	@Override
	public String execute(JCommander jc) throws IOException {
		
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
		String version = properties.getProperty("version");
		
		log.info(version);
		
		return version;
	}

}