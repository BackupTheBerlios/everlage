/*
 * Created on Mar 11, 2003
 * File PASearchRequestRecord.java
 * 
 */
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class PASearchRequestRecord implements Serializable {

  private long questionID;
  private String searchString;
  private long userAgentID; // id des UserAgents, der gefragt hat

	/**
	 * @return long
	 */
	public long getQuestionID() {
		return questionID;
	}

	/**
	 * @return String
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * Sets the questionID.
	 * @param questionID The questionID to set
	 */
	public void setQuestionID(long paID) {
		this.questionID = paID;
	}

	/**
	 * Sets the searchString.
	 * @param searchString The searchString to set
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
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
