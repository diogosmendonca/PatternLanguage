@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        String someVariable;

        someVariable = anyMethod();

		not:{
            try{
                exists:{
                    someVariable.anyMethod();
                }
            }catch(Exception e){
                
            }
        }
    }
}