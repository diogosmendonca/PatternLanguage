

public class someSingleton {
	
	private static someSingleton instance;
	
	//Alert: A singleton constructor should be private 
	@NotPrivate
	someSingleton() {}
	
	public static someSingleton getInstance() {
		
	}

}

