/**
 * $Id: DefaultTemplRequest.java,v 1.1 2003/01/28 13:20:22 waffel Exp $ 
 * File: DefaultTemplRequest.java    Created on Jan 27, 2003
 *
*/
package de.everlage.ua.minimal.html.ui;

/**
 * Klasse, welche nur Templates, aber keine übergebenen Parameter behandelt. Sie wird verwendet,
 * wenn anstatt type der parameter tmpl dem Servlet übergeben wird. Dies ist dann sinnvoll, wenn z.
 * B. reine HTML mit Hilfe des Servlets verarbeitet werden sollen. In diesem Falle wird einfach das
 * Template angegeben, welches verarbeitet werden soll.
 * @author waffel
 *
 * 
 */
public class DefaultTemplRequest extends UIRequest {

	public DefaultTemplRequest(String template) {
		super(null, template);
	}
	/* (non-Javadoc)
	 * @see de.everlage.ua.minimal.html.ui.UIRequest#performAction()
	 */
	public void performAction() {
	}

}
