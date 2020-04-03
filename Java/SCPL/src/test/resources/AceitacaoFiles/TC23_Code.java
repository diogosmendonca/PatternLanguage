public class SourceCode{
    public static void run() {
        String a;
        a = teste();
        
        try{
            a.hashCode();         
        }catch(Exception e){
            System.out.println(e);
        }
    }
}