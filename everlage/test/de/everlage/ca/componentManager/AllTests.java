/*
 * Created on Feb 21, 2003
 * File AllTests.java
 * 
 */
package de.everlage.ca.componentManager;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage.ca.componentManager");
		//$JUnit-BEGIN$
    suite.addTest(new TestSuite(TestComponentManagerUALogin.class));
		//$JUnit-END$
		return suite;
	}
}
