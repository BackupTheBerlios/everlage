/**
 *  File: UserAgentAbs.java    
 * Created on Jan 20, 2003
 *
*/
package de.everlage.ua;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.userManager.UserManagerInt;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public abstract class UserAgentAbs extends UnicastRemoteObject {

	protected UserManagerInt userManager;
	protected ComponentManagerInt componentManager;

	public UserAgentAbs() throws RemoteException {
		super();
	}

	public void registerInterfaces(String rmiServer) {
		try {
			// ComponentManager holen
			componentManager = (ComponentManagerInt) Naming.lookup(rmiServer + "ComponentManager");
			// UserManager holen
			userManager = (UserManagerInt) Naming.lookup(rmiServer + "UserManager");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void tellRMIAddress(String uaRMIServer, String uaName, Remote uaClass)
		throws RemoteException, MalformedURLException {
		Naming.rebind(uaRMIServer + uaName, uaClass);
    System.out.println("rebind finished on "+uaRMIServer+uaName+uaClass);
	}

	public abstract void init();
}
