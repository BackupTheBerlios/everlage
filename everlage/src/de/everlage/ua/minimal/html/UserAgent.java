/**
 * $Id: UserAgent.java,v 1.3 2003/03/25 19:42:18 waffel Exp $  
 * File:   UserAgent.java    Created on Jan 24, 2003
 *
*/
package de.everlage.ua.minimal.html;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.everlage.ca.exception.extern.InternalEVerlageError;
import de.everlage.ua.UserAgentAbs;
import de.everlage.ua.minimal.html.ui.DefaultTemplRequest;
import de.everlage.ua.minimal.html.ui.UIRequest;

/**
 * Der Beispiel HTML UserAgent.
 * @author waffel
 *
 * 
 */
public class UserAgent extends UserAgentAbs implements Servlet, ServletConfig {

	private ServletConfig conf;

	private String uaName;
	private String uaPassword;
	private String uaRegistryServer;
	private String caRegistryServer;

	public UserAgent() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			this.conf = config;
			UAGlobal.log = Logger.getLogger(UserAgent.class.getName());
			// versuchen, die in der zone.properties angegebene log4j.properties datei zu laden
			String logj4Str = getInitParameter("log4jProperty");
			if ((logj4Str == null) || (logj4Str.length() == 0)) {
				this.conf.getServletContext().log("log4j Property not found! No Message to be print");
			}
			UAGlobal.log4jProperty = logj4Str;
			this.conf.getServletContext().log("log4j Property: " + UAGlobal.log4jProperty);
			PropertyConfigurator.configureAndWatch(UAGlobal.log4jProperty);
			UAGlobal.log.info("Start of eVerlage HTML UserAgent (the servlet)");
			UAGlobal.log.info("start reading Servlet Parameters");
			this.readParams();
			UAGlobal.log.info("end of reading Servlet Parameters");
			super.registerComponents(this.caRegistryServer);
			Naming.rebind(this.uaRegistryServer + this.uaName, this);
			final long uaSessionID = new Random().nextLong();
			UAGlobal.uaLoginRes =
				componentManager.UALogin(
					this.uaName,
					this.uaPassword,
					this.uaRegistryServer + this.uaName,
					uaSessionID);
			UAGlobal.log.info("Login for UA ok. UA-ID: " + UAGlobal.uaLoginRes.userAgentID);
		} catch (Exception e) {
			UAGlobal.log.fatal(e);
			System.exit(2);
		}
	}

	private void readParams() throws Exception {
		String uaRegistryServer = getInitParameter("uaRegistryServer");
		if ((uaRegistryServer == null) || (uaRegistryServer.length() == 0)) {
			UAGlobal.log.error("Property uaRegistryServer not defined");
			throw new ServletException("Property uaRegistryServer not defined");
		}
		this.uaRegistryServer = "//" + uaRegistryServer + "/";
		UAGlobal.log.info("uaRegistryServer: " + this.uaRegistryServer);

		String hostname = getInitParameter("uaHostName");
		if ((hostname == null) || (hostname.length() == 0)) {
			UAGlobal.log.error("Property uaHostName not defined");
			throw new ServletException("Property uaHostName not defined");
		}
		UAGlobal.HOSTNAME = hostname;
		UAGlobal.log.info("hostname: " + UAGlobal.HOSTNAME);

		String uaLoginName = getInitParameter("uaLoginName");
		if ((uaLoginName == null) || (uaLoginName.length() == 0)) {
			UAGlobal.log.error("Property uaLoginName not defined");
			throw new ServletException("Property uaLoginNamenot defined");
		}
		this.uaName = uaLoginName;
		UAGlobal.log.info("uaLoginName: " + this.uaName);

		String password = getInitParameter("uaPassword");
		if ((password == null) || (password.length() == 0)) {
			UAGlobal.log.error("Property uaPassword not defined");
			throw new ServletException("Property uaPassword not defined");
		}
		this.uaPassword = password;
		UAGlobal.log.info("password: " + this.uaPassword);

		String caRegistryServer = getInitParameter("caRegistryServer");
		if ((caRegistryServer == null) || (caRegistryServer.length() == 0)) {
			UAGlobal.log.error("Property caRegistryServer not defined");
			throw new ServletException("Property caRegistryServer not defined");
		}
		this.caRegistryServer = "//" + caRegistryServer + "/";
		UAGlobal.log.info("caRegistryServer: " + this.caRegistryServer);

		String templateContainer = getInitParameter("templateContainer");
		if ((templateContainer == null) || (templateContainer.length() == 0)) {
			UAGlobal.log.error("Property templateContainer not defined");
			throw new ServletException("Property templateContainer not defined");
		}
		UAGlobal.TEMPLATE_CONTAINER = templateContainer;
		UAGlobal.log.info("templateContainer: " + UAGlobal.TEMPLATE_CONTAINER);

		String body1 = getInitParameter("body1");
		if ((body1 == null) || (body1.length() == 0)) {
			UAGlobal.log.error("Property body1 not defined");
			throw new ServletException("Property body1 not defined");
		}
		UAGlobal.HTML_BODY1 = body1;
		UAGlobal.log.info("body1: " + UAGlobal.HTML_BODY1);

		String bodyEnd = getInitParameter("bodyEnd");
		if ((bodyEnd == null) || (bodyEnd.length() == 0)) {
			UAGlobal.log.error("Property bodyEnd not defined");
			throw new ServletException("Property bodyEnd not defined");
		}
		UAGlobal.HTML_BODY_END = bodyEnd;
		UAGlobal.log.info("bodyEnd: " + UAGlobal.HTML_BODY_END);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return conf;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(ServletRequest req, ServletResponse res)
		throws ServletException, IOException {
		String type = null;
		String tmpl = null;
		UIRequest requestObject = null;
		UAGlobal.log.info("Request from " + req.getRemoteAddr() + " " + req.getRemoteHost());
		res.setContentType("text/html");
		type = req.getParameter("type");
		tmpl = req.getParameter("tmpl");
		if (type != null) {
			requestObject = UIRequest.getRequestObject(type);
			UAGlobal.log.debug(requestObject);
			if (requestObject == null) {
				requestObject =
					UIRequest.getRequestObject("de.everlage.ua.minimal.html.ui.ErrorRequest");
			}
			requestObject.handleRequest(req, res);
		} else if (tmpl != null) {
			DefaultTemplRequest tmplRequest = new DefaultTemplRequest(UAGlobal.TEMPLATE_CONTAINER + tmpl);
			UAGlobal.log.debug("get template from dir: " + UAGlobal.TEMPLATE_CONTAINER + tmpl);
			tmplRequest.handleRequest(req, res);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		return "everlage minimal html UserAgent";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		UAGlobal.log.info("start UA shutdown");
		try {
			componentManager.UALogout(UAGlobal.uaLoginRes.userAgentID, UAGlobal.uaLoginRes.caSessionID);
		} catch (Exception e) {
			UAGlobal.log.fatal(e);
		}
		UAGlobal.log.info("UA destroyed");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		return conf.getServletContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String arg0) {
		return conf.getInitParameter(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return conf.getInitParameterNames();
	}

	/* (non-Javadoc)
	 * @see de.everlage.ua.UserAgentInt#initProperties()
	 */
	public void initProperties() throws RemoteException, InternalEVerlageError {
		
	}

}
