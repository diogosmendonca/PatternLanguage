
@AlertIfNotPrivate("ruleid=10, type=BUG, severity=MAJOR, message = msg1")
@AlertIfNotAbstract("ruleid=11, type=BUG, severity=MAJOR, message = msg2")
private abstract class SourceCode{
    public static void run() {
        System.out.println("Hello Nico");
    }
}