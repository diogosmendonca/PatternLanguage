

public class someSingleton {
	
	private static someSingleton instance;
	
	//Alert: A singleton constructor should be private 
	@NotPrivate 
	private someSingleton() {}
	
	public static someSingleton getInstance() {
		
	}

}

