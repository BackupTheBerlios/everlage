/**
 *  File: LocalAccountManager.java    
 * Created on Jan 13, 2003
 *
*/
package de.everlage.ca.accountManager.core;

import de.everlage.ca.accountManager.comm.extern.Account;
import de.everlage.ca.core.CAGlobal;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class LocalAccountManager {
  
  public Account getAccount(long userID, long accountID) {
    CAGlobal.log.debug(this);
    return new Account();
  }

}
