/**
 * $Id: UserAgentInt.java,v 1.2 2003/01/29 17:34:21 waffel Exp $ 
 * File: UserAgentInt.java    Created on Jan 21, 2003
 *
*/
package de.everlage.ua;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface für die UserAgents. Auf dieses Interface wird im CentralAgent gecastet und die
 * entsprechenden Methoden aufgerufen.
 * @author waffel
 *
 * 
 */
public interface UserAgentInt extends Remote {

	public void init() throws RemoteException;

}
