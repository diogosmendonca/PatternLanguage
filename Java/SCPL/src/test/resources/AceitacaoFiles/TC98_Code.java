public class MySingleton 
{
    private MySingleton instance;
 
    private MySingleton(){
 
    }
 
    public static MySingleton getInstance(){
        if(instance == null)
            instance = new MySingleton();
        return null;
    }
}