/**
 * $Id: ProviderAgentAbs.java,v 1.3 2003/03/13 17:30:00 waffel Exp $ 
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
import de.everlage.ca.componentManager.comm.extern.PAAnswerRecord;
import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
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
		
	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#getDocumentWithID(long)
	 */
	public void getDocumentWithID(long documentID) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#search(de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord)
	 */
	public void search(PASearchRequestRecord paSearchRec) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#putPASearchAnswerToUA(de.everlage.ca.componentManager.comm.extern.PAAnswerRecord)
	 */
	public void putPASearchAnswerToUA(PAAnswerRecord paAnswerRec) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
