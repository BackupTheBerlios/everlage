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

import de.everlage.ca.componentManager.ComponentManagerInt;
import de.everlage.ca.userManager.UserManagerInt;

public class UserAgentAbs extends UnicastRemoteObject implements UserAgentInt {

	protected UserManagerInt userManager;
	protected ComponentManagerInt componentManager;

	/**
	 * Constructor for UserAgentAbs.
	 * @throws RemoteException
	 */
	public UserAgentAbs() throws RemoteException {
		super();
	}

	public void registerComponents(String rmiReg)
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

}
