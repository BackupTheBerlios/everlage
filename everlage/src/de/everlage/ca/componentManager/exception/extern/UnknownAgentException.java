/**
 * $Id: UnknownAgentException.java,v 1.1 2003/01/29 17:32:25 waffel Exp $ 
 * File: UnknownUserAgentException.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.exception.extern;

import java.io.Serializable;

/**
 * Allgemeine Exception, wenn ein Agent nicht bekannt ist. Es wird hier nicht unterschieden, ob der
 * Agent ein User- oder ProviderAgent ist.
 * @author waffel
 *
 * 
 */
public final class UnknownAgentException extends Exception implements Serializable {
	private String name;
	public UnknownAgentException(String msg, String nameStr) {
		super(msg);
		this.name = nameStr;
	}

	public String getName() {
		return this.name;
	}
}
