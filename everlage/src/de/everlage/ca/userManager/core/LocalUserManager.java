/**
 * $Id: LocalUserManager.java,v 1.4 2003/02/17 15:23:32 waffel Exp $ 
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
 * Der LocalUserManager macht die eigentlichen Datenbankaufgaben. Er ist grundsätzlich für die
 * Nutzerverwaltung zuständig.
 * @author waffel
 *
 * 
 */
public final class LocalUserManager extends LocalManagerAbs {

	/**
	 * Default Konstruktor des lokalen UserManagers. Hier werden die SQL-Properties des lokalen
	 * UserManagers registriert (diese müssen in einer Datei "userManager-sql.properties vorhanden
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
	 * Nutzer engemeldet hat, um später beim ausloggen des Agents auch die Nutzer des Agents mit
	 * ausloggen zu können. Der anonyme User wird bei dem einloggen als erstes automatisch registriert 
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
			final long userID = res.getLong(1);
			pstmt.close();
			pstmt = null;
			res.close();
			res = null;
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("insertSystemUser", this));
			int column = 1;
			pstmt.setLong(column++, userID); // userID
			pstmt.setLong(column++, new Date().getTime()); //registration Date
			pstmt.setLong(column++, agentID); // agentID
			pstmt.setLong(column++, -1); // number of results
			pstmt.setLong(column++, 0); // timeout
			pstmt.setBoolean(column, false); // frozen
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			column = 1;
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("insertSingleUser", this));
			pstmt.setLong(column++, userID); //userID
			pstmt.setString(column++, null); //login
			pstmt.setString(column++, null); //password
			pstmt.setString(column++, null); //email
			pstmt.setString(column++, null); //lastName
			pstmt.setString(column++, null); //firstName
			pstmt.setString(column++, null); // title
			pstmt.setBoolean(column++, true); //isGuest
			pstmt.setBoolean(column++, true); //isLoggedIn
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
	 * Loggt alle Nutzer eines Agenten aus. Diese Methode wird ursprünglich vom ComponentManager
	 * aufgerufen, wenn sich ein UserAgent ausloggt. Dabei muss sich nun nicht mehr der UserAgent
	 * darum kümmern, vorm herunterfahren seine Nutzer auszuloggen, sondern der CentralAgent macht
	 * dies für den UserAgent. 
	 * 
	 * In diser Methode werden alle Nutzer eines Agenten in der Tabelle SystemUser gesucht, welche zu
	 * einem Agenten gehören. Dann wird für jeden dieser User das Flag isLoggedIn in der Tabelle
	 * Singleuser auf false gesetzt.
	 * @param agentID Id des Agenten, zu dem Nutzer gehören können
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
	 * Loggt einen bereits registrierten User ein. Vorher wird überprüft, ob der User bereits 
	 * registriert war, ob sein Login noch gültig ist, ob das Passwort stimmt und ob der user nicht
	 * schon eingeloggt ist. Wenn diese tests alls ok sind, wird der user eingeloggt und die ID des
	 * Agents geupdated, der den user einloggt, damit beim abmelden des Agents auch der User 
	 * ordentlich ausgeloggt werden kann.
	 * @param login Loginname des Users
	 * @param password Passwort eines users
	 * @param agentID agentID ID des Agents, über welchen der User eingeloggt wird. Dies wird 
	 * benötigt, um beim Beenden des Agents den User automatisch ausloggen zu können
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
			final long dbUserID = res.getLong("userID");
			final String dbPassword = res.getString("password");
			if (!password.equals(dbPassword)) {
				// passwort stimmt nicht überein
				CAGlobal.log.error("password not identical");
				throw new InvalidPasswordException();
			}
			final boolean dbIsLoggedIn = res.getBoolean("isLoggedIn");
			if (dbIsLoggedIn) {
				// nutzer ist bereits eingeloggt
				CAGlobal.log.error("user alrady logged in");
				throw new UserAlreadyLoggedInException();
			}
			final UserData userData = new UserData();
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
			final boolean dbIsFrozen = res.getBoolean("frozen");
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
			// agentID updaten, für den Agent, der den User eingeloggt hat, damit das automatische 
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
