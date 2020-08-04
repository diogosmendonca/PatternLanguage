package br.scpl.designpatterns.singleton;

public class PrivateGetInstanceSingleton {
	
	private static PrivateGetInstanceSingleton instance;
	
	private PrivateGetInstanceSingleton() {}
	
	private static PrivateGetInstanceSingleton getInstance() {
		if(instance == null)
			instance = new PrivateGetInstanceSingleton();
		return instance;
	}

}
