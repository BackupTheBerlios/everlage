/**
 * $Id: ComponentManagerImpl.java,v 1.10 2003/04/01 13:57:58 waffel Exp $ 
 * File: ComponentManagerImpl.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.componentManager.comm.extern.DocumentRequest;
import de.everlage.ca.componentManager.comm.extern.DocumentResult;
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.InvalidQueryException;
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
			// no more needed, while pa-list and ua-list only in CentralAgent used
			//CentralAgent.localComponentManager.updatePAListForAllUA();
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
		// no more needed
		//CentralAgent.localComponentManager.updatePAListForAllUA();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#sendSearchToAllPAs(long, long, java.lang.String)
	 */
	public void sendSearchToAllPAs(long userAgentID, long caSessionID, String searchString)
		throws RemoteException, InvalidQueryException, InvalidAgentException {
		CAGlobal.log.debug("!!!!" + userAgentID + " " + caSessionID);
		CentralAgent.localComponentManager.authentification(userAgentID, caSessionID);
		CentralAgent.localComponentManager.sendSearchToAllPAs(searchString, userAgentID);
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#putPASearchAnswerToUA(de.everlage.ca.componentManager.comm.extern.PAAnswerRecord)
	 */
	public void putPASearchAnswerToUA(long agentID, long caSessionID, PAAnswerRecord paAnswerRec)
		throws InternalEVerlageError, RemoteException, InvalidAgentException {
		CentralAgent.localComponentManager.authentification(agentID, caSessionID);
		CentralAgent.localComponentManager.putPASearchAnswerToUA(paAnswerRec);
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.componentManager.ComponentManagerInt#getDocumentFromPA(long, long, de.everlage.ca.componentManager.comm.extern.DocumentRequest)
	 */
	public DocumentResult getDocumentFromPA(
		long agentID,
		long caSessionID,
		DocumentRequest documentRequest)
		throws InternalEVerlageError, RemoteException, InvalidAgentException {
		// TODO Auto-generated method stub
    CentralAgent.localComponentManager.authentification(agentID, caSessionID);
    return CentralAgent.localComponentManager.getDocumentFromPA(documentRequest);
	}

}
