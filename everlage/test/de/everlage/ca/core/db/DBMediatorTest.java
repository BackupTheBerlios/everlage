/*
 * Created on Feb 28, 2003
 * File DBMediatorTest.java
 * 
 */
package de.everlage.ca.core.db;

import java.sql.Connection;

import de.everlage.ca.exception.extern.InternalEVerlageError;
import junit.framework.TestCase;

/**
 * @author waffel
 */
public class DBMediatorTest extends TestCase {

	private final String dbDriver = "org.postgresql.Driver";
	private final String dbURL = "jdbc:postgresql:everlage";
	private final String dbLogin = "waffel";
	private final String dbPassword = "waffel";
	private final String conNumber = "1";
	private DBMediator dbM = null;
	private Connection con = null;

	/**
	 * Constructor for DBMediatorTest.
	 * @param arg0
	 */
	public DBMediatorTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (this.con != null) {
			if (dbM != null) {
				dbM.freeConnection(con);
			}
		}
		if (dbM != null) {
			dbM = null;
		}
	}

	public void testDBMediatorAllOK() {
		try {
			DBMediator dbM =
				new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, this.conNumber);
			assertNotNull(dbM);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}

	public void testDBMediatorDriverFalse() {
		try {
			new DBMediator("false", this.dbURL, this.dbLogin, this.dbPassword, this.conNumber);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		}
	}

	public void testDBMediatorURLFalse() {
		try {
			new DBMediator(this.dbDriver, "false", this.dbLogin, this.dbPassword, this.conNumber);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		}
	}

	public void testDBMediatorLoginFalse() {
		try {
			new DBMediator(this.dbDriver, this.dbURL, "false", this.dbPassword, this.conNumber);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		}
	}


	public void testDBMediatorConNumberFalse() {
		try {
			new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, "false");
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		}
	}

	public void testDBMediatorConNumberLessOne() {
		try {
			new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, "0");
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		}
	}

	public void testGetConnectionAllOk() {
		try {
			dbM =
				new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, this.conNumber);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
		try {
			con = dbM.getConnection();
			assertNotNull(con);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}

	public void testGetConnectionToManyCons() {
		try {
			dbM =
				new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, this.conNumber);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
		try {
			con = dbM.getConnection();
			Connection con2 = dbM.getConnection();
			assertTrue(false);
		} catch (InternalEVerlageError e) {
      assertTrue(true);
		}
  }

		public void testFreeConnectionAllOK() {
      try {
        dbM =
          new DBMediator(this.dbDriver, this.dbURL, this.dbLogin, this.dbPassword, this.conNumber);
        con = dbM.getConnection();
      } catch (InternalEVerlageError e) {
        fail(e.getMessage());
      }
      try {
        dbM.freeConnection(con);
        assertTrue(true);
      } catch (InternalEVerlageError e) {
        fail(e.getMessage());
      }
		}

	}
