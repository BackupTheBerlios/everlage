/**
 *  File: LocalAccountManager.java    
 * Created on Jan 13, 2003
 *
*/
package de.everlage.ca.accountManager.core;

import de.everlage.ca.LocalManagerAbs;
import de.everlage.ca.accountManager.comm.extern.Account;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class LocalAccountManager extends LocalManagerAbs {

	public LocalAccountManager() throws InternalEVerlageError {
		super();
		super.registerProperty("accountManager-sql.properties", this);
	}

	public Account getAccount(long userID, long accountID) {
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(this);
		}
		return new Account();
	}

}
