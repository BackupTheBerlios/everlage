/**
 * $Id: UALoginResult.java,v 1.4 2003/02/17 15:46:55 waffel Exp $ 
 * File: UALoginResult.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;
import java.util.Map;

/**
 * Daten nach einem UA-Login. Hier steht dann die caSessionID und die userAgentID drin, welche dem
 * eingeloggten UA zugewiesen wurden.
 * @author waffel
 *
 * 
 */
public final class UALoginResult implements Serializable {

	public long userAgentID;
	public long caSessionID;
  public Map providerAgentList;
}
