/*
 * Created on Mar 21, 2003
 * File ProviderAgent.java
 * 
 */
package de.everlage.pa.minimal.java;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
import de.everlage.ca.core.PropertyHandler;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.pa.ProviderAgentAbs;
import de.everlage.pa.minimal.java.search.Searcher;

/**
 * @author waffel
 */
public class ProviderAgent extends ProviderAgentAbs {

	private static Searcher searcher;

	/**
	 * @throws RemoteException
	 */
	public ProviderAgent() throws RemoteException {
		super();
		try {
      initProperties();
			super.registerComponents(pHandler.getProperty("CentralAgentRMIAddress", this));
			// ProviderAgent als RMI-Objekt bekannt machen
      System.out.println(pHandler.getProperty("OwnRMIUrl", this));
			Naming.rebind(pHandler.getProperty("OwnRMIUrl", this), this);
			System.out.println("rebind ok");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void logout() {
		try {
			System.out.println("PA logout");
			componentManager.PALogout(PAGlobal.paData.providerAgentID, PAGlobal.paData.caSessionID);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void login() {
		try {
			final long paSessionID = new Random().nextLong();
			PAGlobal.paData =
				componentManager.PALogin(
					pHandler.getProperty("Login", this),
					pHandler.getProperty("Password", this),
					pHandler.getProperty("OwnRMIUrl", this),
					paSessionID);
			System.out.println("PA logged in with ID" + PAGlobal.paData.providerAgentID);
		} catch (Exception e) {
			System.out.println("Login Error: " + e.getMessage());
		}
	}

	public void search(PASearchRequestRecord searchQuestion) throws RemoteException {
		searcher = new Searcher();
		PAAnswerRecord answer = new PAAnswerRecord();
		searcher.startSearch(searchQuestion);
		List documentInfo = searcher.getSearchList();
		answer.setPaID(PAGlobal.paData.providerAgentID);
		answer.setDocumentInfo(documentInfo);
		answer.setQuestionID(searchQuestion.getQuestionID());
		answer.setUserAgentID(searchQuestion.getUserAgentID());
		putPASearchAnswerToUA(answer);
	}

	public void putPASearchAnswerToUA(PAAnswerRecord answer) throws RemoteException {
		try {
			this.componentManager.putPASearchAnswerToUA(
				PAGlobal.paData.providerAgentID,
				PAGlobal.paData.caSessionID,
				answer);
		} catch (InvalidAgentException e) {
			e.printStackTrace();
		} catch (InternalEVerlageError e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			final ProviderAgent pa = new ProviderAgent();
			pa.login();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					pa.logout();
				}
			});
		} catch (Exception e) {
			System.out.println("PA Error: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#initProperties(java.lang.String, java.lang.Class)
	 */
	public void initProperties() throws InternalEVerlageError {
		// TODO Auto-generated method stub
		this.pHandler = new PropertyHandler();
		pHandler.registerProperty("pa-java.properties", this);
	}

}
