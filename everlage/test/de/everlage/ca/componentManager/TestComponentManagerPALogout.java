/*
 * Created on Feb 27, 2003
 * File TestComponentManagerPALogout.java
 * 
 */
package de.everlage.ca.componentManager;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

import junit.framework.TestCase;

/**
 * @author waffel
 */
public class TestComponentManagerPALogout extends TestCase {

	private TestPA testPA;
	private long agentID;
	private PALoginResult paRes;

	/**
	 * Constructor for TestComponentManagerPALogout.
	 * @param arg0
	 */
	public TestComponentManagerPALogout(String arg0) {
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
		pstmt.setString(4, TestGlobal.uaRMIAddress);
		pstmt.setString(5, "TestPA");
		pstmt.setString(6, "test");
		pstmt.setBoolean(7, true);
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		this.paRes =
			testPA.getComponentManager().PALogin("TestPA", "test", TestGlobal.paRMIAddress, this.agentID);
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
		this.paRes = null;
		// sicherheitshalber den UA wieder versuchen auszuloggen
		try {
			testPA.getComponentManager().PALogout(this.paRes.providerAgentID, this.paRes.caSessionID);
		} catch (Exception e) {
		}
	}

	public void testPALogoutALLOk() {
		try {
			testPA.getComponentManager().PALogout(paRes.providerAgentID, paRes.caSessionID);
			assertTrue(true);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
			fail(e.getMessage());
		} catch (RemoteException e) {
			fail(e.getMessage());
		}
	}

	public void testPALogoutAgentIDFalse() {
		try {
			testPA.getComponentManager().PALogout(1000, paRes.caSessionID);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
      assertTrue(true);
		} catch (RemoteException e) {
			fail(e.getMessage());
		}
	}

	public void testPALogoutCASessionIDFalse() {
		try {
			testPA.getComponentManager().PALogout(paRes.providerAgentID, 0);
			assertTrue(false);
		} catch (InternalEVerlageError e) {
			fail(e.getMessage());
		} catch (InvalidAgentException e) {
      assertTrue(true);
		} catch (RemoteException e) {
			fail(e.getMessage());
		}
	}
  
  public void testPALogoutDouble() {
    try {
      testPA.getComponentManager().PALogout(this.paRes.providerAgentID, this.paRes.caSessionID);
      testPA.getComponentManager().PALogout(this.paRes.providerAgentID, this.paRes.caSessionID);
      assertTrue(false);
    } catch (InternalEVerlageError e) {
      fail(e.getMessage());
    } catch (InvalidAgentException e) {
      assertTrue(true);
    } catch (RemoteException e) {
      fail(e.getMessage());
    }
  }

}
