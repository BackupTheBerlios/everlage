/*
 * Created on Feb 27, 2003
 * File TestComponentManagerUALogout.java
 * 
 */
package de.everlage.ca.componentManager;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestComponentManagerUALogout extends TestCase {

	private TestUA testUA;
	private long agentID;
	private UALoginResult uaRes;
	/**
	 * Constructor for TestComponentManagerUALogout.
	 * @param arg0
	 */
	public TestComponentManagerUALogout(String arg0) {
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
				"INSERT INTO AGENT (agentID, caSessionID, agentSessionID, addressrmi, name, password,"
					+ "isProviderAgent) VALUES(?,?,?,?,?,?)");
		pstmt.setLong(1, 1);
		pstmt.setLong(2, 0);
		pstmt.setLong(3, agentID);
		pstmt.setString(4, TestGlobal.uaRMIAddress);
		pstmt.setString(5, "TestUA");
		pstmt.setString(6, "test");
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		this.uaRes =
			testUA.getComponentManager().UALogin("TestUA", "test", TestGlobal.uaRMIAddress, this.agentID);
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
    this.uaRes=null;
	}

	public void testUALogoutAllOk() {
		try {
			testUA.getComponentManager().UALogout(this.uaRes.userAgentID, this.uaRes.caSessionID);
			assertTrue(true);
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}

	public void testUALogoutAgentIDFalse() {
		try {
			testUA.getComponentManager().UALogout(0, this.uaRes.caSessionID);
			assertTrue(false);
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			assertTrue(true);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}

	public void testUALogoutCASessionIDFalse() {
		try {
			testUA.getComponentManager().UALogout(this.uaRes.userAgentID, 1);
			assertTrue(false);
		} catch (RemoteException e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			assertTrue(true);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		}
	}
  
  public void testUALogoutDouble() {
    try {
      testUA.getComponentManager().UALogout(this.uaRes.userAgentID, this.uaRes.caSessionID);
      testUA.getComponentManager().UALogout(this.uaRes.userAgentID, this.uaRes.caSessionID);
      assertTrue(false);
    } catch (RemoteException e) {
      fail(e.getMessage());
    } catch (InvalidAgentException e) {
      assertTrue(true);
    } catch (InternalEVerlageError e) {
      fail(e.getMessage());
    }
  }

}
