/**
 *  File: PrivoderAgent.java    
 * Created on Jan 30, 2003
 *
*/
package de.everlage.pa.minimal.text;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.DocumentResult;
import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.ca.core.PropertyHandler;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.pa.ProviderAgentAbs;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public class ProviderAgent extends ProviderAgentAbs {

	private PALoginResult paData;

	/**
	 * Constructor.
	 * @throws RemoteException
	 */
	public ProviderAgent() throws RemoteException {
		super();
		try {
			super.registerComponents(pHandler.getProperty("CentralAgentRMIAddress", this));
			// ProviderAgent als RMI-Objekt bekannt machen
			Naming.rebind(pHandler.getProperty("OwnRMIURL", this), this);
			System.out.println("rebind ok");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void login() {
		try {
			final long paSessionID = new Random().nextLong();
			paData =
				componentManager.PALogin(
					pHandler.getProperty("Login", this),
					pHandler.getProperty("Password", this),
					pHandler.getProperty("OwnRMIUrl", this),
					paSessionID);
			System.out.println("PA logged in with ID" + paData.providerAgentID);
		} catch (Exception e) {
			System.out.println("Login Error: " + e.getMessage());
		}
	}

	public void logout() {
		try {
			componentManager.PALogout(this.paData.providerAgentID, this.paData.caSessionID);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			final ProviderAgent pa = new ProviderAgent();
			pa.login();
			//pa.logout();
		} catch (Exception e) {
			System.out.println("PA Error: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#initProperties()
	 */
	public void initProperties() throws InternalEVerlageError {
		// TODO Auto-generated method stub
		pHandler = new PropertyHandler();
		pHandler.registerProperty("pa-text.properties", this);
	}

	/* (non-Javadoc)
	 * @see de.everlage.pa.ProviderAgentInt#getDocumentWithID(java.lang.String)
	 */
	public DocumentResult getDocumentWithID(String documentID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
