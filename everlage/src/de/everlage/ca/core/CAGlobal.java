/**
 *  File: CAGlobal.java    
 * Created on Jan 14, 2003
 *
*/
package de.everlage.ca.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class CAGlobal {

	public static Logger log;

	public static Map dbDrivers;
  
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
