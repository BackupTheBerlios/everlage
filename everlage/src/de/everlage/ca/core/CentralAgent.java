/* $Id: CentralAgent.java,v 1.4 2003/02/17 14:55:03 waffel Exp $ */

package de.everlage.ca.core;

import java.rmi.Naming;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.everlage.ca.CentralAgentInt;
import de.everlage.ca.accountManager.core.AccountManagerImpl;
import de.everlage.ca.accountManager.core.LocalAccountManager;
import de.everlage.ca.componentManager.core.ComponentManagerImpl;
import de.everlage.ca.componentManager.core.LocalComponentManager;
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
	/* name des ComponentManagers, welcher über die RMI-Schnittstelle benutzt wird; wird vom Property-file initialisiert*/
	private static String COMPONENT_MANAGER = null;
	/* instanz des LocalAccountManagers, um von anderen Managern an die lokalen Methoden dieses Managers heranzukommen */
	public static LocalAccountManager localAccountManager = null;
	/* instanz des LocalUserManagers, um von anderen Managern an die lokalen Methoden dieses Managers heranzukommen */
	public static LocalUserManager localUserManager = null;
	/* instanz des LocalComponentManagers, um von anderen Managern an die loakalen Methoden dieses Managers heranzukommen */
	public static LocalComponentManager localComponentManager = null;

	public static DBMediator dbMediator = null;
	public static PropertyHandler propHandler = null;

	/**
	 * Default Konstruktor des Central Agents. Hier wird die Property-datei des CentralAgents geladen
	 * und der DBMediator initialisiert
	 */
	public CentralAgent() {
	}

	/**
	 * Öffnet das Property-File das CentralAgents (ca.properties) und fügt die hier angegebenen
	 * Properties zu den systemweiten Properties hinzu 
	 * @throws InternalEVerlageError Wenn das Property-file nicht gelesen werden kann
	 */
	public void initProps() throws InternalEVerlageError {
		// neuen Property-Handler installieren
		propHandler = new PropertyHandler();
		//  CentralAgent Properties registrieren
		propHandler.registerProperty("ca.properties", this);
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug("finish initProps");
		}
	}


	/**
	 * Initialisiert den Datenbankverwalter mit den, in der Property-datei angegebenen Daten.
	 * @throws InternalEVerlageError Wenn die Datenbankverbindung nicht hergestellt werden kann.
	 */
	public void initDBMediator() throws InternalEVerlageError {
		String dbDriverStr =
			(String) CAGlobal.dbDrivers.get(propHandler.getProperty("dbSystem", this).toUpperCase());
		String dbURLStr =
			(String) CAGlobal.dbUrls.get(propHandler.getProperty("dbSystem", this).toUpperCase())
				+ propHandler.getProperty("dbDatabase", this);
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(dbDriverStr + "  " + dbURLStr);
		}
		dbMediator =
			new DBMediator(
				dbDriverStr,
				dbURLStr,
				propHandler.getProperty("dbLogin", this),
				propHandler.getProperty("dbPassword", this),
				propHandler.getProperty("conNumber", this));
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug("finish initDBMediator");
		}
	}

	/**
	 * Initialisiert das Loggen für den CentralAgent. Dabei wird eine Property-Datei (ca-log4j.
	 * properties) geladen, in welcher die Einstellungen für das Loggen vorhanden sind. Die Logdatei
	 * wird so geöffnet, dass die Einstellungen zur Laufzeit geändert werden können, ohne den CA neu
	 * zu starten (@see PropertyConfigurator#configureAndWatch(java.lang.String) )
	 */
	public void initLogging() {
		CAGlobal.log = Logger.getLogger(this.getClass().getName());
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePrefix = this.getClass().getPackage().getName();
		packagePrefix = packagePrefix.replace('.', '/');
		PropertyConfigurator.configureAndWatch(packagePrefix + "/ca-log4j.properties");
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
			CAGlobal.log.info("init ca done");
			// den registry server zuweisen
			REGESTRY_SERVER = "//localhost/";

			// den namen für den UserManager zuweisen
			USER_MANAGER = "UserManager";

			// den namen für den AccountManager zuweisen
			ACCOUNT_MANAGER = "AccountManager";

			// den namen für den ComponentManager zuweisen
			COMPONENT_MANAGER = "ComponentManager";

			// neuen RMISecurityManager erzeugen und im Java-System anmelden
			// RMISecurityManager sec = new RMISecurityManager();
			//System.setSecurityManager(sec);
			// Security Provider auf ssl-verschlüsselung setzen
			// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			// ComponentManager erzeugen
			ComponentManagerImpl componentmanager = new ComponentManagerImpl();
			// ComponentManager per RMI bekannt machen
			Naming.rebind(REGESTRY_SERVER + COMPONENT_MANAGER, componentmanager);
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("finish rebind ComponentManager");
			}
			// UserManager erzeugen
			UserManagerImpl usermanager = new UserManagerImpl();
			// UserManager per RMI bekannt machen
			Naming.rebind(REGESTRY_SERVER + USER_MANAGER, usermanager);
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("finish rebind UserManager");
			}
			// AccountManager erzeugen
			AccountManagerImpl accountmanager = new AccountManagerImpl();
			// AccountManager per RMI bekannt machen
			Naming.rebind(REGESTRY_SERVER + ACCOUNT_MANAGER, accountmanager);
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("finish rebind AccountManager");
			}
			CAGlobal.log.info("CentralAgent started ...");
		} catch (Exception e) {
			CAGlobal.log.error(e);
		}
	}
}
