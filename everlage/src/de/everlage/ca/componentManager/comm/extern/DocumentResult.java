/*
 * Created on Mar 26, 2003
 * File DocumentResult.java
 * 
 */
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class DocumentResult extends PASearchRequestRecord implements Serializable {
  private String documentFormat;
  private byte[] content;
	/**
	 * @return
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * @return
	 */
	public String getDocumentFormat() {
		return documentFormat;
	}

	/**
	 * @param bs
	 */
	public void setContent(byte[] bs) {
		content = bs;
	}

	/**
	 * @param string
	 */
	public void setDocumentFormat(String string) {
		documentFormat = string;
	}

}
