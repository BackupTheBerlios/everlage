/*
 * Created on Mar 12, 2003
 * File TestSearchSimple.java
 * 
 */
package de.everlage.ca.componentManager;

import java.rmi.Naming;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;

/**
 * @author waffel
 */
public class TestSearchSimple extends TestCase {

	private TestUA testUA;
	private TestPA testPA;
	private UALoginResult uaLoginRes;
	private PALoginResult paLoginRes;
	private long uaSessionID;
	private long paSessionID;
	private long caSessionID;

	public TestSearchSimple(String arg0) {
		super(arg0);
		try {
			this.uaSessionID = 1;
			this.paSessionID = 2;
			this.caSessionID = 0;

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			Naming.unbind(TestGlobal.uaRMIAddress);
			Naming.unbind(TestGlobal.paRMIAddress);
		} catch (Exception e) {
		}
		this.testUA = new TestUA();
		this.testPA = new TestPA();
		this.testUA.init();
		this.testPA.init();
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt =
			con.prepareStatement(
				"INSERT INTO AGENT (agentID, caSessionID, agentSessionID, addressrmi, name, password,"
					+ "isProviderAgent) VALUES(?,?,?,?,?,?,?)");
		pstmt.setLong(1, 1);
		pstmt.setLong(2, this.caSessionID);
		pstmt.setLong(3, this.uaSessionID);
		pstmt.setString(4, TestGlobal.uaRMIAddress);
		pstmt.setString(5, "TestUA");
		pstmt.setString(6, "test");
		pstmt.setBoolean(7, false);
		pstmt.executeUpdate();
		pstmt.setLong(1, 2);
		pstmt.setLong(3, this.paSessionID);
		pstmt.setString(4, TestGlobal.paRMIAddress);
		pstmt.setString(5, "TestPA");
		pstmt.setBoolean(7, true);
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		this.uaLoginRes =
			testUA.getComponentManager().UALogin(
				"TestUA",
				"test",
				TestGlobal.uaRMIAddress,
				this.uaSessionID);
		this.paLoginRes =
			testPA.getComponentManager().PALogin(
				"TestPA",
				"test",
				TestGlobal.paRMIAddress,
				this.paSessionID);
		this.caSessionID = this.uaLoginRes.caSessionID;
		this.testUA.setAgentID(this.uaSessionID);
		this.testUA.setCaSessionID(this.caSessionID);
		this.testPA.setAgentID(this.paSessionID);
		this.testPA.setCaSessionID(this.caSessionID);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Connection con = TestGlobal.dbMediator.getConnection();
		PreparedStatement pstmt = con.prepareStatement("DELETE FROM AGENT");
		pstmt.executeUpdate();
		con.commit();
		TestGlobal.dbMediator.freeConnection(con);
		pstmt = null;
		// sicherheitshalber den UA und PA wieder versuchen auszuloggen
		try {
			testUA.getComponentManager().UALogout(
				this.uaLoginRes.userAgentID,
				this.uaLoginRes.caSessionID);
			testPA.getComponentManager().PALogout(this.paSessionID, this.caSessionID);
		} catch (Exception e) {
		}
		this.uaLoginRes = null;
		this.paLoginRes = null;
		try {
			Naming.unbind(TestGlobal.uaRMIAddress);
      Naming.unbind(TestGlobal.paRMIAddress);
		} catch (Exception e) {
		}
		this.testUA = null;
		this.testPA = null;
	}

	public void testSearchAllOK() {
		try {
			this.testUA.search("waffel");
			Date date = new Date();
			long timeEnd = date.getTime() + 100;
			long currTime = 0;
			List answerList = this.testUA.getAnswerList();
			while ((answerList == null) && (currTime < timeEnd)) {
				currTime = new Date().getTime();
				answerList = this.testUA.getAnswerList();
			}
			assertNotNull(answerList);
      assertEquals(1, answerList.size());
      PAAnswerRecord paAnswer = (PAAnswerRecord)answerList.get(0);
      List documents = paAnswer.getDocumentInfo();
      assertEquals("waffels erstes Buch", (String)documents.get(0));
      assertEquals("waffels zweites Buch", (String)documents.get(1));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
