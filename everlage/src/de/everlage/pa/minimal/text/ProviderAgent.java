/**
 *  File: PrivoderAgent.java    
 * Created on Jan 30, 2003
 *
*/
package de.everlage.pa.minimal.text;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Random;

import de.everlage.ca.componentManager.comm.extern.PALoginResult;
import de.everlage.pa.ProviderAgentAbs;
import de.everlage.pa.comm.extern.TitleSearchRes;

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
			super.registerComponents("//127.0.0.1/");
			// ProviderAgent als RMI-Objekt bekannt machen
			Naming.rebind("//127.0.0.1/TextPA", this);
			System.out.println("rebind ok");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void login() {
		try {
			final long paSessionID = new Random().nextLong();
			paData = componentManager.PALogin("TestTextPA", "test", "//127.0.0.1/TextPA", paSessionID);
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
	/**
	 * only for demonstration!!! This is not the real search method that a providerAgent should be use
	 * @see de.everlage.pa.ProviderAgentInt#searchTitle(String)
	 */
	public TitleSearchRes searchTitle(String searchStr) {
    System.out.println("start Search");
		TitleSearchRes result = new TitleSearchRes();
    result.setEmptyFlag(TitleSearchRes.EMPTY);
		if (searchStr.indexOf("waffel") != -1) {
      Hashtable authors = new Hashtable();
      authors.put(new Integer(1), "Thomas Wabner");
      authors.put(new Integer(2), "Ein kleiner Hick");
      result.setAuthors(authors);
      result.setTitle("Das hatte waffel schon immer gesagt");
      Hashtable highTitle = new Hashtable();
      highTitle.put(new Integer(10), new Integer(16));
      result.setHighlightTitle(highTitle);
      result.setYear("2006");
      result.setEmptyFlag(TitleSearchRes.FULL);
		}
    System.out.println("end search");
		return result;
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

}
