/**
 * $Id: UserManagerImpl.java,v 1.7 2003/03/05 16:46:33 waffel Exp $ 
 * File:  UserManagerImpl.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.userManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;

import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.CentralAgent;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.UserManagerInt;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.ca.userManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.userManager.exception.extern.LoginNotExistsException;
import de.everlage.ca.userManager.exception.extern.UserAlradyLoggedOutException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;
import de.everlage.ca.userManager.exception.extern.UserNotExistsException;

/**
 * Implementation des UserManager Interfaces. Hier werden die Agenten und SessionID's des CA
 * �berpr�ft, Datenbankverbindungen geholt und die entsprechenden Funktionen aus dem
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
		CentralAgent.localUserManager = new LocalUserManager();
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
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		Connection dbCon = null;
		boolean dbOk = false;
		try {
			dbCon = CentralAgent.dbMediator.getConnection();
			final UserData userData =
				CentralAgent.localUserManager.userLogin(login, password, agentID, dbCon);
			CAGlobal.log.info("User " + login + "logged in");
			dbCon.commit();
			dbOk = true;
			return userData;
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} finally {
			if (!dbOk) {
				try {
					dbCon.rollback();
				} catch (SQLException e) {
					throw new InternalEVerlageError(e);
				}
			}
			if (dbCon != null) {
				CentralAgent.dbMediator.freeConnection(dbCon);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.userManager.UserManagerInt#anonymousLogin(long, long)
	 */
	public long anonymousLogin(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, AnonymousLoginNotPossible {
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		// schauen ob das anonymous login auch erlaubt ist
		final boolean isGuestAllowed =
			new Boolean(CentralAgent.propHandler.getProperty("isGuestAllowed", this)).booleanValue();
		if (!isGuestAllowed) {
			throw new AnonymousLoginNotPossible();
		}
		Connection dbCon = null;
		boolean dbOk = false;
		try {
			dbCon = CentralAgent.dbMediator.getConnection();
			long userID = CentralAgent.localUserManager.anonymousLogin(agentID, dbCon);
			CAGlobal.log.info("new user with ID " + userID + " created");
			// creates a new account for the new user with the init balance 0
			long accountID = CentralAgent.localAccountManager.createAccountForUser(userID, 0, dbCon);
			CAGlobal.log.info("new account with ID " + accountID + " for user " + userID + " created");
			dbCon.commit();
			dbOk = true;
			return userID;
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} finally {
			if (!dbOk) {
				try {
					dbCon.rollback();
				} catch (SQLException e) {
					throw new InternalEVerlageError(e);
				}
			}
			if (dbCon != null) {
				CentralAgent.dbMediator.freeConnection(dbCon);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.userManager.UserManagerInt#userLogout(long, long, long)
	 */
	public void userLogout(long agentID, long caSessionID, long userID)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			UserAlradyLoggedOutException,
			UserNotExistsException {
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		Connection dbCon = null;
		boolean dbOk = false;
		try {
			dbCon = CentralAgent.dbMediator.getConnection();
			CentralAgent.localUserManager.userLogout(dbCon, userID);
			dbCon.commit();
			dbOk = true;
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} finally {
			if (!dbOk) {
				try {
					dbCon.rollback();
				} catch (SQLException e) {
					throw new InternalEVerlageError(e);
				}
			}
			if (dbCon != null) {
				CentralAgent.dbMediator.freeConnection(dbCon);
			}
		}
	}

}
