package br.scpl.util;

import java.io.IOException;
import java.util.Properties;

import br.scpl.view.CLIView;

public class ConfigUtils {
	
	private static Properties properties;
	
	private ConfigUtils() {}
	
	public static Properties getProperties() {
		if(properties == null) {
			Properties p = new Properties();
			try {
				p.load(CLIView.class.getClassLoader().getResourceAsStream("config.properties"));
				properties = p;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
}
