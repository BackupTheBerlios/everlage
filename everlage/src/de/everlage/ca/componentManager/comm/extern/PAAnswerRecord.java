/*
 * Created on Mar 11, 2003
 * File PAAnswerRecord.java
 * 
 */
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;
import java.util.List;

/**
 * @author waffel
 */
public class PAAnswerRecord implements Serializable {

  private long questionID;
  private List documentInfo;
  private long paID;
  private long userAgentID; // id des UserAgents, der gefragt hatte
	/**
	 * @return List
	 */
	public List getDocumentInfo() {
		return documentInfo;
	}

	/**
	 * @return long
	 */
	public long getQuestionID() {
		return questionID;
	}

	/**
	 * Sets the documentInfo.
	 * @param documentInfo The documentInfo to set
	 */
	public void setDocumentInfo(List documentInfo) {
		this.documentInfo = documentInfo;
	}

	/**
	 * Sets the questionID.
	 * @param questionID The questionID to set
	 */
	public void setQuestionID(long questionID) {
		this.questionID = questionID;
	}

	/**
	 * @return long
	 */
	public long getPaID() {
		return paID;
	}

	/**
	 * Sets the paID.
	 * @param paID The paID to set
	 */
	public void setPaID(long paID) {
		this.paID = paID;
	}

	/**
	 * @return long
	 */
	public long getUserAgentID() {
		return userAgentID;
	}

	/**
	 * Sets the userAgentID.
	 * @param userAgentID The userAgentID to set
	 */
	public void setUserAgentID(long userAgentID) {
		this.userAgentID = userAgentID;
	}

}
