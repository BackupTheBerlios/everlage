/*
 * Created on Mar 5, 2003
 * File AllTests.java
 * 
 */
package de.everlage.ca;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests extends de.everlage.AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage.ca");
		//$JUnit-BEGIN$
    suite.addTest(de.everlage.ca.core.AllTests.suite());
    suite.addTest(de.everlage.ca.componentManager.AllTests.suite());
    suite.addTest(de.everlage.ca.userManager.AllTests.suite());
		//$JUnit-END$
		return suite;
	}
}
