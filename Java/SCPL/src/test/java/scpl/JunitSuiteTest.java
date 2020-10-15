package scpl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        /*CLITest.class,*/
        AceitacaoTest.class,
        SingletonTest.class
})
public class JunitSuiteTest {
	private JunitSuiteTest() {}
}
