/**
 * $Id: DBMediator.java,v 1.7 2003/02/19 12:53:04 waffel Exp $
 */

package de.everlage.ca.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * Verwaltet Datenbankverbindungen. Die Anzahl der offen gehaltenen Datenbankverbindungen kann zur
 * Laufzeit dynamisch verändert werden.
 * @author waffel
 * @todo die dynamische Verwaltung der offen gehaltenen Datenbankverbindungen muss noch
 * implementiert  werden
 * 
 */
public final class DBMediator {

	/**
	 * Elemente vom Typ Connection in einem Stack
	 */
	private Stack conStack;

	/**
	 * Konstruktor zum erzeugen von n Datenbankverbindungen.
	 * @param dbDriver Name des Datenbanktreibers
	 * @param dbURL URL der Datenbankverbindung
	 * @param dbLogin Loginname zur Datenbank
	 * @param dbPassword Passwort des Datenbanknutzers
	 * @param conNumber Anzahl der defaultmässig zu öffnenden Verbindungen
	 * @throws InternalEVerlageError Wenn eine Datenbankverbindung nicht erzeugt werden kann.
	 */
	public DBMediator(
		String dbDriver,
		String dbURL,
		String dbLogin,
		String dbPassword,
		String conNumber)
		throws InternalEVerlageError {
		conStack = new Stack();
		try {
			if ((conNumber == null) || (conNumber.length() == 0)) {
				conNumber = "0";
			}
			final int conNum = new Integer(conNumber).intValue();
			if (conNum < 1) {
				throw new InternalEVerlageError("no database Connection available (reason: Number of to opening connections < 1)");
      }
				Class.forName(dbDriver);
			Connection con = null;
			for (int i = 0; i < conNum; i++) {
				con = DriverManager.getConnection(dbURL, dbLogin, dbPassword);
				if (con == null) {
					throw new InternalEVerlageError("no database connection available");
				}
				con.setAutoCommit(false);
				this.conStack.push(con);
			}
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} catch (ClassNotFoundException e) {
			throw new InternalEVerlageError(e);
		}
	}

	/**
	 * Gibt eine freie Datenbankverbindung zurück. Ist keine Verbindung frei, wird gewartet, bis
	 * wieder eine Verbindung freigegeben wird.
	 * @return Connection Neue Datenbankverbindung.
	 * @throws InternalEVerlageError Wenn der Prozess gestört wird
	 * @see InterruptedException
	 */
	public Connection getConnection() throws InternalEVerlageError {
		synchronized (this) {
			try {
				Connection con = (Connection) this.conStack.pop();
				while (con == null) {
					wait();
					con = (Connection) this.conStack.pop();
				}
				//CAGlobal.log.debug(conStack.size()+"");
				return con;
			} catch (InterruptedException e) {
				throw new InternalEVerlageError(e);
			}
		}
	}

	/**
	 * Gibt eine Datenbankverbindung frei und packt diese auf den internen Verbindungsstack.
	 * @param con Datenbankverbindung, die freigegeben werden soll.
	 */
	public void freeConnection(Connection con) {
		synchronized (this) {
			this.conStack.push(con);
			notify();
			//CAGlobal.log.debug(conStack.size()+"");
		}
	}
}
