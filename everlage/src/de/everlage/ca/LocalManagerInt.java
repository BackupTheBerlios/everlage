/**
 * $Id: LocalManagerInt.java,v 1.1 2003/01/22 16:37:11 waffel Exp $ 
 * File: LocalManagerInt.java    Created on Jan 21, 2003
 *
*/
package de.everlage.ca;

import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * Interface, welches Methoden deklariert, welche von allen lokalen Managern des CentralAgents
 * implementiert werden sollten. Es ist im Moment so gelöst, dass es noch einen abstrakten
 * LocalManager gibt, welcher diese Methoden schon implementiert und damit das default-Verhalten für
 * die Manager vorgibt.
 * @author waffel
 *
 * 
 */
public interface LocalManagerInt {

	/**
	 * Registriert eine Property-Datei mit allen Ihren Properties für eine übergebene Klasse. 
	 * @param propertyName Name der Property Datei, in welcher sich dann die Poperties befinden
	 * @param regClass Klasse, für welche die Properties gelten sollen
	 * @throws InternalEVerlageError Wenn ein interner Fehler bei der Registrierung der Properties
	 * auftritt
   * @see de.everlage.ca.core.PropertyHandler
	 */
	public void registerProperty(String propertyName, Object regClass) throws InternalEVerlageError;
}
