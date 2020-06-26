@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        //Alert: Teste
        String someVariable;
        //Alert: Teste
        someVariable = anyMethod();

		not_exists:
        try{
            exists:{
                //Alert: Teste
                someVariable.anyMethod();
            }
        }catch(Exception e){
                
        }
    }
}