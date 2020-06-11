@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        String someVariable;
		not_exists:{
            someVariable = any;
        }
        someVariable.any();
    }
}