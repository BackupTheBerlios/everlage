/*
 * Created on Feb 27, 2003
 * File TestPA.java
 * 
 */
package de.everlage.ca.componentManager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.pa.ProviderAgentInt;
import de.everlage.pa.comm.extern.TitleSearchRes;

/**
 * @author waffel
 */
public class TestPA extends UnicastRemoteObject implements ProviderAgentInt {

	private ComponentManagerInt componentManager;
	private long agentID;
	private long caSessionID;

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#searchTitle(java.lang.String)
	 */
	/**
	 * @throws RemoteException
	 */
	protected TestPA() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws RemoteException {
		try {
			Naming.rebind(TestGlobal.paRMIAddress, this);
			setComponentManager((ComponentManagerInt) Naming.lookup(TestGlobal.componentManagerRMI));
		} catch (MalformedURLException e) {
			throw new RemoteException(e.getMessage());
		} catch (NotBoundException e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public TitleSearchRes searchTitle(String searchStr) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return ComponentManagerInt
	 */
	public ComponentManagerInt getComponentManager() {
		return componentManager;
	}

	/**
	 * Sets the componentManager.
	 * @param componentManager The componentManager to set
	 */
	public void setComponentManager(ComponentManagerInt componentManager) {
		this.componentManager = componentManager;
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#search(de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord)
	 */
	public void search(PASearchRequestRecord paSearchRec) throws RemoteException {
		String searchString = paSearchRec.getSearchString();
		PAAnswerRecord paAnswer = new PAAnswerRecord();
		if (searchString.equalsIgnoreCase("waffel")) {
			paAnswer.setPaID(1);
			paAnswer.setQuestionID(paSearchRec.getQuestionID());
			paAnswer.setUserAgentID(paSearchRec.getUserAgentID());
			List documentInfoList = new ArrayList(2);
			documentInfoList.add(new String("waffels erstes Buch"));
			documentInfoList.add(new String("waffels zweites Buch"));
			paAnswer.setDocumentInfo(documentInfoList);
			putPASearchAnswerToUA(paAnswer);
		} else {
			// leere Antwort
			paAnswer.setPaID(1);
			paAnswer.setQuestionID(paSearchRec.getQuestionID());
			paAnswer.setUserAgentID(paSearchRec.getUserAgentID());
			List documentInfoList = new ArrayList();
			paAnswer.setDocumentInfo(documentInfoList);
			putPASearchAnswerToUA(paAnswer);
		}

	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#putPASearchAnswerToUA(de.everlage.ca.componentManager.comm.extern.PAAnswerRecord)
	 */
	public void putPASearchAnswerToUA(PAAnswerRecord paAnswerRec) throws RemoteException {
		try {
			this.componentManager.putPASearchAnswerToUA(this.agentID, this.caSessionID, paAnswerRec);
		} catch (InvalidAgentException e) {
			e.printStackTrace();
		} catch (InternalEVerlageError e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#getDocumentWithID(long)
	 */
	public void getDocumentWithID(long documentID) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * @return long
	 */
	public long getAgentID() {
		return agentID;
	}

	/**
	 * @return long
	 */
	public long getCaSessionID() {
		return caSessionID;
	}

	/**
	 * Sets the agentID.
	 * @param agentID The agentID to set
	 */
	public void setAgentID(long agentID) {
		this.agentID = agentID;
	}

	/**
	 * Sets the caSessionID.
	 * @param caSessionID The caSessionID to set
	 */
	public void setCaSessionID(long caSessionID) {
		this.caSessionID = caSessionID;
	}

}
