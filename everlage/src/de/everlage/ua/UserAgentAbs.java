/**
 *  File: UserAgentAbs.java    
 * Created on Jan 20, 2003
 *
*/
package de.everlage.ua;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.componentManager.comm.extern.PAData;
import de.everlage.ca.userManager.UserManagerInt;

public abstract class UserAgentAbs extends UnicastRemoteObject implements UserAgentInt {

	/**
	 * Schnittestelle zum UserManager des CentralAgent
	 */
	protected UserManagerInt userManager;
	/**
	 * Schnittstelle zum ComponentManager des CentralAgent
	 */
	protected ComponentManagerInt componentManager;
  
  protected Map providerAgentData;
  

	/**
	 * Constructor for UserAgentAbs.
	 * @throws RemoteException
	 */
	public UserAgentAbs() throws RemoteException {
		super();
	}

	/**
	 * Registriert die verschiedenen Komponenten des CentralAgents per RMI und bindet diese an eigene
	 * Variablen. Damit ist die Schnittstelle zum CentralAgent festgelegt.
	 * @param rmiReg URL zu dem RMI Registry Server unter welchem der CentralAgent läuft
	 * @throws RemoteException Wenn ein RMI Fehler auftrat
	 * @throws MalformedURLException Wenn die URL nicht stimmt
	 * @throws NotBoundException Wenn die Komponenten nicht per RMI gebunden werden können
	 */
	protected void registerComponents(String rmiReg)
		throws RemoteException, MalformedURLException, NotBoundException {
		componentManager = (ComponentManagerInt) Naming.lookup(rmiReg + "ComponentManager");
		System.out.println("ComponentManager registered");
		userManager = (UserManagerInt) Naming.lookup(rmiReg + "UserManager");
		System.out.println("UserManager registered");
	}

	public void init() throws RemoteException {
		try {
			registerComponents("//127.0.0.1/");
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (NotBoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#updateProviderAgentList(java.util.Map)
	 */
	public void updateProviderAgentData(Map pas) throws RemoteException {
    System.out.println("updateProviderAgentDate begin ");
    this.providerAgentData = new Hashtable();
    this.providerAgentData.putAll(pas);
    Long keyID;
    for (Iterator it = this.providerAgentData.keySet().iterator(); it.hasNext();) {
      keyID = (Long)it.next();
      System.out.println(""+((PAData)this.providerAgentData.get(keyID)).paRMIAddress);
    }
  }

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#putAnsers(java.util.List)
	 */
	public void putAnswers(List answerList) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#search()
	 */
	public void search(String searchString) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
