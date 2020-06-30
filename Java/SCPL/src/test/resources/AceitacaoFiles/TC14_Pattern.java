@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(){
        //not_exists
        any someVariable = null;

        // Alert:
        someVariable.anyMethod(any);
    }
}