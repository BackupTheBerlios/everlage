/**
 * $Id: ProviderAgentInt.java,v 1.3 2003/03/13 17:30:00 waffel Exp $ 
 * File: ProviderAgentInt.java    Created on Jan 29, 2003
 *
*/
package de.everlage.pa;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
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

  void search(PASearchRequestRecord paSearchRec) throws RemoteException;
  
  void getDocumentWithID(long documentID) throws RemoteException;
  
  void putPASearchAnswerToUA(PAAnswerRecord paAnswerRec) throws RemoteException;
}
