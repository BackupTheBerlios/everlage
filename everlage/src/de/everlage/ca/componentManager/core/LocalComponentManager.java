/**
 * $Id: LocalComponentManager.java,v 1.6 2003/02/17 15:33:33 waffel Exp $ 
 * File: LocalComponentManager.java    Created on Jan 20, 2003
 *
*/
package de.everlage.ca.componentManager.core;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.everlage.ca.LocalManagerAbs;
import de.everlage.ca.componentManager.comm.extern.PAData;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.comm.intern.UAData;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownAgentException;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.pa.ProviderAgentInt;
import de.everlage.ua.UserAgentInt;

/**
 * Der LocalComponentManager ist für die durchführung der Methoden aus dem ComponentManagerImpl
 * verantwortlich. Hier werden die echten Datenbankabfragen und Selects gemacht. Ebenfalls kann der
 * LocalComponentManager innerhalb des CA von anderen Componenten verwendet werden um so auf
 * Methoden zugreifen zu können, welche über das Interface nicht sichtbar sind
 * @author waffel
 *
 * 
 */
public final class LocalComponentManager extends LocalManagerAbs {

	private long caSessionID = 0;
	private Map userAgents = null;
	private Map providerAgents = null;

	// zu Synchronisationszwecken
	private String uaSync;
	private String paSync;

	public LocalComponentManager() throws InternalEVerlageError {
		super();
		super.registerProperty("componentManager-sql.properties", this);
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug("phandler: " + pHandler);
		}
		// neue SessionID erzeugen
		this.caSessionID = new Random().nextLong();
		CAGlobal.log.info("CASessionid = " + caSessionID);
		// Map mit userAgent initialisieren
		this.userAgents = new Hashtable(10);
		// Map mit den providerAgents initialisieren
		this.providerAgents = new Hashtable(10);

		uaSync = new String("");
		paSync = new String("");
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug("finished init LocalComponentManager");
		}
	}

	UALoginResult UALogin(
		String name,
		String password,
		String uaRMIAddress,
		long uaSessionID,
		Connection dbCon)
		throws InternalEVerlageError, UnknownAgentException, InvalidPasswordException {
		CAGlobal.log.info("start UALogin");
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(name + "  " + password + "  " + uaRMIAddress + "  " + uaSessionID);
			CAGlobal.log.debug(this.pHandler);
		}
		try {
			final Long userAgentID = this.checkAgentData(dbCon, 0, name, password);
			// überprüfen, ob der UserAgent schon angemeldet ist
			if (this.userAgents.containsKey(userAgentID)) {
				Object data = this.userAgents.remove(userAgentID);
				// für den GB freigeben
				data = null;
			}

			// alles ok
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("all ok for UserAgent");
			}
			this.updateAgent(dbCon, uaRMIAddress, userAgentID.longValue(), uaSessionID);
			//UA RMIObjekt initialisieren
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("uaRMIAddress: " + uaRMIAddress);
			}
			final UserAgentInt userAgent = (UserAgentInt) Naming.lookup(uaRMIAddress);
			CAGlobal.log.info("userAgent per RMI found");
      CAGlobal.log.debug("PA list "+this.providerAgents.size());
			// UserAgent in die Interne userAgents Tabelle eintragen
			this.userAgents.put(
				userAgentID,
				new UAData(
					userAgentID.longValue(),
					uaSessionID,
					uaRMIAddress,
					userAgent,
					this.providerAgents));

			// Ergebnis zusammenstellen
			final UALoginResult result = new UALoginResult();
			result.userAgentID = userAgentID.longValue();
			result.caSessionID = caSessionID;
			result.providerAgentList = this.providerAgents;
			CAGlobal.log.info("UserAgent finished login");
			return result;
		} catch (NotBoundException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (MalformedURLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (RemoteException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		}
	}

	PALoginResult PALogin(
		String name,
		String password,
		String paRMIAddress,
		long paSessionID,
		Connection dbCon)
		throws InternalEVerlageError, UnknownAgentException, InvalidPasswordException {
		CAGlobal.log.info("start PALogin");
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(name + "  " + password + "  " + paRMIAddress + "  " + paSessionID);
			CAGlobal.log.debug(this.pHandler);
		}
		try {
			final Long providerAgentID = this.checkAgentData(dbCon, 1, name, password);
			// überprüfen, ob der UserAgent schon angemeldet ist
			if (this.providerAgents.containsKey(providerAgentID)) {
				Object data = this.providerAgents.remove(providerAgentID);
				// für den GB freigeben
				data = null;
			}

			// alles ok
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("all ok for ProviderAgent");
			}
			this.updateAgent(dbCon, paRMIAddress, providerAgentID.longValue(), paSessionID);
			//PA RMIObjekt initialisieren
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("paRMIAddress: " + paRMIAddress);
			}
			final ProviderAgentInt providerAgent = (ProviderAgentInt) Naming.lookup(paRMIAddress);
			CAGlobal.log.info("providerAgent per RMI found");

			// UserAgent in die Interne userAgents Tabelle eintragen
			this.providerAgents.put(
				providerAgentID,
				new PAData(providerAgentID.longValue(), paSessionID, paRMIAddress, providerAgent));

			// Ergebnis zusammenstellen
			final PALoginResult result = new PALoginResult();
			result.providerAgentID = providerAgentID.longValue();
			result.caSessionID = caSessionID;
			CAGlobal.log.info("ProviderAgent finished login");
			return result;
		} catch (NotBoundException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (MalformedURLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (RemoteException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		}
	}

	/**
	 * Loggt eine UA beim CentralAgent aus. Der UA wird in diesem Falle aus der internen ua-liste des
	 * componentManagers entfernt.
	 * @param agentID Id des auszuloggenden Agent
	 * @throws InternalEVerlageError wird im Moment nicht benutzt (alter Code)
	 */
	void UALogout(long agentID) throws InternalEVerlageError {
		CAGlobal.log.info("begin logout UA: " + agentID);
		synchronized (this.uaSync) {
			UAData data = (UAData) this.userAgents.remove(new Long(agentID));
			// die daten für den gb freigeben
			data = null;
		}
		CAGlobal.log.info("finshed logout UA: " + agentID);
	}

	/**
	 * überprüft, ob eine agentID und eine caSessionID in der AgentTabelle des CA vorhanden ist. Wenn
	 * nicht, wird eine InvalidAgentException geworfen.
	 * @param agentID ID des zu überprüfenden Agents
	 * @param caSessionID SessionID des CentralAgent
	 * @throws InvalidAgentException Falls der Agent nicht zugeordnet werden kann
	 */
	public void authentification(long agentID, long caSessionID) throws InvalidAgentException {
		if (caSessionID != this.caSessionID) {
			throw new InvalidAgentException();
		}
		// in die liste der UA's schauen
		final Object data = this.userAgents.get(new Long(agentID));
		final Object data2 = this.providerAgents.get(new Long(agentID));
		if ((data == null) && (data2 == null)) {
			throw new InvalidAgentException();
		}
	}

	/**
	 * Updatet die Daten für einen Agent in der Agenttabelle. 
	 * @param dbConnection Datenbankverbindung
	 * @param RMIAddress RMI Adresse des Agents
	 * @param agentID Identifiaktionsnummer des Agents
	 * @param agentSessionID SessionID des Agent zu authetifizierungszwecken
	 * @throws InternalEVerlageError Falls ein SQL Fehler beim update auftrat
	 */
	void updateAgent(Connection dbConnection, String RMIAddress, long agentID, long agentSessionID)
		throws InternalEVerlageError {
		PreparedStatement pstmt = null;
		try {
			// Daten in Tabelle anpassen
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("updateAgent", this));
			pstmt.setString(1, RMIAddress);
			pstmt.setLong(2, agentSessionID);
			pstmt.setLong(3, this.caSessionID);
			pstmt.setLong(4, agentID);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
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
		}
	}

	/**
	 * Überprüft, ob ein agent mit name und passwort sich beim centralAgent anmelden kann. 
	 * @param dbConnection Datenbankverbindung
	 * @param agentFlag Flag, welches anzeigt, ab der sich einloggende Agent ein UserAgent (Flag = 0)
	 * oder ein ProviderAgent (Flag = 1) ist
	 * @param agentName LoginName des Agents, der sich einloggen will
	 * @param agentPassword Passwort des Agents
	 * @return Long Die AgentID für den Agent aus der Tabelle Agent
	 * @throws InternalEVerlageError Wenn ein SQL-Fehler auftritt
	 * @throws InvalidPasswordException Wenn das Passwort des Agents nicht stimmt
	 * @throws UnknownAgentException Wenn der Agent nicht bekannt ist
	 */
	Long checkAgentData(Connection dbCon, int agentFlag, String agentName, String agentPassword)
		throws InternalEVerlageError, InvalidPasswordException, UnknownAgentException {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			CAGlobal.log.debug(agentName + "  " + this.pHandler.getProperty("getAgentLogin", this));
			// Datenbank abfragen
			pstmt = dbCon.prepareStatement(this.pHandler.getProperty("getAgentLogin", this));
			pstmt.setString(1, agentName);
			res = pstmt.executeQuery();
			// überprüfen, ob Agent eingetragen ist
			if (!res.next()) {
				CAGlobal.log.error("no result from database");
				if (agentFlag == 0) {
					throw new UnknownAgentException("Unknown ProviderAgent ", agentName);
				} else if (agentFlag == 1) {
					throw new UnknownAgentException("Unknow UserAgent ", agentName);
				}
			}

			final Long resultID = new Long(res.getLong("agentID"));
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug(resultID);
			}

			// überprüfen, um welche Art von Agent es sich handelt
			final int agentResFlag = res.getInt("isProviderAgent");
			CAGlobal.log.debug("" + agentResFlag + " !! " + agentFlag);
			if (agentResFlag != agentFlag) {
				CAGlobal.log.error("the given Agent is not the correct agenttype");
				throw new UnknownAgentException("No Agent ", agentName);
			}

			// überprüfen, ob das Passwort ok ist
			if (!agentPassword.equals(res.getString("password"))) {
				CAGlobal.log.error("Uncorrect Password for Agent");
				throw new InvalidPasswordException(agentName);
			}
			return resultID;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (res != null) {
					res.close();
					res = null;
				}
			} catch (Exception e) {
			}
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Loggt einen PA beim CentralAgent aus. Der PA wird in diesem Falle aus der internen ua-liste des
	 * componentManagers entfernt.
	 * @param agentID Id des auszuloggenden Agent
	 * @throws InternalEVerlageError wird im Moment nicht benutzt (alter Code)
	 */
	void PALogout(long agentID) throws InternalEVerlageError {
		CAGlobal.log.info("begin logout PA: " + agentID);
		synchronized (this.paSync) {
			PAData data = (PAData) this.providerAgents.remove(new Long(agentID));
			// die daten für den gb freigeben
			data = null;
		}
		CAGlobal.log.info("finshed logout PA: " + agentID);
	}

	void updatePAListForAllUA() throws RemoteException {
		try {
			CAGlobal.log.debug("updatePAListForAllUA()");
			final Set keys = this.userAgents.keySet();
      Long keyID;
      UAData uaData;
      UserAgentInt ua;
			for (Iterator it = keys.iterator(); it.hasNext();) {
				CAGlobal.log.debug("all UA's ");
				keyID = (Long) it.next();
				CAGlobal.log.debug("id:" + keyID.longValue());
				uaData = (UAData) this.userAgents.get(keyID);
				ua = uaData.userAgent;
				CAGlobal.log.debug(ua);
				CAGlobal.log.debug("before updateProviderAgentData ");
				ua.updateProviderAgentData(this.providerAgents);
				CAGlobal.log.debug("after updateProviderAgentData");
			}
		} catch (Exception e) {
			CAGlobal.log.error(e);
		}
	}
}