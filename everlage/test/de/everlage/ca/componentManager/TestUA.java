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
import java.util.Map;

import de.everlage.TestGlobal;
import de.everlage.ua.UserAgentInt;

/**
 * @author waffel
 */
public class TestUA extends UnicastRemoteObject implements UserAgentInt {

  private ComponentManagerInt componentManager;

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#init()
	 */
	/**
	 * @throws RemoteException
	 */
	protected TestUA() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws RemoteException {
    try {
		Naming.rebind(TestGlobal.uaRMIAddress, this);
    setComponentManager((ComponentManagerInt)Naming.lookup(TestGlobal.componentManagerRMI));
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

}
