/**
 * $Id: CAGlobal.java,v 1.3 2003/01/22 16:45:32 waffel Exp $  
 * File: CAGlobal.java    Created on Jan 14, 2003
 *
*/
package de.everlage.ca.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Globale Datenklasse, in welcher statische Variablen f�r alle Manager des CentralAgent zug�nglich
 * sind.
 * @author waffel
 * 
 */
public final class CAGlobal {
	/**
	 * Variable, �ber welche Logausgaben erzeugt werden k�nnen.
	 * @see Logger
	 */
	public static Logger log;
	/**
	 * In dieser Map sind f�r verschiedene Datenbanksysteme die Driver-Strings vorbelegt. Im Moment
	 * unterst�tzte Systeme sind ORACLE und POSTGRES.
	 */
	public static Map dbDrivers;
	/**
	 * In dieser Map sind f�r verschiedene Datenbanksysteme die URL-Strings vorbelegt, Im Moment
	 * unterst�tzte Systeme sind ORACLE und POSTGRES
	 */
	public static Map dbUrls;

	static {
		dbDrivers = new HashMap(2);
		dbUrls = new HashMap(2);
		dbDrivers.put("ORACLE", "oracle.jdbc.driver.OracleDriver");
		dbDrivers.put("POSTGRES", "org.postgresql.Driver");
		dbUrls.put("ORACLE", "jdbc:oracle:thin:@");
		dbUrls.put("POSTGRES", "jdbc:postgresql:");
	}

}
