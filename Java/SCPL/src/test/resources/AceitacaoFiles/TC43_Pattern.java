@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {

        String someVariable;
        
        someVariable = null;

        //not-exists
        someVariable = any;
        
        //alert:Teste        	
        someVariable.anyMethod();

    }
}