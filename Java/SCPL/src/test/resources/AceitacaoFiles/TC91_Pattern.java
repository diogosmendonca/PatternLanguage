
@AlertIfNotPrivate("ruleid=10, type=BUG, severity=MAJOR, message =a msg")
private class SourceCode{
    public static void run() {
        System.out.println("Hello Nico");
    }
}