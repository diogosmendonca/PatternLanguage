public class SomeSingleton { 

    @AlertIfNotPrivate("ruleid=101, type=BUG, severity=MAJOR, message=Instance should be private") 
    @AlertIfNotStatic("ruleid=102, type=BUG, severity=MAJOR, message=Instance should be static") 
    private static SomeSingleton instance; 

    @AlertIfNotPrivate("ruleid=103, type=BUG, severity=MAJOR, message=A singleton constructor should be private")       
    private SomeSingleton() {}

    @AlertIfNotPublic("ruleid=104, type=BUG, severity=MAJOR, message=Method should be public")  
    @AlertIfNotStatic("ruleid=105, type=BUG, severity=MAJOR, message=Method should be static")
    public static SomeSingleton getInstance() {
        //#BEGIN

        //CORRECT CASE
        if(instance == null)
            instance = new someSingleton();
        return instance;

        //#OR

        //not_exists
        if(instance == null)
            //Alert(ruleid=106, type=BUG, severity=MAJOR, message=Instance must be returned)
            instance = new SomeSingleton();

        //#OR

        //Alert(ruleid=107, type=BUG, severity=MAJOR, message=Instance must be returned)
        instance = null;

        //#OR

        //Alert(ruleid=108, type=BUG, severity=MAJOR, message=Instance must be returned)
        return null;
 
        //#END
    }
}