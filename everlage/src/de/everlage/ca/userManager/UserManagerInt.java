/**
 * $Id: UserManagerInt.java,v 1.4 2003/03/05 16:49:52 waffel Exp $ 
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
import de.everlage.ca.userManager.exception.extern.UserAlradyLoggedOutException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;
import de.everlage.ca.userManager.exception.extern.UserNotExistsException;

/**
 * Interface für den UserAgent. Dieses Interface dient als Schnittstelle zu den PA's und UA's. Alle
 * heir definierten Methoden können für die Kommunikation mit dem UserManager des CentralAgents
 * verwendet werden. Der UserAgent ist für die Verwaltung von Nutzern im CentralAgent zuständig.
 * @author waffel
 *
 * 
 */
public interface UserManagerInt extends Remote {

	/**
	 * Loggt einen anonymen Nutzer beim CentralAgent ein. Die Daten des anonymen Nutzers werden
	 * dauerhaft gespeichert. Zu jedem Nutzer wird auch die AgentID des Agent mit gespeichert, der den
	 * Nutzer angemeldet hat, damit beim ausloggen des Agents auch automatische alle Nutzer des Agents
	 * ausgeloggt werden könnne. Der Agent kann ja auch abstürzen und hat dann keine Möglichkeit mehr,
	 * die Nutzer abzumelden. Es muss dann jedoch ein SystemThread existieren, der regelmässig
	 * nachsieht, ob auch noch alle angemeldeten Agents erreichbar sind. Ist dies für einen Agent
	 * nicht der Fall, so wird der Agent ausgeloggt und alle Nutzer dieses Agents.
	 * @param agentID ID des kontaktierenden Agenten
	 * @param caSessionID zur Überprüfung der Autorisierung
	 * @return long userID, id der anonymen Nutzers, wird im verlauf der Kommunikation benötigt
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError Interner eVerlage Fehler (z.B. Datenbankfehler)
	 * @throws InvalidAgentException Ungültiger Agent
	 * @throws AnonymousLoginNotPossible Das anmelden eines anonymen Nutzers ist nicht erlaubt
	 */
	long anonymousLogin(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, AnonymousLoginNotPossible;

	/**
	 * Methode zum einloggen eines bereits bekannten Nutzers. Die AgentID und caSessionID dienen der
	 * Identifizierung der entsprechenden UA's bzw. PA's, damit hier nur authorisierte Clients die
	 * Methode aufrufen können.
	 * @param agentID Identifikation des Aufrufenden Agenten
	 * @param caSessionID eindeutige ID innerhalb einer CentralAgent Session
	 * @param login Loginname des einzuloggenden Nutzers
	 * @param password Passwort des einzuloggenden Nutzers
	 * @return UserData Daten zum Nutzer @see UserData
	 * @throws RemoteException RMI-Fehler
	 * @throws InternalEVerlageError Interner Fehler (bsp. Datenbankfehler)
	 * @throws InvalidAgentException Ungültiger Agent
	 * @throws InvalidPasswordException Ungültiges Passwort
	 * @throws UserIsFrozenException Nutzer ist gesperrt
	 * @throws UserAlreadyLoggedInException Nutzer ist bereits eingeloggt
	 * @throws LoginNotExistsException Loginname existiert nicht
	 */
	UserData userLogin(long agentID, long caSessionID, String login, String password)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			InvalidPasswordException,
			UserIsFrozenException,
			UserAlreadyLoggedInException,
			LoginNotExistsException;

	/**
	 * Loggt einen Nutzer anhand seiner UserID aus dem System aus.
	 * @param agentID agentID Identifikation des Aufrufenden Agenten
	 * @param caSessionID caSessionID eindeutige ID innerhalb einer CentralAgent Session
	 * @param userID Identifiaktion des Users im System
	 * @throws RemoteException RemoteException RMI-Fehler
	 * @throws InternalEVerlageError Interner Fehler (bsp. Datenbankfehler)
	 * @throws InvalidAgentException Ungültiger Agent
	 * @throws UserAlradyLoggedOutException User ist bereits ausgeloggt, oder die userID konnte nicht 
	 * gefunden werden
   * @throws UserNotExistsException User existiert nicht im System mit der angebenen UserID
	 */
	void userLogout(long agentID, long caSessionID, long userID)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			UserAlradyLoggedOutException,
			UserNotExistsException;

}
