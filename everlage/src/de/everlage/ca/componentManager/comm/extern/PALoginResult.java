/**
 * $Id: PALoginResult.java,v 1.2 2003/02/17 15:46:55 waffel Exp $ 
 * File: PALoginResult.java    Created on Jan 29, 2003
 *
*/
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * Kommunikationsobjekt welches Daten für den ProviderAgent beinhaltet.
 * @author waffel
 *
 * 
 */
public final class PALoginResult implements Serializable {

	public long caSessionID;
	public long providerAgentID;
}
