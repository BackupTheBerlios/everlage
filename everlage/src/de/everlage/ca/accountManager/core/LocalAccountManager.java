/**
 *  File: LocalAccountManager.java    
 * Created on Jan 13, 2003
 *
*/
package de.everlage.ca.accountManager.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	public long createAccountForUser(long userID, double balance, Connection dbConnection)
		throws InternalEVerlageError {
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			pstmt = dbConnection.prepareStatement(this.pHandler.getProperty("getNextAccountID", this));
      res = pstmt.executeQuery();
      if (!res.next()) {
        throw new InternalEVerlageError("Cannot create new Account (Sequence Propblem"); 
      }
      long accountID = res.getLong(1);
      res.close();
      res=null;
			pstmt =
				dbConnection.prepareStatement(this.pHandler.getProperty("createAccountForUser", this));
			pstmt.setLong(1, accountID);
      pstmt.setLong(2, userID);
      pstmt.setDouble(3, balance);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
      return accountID;
		} catch (SQLException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} finally {
      try {
        if (pstmt != null) {
          pstmt.close(); pstmt = null;
        }
        if (res != null) {
          res.close(); res = null;
        }
      }  catch (Exception e) {}
		}
	}
}
