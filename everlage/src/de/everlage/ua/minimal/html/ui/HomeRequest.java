/**
 * $Id: HomeRequest.java,v 1.1 2003/01/28 13:20:50 waffel Exp $
 * File: HomeRequest.java    Created on Jan 27, 2003
 *
*/
package de.everlage.ua.minimal.html.ui;

import de.everlage.ua.minimal.html.UAGlobal;

/**
 * New Class
 * @author waffel
 *
 * 
 */

public final class HomeRequest extends UIRequest {


	/* (non-Javadoc)
	 * @see java.lang.Object#Object()
	 */
	public HomeRequest() {
		super(null, UAGlobal.TEMPLATE_CONTAINER+"home-main.html");
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.minimal.html.ui.UIRequest#performAction()
	 */
	public void performAction() {
	}

}
