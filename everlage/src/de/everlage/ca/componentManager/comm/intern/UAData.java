/**
 * $Id: UAData.java,v 1.3 2003/02/11 15:16:01 waffel Exp $ 
 * File: UAData.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.comm.intern;

import java.util.Map;

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
  public Map providerAgentList;

	public UAData(long uaID, long sessionID, String rmi, UserAgentInt ua, Map paList) {
		this.userAgentID = uaID;
		this.uaSessionID = sessionID;
		this.uaRMIAddress = rmi;
		this.userAgent = ua;
    this.providerAgentList = paList;
	}
}
