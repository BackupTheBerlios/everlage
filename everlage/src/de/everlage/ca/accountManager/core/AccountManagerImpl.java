/**
 * $ID$
 * File: AccountManagerImpl.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ca.accountManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.everlage.ca.accountManager.AccountManagerInt;
import de.everlage.ca.accountManager.comm.extern.Account;
import de.everlage.ca.accountManager.exception.extern.InvalidAccountIDException;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.CentralAgent;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class AccountManagerImpl extends UnicastRemoteObject implements AccountManagerInt {

	/**
	 * Constructor for AccountManagerImpl.
		 * @throws RemoteException
		 */
	public AccountManagerImpl() throws RemoteException, InternalEVerlageError {
		super();
		CentralAgent.localAccountManager = new LocalAccountManager();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.accountManager.AccountManagerInt#getAccount(long, long, long, long)
	 */
	public Account getAccount(long agentID, long caSessionID, long userID, long accountID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, InvalidAccountIDException {
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(this);
		}
		return CentralAgent.localAccountManager.getAccount(userID, accountID);
	}

}
