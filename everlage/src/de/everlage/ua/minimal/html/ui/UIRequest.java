/**
 * $Id: UIRequest.java,v 1.2 2003/02/17 15:03:49 waffel Exp $ 
 * File: UIRequest.java    Created on Jan 27, 2003
 *
*/
package de.everlage.ua.minimal.html.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import HTML.Template;
import de.everlage.ua.minimal.html.UAGlobal;
import de.everlage.ua.minimal.html.ui.param.UIParameter;

/**
 * Der UIRequest (UserInterfaceRequest) ist eine abstrakte Klasse, welche f�r das Ausf�hren
 * beliebiger Java-Requests �ber den Apache verantwortlich ist. Alle Requests, welche �ber den
 * Servlet-Workflow laufen sollen, m�ssen von UIRequest abgeleitet sein, damit ein entsprechend
 * eingebautes Klassen-casting funktionieren kann. Der UIRequest erm�glicht ebenfalls das laden und
 * parsen von Templates, ohne dass sich die abgeleiteten Klassen darum k�mmern m�ssen. Wichtig
 * hierbei ist, dass der Suchpfad f�r die Includes der Templates und f�r die Templates allgemein mit
 * Semikolon getrennt angegeben werden muss. Der Suchpfad ist in der globalen Varibale
 * TEMPLATE_CONTAINER einzutragen. Dies geschieht mit Hilfe des setzens eines entsprechenden
 * Properties in der Konfigurationsdatei des Servlets.
 * @author waffel
 *
 * 
 */
public abstract class UIRequest {
	/*
	 * @see UIParameter
	 */
	protected UIParameter parameter;
  /*
   * @see Template
   */
	protected Template template;

	/**
	 * Erstellt ein neues Objekt vom Typ der Basisklasse UIRequest. Dabei wird die �bergebene
	 * Parameterklasse auf die interne Parameter-Variable gemappt und ein entsprechendes Template
	 * initialisiert. Ist in diesem Template keine Template-Variable vorhanden, welche ersetzt werden
	 * kann, so wird der HTML-Code einfach durchgereicht und nichts ersetzt. Damit ist es m�glich,
	 * auch nicht-Template-HTML-Seiten zu verarbeiten. Der Suchpfad f�r Templates ist auf die Variable
	 * TEMPLATE_CONTAINER gesetzt, welche mit Semicolon gertrennte Pfade beinhalten kann. In jedem
	 * dieser Pfade wird nach Templates gesucht (auch bei einem include). Das Template ist so
	 * initialisiert, dass es case_sensitive arbeitet und maximal 5 rekursive include-ebenen
	 * verarbeitet. 
	 * @param param Parameterklassem welche in HTML �bergebene Parameter behandelt
	 * @param templateName Name des HTML Templates, welches verarbeitet werden soll
	 */
	public UIRequest(UIParameter param, String templateName) {
		try {
			if (templateName.length() != 0) {
				UAGlobal.log.info("Templatename: " + templateName);
				Hashtable args = new Hashtable();
				args.put("filename", templateName);
				args.put("case_sensitive", "true");
				args.put("loop_context_vars", Boolean.TRUE);
				args.put("max_includes", new Integer(5));
				args.put("search_path_on_include", Boolean.TRUE);
				args.put("path", UAGlobal.TEMPLATE_CONTAINER);
				this.template = new Template(args);
				UAGlobal.log.debug(this.template);
			} else {
				UAGlobal.log.error("No Template name given");
			}
			this.parameter = param;
		} catch (FileNotFoundException e) {
			UAGlobal.log.error(e);
		} catch (IOException e) {
			UAGlobal.log.error(e);
		}
	}

	/**
	 * Erzeugt eine neue Instanz der �bergebenen Klasse mit dem Namen "type". Diese Klasse muss von
	 * UIRequest abgeleitet sein, damit auf ihre Basisklasse (dem UIRequest) gecastet werden kann.
	 * @param type Name der Klasse welche intanziiert werden soll.
	 * @return UIRequest eine neue Instanze mit dem Basistype UIRequest
	 */
	public static final UIRequest getRequestObject(String type) {
		try {
			Class requestClass = Class.forName(type);
			UAGlobal.log.debug("requestClass: " + requestClass);
			UIRequest requestObject = (UIRequest) requestClass.newInstance();
			return requestObject;
		} catch (Exception e) {
			UAGlobal.log.error(e);
		}
		return null;
	}

	/**
	 * Auf jeden Servletrequest sollte mit einem entsprechenden ServletResponse geantwortet werden.
	 * Dies geschieht mit Hilfe dieser Methode. Dabei werden bei dem vorhandensein (!= null) einer
	 * instantiierten Parameterklasse vorher alle, in dem Request �bermittelten Parameter bearbeitet.
	 * Danach wird performAction, f�r das besetzen von AntwortParametern mit Werten f�r die Antwort-
	 * HTML-Seite aufgerufen. Defaultm�ssig werden auch schon die Parameter "hostname" f�r die
	 * Template-Variablen gesetzt. Als Ausgabe wird die geparste Template-Seite (dies kann auch eine
	 * einfache HTML-Seite sein, die nicht ver�ndert wurde) �ber den ServletResponse ausgegeben.
	 * @param req hereinkommender ServletRequest, welcher behandelt werden soll
	 * @param res zu erzeugender ServletResponse. Von diesem wird die Methode
	 * @see ServletResponse#print verwendet um eine HTML-Seite zu erzeugen
	 */
	public void handleRequest(ServletRequest req, ServletResponse res) {
		try {
			UAGlobal.log.debug("handle Request");
			PrintWriter output = res.getWriter();
			if (this.parameter != null) {
				this.parameter.checkAndFill(req);
			}
			this.performAction();
			UAGlobal.log.debug("finish performAction " + this.template);
			this.template.setParam("hostname", UAGlobal.HOSTNAME);
			output.print(this.template.output());
		} catch (Exception e) {
			UAGlobal.log.error(e);
		}
	}

	/**
	 * Hier werden erforderliche Parameter f�r die Ausgabeseite besetzt.
	 */
	public abstract void performAction();
}
