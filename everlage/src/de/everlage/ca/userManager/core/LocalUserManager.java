/**
 * $Id: LocalUserManager.java,v 1.2 2003/01/22 16:52:21 waffel Exp $ 
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
	 * ausloggen zu können.
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
}
