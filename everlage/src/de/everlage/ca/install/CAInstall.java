/**
 * $Id: CAInstall.java,v 1.3 2003/01/20 16:09:58 waffel Exp $  
 * File: CAInstall.java    Created on Jan 15, 2003
 *
*/
package de.everlage.ca.install;

import jargs.gnu.CmdLineParser;
import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * The Installer for the CentralAgent. The database would be created with all needed tables.
 * @author waffel
 *
 * 
 */

public final class CAInstall {

	private String dbDatabase;
	private String dbSystem;
	private String dbLogin;
	private String dbPassword;
	private boolean remove = false;

	/**
	 * Database connection holder
	 */
	public static DBMediator dbMediator;

	/**
	 * Initialize the database connection
	 * @throws InternalEVerlageError
	 */
	public void init() throws InternalEVerlageError {
		String dbDriverStr = (String) CAGlobal.dbDrivers.get(this.dbSystem.toUpperCase());
		String dbURLStr =
			(String) CAGlobal.dbUrls.get(this.dbSystem.toUpperCase()) + this.dbDatabase.toLowerCase();
		String numCon = "1";
		dbMediator = new DBMediator(dbDriverStr, dbURLStr, dbLogin, dbPassword, numCon);
	}

	/**
	 * Starts the install for the given (on command line) DatabaseSystem. If the Installer detectes an
	 * error the InternalEVerlageError is thrown.
	 * @throws InternalEVerlageError if the installer detectes an error
	 */
	public void startInstall() throws InternalEVerlageError {
		CASQLInstaller caInstaller = new CASQLInstaller(this.dbSystem.toUpperCase());
		caInstaller.startInstall();
	}

	/**
	 * removes all tables in the database
	 */
	public void startUnInstall() throws InternalEVerlageError {
		CASQLInstaller caUnInstaller = new CASQLInstaller(this.dbSystem.toUpperCase());
		caUnInstaller.startUnInstall();
	}

	public void initCommandLine(String[] args) throws InternalEVerlageError {
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option verbose = parser.addBooleanOption('v', "verbose");
		CmdLineParser.Option dbSystem = parser.addStringOption('s', "system");
		CmdLineParser.Option database = parser.addStringOption('d', "database");
		CmdLineParser.Option dbUser = parser.addStringOption('u', "user");
		CmdLineParser.Option dbPassword = parser.addStringOption('p', "password");
		CmdLineParser.Option remove = parser.addBooleanOption('r', "remove");
		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		// check if verbose is asked
		Boolean verboseValue = (Boolean) parser.getOptionValue(verbose);
		if (verboseValue != null) {
			if (verboseValue.booleanValue()) {
				printVerboseInfo();
				System.exit(0);
			}
		}
		// check if remove is asked
		Boolean removeValue = (Boolean) parser.getOptionValue(remove);
		if (removeValue != null) {
			this.remove = removeValue.booleanValue();
		}
		this.dbSystem = (String) parser.getOptionValue(dbSystem);
		this.dbDatabase = (String) parser.getOptionValue(database);
		this.dbLogin = (String) parser.getOptionValue(dbUser);
		this.dbPassword = (String) parser.getOptionValue(dbPassword);
		if ((this.dbSystem == null)
			|| (this.dbDatabase == null)
			|| (this.dbLogin == null)
			|| (this.dbPassword == null)) {
			printUsage();
			System.exit(2);
		}
	}

	/**
	 * Prints the usage for this Program.
	 */
	private void printUsage() {
		System.out.println();
		System.err.println(
			"usage: prog [{-v,--verbose}] {-s,--system} {-d,--database} {-u,--user} {-p,--password} [{-r, --remove}]");
		System.out.println();
	}

	/**
	 * Prints verbose information about the commandline parameters for this Program.
	 */
	private void printVerboseInfo() {
		System.out.println();
		System.out.println("-v,--verbose\t\t this Message");
		System.out.println("-s,--system\t\t the Database System, e.g. postgres or oracle");
		System.out.println("-d,--databse\t\t name of the database, e.g. everlage");
		System.out.println("-u,--user\t\t loginname for the database user");
		System.out.println("-p,--password\t\t password for the database user");
    System.out.println("-r,--remove\t\t removes all contents from the database (like uninstall)");
		System.out.println();
	}

	/**
	 * Starts the Installer with the given arguments.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CAInstall caInstall = new CAInstall();
			caInstall.initCommandLine(args);
			caInstall.init();
			if (caInstall.remove) {
				caInstall.startUnInstall();
			} else {
				caInstall.startInstall();
			}
		} catch (InternalEVerlageError e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(2);
		}
	}
}
