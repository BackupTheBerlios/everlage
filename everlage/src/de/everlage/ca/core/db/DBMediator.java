/**
 * $Id: DBMediator.java,v 1.6 2003/02/17 15:34:59 waffel Exp $
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
			final int conNum = new Integer(conNumber).intValue();
			Class.forName(dbDriver);
      Connection con;
			for (int i = 0; i < conNum; i++) {
				con = DriverManager.getConnection(dbURL, dbLogin, dbPassword);
				con.setAutoCommit(false);
				this.conStack.push(con);
			}
		} catch (SQLException e) {
			throw new InternalEVerlageError(e);
		} catch (ClassNotFoundException e) {
			//CAGlobal.log.error(
			//	"Class not found! Are you sure, that the driver class \""
			//		+ e.getMessage()
			//		+ "\" is in you Classpath?");
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
