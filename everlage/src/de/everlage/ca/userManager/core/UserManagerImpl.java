/**
 * $ID$
 * File:  UserManagerImpl.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.userManager.core;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.UserManagerInt;
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
public final class UserManagerImpl extends UnicastRemoteObject implements UserManagerInt {

	/**
	 * Constructor for UserManagerImpl.
	 * @throws RemoteException
	 */
	public UserManagerImpl() throws RemoteException {
		super();
	}

	/**
	 * Constructor for UserManagerImpl.
	 * @param port
	 * @throws RemoteException
	 */
	public UserManagerImpl(int port) throws RemoteException {
		super(port);
	}

	/**
	 * Constructor for UserManagerImpl.
	 * @param port
	 * @param csf
	 * @param ssf
	 * @throws RemoteException
	 */
	public UserManagerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
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

}
