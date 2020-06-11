@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {

        String someVariable;
        
        someVariable = null;

        //not_exists
        someVariable = any;
        
        //alert:Teste        	
        someVariable.anyMethod();

    }
}