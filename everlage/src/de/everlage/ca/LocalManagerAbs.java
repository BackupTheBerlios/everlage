/**
 * $Id: LocalManagerAbs.java,v 1.1 2003/01/22 16:36:37 waffel Exp $ 
 * File: LocalManagerAbs.java    Created on Jan 21, 2003
 *
*/
package de.everlage.ca;

import de.everlage.ca.core.CAGlobal;
import de.everlage.ca.core.PropertyHandler;
import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * Abstrakte Klasse für alle lokalen Manager des CentralAgents. Diese Klasse implementiert schon
 * verschiedene Methoden (vor allem den Konstruktor und die Methode registertProperties) um ein
 * Default-verhalten für die Manager vorzugeben.
 * @author waffel
 *
 * 
 */
public abstract class LocalManagerAbs implements LocalManagerInt {

	protected PropertyHandler pHandler;

	public LocalManagerAbs() throws InternalEVerlageError {
		// die SQLProperty datei registrieren
		pHandler = new PropertyHandler();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ca.LocalManagerInt#registerProperty(java.lang.String)
	 */
	public void registerProperty(String propertyName, Object regClass) throws InternalEVerlageError {
		pHandler.registerProperty(propertyName, regClass);
		if (CAGlobal.log.isDebugEnabled()) {
			CAGlobal.log.debug(
				"finish register Property: " + propertyName + "  " + regClass + "  " + pHandler);
		}
	}

}
