/**
 * $Id: ComponentManagerInt.java,v 1.2 2003/01/22 16:40:09 waffel Exp $   
 * File: ComponentManagerInt.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownUserAgentException;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

/**
 * Der ComponentManager verwaltet die verschiedenen Komponenten (PA's und UA'a) welche als Client
 * des Central-Agents betrachtet werden k�nnen. Er stellt Funktionen, wie das an- und abmelden von
 * Komponenten bereit und stellt deren Authentifizierung gegen�ber dem System fest.
 * @author waffel
 *
 * 
 */
public interface ComponentManagerInt extends Remote {

	/**
	 * Mit Hilfe dieser Methode kann sich ein bereits registrierter UserAgent beim CentralAgent
	 * anmelden. Die Registrierung verl�uft offline, dass heisst ein Administrator muss die
	 * entsprechenden Daten in die Datenbank eintragen. Der UserAgent muss dazu seinen Namen, sein
	 * Passwort und seine aktuelle SessionID �bergeben. Die SessionID muss der CentralAgent bei jedem
	 * Request an den UserAgent zur Authentifizierung angeben. Ausserdem wird eine Referenz auf ein
	 * RMI-Objekt �bergeben, dass das Interface @link de.everlage.ua.minimal.text.UserAgent
	 * implementiert.
   * 
   * Als Ergebnis �bermittelt der ComponentManager die userAgentID, die aktuell zugeordnete
   * caSessionID, die aktuell angemeldeten ProviderAgenten.
	 * @param name Name des Anzumeldenden UserAgents (z.B. MinimalTextUA)
	 * @param password Passwort des anzumeldenden UserAgents 
	 * @param uaRMIAddress RMI-Adresse des UserAgents zur Kommunikation
	 * @param uaSessionID aktuelle SessionID, mit der sich der CentralAgent gegen�ber dem UserAgent
	 * identifizieren muss, wenn er einen Request an ihn stellt
	 * @return UALoginResult Initialisierungsdaten f�r den UserAgent
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError interner System-Fehler, z.B. Datenbankfehler
	 * @throws UnknownUserAgentException nicht registrierter UserAgent (falscher Name)
	 * @throws InvalidPasswordException falsches Passwort f�r den UserAgent
	 */
	public UALoginResult UALogin(String name, String password, String uaRMIAddress, long uaSessionID)
		throws RemoteException, InternalEVerlageError, UnknownUserAgentException, InvalidPasswordException;

	/**
	 * Mit Hilfe dieser Methode kann sich ein UserAgent beim CentralAgent abmelden. Vorher sollten
	 * jedoch alle noch angemeldeten User auch abgemeldet werden, damit die Daten gesichert werden
	 * k�nnen.
	 * @param agentID ID des UserAgent, der abgemeldet werden soll
	 * @param caSessionID zur �berpr�fung der Authorisierung
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError interner Systemfehler (z.B. Datenbankfehler)
	 * @throws InvalidAgentException unauthorisierter Request (falsche agentID/caSessionID)
	 */
	public void UALogout(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException;
}
