//Alert: Create in the invoker an static attribute that maps strings to command for store the commands.
public class SomeInvoker{
    
    //#BEGIN 

    private static Map<String, Command> someVariableName;

    //#AND

    private static Map<String, Command> someVariableName = null;

    //#AND

    private static Map<String, Command> someVariableName = new AnyClass<>();

    //#AND

    private static Map<String, Command> someVariableName = new AnyClass<String, Command>();

    //#END

}