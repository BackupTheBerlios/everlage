/**
 * $Id: UAData.java,v 1.1 2003/01/20 16:13:49 waffel Exp $ 
 * File: UAData.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.comm.intern;

import de.everlage.ua.minimal.text.UserAgent;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class UAData {

	public long userAgentID;
	public long uaSessionID;
	public String uaRMIAddress;
	public UserAgent userAgent;

	public UAData(long uaID, long sessionID, String rmi, UserAgent ua) {
		this.userAgentID = uaID;
		this.uaSessionID = sessionID;
		this.uaRMIAddress = rmi;
		this.userAgent = ua;
	}
}
