/**
 * $Id: ProviderAgentAbs.java,v 1.2 2003/02/11 15:22:36 waffel Exp $ 
 * File: ProviderAgentAbs.java    Created on Jan 29, 2003
 *
*/
package de.everlage.pa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.userManager.UserManagerInt;
import de.everlage.pa.comm.extern.TitleSearchRes;

/**
 * Abstrakte Klasse für alle ProviderAgents. Hier werden die Schnittstellen zum CentralAgent per RMI
 * in der Methode #registerComponents angebunden.
 * @author waffel
 *
 * 
 */
public class ProviderAgentAbs extends UnicastRemoteObject implements ProviderAgentInt {

	/**
	 * Schnittestelle zum UserManager des CentralAgent
	 */
	protected UserManagerInt userManager;
	/**
	 * Schnittstelle zum ComponentManager des CentralAgent
	 */
	protected ComponentManagerInt componentManager;

	public ProviderAgentAbs() throws RemoteException {
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

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#searchTitle(java.lang.String)
	 */
	public TitleSearchRes searchTitle(String searchStr) throws RemoteException {
		return null;
	}
}
