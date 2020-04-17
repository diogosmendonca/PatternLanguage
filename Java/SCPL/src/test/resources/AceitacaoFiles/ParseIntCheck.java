/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 09513592740
 */
public class ParseIntCheck {
   
    public void method(){
        
        String intParam = "abc";
        Integer intValue = Integer.parseInt(intParam);  // Noncompliant {{Surrond with try/catch}}
        
        
        intValue = Integer.parseInt(intParam);
        
            
        
    }
    
}
