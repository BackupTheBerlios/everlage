/**
 * $Id: UserAgent.java,v 1.5 2003/03/25 19:43:51 waffel Exp $  
 * File: UserAgent.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ua.minimal.text;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PAData;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.componentManager.exception.extern.InvalidQueryException;
import de.everlage.ca.core.PropertyHandler;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.pa.comm.extern.SearchResult;
import de.everlage.ua.UserAgentAbs;

/**
 * Beispiel eines textbasierten UserAgents
 * @author waffel
 *
 * 
 */
public class UserAgent extends UserAgentAbs {

	private UALoginResult uaData;
	private List answerList;
	private PropertyHandler pHandler;

	public UserAgent() throws RemoteException {
		super();
    try {
    initProperties();
    } catch (InternalEVerlageError e) {
      e.printStackTrace();
      System.exit(-1);
    }
	}

	public void init() throws RemoteException {
		try {
			registerComponents(pHandler.getProperty("CentralAgentRMIAddress", this));
			// UserAgent als RMI-Objekt bekannt machen
			Naming.rebind(pHandler.getProperty("OwnRMIUrl", this), this);
			System.out.println("rebind ok");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void login() {
		try {
			long uaSessionID = new Random().nextLong();
			uaData =
				componentManager.UALogin(
					pHandler.getProperty("Login", this),
					pHandler.getProperty("Password", this),
					pHandler.getProperty("OwnRMIUrl", this),
					uaSessionID);
			this.providerAgentData = uaData.providerAgentList;
			System.out.println("found " + uaData.providerAgentList.size() + " provider Agents");
			for (Iterator it = uaData.providerAgentList.keySet().iterator(); it.hasNext();) {
				Long keyID = (Long) it.next();
				System.out.println(((PAData) uaData.providerAgentList.get(keyID)).paRMIAddress);
			}
		} catch (Exception e) {
			System.out.println("Login Error: " + e.getMessage());
		}
	}

	public void loginAnonymousUser() {
		try {
			System.out.println(
				"new UserID: " + userManager.anonymousLogin(uaData.userAgentID, uaData.caSessionID));
		} catch (AnonymousLoginNotPossible e) {
			System.out.println("Error: Anonymous Login not possible");
		} catch (InternalEVerlageError e) {
			System.out.println("Error: Interner SystemFehler");
		} catch (InvalidAgentException e) {
			System.out.println("Error: Invalid Agent");
		} catch (RemoteException e) {
			System.out.println("RMI Fehler");
		}
	}

	public void loginTestUser() {
		try {
			UserData userData =
				userManager.userLogin(uaData.userAgentID, uaData.caSessionID, "testuser", "test");
			System.out.println(
				"user "
					+ userData.title
					+ " "
					+ userData.firstName
					+ " "
					+ userData.lastName
					+ " logged in");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		try {
			System.out.println("logout ua");
			componentManager.UALogout(uaData.userAgentID, uaData.caSessionID);
		} catch (Exception e) {
			System.out.println("Logout Error: " + e.getMessage());
		}
	}

	public void search(String searchString) throws RemoteException {
		try {
			this.componentManager.sendSearchToAllPAs(
				uaData.userAgentID,
				uaData.caSessionID,
				searchString);
		} catch (InvalidQueryException e) {
			e.printStackTrace();
		} catch (InvalidAgentException e) {
			e.printStackTrace();
		}
	}

	public void putAnswers(List answers) throws RemoteException {
		setAnswerList(answers);
	}

	/**
	 * @return List
	 */
	public List getAnswerList() {
		return this.answerList;
	}

	/**
	 * Sets the answerList.
	 * @param answerList The answerList to set
	 */
	public void setAnswerList(List answerList) {
		this.answerList = answerList;
	}

	public static void main(String[] args) {
		try {
			final UserAgent ua = new UserAgent();
			ua.init();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					ua.logout();
				}
			});
			ua.login();
			ua.search("String");
			Date date = new Date();
			long timeEnd = date.getTime() + 100;
			long currTime = 0;
			List answerList = ua.getAnswerList();
			while ((answerList == null) && (currTime < timeEnd)) {
				currTime = new Date().getTime();
				answerList = ua.getAnswerList();
			}
			PAAnswerRecord paAnswer = (PAAnswerRecord) answerList.get(0);
			List documents = paAnswer.getDocumentInfo();
			for (Iterator it = documents.iterator(); it.hasNext();) {
        SearchResult searchRes = (SearchResult)it.next();
				System.out.println(searchRes.getDocumentID()+" : "+searchRes.getDocumentTitle());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#initProperties()
	 */
	public void initProperties() throws RemoteException, InternalEVerlageError {
		this.pHandler = new PropertyHandler();
		this.pHandler.registerProperty("ua-text.properties", this);
    System.out.println("register properties finished: "+this.pHandler);
    System.out.println("CentralAgentRMIAddress: "+pHandler.getProperty("CentralAgentRMIAddress", this));
	}

}
