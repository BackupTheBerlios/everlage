/**
 *  File: UserAgent.java    
 * Created on Jan 10, 2003
 *
*/
package de.everlage.ua.minimal.text;

import java.rmi.Naming;

import de.everlage.ca.userManager.UserManagerInt;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public class UserAgent {



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
			// UserAgent als RMI-Objekt bekanntmachen
			UserManagerInt userManager = (UserManagerInt)Naming.lookup("//127.0.0.1/UserManager");
      // versichen, den ca.userManager aufzurufen
      userManager.userLogin(0, 0, "login", "password");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
