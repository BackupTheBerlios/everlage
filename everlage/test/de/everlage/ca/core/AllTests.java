/*
 * Created on Feb 26, 2003
 * File AllTests.java
 * 
 */
package de.everlage.ca.core;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage.ca.core");
		//$JUnit-BEGIN$
		//suite.addTest(new TestSuite(CentralAgentTest.class));
    suite.addTest(de.everlage.ca.core.db.AllTests.suite());
		//$JUnit-END$
		return suite;
	}
}
