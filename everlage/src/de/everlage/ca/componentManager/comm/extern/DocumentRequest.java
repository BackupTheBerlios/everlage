/*
 * Created on Mar 26, 2003
 * File DocumentRequest.java
 * 
 */
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class DocumentRequest implements Serializable {

  private String documentID;  // eindeutige ID des angefordeten Documents
  private String documentFormat; // bezeichnung des Dokumentenformates
  private long paID; // damit man weiss, an welchen PA der Request gesendet werden soll

	/**
	 * @return
	 */
	public String getDocumentFormat() {
		return documentFormat;
	}

	/**
	 * @return
	 */
	public String getDocumentID() {
		return documentID;
	}

	/**
	 * @return
	 */
	public long getPaID() {
		return paID;
	}

	/**
	 * @param string
	 */
	public void setDocumentFormat(String string) {
		documentFormat = string;
	}

	/**
	 * @param string
	 */
	public void setDocumentID(String string) {
		documentID = string;
	}

	/**
	 * @param l
	 */
	public void setPaID(long l) {
		paID = l;
	}

}
