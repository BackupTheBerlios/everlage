/*
 * Created on Mar 11, 2003
 * File PADocumentRequestRecord.java
 * 
 */
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class PADocumentRequestRecord implements Serializable {
  
  private long paID;
  private long documentID;
  

	/**
	 * @return long
	 */
	public long getDocumentID() {
		return documentID;
	}

	/**
	 * @return long
	 */
	public long getPaID() {
		return paID;
	}

	/**
	 * Sets the documentID.
	 * @param documentID The documentID to set
	 */
	public void setDocumentID(long documentID) {
		this.documentID = documentID;
	}

	/**
	 * Sets the paID.
	 * @param paID The paID to set
	 */
	public void setPaID(long paID) {
		this.paID = paID;
	}

}
