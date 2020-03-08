public class AnyClass {
    void anyMethod() {
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