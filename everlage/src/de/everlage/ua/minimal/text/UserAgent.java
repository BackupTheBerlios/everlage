/**
 *  File: UserAgent.java    
 * Created on Jan 10, 2003
 *
*/
package de.everlage.ua.minimal.text;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ua.UserAgentAbs;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public class UserAgent extends UserAgentAbs implements Remote {

	public UserAgent() throws RemoteException {
		super();
	}

	public static void main(String[] args) {
		try {
			UserAgent ua = new UserAgent();
			ua.init();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void init() {
		try {
			long uaSessionID = new Random().nextLong();
			registerInterfaces("//127.0.0.1/");
			tellRMIAddress("//127.0.0.1/", "TextUA", this);
			UALoginResult uaLoginRes =
				componentManager.UALogin("TestTextUA", "test", "//127.0.0.1/TextUA", uaSessionID);
			System.out.println(uaLoginRes.caSessionID + "  " + uaLoginRes.userAgentID);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
