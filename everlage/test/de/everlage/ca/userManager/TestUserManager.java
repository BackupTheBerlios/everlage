/*
 * Created on Feb 28, 2003
 * File TestUserManager.java
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
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;

import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestUserManager extends TestCase {

	private long agentSessionID;
	private TestUA testUA;
	private UALoginResult uaLoginRes;

	/**
	 * Constructor for TestUserManager.
	 * @param arg0
	 */
	public TestUserManager(String arg0) {
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

	public void testAnonymousLoginAllOK() {
		try {
			long userID =
				this.testUA.getUserManager().anonymousLogin(
					this.uaLoginRes.userAgentID,
					this.uaLoginRes.caSessionID);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (AnonymousLoginNotPossible e) {
			fail(e.getMessage());
		}
	}

	public void testAnonymousLoginAgentIDFalse() {
		try {
			long userID = this.testUA.getUserManager().anonymousLogin(1000, this.uaLoginRes.caSessionID);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			assertTrue(true);
		} catch (AnonymousLoginNotPossible e) {
			fail(e.getMessage());
		}
	}

	public void testAnonymousLoginCASessionIDFalse() {
		try {
			long userID = this.testUA.getUserManager().anonymousLogin(this.uaLoginRes.userAgentID, 1000);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			assertTrue(true);
		} catch (AnonymousLoginNotPossible e) {
			fail(e.getMessage());
		}
	}

}
