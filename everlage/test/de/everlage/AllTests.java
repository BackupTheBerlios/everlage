/*
 * Created on Feb 21, 2003
 * File AllTests.java
 * 
 */
package de.everlage;

import java.io.FileInputStream;
import java.util.Properties;

import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.db.DBMediator;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author waffel
 */
public class AllTests {

	private Properties props;

	public static void main(String[] args) {
		System.out.println("start tests");
		junit.swingui.TestRunner.run(de.everlage.AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.everlage");
		//$JUnit-BEGIN$
		try {
			Properties props = new Properties();
			FileInputStream in =
				new FileInputStream(AllTests.class.getResource("test-database.properties").getFile());
			props.load(in);
			in.close();
			TestGlobal.dbDriverStr =
				(String) CAGlobal.dbDrivers.get(props.getProperty("dbSystem").toUpperCase());
			TestGlobal.dbURLStr =
				(String) CAGlobal.dbUrls.get(props.getProperty("dbSystem").toUpperCase())
					+ props.getProperty("dbDatabase");
			TestGlobal.dbLoginStr = props.getProperty("dbLogin");
			TestGlobal.dbPasswordStr = props.getProperty("dbPassword");
			TestGlobal.conNumber = props.getProperty("conNumber");
      System.out.println("TestGlobal setting:"+ TestGlobal.dbDriverStr);
			TestGlobal.dbMediator =
				new DBMediator(
					TestGlobal.dbDriverStr,
					TestGlobal.dbURLStr,
					TestGlobal.dbLoginStr,
					TestGlobal.dbPasswordStr,
					TestGlobal.conNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
    System.out.println("addtest");
		suite.addTest(de.everlage.ca.AllTests.suite());
		//suite.addTest(de.everlage.ua.minimal.html.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
