/*
 * Created on Feb 28, 2003
 * File AllTests.java
 * 
 */
package de.everlage.ca.core.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage.ca.core.db");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(DBMediatorTest.class));
		//$JUnit-END$
		return suite;
	}
}
