/*
 * Created on Feb 28, 2003
 * File AllTests.java
 * 
 */
package de.everlage.ca.userManager;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage.ca.userManager");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TestUserManager.class));
    suite.addTest(new TestSuite(TestUserLogout.class));
		//$JUnit-END$
		return suite;
	}
}
