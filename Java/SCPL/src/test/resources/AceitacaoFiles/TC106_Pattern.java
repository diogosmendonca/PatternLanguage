public class AnyClass{
    //Alert: Do not declare specific Apps (AppA or AppB) use the App instead.
    //#BEGIN
    AppA anyVariable;
    //#OR
    AppA anyVariable = null;
    //#OR
    AppA anyVariable = new AppA();
    //#OR
    AppB anyVariable;
    //#OR
    AppB anyVariable = null;
    //#OR
    AppB anyVariable = new AppB();
    //#END
}