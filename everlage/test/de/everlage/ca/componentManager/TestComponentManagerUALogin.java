/*
 * Created on Feb 21, 2003
 * File TestComponentManagerUALogin.java
 * 
 */
package de.everlage.ca.componentManager;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownAgentException;
import de.everlage.ca.exception.extern.InternalEVerlageError;

import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestComponentManagerUALogin extends TestCase {

	private TestUA testUA;
	private long agentID;

	/**
	 * Constructor for TestComponentManagerUALogin.
	 * @param arg0
	 */
	public TestComponentManagerUALogin(String arg0) {
		super(arg0);
		try {
			agentID = new Random().nextLong();
			testUA = new TestUA();
			testUA.init();
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
				"INSERT INTO AGENT (id, agentID, caSessionID, agentSessionID, addressrmi, name, password,"
					+ "isProviderAgent) VALUES(?,?,?,?,?,?,?)");
		pstmt.setLong(1, 1);
		pstmt.setLong(2, 1);
		pstmt.setLong(3, 0);
		pstmt.setLong(4, agentID);
		pstmt.setString(5, TestGlobal.uaRMIAddress);
		pstmt.setString(6, "TestUA");
		pstmt.setString(7, "test");
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

	public void testUALoginAllOK() {
		try {
			UALoginResult res =
				testUA.getComponentManager().UALogin(
					"TestUA",
					"test",
					TestGlobal.uaRMIAddress,
					this.agentID);
			assertNotNull(res);
      assertEquals(1, res.userAgentID);
		} catch (UnknownAgentException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidPasswordException e) {
			fail(e.getMessage());
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}

}
