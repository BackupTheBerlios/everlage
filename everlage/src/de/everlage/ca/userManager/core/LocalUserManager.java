/**
 * $Id: LocalUserManager.java,v 1.3 2003/02/11 15:36:02 waffel Exp $ 
 * File: LocalUserManager.java    Created on Jan 13, 2003
 *
*/
package de.everlage.ca.userManager.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import de.everlage.ca.LocalManagerAbs;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.userManager.exception.extern.LoginNotExistsException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;

/**
 * Der LocalUserManager macht die eigentlichen Datenbankaufgaben. Er ist grunds�tzlich f�r die
 * Nutzerverwaltung zust�ndig.
 * @author waffel
 *
 * 
 */
public final class LocalUserManager extends LocalManagerAbs {

	/**
	 * Default Konstruktor des lokalen UserManagers. Hier werden die SQL-Properties des lokalen
	 * UserManagers registriert (diese m�ssen in einer Datei "userManager-sql.properties vorhanden
	 * sein, welche sich in dem Gleichen Verzeichnis befinden muss, wie der lokale UserManager).
	 */
	public LocalUserManager() throws InternalEVerlageError {
		super();
		registerProperty("userManager-sql.properties", this);
		CAGlobal.log.info("LocalUserManager finished init");
	}

	/**
	 * Loggt einen anonymen User in den CentralAgent ein. Die Daten des Nutzers werden in die Tabellen
	 * SystemUser und SingleUser eingetragen. Ebenfalls wird die AgentId mit eingetragen, welche den
	 * Nutzer engemeldet hat, um sp�ter beim ausloggen des Agents auch die Nutzer des Agents mit
	 * ausloggen zu k�nnen. Der anonyme User wird bei dem einloggen als erstes automatisch registriert 
	 * und danach eingeloggt.
	 * @param agentID
	 * @param dbConnection
	 * @return long
	 * @throws InternalEVerlageError
	 */
	public long anonymousLogin(long agentID, Connection dbConnection) throws InternalEVerlageError {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			pstmt =
				dbConnection.prepareStatement(this.pHandler.getProperty("getNextSystemUserSeq", this));
			res = pstmt.executeQuery();
			if (!res.next()) {
				throw new InternalEVerlageError("no sequence for SystemUser found");
			}
			long userID = res.getLong(1);
			pstmt.close();
			pstmt = null;
			res.close();
			res = null;
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("insertSystemUser", this));
			int l = 1;
			pstmt.setLong(l++, userID); // userID
			pstmt.setLong(l++, new Date().getTime()); //registration Date
			pstmt.setLong(l++, agentID); // agentID
			pstmt.setLong(l++, -1); // number of results
			pstmt.setLong(l++, 0); // timeout
			pstmt.setBoolean(l, false); // frozen
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			l = 1;
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("insertSingleUser", this));
			pstmt.setLong(l++, userID); //userID
			pstmt.setString(l++, null); //login
			pstmt.setString(l++, null); //password
			pstmt.setString(l++, null); //email
			pstmt.setString(l++, null); //lastName
			pstmt.setString(l++, null); //firstName
			pstmt.setString(l++, null); // title
			pstmt.setBoolean(l++, true); //isGuest
			pstmt.setBoolean(l++, true); //isLoggedIn
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			CAGlobal.log.info("anonymous login finished");
			return userID;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (Exception e) {
			}
			try {
				if (res != null) {
					res.close();
					res = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Loggt alle Nutzer eines Agenten aus. Diese Methode wird urspr�nglich vom ComponentManager
	 * aufgerufen, wenn sich ein UserAgent ausloggt. Dabei muss sich nun nicht mehr der UserAgent
	 * darum k�mmern, vorm herunterfahren seine Nutzer auszuloggen, sondern der CentralAgent macht
	 * dies f�r den UserAgent. 
	 * 
	 * In diser Methode werden alle Nutzer eines Agenten in der Tabelle SystemUser gesucht, welche zu
	 * einem Agenten geh�ren. Dann wird f�r jeden dieser User das Flag isLoggedIn in der Tabelle
	 * Singleuser auf false gesetzt.
	 * @param agentID Id des Agenten, zu dem Nutzer geh�ren k�nnen
	 * @param dbConnection Datenbankverbindung des CentralAgents
	 * @throws InternalEVerlageError Wenn ein SQL-Fehler aufgetreten ist.
	 */
	public void logoutUsersForAgent(long agentID, Connection dbConnection)
		throws InternalEVerlageError {
		CAGlobal.log.debug("logoutUsersForAgent");
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("logoutUsersForAgent", this));
			pstmt.setBoolean(1, false);
			pstmt.setLong(2, agentID);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (Exception e) {
			}
			try {
				if (res != null) {
					res.close();
					res = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Loggt einen bereits registrierten User ein. Vorher wird �berpr�ft, ob der User bereits 
	 * registriert war, ob sein Login noch g�ltig ist, ob das Passwort stimmt und ob der user nicht
	 * schon eingeloggt ist. Wenn diese tests alls ok sind, wird der user eingeloggt und die ID des
	 * Agents geupdated, der den user einloggt, damit beim abmelden des Agents auch der User 
	 * ordentlich ausgeloggt werden kann.
	 * @param login Loginname des Users
	 * @param password Passwort eines users
	 * @param agentID agentID ID des Agents, �ber welchen der User eingeloggt wird. Dies wird 
	 * ben�tigt, um beim Beenden des Agents den User automatisch ausloggen zu k�nnen
	 * @param dbCon Datenbankverbindung
	 * @return UserData Verschiedene Daten des Users @see UserData
	 * @throws InternalEVerlageError falls ein SQL-Fehler auftrat
	 * @throws InvalidPasswordException falls das Passwort nicht stimmt
	 * @throws UserIsFrozenException falls der user gesperrt ist
	 * @throws UserAlreadyLoggedInException falls der user bereits eingeloggt ist
	 * @throws LoginNotExistsException falls der user noch nicht registriert ist (oder es das login 
	 * noch nicht gibt)
	 */
	public UserData userLogin(String login, String password, long agentID, Connection dbCon)
		throws
			InternalEVerlageError,
			InvalidPasswordException,
			UserIsFrozenException,
			UserAlreadyLoggedInException,
			LoginNotExistsException {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
      CAGlobal.log.debug(this.pHandler.getProperty("getSingleUserDataForLogin", this));
      CAGlobal.log.debug("login: "+login);
			pstmt = dbCon.prepareStatement(this.pHandler.getProperty("getSingleUserDataForLogin", this));
			pstmt.setString(1, login);
			res = pstmt.executeQuery();
			if (!res.next()) {
				// User ist nich registriert
				CAGlobal.log.error("user not registered");
				throw new LoginNotExistsException();
			}
			long dbUserID = res.getLong("userID");
			String dbPassword = res.getString("password");
			if (!password.equals(dbPassword)) {
				// passwort stimmt nicht �berein
				CAGlobal.log.error("password not identical");
				throw new InvalidPasswordException();
			}
			boolean dbIsLoggedIn = res.getBoolean("isLoggedIn");
			if (dbIsLoggedIn) {
				// nutzer ist bereits eingeloggt
				CAGlobal.log.error("user alrady logged in");
				throw new UserAlreadyLoggedInException();
			}
			UserData userData = new UserData();
			userData.firstName = res.getString("firstName");
			userData.lastName = res.getString("lastName");
			userData.title = res.getString("title");
			// erweiterte Daten aus systemUser holen
			res.close();
			res = null;
			pstmt.close();
			pstmt = null;
			pstmt = dbCon.prepareStatement(pHandler.getProperty("getSystemUserDataForUserID", this));
			pstmt.setLong(1, dbUserID);
			res = pstmt.executeQuery();
			if (!res.next()) {
				// Datenunstimmigkeiten
				CAGlobal.log.error("no result from database");
				throw new InternalEVerlageError();
			}
			boolean dbIsFrozen = res.getBoolean("frozen");
			if (dbIsFrozen) {
				// user ist gesperrt
				CAGlobal.log.error("user is currently frozen");
				throw new UserIsFrozenException();
			}
			// alles ok
			// user einloggen
			res.close();
			res = null;
			pstmt.close();
			pstmt = null;
			pstmt = dbCon.prepareStatement(pHandler.getProperty("updateLoginStatusForSingleUser", this));
			pstmt.setBoolean(1, true); // isLoggedIn
			pstmt.setLong(2, dbUserID); // userID
			pstmt.executeUpdate();
			// agentID updaten, f�r den Agent, der den User eingeloggt hat, damit das automatische 
			// ausloggen funktioniert, wenn ein Agent sich abmeldet
			pstmt.close();
			pstmt = dbCon.prepareStatement(pHandler.getProperty("updateAgentIDForSystemUser", this));
			pstmt.setLong(1, agentID);
			pstmt.setLong(2, dbUserID);
			pstmt.executeUpdate();
			pstmt.close();
			CAGlobal.log.info("user with login " + login + " and ID " + dbUserID + " logged in.");
			return userData;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (Exception e) {
			}
			try {
				if (res != null) {
					res.close();
					res = null;
				}
			} catch (Exception e) {
			}
		}
	}
}
