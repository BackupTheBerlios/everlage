/**
 * $ID$
 * File: UserManagerInt.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.userManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.userManager.exception.extern.LoginNotExistsException;
import de.everlage.ca.userManager.exception.extern.UserAlreadyLoggedInException;
import de.everlage.ca.userManager.exception.extern.UserIsFrozenException;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public interface UserManagerInt extends Remote {

	public UserData userLogin(long agentID, long caSessionID, String login, String password)
		throws
			RemoteException,
			InternalEVerlageError,
			InvalidAgentException,
			InvalidPasswordException,
			UserIsFrozenException,
			UserAlreadyLoggedInException,
			LoginNotExistsException;

}
