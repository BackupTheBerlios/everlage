/**
 * $Id: UserAgent.java,v 1.3 2003/01/22 16:54:26 waffel Exp $  
 * File: UserAgent.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ua.minimal.text;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.ua.UserAgentAbs;

/**
 * Beispiel eines textbasierten UserAgents
 * @author waffel
 *
 * 
 */
public class UserAgent extends UserAgentAbs {

	private UALoginResult uaData;

	public UserAgent() throws RemoteException {
		super();
	}

	public void init() throws RemoteException {
		super.init();
		try {
			// UserAgent als RMI-Objekt bekannt machen
			Naming.rebind("//127.0.0.1/TextUA", this);
			System.out.println("rebind ok");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void login() {
		try {
			long uaSessionID = new Random().nextLong();
			uaData = componentManager.UALogin("TestTextUA", "test", "//127.0.0.1/TextUA", uaSessionID);
		} catch (Exception e) {
			System.out.println("Login Error: " + e.getMessage());
		}
	}

	public void loginAnonymousUser() {
		try {
			System.out.println(
				"new UserID: " + userManager.anonymousLogin(uaData.userAgentID, uaData.caSessionID));
		} catch (AnonymousLoginNotPossible e) {
			System.out.println("Error: Anonymous Login not possible");
		} catch (InternalEVerlageError e) {
      System.out.println("Error: Interner SystemFehler");
    } catch (InvalidAgentException e) {
      System.out.println("Error: Invalid Agent"); 
    } catch (RemoteException e) {
      System.out.println("RMI Fehler");
    }
	}
	public void logout() {
		try {
			componentManager.UALogout(uaData.userAgentID, uaData.caSessionID);
		} catch (Exception e) {
			System.out.println("Logout Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			UserAgent ua = new UserAgent();
			ua.init();
			ua.login();
			ua.loginAnonymousUser();
			ua.logout();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
