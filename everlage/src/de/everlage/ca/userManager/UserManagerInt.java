/**
 * $Id: UserManagerInt.java,v 1.2 2003/01/22 16:50:16 waffel Exp $ 
 * File: UserManagerInt.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.userManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.ca.userManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.userManager.exception.extern.LoginNotExistsException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;

/**
 * Interface f�r den UserAgent. Dieses Interface dient als Schnittstelle zu den PA's und UA's. Alle
 * heir definierten Methoden k�nnen f�r die Kommunikation mit dem UserManager des CentralAgents
 * verwendet werden. Der UserAgent ist f�r die Verwaltung von Nutzern im CentralAgent zust�ndig.
 * @author waffel
 *
 * 
 */
public interface UserManagerInt extends Remote {

	/**
	 * Methode zum einloggen eines bereits bekannten Nutzers. Die AgentID und caSessionID dienen der
	 * Identifizierung der entsprechenden UA's bzw. PA's, damit hier nur authorisierte Clients die
	 * Methode aufrufen k�nnen.
	 * @param agentID Identifikation des Aufrufenden Agenten
	 * @param caSessionID eindeutige ID innerhalb einer CentralAgent Session
	 * @param login Loginname des einzuloggenden Nutzers
	 * @param password Passwort des einzuloggenden Nutzers
	 * @return UserData Daten zum Nutzer @see UserData
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError Interner Fehler (bsp. Datenbankfehler)
	 * @throws InvalidAgentException Ung�ltiger Agent
	 * @throws InvalidPasswordException Ung�ltiges Passwort
	 * @throws UserIsFrozenException Nutzer ist gesperrt
	 * @throws UserAlreadyLoggedInException Nutzer ist bereits eingeloggt
	 * @throws LoginNotExistsException Loginname existiert nicht
	 */
	public UserData userLogin(long agentID, long caSessionID, String login, String password)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			InvalidPasswordException,
			UserIsFrozenException,
			UserAlreadyLoggedInException,
			LoginNotExistsException;

	/**
	 * Loggt einen anonymen Nutzer beim CentralAgent ein. Die Daten des anonymen Nutzers werden
	 * dauerhaft gespeichert. Zu jedem Nutzer wird auch die AgentID des Agent mit gespeichert, der den
	 * Nutzer angemeldet hat, damit beim ausloggen des Agents auch automatische alle Nutzer des Agents
	 * ausgeloggt werden k�nnne. Der Agent kann ja auch abst�rzen und hat dann keine M�glichkeit mehr,
	 * die Nutzer abzumelden. Es muss dann jedoch ein SystemThread existieren, der regelm�ssig
	 * nachsieht, ob auch noch alle angemeldeten Agents erreichbar sind. Ist dies f�r einen Agent
	 * nicht der Fall, so wird der Agent ausgeloggt und alle Nutzer dieses Agents.
	 * @param agentID ID des kontaktierenden Agenten
	 * @param caSessionID zur �berpr�fung der Autorisierung
	 * @return long userID, id der anonymen Nutzers, wird im verlauf der Kommunikation ben�tigt
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError Interner eVerlage Fehler (z.B. Datenbankfehler)
	 * @throws InvalidAgentException Ung�ltiger Agent
	 * @throws AnonymousLoginNotPossible Das anmelden eines anonymen Nutzers ist nicht erlaubt
	 */
	public long anonymousLogin(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, AnonymousLoginNotPossible;
}
