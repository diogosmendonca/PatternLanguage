package br.scpl.designpatterns.singleton;

public class NotPrivateConstructorSingleton {
	
	private static NotPrivateConstructorSingleton instance;
	
	public NotPrivateConstructorSingleton() {}
	
	public static NotPrivateConstructorSingleton getInstance() {
		if(instance == null)
			instance = new NotPrivateConstructorSingleton();
		return instance;
	}

}
