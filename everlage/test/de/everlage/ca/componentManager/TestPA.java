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

import de.everlage.TestGlobal;
import de.everlage.pa.ProviderAgentInt;
import de.everlage.pa.comm.extern.TitleSearchRes;

/**
 * @author waffel
 */
public class TestPA extends UnicastRemoteObject implements ProviderAgentInt {

  private ComponentManagerInt componentManager;

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
  
  public void init() throws RemoteException{
    try {
    Naming.rebind(TestGlobal.paRMIAddress, this);
    setComponentManager((ComponentManagerInt)Naming.lookup(TestGlobal.componentManagerRMI));
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

}
