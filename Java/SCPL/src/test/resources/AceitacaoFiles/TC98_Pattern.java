public class SomeSingleton {

    @AlertIfNotStatic("Instance attribute should be static") 
    @AnyModifier
    SomeSingleton instance;
    
}