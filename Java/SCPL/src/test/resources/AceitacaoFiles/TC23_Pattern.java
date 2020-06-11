@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        String someVariable;

        someVariable = anyMethod();

		not_exists:
        try{
            exists:{
                someVariable.anyMethod();
            }
        }catch(Exception e){
                
        }
    }
}