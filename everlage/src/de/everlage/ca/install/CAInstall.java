/**
 * $ID$
 *  File: CAInstall.java    Created on Jan 15, 2003
 *
*/
package de.everlage.ca.install;

import jargs.gnu.CmdLineParser;

import java.util.HashMap;
import java.util.Map;

import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * The Installer for the CentralAgent. The database would be created with all needed tables.
 * @author waffel
 *
 * 
 */

public final class CAInstall {

	/**
	 * Database connection holder
	 */
	public static DBMediator dbMediator;

	private static Map dbDrivers;
	private static Map dbUrls;

	private String dbDatabase;
	private String dbSystem;
	private String dbLogin;
	private String dbPassword;

	static {
		dbDrivers = new HashMap(2);
		dbUrls = new HashMap(2);
		dbDrivers.put("ORACLE", "oracle.jdbc.driver.OracleDriver");
		dbDrivers.put("POSTGRES", "org.postgresql.Driver");
		dbUrls.put("ORACLE", "jdbc:oracle:thin:@");
		dbUrls.put("POSTGRES", "jdbc:postgresql:");
	}

	/**
	 * Initialize the database connection
	 * @throws InternalEVerlageError
	 */
	public void init() throws InternalEVerlageError {
		String dbDriverStr = (String) dbDrivers.get(this.dbSystem.toUpperCase());
		String dbURLStr =
			(String) dbUrls.get(this.dbSystem.toUpperCase()) + this.dbDatabase.toLowerCase();
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
	 * does nothing at moment
	 */
	public void startUnInstall() {
	}

	public void initCommandLine(String[] args) throws InternalEVerlageError {
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option verbose = parser.addBooleanOption('v', "verbose");
		CmdLineParser.Option dbSystem = parser.addStringOption('s', "system");
		CmdLineParser.Option database = parser.addStringOption('d', "database");
		CmdLineParser.Option dbUser = parser.addStringOption('u', "user");
		CmdLineParser.Option dbPassword = parser.addStringOption('p', "password");
		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		Boolean verboseValue = (Boolean) parser.getOptionValue(verbose);
		if (verboseValue != null) {
			if (verboseValue.booleanValue()) {
				printVerboseInfo();
				System.exit(0);
			}
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
			"usage: prog [{-v,--verbose}] {-s,--system} {-d,--database} {-u,--user} {-p,--password}");
		System.out.println();
	}

	/**
	 * Prints verbose information about the commandline parameters for this Program.
	 */
	private void printVerboseInfo() {
		System.out.println();
		System.out.println("-v,--verbose\t\tThis Message");
		System.out.println("-s,--system\t\tThe Database System, e.g. postgres or oracle");
		System.out.println("-d,--databse\t\tname of the database, e.g. everlage");
		System.out.println("-u,--user\t\tloginname for the database user");
		System.out.println("-p,--password\t\tpassword for the database user");
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
			caInstall.startInstall();
		} catch (InternalEVerlageError e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(2);
		}
	}
}
