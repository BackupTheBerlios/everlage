/**
 * $Id: PAData.java,v 1.1 2003/01/29 17:30:31 waffel Exp $ 
 * File: PAData.java    Created on Jan 29, 2003
 *
*/
package de.everlage.ca.componentManager.comm.intern;

import de.everlage.pa.ProviderAgentInt;

/**
 * Datenklasse, welche die Daten für einen ProviderAgent beinhaltet. Dient nur zur internen CA Kommunikation
 * @author waffel
 *
 * 
 */
public final class PAData {

	public long providerAgentID;
	public long uaSessionID;
	public String uaRMIAddress;
	public ProviderAgentInt providerAgent;

	public PAData(long uaID, long sessionID, String rmi, ProviderAgentInt pa) {
		this.providerAgentID = uaID;
		this.uaSessionID = sessionID;
		this.uaRMIAddress = rmi;
		this.providerAgent = pa;
	}
}
