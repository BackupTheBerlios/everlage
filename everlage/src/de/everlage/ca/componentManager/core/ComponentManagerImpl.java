/**
 * $Id: ComponentManagerImpl.java,v 1.1 2003/01/20 16:15:02 waffel Exp $ 
 * File: ComponentManagerImpl.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownUserAgentException;
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
public class ComponentManagerImpl extends UnicastRemoteObject implements ComponentManagerInt {

	public ComponentManagerImpl() throws RemoteException, InternalEVerlageError {
		super();
		CentralAgent.l_componentManager = new LocalComponentManager();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#UALogin(java.lang.String, java.lang.String, java.lang.String, long)
	 */
	public UALoginResult UALogin(String name, String password, String uaRMIAddress, long uaSessionID)
		throws RemoteException, InternalEVerlageError, UnknownUserAgentException, InvalidPasswordException {
		Connection dbConnection = null;
		boolean dbOk = false;
		try {
			dbConnection = CentralAgent.dbMediator.getConnection();
			UALoginResult result =
				CentralAgent.l_componentManager.UALogin(
					name,
					password,
					uaRMIAddress,
					uaSessionID,
					dbConnection);
			dbConnection.commit();
			dbOk = true;
			return result;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e.getMessage());
		} finally {
			if (!dbOk) {
				try {
					dbConnection.rollback();
				} catch (SQLException e2) {
					CAGlobal.log.error(e2);
					throw new InternalEVerlageError(e2);
				}
			}
			if (dbConnection != null) {
				CentralAgent.dbMediator.freeConnection(dbConnection);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#UALogout(long, long)
	 */
	public void UALogout(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException {
	}

}
