/**
 *  File: UserAgentInt.java    
 * Created on Jan 21, 2003
 *
*/
package de.everlage.ua;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public interface UserAgentInt extends Remote {

	public void init() throws RemoteException;

}
