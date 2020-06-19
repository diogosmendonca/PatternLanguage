@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        
		not_exists:{
            if(true){
                //exists
                String someVariable = any;
                //exists   
                someVariable.anyMethod();
            }
        }
    }
}