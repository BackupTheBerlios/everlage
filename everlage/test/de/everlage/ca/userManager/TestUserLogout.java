/*
 * Created on Mar 5, 2003
 * File TestUserLogout.java
 * 
 */
package de.everlage.ca.userManager;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.TestUA;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.exception.extern.UserAlradyLoggedOutException;
import de.everlage.ca.userManager.exception.extern.UserNotExistsException;
import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestUserLogout extends TestCase {

	private long agentSessionID;
	private TestUA testUA;
	private UALoginResult uaLoginRes;

	/**
	 * Constructor for TestUserLogout.
	 * @param arg0
	 */
	public TestUserLogout(String arg0) {
		super(arg0);
		this.agentSessionID = new Random().nextLong();
		try {
			this.testUA = new TestUA();
			testUA.init();
		} catch (Exception e) {
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
		pstmt.setLong(3, agentSessionID);
		pstmt.setString(4, TestGlobal.uaRMIAddress);
		pstmt.setString(5, "TestUA");
		pstmt.setString(6, "test");
		pstmt.setBoolean(7, false);
		pstmt.executeUpdate();
		pstmt.close();
		pstmt =
			con.prepareStatement(
				"INSERT INTO SYSTEMUSER (userID, registrationDate, agentID,"
					+ "numberofresults, timeout, frozen) VALUES(?,?,?,?,?,?)");
		pstmt.setLong(1, 1);
		pstmt.setLong(2, 0);
		pstmt.setLong(3, 1);
		pstmt.setLong(4, 0);
		pstmt.setLong(5, 0);
		pstmt.setBoolean(6, false);
		pstmt.executeUpdate();
		pstmt.close();
		pstmt = con.prepareStatement("INSERT INTO SINGLEUSER (userID, isLoggedIn) VALUES(?,?)");
		pstmt.setLong(1, 1);
		pstmt.setBoolean(2, true);
		pstmt.executeUpdate();
		pstmt.close();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		uaLoginRes =
			testUA.getComponentManager().UALogin(
				"TestUA",
				"test",
				TestGlobal.uaRMIAddress,
				this.agentSessionID);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt = con.prepareStatement("DELETE FROM SingleUser");
		pstmt.executeUpdate();
		pstmt = con.prepareStatement("DELETE FROM SystemUser");
		pstmt.executeUpdate();
		pstmt = con.prepareStatement("DELETE FROM AGENT");
		pstmt.executeUpdate();
		con.commit();

		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		this.testUA.getComponentManager().UALogout(
			this.uaLoginRes.userAgentID,
			this.uaLoginRes.caSessionID);
	}

	public void testUserLogoutAllOK() {
		try {
			testUA.getUserManager().userLogout(1, this.uaLoginRes.caSessionID, 1);
			assertTrue(true);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (UserAlradyLoggedOutException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (UserNotExistsException e) {
			fail(e.getMessage());
		}
	}

	public void testUserAlradyLoggedOut() throws Exception {
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt =
			con.prepareStatement("UPDATE SINGLEUSER SET isLoggedIn=? WHERE userID=?");
		pstmt.setBoolean(1, false);
		pstmt.setLong(2, 1);
    pstmt.executeUpdate();
    con.commit();
    TestGlobal.dbMediator.freeConnection(con);
		try {
			testUA.getUserManager().userLogout(1, this.uaLoginRes.caSessionID, 1);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (UserAlradyLoggedOutException e) {
			assertTrue(true);
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (UserNotExistsException e) {
			fail(e.getMessage());
		}
	}

	public void testUserNotExists() {
		try {
			testUA.getUserManager().userLogout(1, this.uaLoginRes.caSessionID, 1000);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (UserAlradyLoggedOutException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (UserNotExistsException e) {
			assertTrue(true);
		}
	}

}
