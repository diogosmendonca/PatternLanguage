

public class someSingleton {
	
	private static someSingleton instance;
	
	@AlertIfNotPrivate("A singleton constructor should be private") 
	private someSingleton() {}
	
	public static someSingleton getInstance() {
		
	}

}

