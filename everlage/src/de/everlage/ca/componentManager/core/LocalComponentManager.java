/**
 * $Id: LocalComponentManager.java,v 1.2 2003/01/22 16:44:41 waffel Exp $ 
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
import java.util.Map;
import java.util.Random;

import de.everlage.ca.LocalManagerAbs;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.comm.intern.UAData;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownUserAgentException;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ua.UserAgentInt;

/**
 * New Class
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
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug("finished init LocalComponentManager");
		}
	}

	UALoginResult UALogin(
		String name,
		String password,
		String uaRMIAddress,
		long uaSessionID,
		Connection dbConnection)
		throws InternalEVerlageError, UnknownUserAgentException, InvalidPasswordException {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		CAGlobal.log.info("start UALogin");
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(name + "  " + password + "  " + uaRMIAddress + "  " + uaSessionID);
			CAGlobal.log.debug(this.pHandler);
		}
		try {
			// Datenbank abfragen
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("getUALogin", this));
			pstmt.setString(1, name);
			res = pstmt.executeQuery();
			// überprüfen, ob UserAgent eingetragen ist
			if (!res.next()) {
				CAGlobal.log.error("no result from database");
				throw new UnknownUserAgentException("Unknown UserAgent ", name);
			}

			Long userAgentID = new Long(res.getLong("agentID"));
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug(userAgentID);
			}

			// überprüfen, ob es sich um einen UserAgent handelt
			if (res.getInt("isProviderAgent") == 1) {
				CAGlobal.log.error("the given UserAgent is a ProviderAgent");
				throw new UnknownUserAgentException("No UserAgent ", name);
			}

			// überprüfen, ob das Passwort ok ist
			if (!password.equals(res.getString("password"))) {
				CAGlobal.log.error("Uncorrect Password for UserAgent");
				throw new InvalidPasswordException(name);
			}

			// überprüfen, ob der UserAgent schon angemeldet ist
			if (this.userAgents.containsKey(userAgentID)) {
				Object data = this.userAgents.remove(userAgentID);
				// für den GB freigeben
				data = null;
			}

			// alles ok
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("all ok");
			}
			// statements und resultsets schließen
			res.close();
			res = null;
			pstmt.close();
			pstmt = null;
			// Daten in Tabelle anpassen
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("updateUA", this));
			pstmt.setString(1, uaRMIAddress);
			pstmt.setLong(2, uaSessionID);
			pstmt.setLong(3, this.caSessionID);
			pstmt.setLong(4, userAgentID.longValue());
			pstmt.executeUpdate();
			//UA RMIObjekt initialisieren
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("uaRMIAddress: " + uaRMIAddress);
			}
			UserAgentInt userAgent = (UserAgentInt) Naming.lookup(uaRMIAddress);
			CAGlobal.log.info("userAgent per RMI found");

			// UserAgent in die Interne userAgents Tabelle eintragen
			this.userAgents.put(
				userAgentID,
				new UAData(userAgentID.longValue(), uaSessionID, uaRMIAddress, userAgent));

			// Ergebnis zusammenstellen
			UALoginResult result = new UALoginResult();
			result.userAgentID = userAgentID.longValue();
			result.caSessionID = caSessionID;
			CAGlobal.log.info("UserAgent finished login");
			return result;
		} catch (SQLException sqle) {
			CAGlobal.log.error(sqle);
			throw new InternalEVerlageError(sqle);
		} catch (NotBoundException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (MalformedURLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (RemoteException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (Exception e) {
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
		}
	}

	void UALogout(long agentID) throws InternalEVerlageError {
		CAGlobal.log.info("begin logout UA: " + agentID);
		synchronized (this.uaSync) {
			UAData data = (UAData) this.userAgents.remove(new Long(agentID));
			// die daten für den gb freigeben
			data = null;
		}
		CAGlobal.log.info("finshed logout UA: " + agentID);
	}

	public void authentification(long agentID, long caSessionID) throws InvalidAgentException {
		if (caSessionID != this.caSessionID) {
			throw new InvalidAgentException();
		}
		// in die liste der UA's schauen
		Object data = this.userAgents.get(new Long(agentID));
		Object data2 = this.providerAgents.get(new Long(agentID));
		if ((data == null) && (data2 == null)) {
			throw new InvalidAgentException();
		}
	}
}
