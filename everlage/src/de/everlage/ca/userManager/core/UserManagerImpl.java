/**
 * $Id: UserManagerImpl.java,v 1.2 2003/01/22 16:52:07 waffel Exp $ 
 * File:  UserManagerImpl.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.userManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;

import de.everlage.ca.core.CentralAgent;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.UserManagerInt;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.ca.userManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.userManager.exception.extern.LoginNotExistsException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;

/**
 * Implementation des UserManager Interfaces. Hier werden die Agenten und SessionID's des CA
 * überprüft, Datenbankverbindungen geholt und die entsprechenden Funktionen aus dem
 * LokalUserManager aufgerufen um diese zu einer Funktionsmethode nach aussen hin zu binden.
 * @author waffel
 *
 * 
 */
public final class UserManagerImpl extends UnicastRemoteObject implements UserManagerInt {

	/**
	 * Default Konstruktor. Muss expliziet mit angegeben werden, wegen RMI.
	 * @throws RemoteException
	 */
	public UserManagerImpl() throws RemoteException, InternalEVerlageError {
		super();
		CentralAgent.l_userManager = new LocalUserManager();
		CentralAgent.propHandler.registerProperty("userManager.properties", this);
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.userManager.UserManagerInt#userLogin(long, long, java.lang.String, java.lang.String)
	 */
	public UserData userLogin(long agentID, long caSessionID, String login, String password)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			InvalidPasswordException,
			UserIsFrozenException,
			UserAlreadyLoggedInException,
			LoginNotExistsException {
		System.out.println("userLogin");
		return null;
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.userManager.UserManagerInt#anonymousLogin(long, long)
	 */
	public long anonymousLogin(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, AnonymousLoginNotPossible {
		CentralAgent.l_componentManager.authentification(agentID, caSessionID);
		// schauen ob das anonymous login auch erlaubt ist
		boolean isGuestAllowed =
			new Boolean(CentralAgent.propHandler.getProperty("isGuestAllowed", this)).booleanValue();
    if (!isGuestAllowed) {
      throw new AnonymousLoginNotPossible();
    }
		Connection dbConnection = null;
		boolean dbOk = false;
		try {
			dbConnection = CentralAgent.dbMediator.getConnection();
			long userID = CentralAgent.l_userManager.anonymousLogin(agentID, dbConnection);
			dbConnection.commit();
			dbOk = true;
			return userID;
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} finally {
			if (!dbOk) {
				try {
					dbConnection.rollback();
				} catch (SQLException e) {
					throw new InternalEVerlageError(e);
				}
			}
			if (dbConnection != null) {
				CentralAgent.dbMediator.freeConnection(dbConnection);
			}
		}
	}

}
