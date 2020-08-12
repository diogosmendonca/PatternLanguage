

public class someSingleton {
	
	//Alert: Instance should be private
	@NotPrivate
	private static someSingleton instance;
	
	private someSingleton() {}
	
	public static someSingleton getInstance() {
		if(instance == null)
			instance = new someSingleton();
		return instance;
	}

}

