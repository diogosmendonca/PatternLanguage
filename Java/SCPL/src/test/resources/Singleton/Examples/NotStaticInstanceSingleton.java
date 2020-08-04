package br.scpl.designpatterns.singleton;

public class NotStaticInstanceSingleton {
	
	private NotStaticInstanceSingleton instance;
	
	private NotStaticInstanceSingleton() {}
	
	public NotStaticInstanceSingleton getInstance() {
		if(instance == null)
			instance = new NotStaticInstanceSingleton();
		return instance;
	}

}
