package br.scpl.util;

import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigUtils {
	
	private static Properties properties;
	
	private ConfigUtils() {}
		
	public static synchronized Properties getProperties() {
		if(properties == null) {
			Properties p = new Properties();
			
			ResourceBundle config = ResourceBundle.getBundle("config");
			p.setProperty("any", config.getString("any"));
			p.setProperty("some", config.getString("some"));
			p.setProperty("not", config.getString("not"));
			p.setProperty("exists", config.getString("exists"));
			p.setProperty("verbose", config.getString("verbose"));
			p.setProperty("debug", config.getString("debug"));
			
			properties = p;
		}
		return properties;
	}
}
