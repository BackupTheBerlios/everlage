/**
 * $Id: UAGlobal.java,v 1.1 2003/01/28 13:18:50 waffel Exp $
 * File: UAGlobal.java    Created on Jan 27, 2003
 *
*/
package de.everlage.ua.minimal.html;

import org.apache.log4j.Logger;

import de.everlage.ca.componentManager.comm.extern.UALoginResult;

/**
 * Globale Datenklasse des UA. Hier kann auf verschiedene Parameter zurückgegriffen werden, welche
 * fast in allen Komponenten des UA benutzt werden.
 * @author waffel
 *
 * 
 */
public final class UAGlobal {
  /**
   * Der globale logger für alle Klassen des UserAgents
   */
	public static Logger log;
	public static String log4jProperty;
  /**
   * der Hostname, auf dem der UA läuft
   */
	public static String HOSTNAME;
  /**
   * Das Verzeichnis, in dem die Bilder für die HTML-Seiten liegen
   */
	public static String IMG_DIR;
  /**
   * Verzeichnisse, in denen die HTML-Templates liegen
   */
	public static String TEMPLATE_CONTAINER;
  /**
   * Globale include-datei für den ersten HTML-Body
   */
	public static String HTML_BODY1;
  /**
   * Globale include-datei für den letzten HTML-Body
   */
	public static String HTML_BODY_END;
  /**
   * Datenklasse, in welcher alle relevanten Daten über den UA nach dem einloggen beim CA
   * gespeichert sind
   */
	public static UALoginResult uaLoginRes;
}
