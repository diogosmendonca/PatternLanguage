//Alert: Create in the invoker an static attribute that maps strings to command for store the commands.
public class SomeInvoker{
    
    //#BEGIN 

    //not_exists
    private static Map<String, Command> someVariableName;

    //#AND

    //not_exists
    private static Map<String, Command> someVariableName = null;

    //#AND

    //not_exists
    private static Map<String, Command> someVariableName = new AnyClass<>();

    //#AND

    //not_exists
    private static Map<String, Command> someVariableName = new AnyClass<String, Command>();

    //#END

}