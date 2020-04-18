public class ParseIntCheck {
   
    public void method(){
        
        String intParam = "abc";
        Integer intValue = Integer.parseInt(intParam);  // Noncompliant {{Surrond with try/catch}}
        
        try{
            intValue = Integer.parseInt(intParam);
        }catch(Exception e){
            
        }
        
    }
    
}
