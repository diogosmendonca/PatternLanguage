@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        
		not:{
            if(true){
                exists:{
                    String someVariable = any;
                    someVariable.anyMethod();
                }
            }
        }
    }
}