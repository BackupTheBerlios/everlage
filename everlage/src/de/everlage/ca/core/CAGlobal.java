/**
 * $Id: CAGlobal.java,v 1.4 2003/02/17 14:46:51 waffel Exp $  
 * File: CAGlobal.java    Created on Jan 14, 2003
 *
*/
package de.everlage.ca.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Globale Datenklasse, in welcher statische Variablen für alle Manager des CentralAgent zugänglich
 * sind.
 * @author waffel
 * 
 */
public final class CAGlobal {
	/**
	 * Variable, über welche Logausgaben erzeugt werden können.
	 * @see Logger
	 */
	public static Logger log;
	/**
	 * In dieser Map sind für verschiedene Datenbanksysteme die Driver-Strings vorbelegt. Im Moment
	 * unterstützte Systeme sind ORACLE und POSTGRES.
   * @associates String,String
	 */
	public static Map dbDrivers;
	/**
	 * In dieser Map sind für verschiedene Datenbanksysteme die URL-Strings vorbelegt, Im Moment
	 * unterstützte Systeme sind ORACLE und POSTGRES
   * @associates String,String
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
