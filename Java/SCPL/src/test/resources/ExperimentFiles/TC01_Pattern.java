@anyModifier
class anyClass {
    @anyModifier
    any anyMethod(anyType any) {
        //alert: Possible defect
        Integer.parseInt(any);
    }
}