@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        String someVariable;
		not:{
            someVariable = any;
        }
        someVariable.any();
    }
}