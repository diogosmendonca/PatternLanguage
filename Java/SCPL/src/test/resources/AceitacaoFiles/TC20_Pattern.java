public class AnyClass {
    void anyMethod() {
        String someVariable;
		not:{
            someVariable = "anyLiteralValue";
        }
        someVariable.anyMethod();
    }
}