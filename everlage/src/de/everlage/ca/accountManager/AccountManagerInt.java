package de.everlage.ca.accountManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.everlage.ca.accountManager.comm.extern.Account;
import de.everlage.ca.accountManager.exception.extern.InvalidAccountIDException;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

public interface AccountManagerInt extends Remote {

	public Account getAccount(long agentID, long caSessionID, long userID, long accountID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException, InvalidAccountIDException;
}
