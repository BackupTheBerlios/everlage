/**
 *  File: InvalidPasswordException.java    
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
public final class InvalidPasswordException extends Exception implements Serializable {

	private String compName;

	public InvalidPasswordException(String compNameStr) {
		super();
		this.compName = compNameStr;
	}

	public String getCompName() {
		return this.compName;
	}

}
