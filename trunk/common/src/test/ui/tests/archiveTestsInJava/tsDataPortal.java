import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class tsDataPortal {

	public static Test suite(Class<TestCase> testClass) {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(testClass);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite(tcDataPortal.class));
	}
}
