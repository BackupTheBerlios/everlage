/**
 * $Id: ComponentManagerImpl.java,v 1.8 2003/02/27 17:56:45 waffel Exp $ 
 * File: ComponentManagerImpl.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownAgentException;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.CentralAgent;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;

/**
 * Implementation des ComponentManager Interfaces. Hier werden die Agents auf ihre identität
 * überprüft und die Datenbankverbindungen für den Lokalen ComponentManager bereitgestellt.
 * @author waffel
 *
 * 
 */
public class ComponentManagerImpl extends UnicastRemoteObject implements ComponentManagerInt {

	/** 
	 * Initialisiert den Lokalen ComponentManager
	 */
	public ComponentManagerImpl() throws RemoteException, InternalEVerlageError {
		super();
		CentralAgent.localComponentManager = new LocalComponentManager();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#UALogin(java.lang.String, java.lang.String, java.lang.String, long)
	 */
	public UALoginResult UALogin(String name, String password, String uaRMIAddress, long uaSessionID)
		throws RemoteException, InternalEVerlageError, UnknownAgentException, InvalidPasswordException {
		Connection dbConnection = null;
		boolean dbOk = false;
		try {
			dbConnection = CentralAgent.dbMediator.getConnection();
			UALoginResult result =
				CentralAgent.localComponentManager.UALogin(
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
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		Connection dbConnection = null;
		boolean dbOk = false;
		try {
			dbConnection = CentralAgent.dbMediator.getConnection();
			CentralAgent.localUserManager.logoutUsersForAgent(agentID, dbConnection);
			dbConnection.commit();
			dbOk = true;
			CentralAgent.localComponentManager.UALogout(agentID);
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
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
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#PALogin(java.lang.String, java.lang.String, java.lang.String, long)
	 */
	public PALoginResult PALogin(String name, String password, String paRMIAddress, long paSessionID)
		throws RemoteException, InternalEVerlageError, UnknownAgentException, InvalidPasswordException {
		Connection dbCon = null;
		boolean dbOk = false;
		try {
			dbCon = CentralAgent.dbMediator.getConnection();
			PALoginResult res =
				CentralAgent.localComponentManager.PALogin(
					name,
					password,
					paRMIAddress,
					paSessionID,
					dbCon);
			CentralAgent.localComponentManager.updatePAListForAllUA();
			dbCon.commit();
			dbOk = true;
			return res;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			if (!dbOk) {
				try {
					dbCon.rollback();
				} catch (SQLException e2) {
					CAGlobal.log.error(e2);
					throw new InternalEVerlageError(e2);
				}
			}
			if (dbCon != null) {
				CentralAgent.dbMediator.freeConnection(dbCon);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#PALogout(long, long)
	 */
	public void PALogout(long agentID, long caSessionID)
		throws RemoteException, InternalEVerlageError, InvalidAgentException {
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		CentralAgent.localComponentManager.PALogout(agentID);
		CentralAgent.localComponentManager.updatePAListForAllUA();
	}

}
