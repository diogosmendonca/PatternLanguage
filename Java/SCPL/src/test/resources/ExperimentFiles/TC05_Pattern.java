@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {

      //not-exists
      someVariable = ValidaUtils.validaInteger(any, someVariable2);
      
      not:
      try{
        if(someVariable.trim().isEmpty()){

            //alert: defect
            Integer.parseInt(someVariable2);
        }
      }catch(Exception any){
                  
      }
    }
}