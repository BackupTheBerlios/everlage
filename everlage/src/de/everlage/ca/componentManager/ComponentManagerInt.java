/**
 * $Id: ComponentManagerInt.java,v 1.10 2003/04/01 13:57:58 waffel Exp $   
 * File: ComponentManagerInt.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.componentManager.comm.extern.DocumentRequest;
import de.everlage.ca.componentManager.comm.extern.DocumentResult;
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.InvalidQueryException;
import de.everlage.ca.componentManager.exception.extern.UnknownAgentException;
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
	 * Ein UserAgent kann sich auch mehrmals hintereinander beim CentralAgent anmelden. 
	 * In diesem Falle wird auch keine Exception geworfen, sondern der UserAgent einfach neu 
	 * eingeloggt.
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
	 * @throws InternalEVerlageError interner System-Fehler, z.B. Datenbankfehler, oder der UserAgent
	 * konnte per RMI nicht gebunden werden. Auch wenn der per RMI angegebene UserAgent nicht von
	 * dem eVerlage UserAgent Interface abgeleitet wurde.
	 * @throws UnknownUserAgentException nicht registrierter UserAgent (falscher Name)
	 * @throws InvalidPasswordException falsches Passwort f�r den UserAgent
	 */
	public UALoginResult UALogin(String name, String password, String uaRMIAddress, long uaSessionID)
		throws RemoteException, InternalEVerlageError, UnknownAgentException, InvalidPasswordException;

	/**
	 * Mit Hilfe dieser Methode kann sich ein UserAgent beim CentralAgent abmelden. Es werden alle
	 * Nutzer des UserAgent automatisch mit ausgeloggt.
	 * @param agentID ID des UserAgent, der abgemeldet werden soll
	 * @param caSessionID zur �berpr�fung der Authorisierung
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError interner Systemfehler (z.B. Datenbankfehler)
	 * @throws InvalidAgentException unauthorisierter Request (falsche agentID/caSessionID)
	 */
	void UALogout(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException;

	/**
	 * Loggt einen ProviderAgent beim CentralAgent ein. Der ProviderAgent muss bereits beim
	 * CentralAgent registriert sein (entsprechende Eintr�ge in der Tabelle Agent). 
	 * @param name Loginname des ProviderAgents, der sich einloggen will.
	 * @param password Passwort des ProviderAgents, der sich einloggen will.
	 * @param paRMIAddress RMI-Adresse des ProviderAgents, damit auch aktiv mit dem ProviderAgent
	 * kommuniziert werden kann
	 * @param paSessionID Vom ProviderAgent generierte SessionID, um eine Auhtentifizierung des
	 * CentralAgent gegen�ber dem ProviderAgent zu erm�glichen
	 * @return PALoginResult Daten f�r den ProviderAgent nach dem Login @see PALoginResult
	 * @throws RemoteException Falls ein RMI Fehler aufgetreten ist 
	 * @throws InternalEVerlageError Falls ein interner Fehler (z.B. Datenbankfehler) aufgetreten ist
	 * @throws UnknownProviderAgentException Falls der ProviderAgent nicht bekannt ist (der Loginname)
	 * @throws InvalidPasswordException Falls das Paswort nicht zum Login passt
	 */
	PALoginResult PALogin(String name, String password, String paRMIAddress, long paSessionID)
		throws RemoteException, InternalEVerlageError, UnknownAgentException, InvalidPasswordException;

	/**
	 * Mit Hilfe dieser Methode kann sich ein ProviderAgent beim CentralAgent abmelden. 
	 * @param agentID ID des ProviderAgent, der abgemeldet werden soll
	 * @param caSessionID zur �berpr�fung der Authorisierung
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError interner Systemfehler (z.B. Datenbankfehler)
	 * @throws InvalidAgentException unauthorisierter Request (falsche agentID/caSessionID)
	 */
	void PALogout(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException;
    
  /**
   * Sendet eine Suchanfrage an alle angeschlossenen PA's. Es gibt kein Ergebnis auf die 
   * Suchanfrage, sondern vielmehr melden die PA's sich selber mit einem Ergebnis zur�ck. Sind
   * alle Ergebnisse eingetroffen, so werden diese an den fragenden UA weitergeleitet. Der UA
   * ist daf�r zust�ndig, auf die Ergebnisse zu warten. 
	 * @param userAgentID ID, der Fragenden UserAgents
	 * @param caSessionID SessionID des CentralAgents
	 * @param searchString Suchanfrage
	 * @throws RemoteException @see RemoteException
	 * @throws InvalidQueryException Falls die Suchanfrage ein ung�ltiges Format enth�lt (noch nicht
   * implementiert!)
	 * @throws InvalidAgentException Falls die authentifizierung des Agents schiefgeht
	 */
	void sendSearchToAllPAs(long userAgentID, long caSessionID, String searchString)
    throws RemoteException, InvalidQueryException, InvalidAgentException;
        
  /**
   * Wird von den PA's aufgerufen. Diese tragen ihre Antworten zu einer Frage ein. Die Methode
   * f�gt die Antworten zu der Frage hinzu, wenn von diesem PA noch keine Antwort vorlag. Lag eine
   * Antwort vor, so wird diese mit der neuen �berschrieben. Wenn alle PA's geantwortet haben, dann
   * wird der fragende UA �ber die Antwort informiert, dass heisst, der UA kriegt eine Liste mit
   * PAAnswerRecords, die er dann weiter bearbeiten kann.
   * @param paAnswerRec Anwort Datenstruktur, die die FragenID, die UAId und eine Liste mit
   * DokumentenInformationen zu der Frage enth�lt.
   * @throws InternalEVerlageError Falls die Frage schon beantwortet wurde (also die questionID nicht
   * mehr existiert)
   * @throws RemoteException @see RemoteException
   * @throws InvalidAgentException Falls die Authentifizerung schiefgeht
   * @TODO was passiert, wenn sich zwischendurch ein neuer PA anmeldet? Der sollte eigentlich nicht
   * aus die question reagieren k�nnen.
   * @TODO was passiert, wenn sich zwischendurch ein PA abmeldet? Dann wird evtl. die Antwort nie
   * geschickt, da die Antwortliste nie mehr gleich der PA-Liste sein kann.
   */  
  void putPASearchAnswerToUA(long agentID, long caSessionID, PAAnswerRecord paAnswerRec)
      throws InternalEVerlageError, RemoteException, InvalidAgentException;
      
  DocumentResult getDocumentFromPA(long agentID, long caSessionID, DocumentRequest documentRequest)
  throws InternalEVerlageError, RemoteException, InvalidAgentException;
}
