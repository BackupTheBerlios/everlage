/**
 * $ID$
 *  File: CAInstall.java    Created on Jan 15, 2003
 *
*/
package de.everlage.ca.install;

import java.util.HashMap;
import java.util.Map;

import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class CAInstall {

	public static DBMediator dbMediator;
  
  private static Map dbDrivers;
  
  static {
    dbDrivers = new HashMap(2);
    dbDrivers.put("ORACLE", "oracle.jdbc.driver.OracleDriver");
    dbDrivers.put("POSTGRES", "postgres.jdbc.driver.PostgresDriver");
  }

	public void init() throws InternalEVerlageError {
		String dbDriver = (String)dbDrivers.get("ORACLE");
		String dbURL = "jdbc:oracle:thin:@goliath.imn.htwk-leipzig.de:1521:imnlehre";
		String dbLogin = "ca_ev1";
		String dbPassword = "ca,ev1";
		String numCon = "1";
		dbMediator = new DBMediator(dbDriver, dbURL, dbLogin, dbPassword, numCon);
	}

	public void startInstall() throws InternalEVerlageError {
    CASQLInstaller caInstaller = new CASQLInstaller();
    caInstaller.startInstall();
	}

	public void startUnInstall() {
	}

	public static void main(String[] args) {
		try {
			CAInstall caInstall = new CAInstall();
      caInstall.init();
			caInstall.startInstall();
		} catch (InternalEVerlageError e) {
			System.out.println("Error: "+e.getMessage());
		}
	}
}
