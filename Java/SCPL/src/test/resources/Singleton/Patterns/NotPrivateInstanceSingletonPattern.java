

public class someSingleton {
	
	@AlertIfNotPrivate("Instance should be private")
	private static someSingleton instance;
	
	private someSingleton() {}
	
	public static someSingleton getInstance() {
		if(instance == null)
			instance = new someSingleton();
		return instance;
	}

}

