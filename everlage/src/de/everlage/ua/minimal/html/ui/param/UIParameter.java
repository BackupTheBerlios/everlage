/**
 * $Id: UIParameter.java,v 1.1 2003/01/28 13:21:33 waffel Exp $ 
 * File: UIParameter.java    Created on Jan 27, 2003
 *
*/
package de.everlage.ua.minimal.html.ui.param;

import javax.servlet.ServletRequest;

/**
 * Diese Klasse wird benutzt, um Parameter aus einem HTML-Request herauszubekommen und in
 * Klassenparametern umzuwandeln. Dabei kann auch geprüft werden, ob ein bestimmter Parameter
 * gesetzt worden ist und notfalls mit einem Defaultwert gesetzt werden.
 * @author waffel
 *
 * 
 */
public abstract class UIParameter {
  
  /**
	 * Holt sich die Parameter von einem ServletRequest als String und wandelt sie gegebenenfalls in
	 * Klassenvariablen um.
	 * @param req Request, in welchem die Parameter mit übergeben werden (als POST oder GET Parameter)
	 */
	public abstract void checkAndFill(ServletRequest req);

}
