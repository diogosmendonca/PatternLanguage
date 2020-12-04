public class SomeSingleton {
    
    //Alert(ruleid=107, type=BUG, severity=MAJOR, message=getInstance method should return instance attribute)
    public static SomeSingleton getInstance(){
 
        //not_exists
        return instance;
    }
 
}