/*
 * Created on Feb 21, 2003
 * File TestUA.java
 * 
 */
package de.everlage.ca.componentManager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import de.everlage.TestGlobal;
import de.everlage.ca.componentManager.exception.extern.InvalidQueryException;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.UserManagerInt;
import de.everlage.ua.UserAgentInt;

/**
 * @author waffel
 */
public class TestUA extends UnicastRemoteObject implements UserAgentInt {

	private ComponentManagerInt componentManager;
	private UserManagerInt userManager;
	private long agentID;
	private long caSessionID;
	private List answerList;

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#init()
	 */
	/**
	 * @throws RemoteException
	 */
	public TestUA() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws RemoteException {
		try {
			Naming.rebind(TestGlobal.uaRMIAddress, this);
			setComponentManager((ComponentManagerInt) Naming.lookup(TestGlobal.componentManagerRMI));
			setUserManager((UserManagerInt) Naming.lookup(TestGlobal.userManagerRMI));
      setAnswerList(null);
		} catch (MalformedURLException e) {
			throw new RemoteException(e.getMessage());
		} catch (NotBoundException e) {
			throw new RemoteException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#updateProviderAgentData(java.util.Map)
	 */
	public void updateProviderAgentData(Map pas) throws RemoteException {
		// TODO Auto-generated method stub

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

	/**
	 * @return UserManagerInt
	 */
	public UserManagerInt getUserManager() {
		return userManager;
	}

	/**
	 * Sets the userManager.
	 * @param userManager The userManager to set
	 */
	public void setUserManager(UserManagerInt userManager) {
		this.userManager = userManager;
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#putAnswers(java.util.List)
	 */
	public void putAnswers(List answers) throws RemoteException {
		setAnswerList(answers);
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

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#search()
	 */
	public void search(String searchString) throws RemoteException {
		try {
			this.componentManager.sendSearchToAllPAs(this.agentID, this.caSessionID, searchString);
		} catch (InvalidQueryException e) {
			e.printStackTrace();
		} catch (InvalidAgentException e) {
			e.printStackTrace();
		}
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

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#initProperties()
	 */
	public void initProperties() throws RemoteException, InternalEVerlageError {
		// TODO Auto-generated method stub
		
	}

}
