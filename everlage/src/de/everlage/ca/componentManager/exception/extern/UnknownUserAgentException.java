/**
 *  File: UnknownUserAgentException.java    
 * Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.exception.extern;

import java.io.Serializable;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class UnknownUserAgentException extends Exception implements Serializable {
	private String name;
	public UnknownUserAgentException(String msg, String nameStr) {
		super(msg);
		this.name = nameStr;
	}

	public String getName() {
		return this.name;
	}
}
