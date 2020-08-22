public class SomeSingleton {

    @AlertIfNotPrivate("Instance should be private") 
    @AlertIfNotStatic("Instance should be static") 
    private static SomeSingleton instance;
   
    @AlertIfNotPrivate("A singleton constructor should be private")       
    private SomeSingleton() {}

    @AlertIfNotPublic @AlertIfNotStatic
    public static SomeSingleton getInstance() {
        //#BEGIN

        //Precisa ter um caso com a implementação correta, por causa das violações de modificadores
        if(instance == null)
			instance = new someSingleton();
		return instance;

        //#OR

        //not_exists
        if(instance == null)
            //Exists Alert: Instance must be verified for null
            instance = new SomeSingleton();

        //#OR

        //Alert: Instance must be instantiated)
        instance = null;

        //#OR

        // Alert: Instance must be returned
        return null;

        //#END
    }
}