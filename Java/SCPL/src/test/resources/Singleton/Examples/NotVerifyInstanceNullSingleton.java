package br.scpl.designpatterns.singleton;

public class NotVerifyInstanceNullSingleton {
	
	private static NotVerifyInstanceNullSingleton instance;
	
	private NotVerifyInstanceNullSingleton() {}
	
	public static NotVerifyInstanceNullSingleton getInstance() {
		if(instance != null)
			instance = new NotVerifyInstanceNullSingleton();
		return instance;
	}

}
