public class SomeFacade{
   //Alert: a Business Object should be called in a Facade method.
    public static String diciplineSpecificService(){
        //not_exists
        return DiciplineBusinessObject.diciplineSpecificService();
    }
}