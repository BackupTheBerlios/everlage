/**
 * $Id: UserAgentInt.java,v 1.3 2003/02/11 15:24:51 waffel Exp $ 
 * File: UserAgentInt.java    Created on Jan 21, 2003
 *
*/
package de.everlage.ua;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Interface für die UserAgents. Auf dieses Interface wird im CentralAgent gecastet und die
 * entsprechenden Methoden aufgerufen.
 * @author waffel
 *
 * 
 */
public interface UserAgentInt extends Remote {

	public void init() throws RemoteException;

	/**
	 * Wenn ein neuer ProviderAgent hinzukommt oder sich abmeldet, wird die Liste der ProviderAgents
	 * im UA aktualisiert. Damit kann der UA immer an online PA's Anfragen stellen oder auf Lizenzen
	 * der jeweiligen PA's zugreifen.
	 * @param paList
	 * @throws RemoteException
	 */
	public void updateProviderAgentData(Map pas) throws RemoteException;
  
}
