/**
 * $Id: UserAgent.java,v 1.4 2003/02/11 15:23:46 waffel Exp $  
 * File: UserAgent.java    Created on Jan 10, 2003
 *
*/
package de.everlage.ua.minimal.text;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import de.everlage.ca.componentManager.comm.extern.PAData;
import de.everlage.ca.componentManager.comm.extern.UALoginResult;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.exception.extern.InvalidAgentException;
import de.everlage.ca.userManager.comm.extern.UserData;
import de.everlage.ca.userManager.exception.extern.AnonymousLoginNotPossible;
import de.everlage.pa.ProviderAgentInt;
import de.everlage.pa.comm.extern.TitleSearchRes;
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
			this.providerAgentData = uaData.providerAgentList;
			System.out.println("found " + uaData.providerAgentList.size() + " provider Agents");
			for (Iterator it = uaData.providerAgentList.keySet().iterator(); it.hasNext();) {
				Long keyID = (Long) it.next();
				System.out.println(((PAData) uaData.providerAgentList.get(keyID)).paRMIAddress);
			}
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

	public void loginTestUser() {
		try {
			UserData userData =
				userManager.userLogin(uaData.userAgentID, uaData.caSessionID, "testuser", "test");
			System.out.println(
				"user "
					+ userData.title
					+ " "
					+ userData.firstName
					+ " "
					+ userData.lastName
					+ " logged in");
		} catch (Exception e) {
      e.printStackTrace();
    }
	}

	public void logout() {
		try {
			componentManager.UALogout(uaData.userAgentID, uaData.caSessionID);
		} catch (Exception e) {
			System.out.println("Logout Error: " + e.getMessage());
		}
	}

	public void searchBookTitle() {
		List result = new LinkedList();
		try {
			if (providerAgentData == null) {
				return;
			}
			// alle Provider Agents fragen
			for (Iterator it = providerAgentData.keySet().iterator(); it.hasNext();) {
				Long keyID = (Long) it.next();
				PAData pdata = (PAData) providerAgentData.get(keyID);
				ProviderAgentInt pa = pdata.providerAgent;
				TitleSearchRes searchRes = pa.searchTitle("eine waffel");
				System.out.println("search on pa finished " + searchRes);
				if (searchRes.getEmptyFlag() != TitleSearchRes.EMPTY) {
					result.add(searchRes);
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		// alles ausgeben:
		System.out.println("Found Book: ");
		for (Iterator it = result.iterator(); it.hasNext();) {
			TitleSearchRes res = (TitleSearchRes) it.next();
			System.out.println("Title: " + res.getTitle());
			System.out.print("Highlight Title: ");
			Map highTitle = res.getHighlightTitle();
			SortedMap highTitleSort = new TreeMap(highTitle);
			int begin = 0;
			for (Iterator it2 = highTitleSort.keySet().iterator(); it2.hasNext();) {
				Integer start = (Integer) it2.next();
				Integer end = (Integer) highTitleSort.get(start);
				System.out.print(res.getTitle().substring(begin, start.intValue()));
				System.out.print("<" + res.getTitle().substring(start.intValue(), end.intValue()) + ">");
				begin = end.intValue() + 1;
			}
			System.out.println(res.getTitle().substring(begin - 1, res.getTitle().length()));
			SortedMap authors = new TreeMap(res.getAuthors());
			if (authors.size() > 1) {
				for (Iterator it2 = authors.keySet().iterator(); it2.hasNext();) {
					Integer id = (Integer) it2.next();
					System.out.println("Author(" + id + "): " + authors.get(id));
				}
			} else {
				System.out.println("Author: " + authors.get(new Integer(1)));
			}
			System.out.println("Erscheinungsjahr: " + res.getYear());
		}
	}

	public static void main(String[] args) {
		try {
			UserAgent ua = new UserAgent();
			ua.init();
			ua.login();
			ua.searchBookTitle();
			ua.loginAnonymousUser();
      ua.loginTestUser();
			ua.logout();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
