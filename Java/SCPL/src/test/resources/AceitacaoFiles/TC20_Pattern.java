@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        //Alert: Teste
        String someVariable;
		not_exists:{
            someVariable = any;
        }
        //Alert: Teste
        someVariable.any();
    }
}