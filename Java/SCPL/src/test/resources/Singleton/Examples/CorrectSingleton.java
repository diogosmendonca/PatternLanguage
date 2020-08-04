package br.scpl.designpatterns.singleton;

public class CorrectSingleton {
	
	private static CorrectSingleton instance;
	
	private CorrectSingleton() {}
	
	public static CorrectSingleton getInstance() {
		if(instance == null)
			instance = new CorrectSingleton();
		return instance;
	}

}
