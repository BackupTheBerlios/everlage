/**
 * $ID$ 
 * File: CASQLInstaller.java    Created on Jan 15, 2003
 *
*/
package de.everlage.ca.install;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import de.everlage.ca.core.db.DBMediator;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class CASQLInstaller {

	private DBMediator db;
	private Properties prop;

	public CASQLInstaller() throws InternalEVerlageError {
		this.db = CAInstall.dbMediator;
		loadProperty();
	}

	private void loadProperty() throws InternalEVerlageError {
		try {
			ClassLoader cl = this.getClass().getClassLoader();
      String packagePrefix = this.getClass().getPackage().getName();
      packagePrefix= packagePrefix.replace('.', '/');
			InputStream in = cl.getResourceAsStream(packagePrefix+"/cainstall-oracle.properties");
			this.prop = new Properties();
			prop.load(in);
			in.close();
		} catch (Exception e) {
			throw new InternalEVerlageError(e);
		}
	}

	public void startInstall() throws InternalEVerlageError {
		createUserTable();
	}

	private void createUserTable() throws InternalEVerlageError {
		String value = prop.getProperty("createUserTable");
		if (value == null) {
			throw new InternalEVerlageError("Property createUserTable not found!");
		}
		executeSQLProperty(value);

	}

	private void executeSQLProperty(String prop) throws InternalEVerlageError {
		PreparedStatement pstmt = null;
		try {
			Connection con = db.getConnection();
			pstmt = con.prepareStatement(prop);
			pstmt.executeUpdate();
			db.freeConnection(con);
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
