/**
 * $Id: LocalComponentManager.java,v 1.1 2003/01/20 16:15:50 waffel Exp $ 
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

import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.comm.intern.UAData;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.UnknownUserAgentException;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.PropertyHandler;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ua.minimal.text.UserAgent;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class LocalComponentManager {

	private long caSessionID = 0;
	private PropertyHandler pHandler = null;
	private Map userAgents = null;

	public LocalComponentManager() throws InternalEVerlageError {
		// neue SessionID erzeugen
		this.caSessionID = new Random().nextLong();
		CAGlobal.log.info("CASessionid = " + caSessionID);
		// die SQLProperty datei registrieren
		this.pHandler = new PropertyHandler();
		CAGlobal.log.debug(pHandler);
		this.pHandler.registerProperty("componentManager-sql.properties", this);
		CAGlobal.log.debug("finish register Property");
		// Map mit userAgent initialisieren
		this.userAgents = new Hashtable(10);
		CAGlobal.log.debug("finished init LocalComponentManager");
	}

	public UALoginResult UALogin(
		String name,
		String password,
		String uaRMIAddress,
		long uaSessionID,
		Connection dbConnection)
		throws InternalEVerlageError, UnknownUserAgentException, InvalidPasswordException {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		CAGlobal.log.info("start UALogin");
		CAGlobal.log.debug(name + "  " + password + "  " + uaRMIAddress + "  " + uaSessionID);
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
			CAGlobal.log.debug(userAgentID);

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
			CAGlobal.log.debug("all ok");
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
      CAGlobal.log.debug("uaRMIAddress: " + uaRMIAddress);
			UserAgent userAgent = (UserAgent) Naming.lookup(uaRMIAddress);
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
}
