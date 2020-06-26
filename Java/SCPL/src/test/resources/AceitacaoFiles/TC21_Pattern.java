@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        
		not_exists:{
            if(true){
                exists:{
                    //Alert: Teste
                    String someVariable = any;
                    //Alert: Teste
                    someVariable.anyMethod();
                }
            }
        }
    }
}