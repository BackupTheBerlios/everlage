/**
 * $Id: ProviderAgentInt.java,v 1.4 2003/03/25 19:41:41 waffel Exp $ 
 * File: ProviderAgentInt.java    Created on Jan 29, 2003
 *
*/
package de.everlage.pa;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * Interface für alle ProviderAgents. Auf dieses Interface wird beim CentralAgent gecastet und die
 * entsprechenden Methoden aufgerufen.
 * @author waffel
 *
 * 
 */
public interface ProviderAgentInt extends Remote {
  

  /**
   * Startet die Suche bei einem PA
	 * @param paSearchRec Einträge für eine Suche, wie SuchQuery, UA der gesucht hat, QueryID usw.
	 * @throws RemoteException falls ein RMI-Fehler auftritt
	 */
	void search(PASearchRequestRecord paSearchRec) throws RemoteException;
  
  void getDocumentWithID(long documentID) throws RemoteException;
  
  /**
   * Gibt ein Suchergebnis an einen UA über den CentralAgent zurück
	 * @param paAnswerRec enthält alle Angaben zu dem Suchergebnis, diese können dann bei einer
   * Dokumentenanforderung ausgewertet werden
	 * @throws RemoteException falls ein RMI-Fehler auftritt
	 */
	void putPASearchAnswerToUA(PAAnswerRecord paAnswerRec) throws RemoteException;
  
  /**
   * Initialisiert die Properties, welche für einen PA gesetzt werden müssen, dazu zählen die 
   * RMI-Adresse usw.
	 * @throws RemoteException falls ein RMI-Fehler auftritt
	 * @throws InternalEVerlageError falls das Property nicht gelesen werden kann
	 */
	void initProperties() throws RemoteException, InternalEVerlageError;
}
