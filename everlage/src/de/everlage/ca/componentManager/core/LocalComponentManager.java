/**
 * $Id: LocalComponentManager.java,v 1.13 2003/04/01 13:57:58 waffel Exp $ 
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.everlage.ca.LocalManagerAbs;
import de.everlage.ca.componentManager.comm.extern.DocumentRequest;
import de.everlage.ca.componentManager.comm.extern.DocumentResult;
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PAData;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.comm.intern.UAData;
import de.everlage.ca.componentManager.exception.extern.InvalidPasswordException;
import de.everlage.ca.componentManager.exception.extern.InvalidQueryException;
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
	/** enthält als Key uaID und als Entry UAData */
	private Map userAgents = null;
	/** enthält als Key paID und als Entry PAData */
	private Map providerAgents = null;

	private Map paAnswerList = null;
	private long questionID = 0;

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
		// Map mit der paAnswerList initialisieren
		this.paAnswerList = new Hashtable(10);

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
			final Long userAgentID = this.checkAgentData(dbCon, false, name, password);
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
			CAGlobal.log.debug("PA list " + this.providerAgents.size());
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
		} catch (ClassCastException e) {
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
			final Long providerAgentID = this.checkAgentData(dbCon, true, name, password);
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
		} catch (ClassCastException e) {
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
			// falls der UA nicht eingeloggt ist
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
			CAGlobal.log.error("Invalid Agent");
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
	Long checkAgentData(
		Connection dbCon,
		boolean isProvAgent,
		String agentName,
		String agentPassword)
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
				if (isProvAgent) {
					throw new UnknownAgentException("Unknown ProviderAgent ", agentName);
				} else {
					throw new UnknownAgentException("Unknow UserAgent ", agentName);
				}
			}

			final Long resultID = new Long(res.getLong("agentID"));
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug(resultID);
			}

			// überprüfen, um welche Art von Agent es sich handelt
			final boolean isProvAgentRes = res.getBoolean("isProviderAgent");
			if (isProvAgentRes != isProvAgent) {
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

	/**
	 * @deprecated never needed. The CentralAgent only managed self the UA-Lists and the PA-Lists
	 * @throws RemoteException
	 */
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

	/**
	 * Sendet eine Suchanfrage an alle angemeldeten PA's. Im Moment ist dies nur ein einfacher String.
	 * Es wird die ID des UserAgents, der gefragt hat mitgeliefert, damit dann bei einer Antwort an
	 * diesen UA zurückgesendet werden kann.
	 * @param searchString Suchstring
	 * @throws RemoteException @see RemoteException
	 * Schritt <b>Suchanfrage vom CA</b> aus der PAFeatures.txt
	 * @TODO die questionID sollte ein Ring mit nummern sein, der durchlaufen wird. So kann sicher
	 * gestellt werden, dass es nicht nach einer Weile zu einem Überlauf kommt. Der Ring muss
	 * gross genug sein.
	 * @TODO die Query sollte noch überprüft werden, bevor diese an die PA's weitergeleitet wird,
	 * dann sollte es auch die InvalidQueryException geben
	 */
	void sendSearchToAllPAs(String searchString, long userAgentID)
		throws RemoteException, InvalidQueryException {
		CAGlobal.log.debug("sendSearchToAllPAs");
		ProviderAgentInt pa;
		PAData paData;
		this.questionID++;
		final Set keySet = this.providerAgents.keySet();
		for (Iterator it = keySet.iterator(); it.hasNext();) {
			Long keyID = (Long) it.next();
			paData = (PAData) this.providerAgents.get(keyID);
			pa = paData.providerAgent;
			PASearchRequestRecord paSearchRec = new PASearchRequestRecord();
			paSearchRec.setQuestionID(questionID);
			paSearchRec.setSearchString(searchString);
			paSearchRec.setUserAgentID(userAgentID);
			// die Question in der Answer Liste vormerken, damit dann später geschaut werden kann,
			// ob die Question noch gültig ist, oder schon alle PA's geantwortet hatten
			this.paAnswerList.put(new Long(this.questionID), new ArrayList());
			pa.search(paSearchRec);
		}
	}

	/**
	 * Wird von den PA's aufgerufen. Diese tragen ihre Antworten zu einer Frage ein. Die Methode
	 * fügt die Antworten zu der Frage hinzu, wenn von diesem PA noch keine Antwort vorlag. Lag eine
	 * Antwort vor, so wird diese mit der neuen Überschrieben. Wenn alle PA's geantwortet haben, dann
	 * wird der fragende UA über die Antwort informiert, dass heisst, der UA kriegt eine Liste mit
	 * PAAnswerRecords, die er dann weiter bearbeiten kann.
	 * @param paAnswerRec Anwort Datenstruktur, die die FragenID, die UAId und eine Liste mit
	 * DokumentenInformationen zu der Frage enthält.
	 * @throws InternalEVerlageError Falls die Frage schon beantwortet wurde (also die questionID nicht
	 * mehr existiert)
	 * @throws RemoteException @see RemoteException
	 * @TODO was passiert, wenn sich zwischendurch ein neuer PA anmeldet? Der sollte eigentlich nicht
	 * aus die question reagieren können.
	 * @TODO was passiert, wenn sich zwischendurch ein PA abmeldet? Dann wird evtl. die Antwort nie
	 * geschickt, da die Antwortliste nie mehr gleich der PA-Liste sein kann.
	 */
	void putPASearchAnswerToUA(PAAnswerRecord paAnswerRec)
		throws InternalEVerlageError, RemoteException {
		CAGlobal.log.debug("putPASearchAnswerToUA");
		final Long questionID = new Long(paAnswerRec.getQuestionID());
		CAGlobal.log.debug("qusetionID: " + questionID);
		final long paID = paAnswerRec.getPaID();
		final Long userAgentID = new Long(paAnswerRec.getUserAgentID());
		// in der Answer Liste stehen die QuestionID's und die dazugehörige Liste mit allen 
		// paAnswerRecords drin
		// versuchen, aus der answer liste die questionID zu holen
		List questionMapping = (List) this.paAnswerList.get(questionID);
		// wenn die Question gar nicht existiert
		if (questionMapping == null) {
			CAGlobal.log.error("no question given for this questionID! " + this.questionID);
			// @TODO change this Exception to NoQuestionException
			throw new InternalEVerlageError();
		}
		// wenn noch keine Antwort eingetroffen ist, die Antwortliste ist leer
		if (questionMapping.size() == 0) {
			questionMapping.add(paAnswerRec);
			this.paAnswerList.put(questionID, questionMapping);
		} else {
			// falls es schon eine Antwort gibt
			// schauen, ob der PA schon geantwortet hatte
			boolean currentInserted = false;
			// für alle Elemente dieser Liste durchgehen
			for (Iterator it = questionMapping.iterator(); it.hasNext();) {
				PAAnswerRecord answerRec = (PAAnswerRecord) it.next();
				if (paID == answerRec.getPaID()) {
					currentInserted = true;
				}
			}
			// wenn der PA noch keine Anwort abgegeben hatte, dann die Antwort einfügen
			if (!currentInserted) {
				questionMapping.add(paAnswerRec);
				paAnswerList.put(questionID, questionMapping);
			}
		}
		// aus der AnswerListe zu einer question die Anzahl der geantworteten PA's holen
		List answertPAs = (List) paAnswerList.get(questionID);
		// wenn alle PA's geantwortet haben
		if (answertPAs.size() == providerAgents.size()) {
			// dann den fragenden UA benachrichtigen; aus der ua-liste holen
			UAData uaData = (UAData) this.userAgents.get(userAgentID);
			// und die Liste löschen, das machen wir, indem wir die question löschen
			this.paAnswerList.remove(questionID);
			// benachrichtigen
			UserAgentInt ua = uaData.userAgent;
			CAGlobal.log.debug("putAnswers " + answertPAs);
			// alle Antworten senden
			ua.putAnswers(answertPAs);
		}
	}

	DocumentResult getDocumentFromPA(DocumentRequest documentRequest)
		throws InternalEVerlageError, RemoteException {

		final long paID = documentRequest.getPaID();
		final PAData paData = (PAData) this.providerAgents.get(new Long(paID));
		DocumentResult docRes = paData.providerAgent.getDocumentWithID(documentRequest.getDocumentID());
		return docRes;
	}
}