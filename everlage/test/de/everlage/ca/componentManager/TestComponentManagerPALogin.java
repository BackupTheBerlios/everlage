/*
 * Created on Feb 27, 2003
 * File TestComponentManagerPALogin.java
 * 
 */
package de.everlage.ca.componentManager;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownAgentException;
import de.everlage.ca.exception.extern.InternalEVerlageError;

import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestComponentManagerPALogin extends TestCase {

	private TestPA testPA;
	private long agentID;

	/**
	 * Constructor for TestComponentManagerPALogin.
	 * @param arg0
	 */
	public TestComponentManagerPALogin(String arg0) {
		super(arg0);
		try {
			agentID = new Random().nextLong();
			testPA = new TestPA();
			testPA.init();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt =
			con.prepareStatement(
				"INSERT INTO AGENT (agentID, caSessionID, agentSessionID, addressrmi, name, password,"
					+ "isProviderAgent) VALUES(?,?,?,?,?,?,?)");
		pstmt.setLong(1, 1);
		pstmt.setLong(2, 0);
		pstmt.setLong(3, agentID);
		pstmt.setString(4, TestGlobal.paRMIAddress);
		pstmt.setString(5, "TestPA");
		pstmt.setString(6, "test");
		pstmt.setBoolean(7, true);
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt = con.prepareStatement("DELETE FROM AGENT");
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
	}

	public void testPALoginAllOK() {
		try {
			PALoginResult res =
				testPA.getComponentManager().PALogin(
					"TestPA",
					"test",
					TestGlobal.paRMIAddress,
					this.agentID);
			assertNotNull(res);
			assertEquals(res.providerAgentID, 1);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidPasswordException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (UnknownAgentException e) {
			fail(e.getMessage());
		}
	}

	public void testPALoginNameFalse() {
		try {
			PALoginResult res =
				testPA.getComponentManager().PALogin(
					"false",
					"test",
					TestGlobal.paRMIAddress,
					this.agentID);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidPasswordException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (UnknownAgentException e) {
			assertTrue(true);
		}
	}

	public void testPALoginPasswordFalse() {
		try {
			PALoginResult res =
				testPA.getComponentManager().PALogin(
					"TestPA",
					"false",
					TestGlobal.paRMIAddress,
					this.agentID);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidPasswordException e) {
			assertTrue(true);
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (UnknownAgentException e) {
			fail(e.getMessage());
		}
	}

	public void testPALoginRMIFalse() {
		try {
			PALoginResult res =
				testPA.getComponentManager().PALogin("TestPA", "test", "//129.0.0.0/", this.agentID);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			assertTrue(true);
		} catch (InvalidPasswordException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (UnknownAgentException e) {
			fail(e.getMessage());
		}
	}

	public void testPADoubleLogin() {
		try {
			PALoginResult res =
				testPA.getComponentManager().PALogin(
					"TestPA",
					"test",
					TestGlobal.paRMIAddress,
					this.agentID);
			res =
				testPA.getComponentManager().PALogin(
					"TestPA",
					"test",
					TestGlobal.paRMIAddress,
					this.agentID);
			assertTrue(true);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidPasswordException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (UnknownAgentException e) {
			fail(e.getMessage());
		}
	}

}
