package br.scpl.designpatterns.singleton;

public class NotReturnInstanceSingleton {
	
	private static NotReturnInstanceSingleton instance;
	
	private NotReturnInstanceSingleton() {}
	
	public static NotReturnInstanceSingleton getInstance() {
		if(instance == null)
			instance = new NotReturnInstanceSingleton();
		return null;
	}

}
