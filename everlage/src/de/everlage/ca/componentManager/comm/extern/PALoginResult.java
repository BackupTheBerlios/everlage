/**
 * $Id: PALoginResult.java,v 1.1 2003/01/29 17:29:56 waffel Exp $ 
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
public class PALoginResult implements Serializable {

	public long caSessionID;
	public long providerAgentID;
}
