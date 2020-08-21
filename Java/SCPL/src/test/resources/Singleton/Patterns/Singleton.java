public class SomeSingleton {

    @AlertIfNotPrivate @AlertIfNotStatic  
    private static SomeSingleton instance;
   
    @AlertIfNotPrivate       
    private SomeSingleton() {}

    @AlertIfNotPublic @AlertIfNotStatic
    public static SomeSingleton getInstance() {
        //#BEGIN

        //not_exists
        if(instance == null)
            //Exists (Alert: Instance must be verified for null)
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