/**
 * $Id: UALoginResult.java,v 1.2 2003/01/22 16:40:49 waffel Exp $ 
 * File: UALoginResult.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

/**
 * Daten nach einem UA-Login. Hier steht dann die caSessionID und die userAgentID drin, welche dem
 * eingeloggten UA zugewiesen wurden.
 * @author waffel
 *
 * 
 */
public class UALoginResult implements Serializable {

	public long userAgentID;
	public long caSessionID;
}
