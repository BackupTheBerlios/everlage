/* $Id: CentralAgent.java,v 1.1 2003/01/16 13:03:07 waffel Exp $ */

package de.everlage.ca.core;

import java.rmi.Naming;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.everlage.ca.CentralAgentInt;
import de.everlage.ca.accountManager.core.AccountManagerImpl;
import de.everlage.ca.accountManager.core.LocalAccountManager;
import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ca.userManager.core.LocalUserManager;
import de.everlage.ca.userManager.core.UserManagerImpl;

/**
 * Zentraler Agent des eVerlage Systems. Hier werden alle Manaqer des Agenten miteinander verknüpft,
 * die Datenbankverbindungen verwaltet und die Schnittstellen über RMI bekannt gemacht.
 * @author waffel
 * 
 */
public final class CentralAgent implements CentralAgentInt {
	/* IP-Adresse des Regestry servers; wird vom Property-file initialisiert */
	private static String REGESTRY_SERVER = null;
	/* name des UserAgents, welcher über die RMI-Schnittstelle benutzt wird; wird vom Property-file initialisiert */
	private static String USER_MANAGER = null;
	/* name des AccountManagers, welcher über die RMI-Schnittstelle benutzt wird; wird vom Property-file initialisiert */
	private static String ACCOUNT_MANAGER = null;
	/* instanz des LocalAccountManagers, um von anderen Managern an die lokalen Methoden dieses Manager heranzukommen */
	public static LocalAccountManager l_accountManager = null;
	/* instanz des LocalUserManagers, um von anderen Managern an die lokalen Methoden dieses Manager heranzukommen */
	public static LocalUserManager l_userManager = null;

	private DBMediator dbMediator = null;
	private PropertyHandler propHandler = null;

	/**
	 * Default Konstruktor des Central Agents. Hier wird die Property-datei des CentralAgents geladen
	 * und der DBMediator initialisiert
	 */
	public CentralAgent() {
	}

	public void initProps() throws InternalEVerlageError {
		// neuen Property-Handler installieren
		propHandler = new PropertyHandler();
		//  CentralAgent Properties registrieren
		propHandler.registerProperty("./ca.properties", this);
	}

	public void initDBMediator() throws InternalEVerlageError {
		this.dbMediator =
			new DBMediator(
				propHandler.getProperty("dbDriver", this),
				propHandler.getProperty("dbURL", this),
				propHandler.getProperty("dbLogin", this),
				propHandler.getProperty("dbPassword", this),
				propHandler.getProperty("conNumber", this));
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.CentralAgentInt#getDBConnection()
	 */
	public Connection getDBConnection() throws InternalEVerlageError {
		return this.dbMediator.getConnection();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.CentralAgentInt#freeDBConnection(java.sql.Connection)
	 */
	public void freeDBConnection(Connection con) {
		this.dbMediator.freeConnection(con);
	}

	public void initLogging() {
		CAGlobal.log = Logger.getLogger(this.getClass().getName());
		PropertyConfigurator.configureAndWatch("./ca-log4j.properties");
	}

	/**
	 * Instanziiert den CentralAgent und macht die Manager über RMI bekannt.
	 * @param args werden im Moment nicht benutzt
	 */
	public static void main(String[] args) {
		try {
      // neuen CantralAgent erzeugen
      CentralAgent ca = new CentralAgent();
      ca.initLogging();
      CAGlobal.log.info("starting CentralAgent ...");
      ca.initProps();
      ca.initDBMediator();

			// den registry server zuweisen
			REGESTRY_SERVER = "//localhost/";

			// den namen für den UserManager zuweisen
			USER_MANAGER = "UserManager";

			// den namen für den AccountManager zuweisen
			ACCOUNT_MANAGER = "AccountManager";

			// neuen RMISecurityManager erzeugen und im Java-System anmelden
			// RMISecurityManager sec = new RMISecurityManager();
			//System.setSecurityManager(sec);
			// Security Provider auf ssl-verschlüsselung setzen
			// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			// ComponentManager erzeugen

			// ComponentManager per RMI bekannt machen
			// UserManager erzeugen
			UserManagerImpl usermanager = new UserManagerImpl();
			// UserManager per RMI bekannt machen
			Naming.rebind(REGESTRY_SERVER + USER_MANAGER, usermanager);
			// AccountManager erzeugen
			AccountManagerImpl accountmanager = new AccountManagerImpl();
			// AccountManager per RMI bekannt machen
			Naming.rebind(REGESTRY_SERVER + ACCOUNT_MANAGER, accountmanager);
			CAGlobal.log.info("CentralAgent started ...");
		} catch (Exception e) {
			CAGlobal.log.error(e);
		}
	}
}
