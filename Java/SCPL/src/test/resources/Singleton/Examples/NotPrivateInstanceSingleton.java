package br.scpl.designpatterns.singleton;

public class NotPrivateInstanceSingleton {
	
	protected static NotPrivateInstanceSingleton instance;
	
	private NotPrivateInstanceSingleton() {}
	
	public static NotPrivateInstanceSingleton getInstance() {
		if(instance == null)
			instance = new NotPrivateInstanceSingleton();
		return instance;
	}

}
