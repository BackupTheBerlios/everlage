/**
 * $Id: ProviderAgentInt.java,v 1.2 2003/02/11 15:23:02 waffel Exp $ 
 * File: ProviderAgentInt.java    Created on Jan 29, 2003
 *
*/
package de.everlage.pa;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.pa.comm.extern.TitleSearchRes;

/**
 * Interface für alle ProviderAgents. Auf dieses Interface wird beim CentralAgent gecastet und die
 * entsprechenden Methoden aufgerufen.
 * @author waffel
 *
 * 
 */
public interface ProviderAgentInt extends Remote {
  
  TitleSearchRes searchTitle(String searchStr) throws RemoteException;

}
