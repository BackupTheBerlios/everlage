/**
 * $Id: UALoginResult.java,v 1.3 2003/02/11 15:15:06 waffel Exp $ 
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
public class UALoginResult implements Serializable {

	public long userAgentID;
	public long caSessionID;
  public Map providerAgentList;
}
