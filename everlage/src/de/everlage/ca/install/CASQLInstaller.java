/**
 * $Id: CASQLInstaller.java,v 1.6 2003/01/23 17:22:17 waffel Exp $ 
 * File: CASQLInstaller.java    Created on Jan 15, 2003
 *
*/
package de.everlage.ca.install;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * The Installer to create the database tables.
 * @author waffel
 *
 * 
 */
public final class CASQLInstaller {

	private DBMediator db;
	private Properties prop;
	private String dbSystem;

	/**
	 * Constructor wich loads the Property.
	 * @param dbSystemStr name of the databaseSystem
	 * @throws InternalEVerlageError if the load of the Property fails
	 */
	public CASQLInstaller(String dbSystemStr) throws InternalEVerlageError {
		this.db = CAInstall.dbMediator;
		this.dbSystem = dbSystemStr;
		loadProperty();
	}

	/**
	 * Loads the Property file for an given database system (e.g. postgres or oracle). The location of
	 * the Property must the same as this class lives in the filesystem or jar. The name of the
	 * property has the prefix "cainstall-" , then the database system name (e.g. postgres) and the
	 * appendix ".properties".
	 * @throws InternalEVerlageError if the property file is not found or cannot load.
	 */
	private void loadProperty() throws InternalEVerlageError {
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			String packagePrefix = this.getClass().getPackage().getName();
			packagePrefix = packagePrefix.replace('.', '/');
			InputStream in =
				cl.getResourceAsStream(
					packagePrefix + "/cainstall-" + this.dbSystem.toLowerCase() + ".properties");
			this.prop = new Properties();
			prop.load(in);
			in.close();
		} catch (Exception e) {
			throw new InternalEVerlageError(e);
		}
	}

	/**
	 * Starts the creation process for the tables.
	 * @throws InternalEVerlageError if the creation fails
	 */
	public void startInstall() throws InternalEVerlageError {
		// alle Property keys holen
		Set propKeys = this.prop.keySet();
		for (Iterator it = propKeys.iterator(); it.hasNext();) {
			String keyStr = (String) it.next();
			if (keyStr.startsWith("create")) {
				executeSQLProperty(keyStr);
			}
		}
	}

	/**
	 * Starts the uninstall process for all tables in everlage
	 * @throws InternalEVerlageError if the removing fails
	 */
	public void startUnInstall() throws InternalEVerlageError {
		// alle Property keys holen
		Set propKeys = this.prop.keySet();
		for (Iterator it = propKeys.iterator(); it.hasNext();) {
			String keyStr = (String) it.next();
			if (keyStr.startsWith("remove")) {
				executeSQLProperty(keyStr);
			}
		}
	}

	/**
	 * Executes an SQL-Command. The command String is the prop String
	 * @param prop the sql-command string
	 * @throws InternalEVerlageError if an SQLError is detected or the given property unknown
	 */
	private void executeSQLProperty(String propStr) throws InternalEVerlageError {
		String value = this.prop.getProperty(propStr);
		System.out.println(value);
		if (value == null) {
			throw new InternalEVerlageError("Property " + propStr + " not found!");
		}
		PreparedStatement pstmt = null;
    Connection con = null;
		try {
			con = db.getConnection();
			pstmt = con.prepareStatement(value);
			pstmt.executeUpdate();
			con.commit();
			db.freeConnection(con);
		} catch (SQLException e) {
			System.err.print(e.getMessage());
			System.out.println("Skipping...");
      System.out.println();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
        if (con != null) {
          db.freeConnection(con);
          con=null;
        }
			} catch (Exception e) {
			}
		}
	}
}
