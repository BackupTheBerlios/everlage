/**
 * $Id: UAData.java,v 1.2 2003/01/22 16:42:42 waffel Exp $ 
 * File: UAData.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.comm.intern;

import de.everlage.ua.UserAgentInt;

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
	public UserAgentInt userAgent;

	public UAData(long uaID, long sessionID, String rmi, UserAgentInt ua) {
		this.userAgentID = uaID;
		this.uaSessionID = sessionID;
		this.uaRMIAddress = rmi;
		this.userAgent = ua;
	}
}
