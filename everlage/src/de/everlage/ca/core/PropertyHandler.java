/**
 * $Id: PropertyHandler.java,v 1.7 2003/02/19 12:53:46 waffel Exp $ 
 * File: PropertyHandler.java    Created on Jan 13, 2003
 *
*/
package de.everlage.ca.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import de.everlage.ca.exception.extern.InternalEVerlageError;

/**
 * Verwaltet alle Properties des CentralAgents in einer Property-Datei.
 * Jeder Manager kann seine eigenen Properties mit dem Property-Handler
 * laden. Der Property Handler hängt einen Prefix an jedes Property an, um
 * gleiche Properties von verschiedenen Manager unterscheiden zu können. Der
 * Prefix ist der Klassenname des Objekts, welches den Property-Manager
 * verwendet. Alle Manager des CentralAgents müssen diesen Handler nutzen, um
 * Propetries benutzen zu können. Nach dem Laden aller Properties hat der
 * PropertyHandler eine einzige Property, in welcher dann alle Eintrage drinn
 * stehen.
 * @author waffel
 *
 * 
 */
public final class PropertyHandler {
	/* internes Property */
	private Properties properties = null;

	/* Default Konstruktor. Hier wird die interne Propetry instanziiert */
	public PropertyHandler() {
		this.properties = new Properties();
	}

	/**
	 * Registriert eine Property aus einer extrenen Property-Datei beim Handler. Es wird der Dateiname
	 * geladen und die als Prefix wird er Klassenname des aufrufenden Objektes angehangen. Das Objekt
	 * wird aus Performance Gründen nicht über java-reflection ermittelt, sondern muss mit übergen
	 * werden. Der angegebene Filename muss eine existierende und lesbare Datei repräsentieren. Kann
	 * die Datei nicht gelesen werden, wir eine InternalEverlageError Exception geworfen.
	 * @param filename Dateiname der zu ladenden Property Datei
	 * @param registerObject Das Objekt, welches die Property registrieren will. Von diesem Objekt
	 * wird der Klassenename genommen und als Prefix an jede Property gehangen, welche geladen wird.
	 * @throws InternalEVerlageError Wenn die Datei nicht geöffnet werden kann.
	 */
	public void registerProperty(String filename, Object registerObject)
		throws InternalEVerlageError {
		Class registerClass = registerObject.getClass();
		Properties loadProps = this.loadProperty(filename, registerClass);
		String propPrefix = new String(registerClass.getName() + "_");
		// durch die Properties druchlaufen und den Prefix des Klassennames dranhängen
		String propKey;
		String propValue;
		for (Iterator it = loadProps.keySet().iterator(); it.hasNext();) {
			propKey = (String) it.next();
			propValue = loadProps.getProperty(propKey);
			propKey = propPrefix + propKey;
			this.properties.setProperty(propKey, propValue);
		}
	}

	/**
	 * Gibt einen Property-Value für den angegebenen Key zurück. Das Objekt muss eine registriertes
	 * Objekt sein, um den Key laden zu können. Von dem Objekt wird der Klassenname verwendet um den
	 * Key zu finden und den entsprechenden Wert zurückzugeben.
	 * @param key Property-Key, zu welchem der dazugehörige Wert ermittelt werden soll. Der Key wird
	 * intern noch um einen Prefix (den Klassennamen des übergebenen Objektes) erweitert.
	 * @param registerObject Objekt, zu welchem der Key geladen werden soll. Das Objekt muss bei dem
	 * Handler registriert sein, ansonsten kann der Key nicht gefunden werden.
	 * @return String Wert zu einem übergebenen Key. Kann der Key nicht gefunden werden, oder das
	 * Objekt ist nicht registriert, wird null zurückgegeben.
	 */
	public String getProperty(String key, Object registerObject) {
		Class registerClass = registerObject.getClass();
		return this.properties.getProperty(registerClass.getName() + "_" + key);
	}

	/**
	 * Öffnet eine übergebene Property Datei. Dabei wird die Datei in dem Verzeichnis
	 * gesucht, in welchem sich auch auf Package-ebene die übergebene register Klasse 
	 * befindet.
	 * @param filename Zu ladende Property-Datei.
	 * @param regClass Klasse, welche sich innerhalb einer Packagestruktur befindet und über welche
	 * herausgefunden werden soll, von welchem Pfad aus die property-Datei geladen werden soll.
	 * @return Properties Neues Property Objekt, welches die geladenen Keys aus der Datei beinhaltet.
	 * @throws InternalEVerlageError Wenn die Datei nicht gelesen werden kann. 
	 * @see FileNotFoundException
	 * @see IOException
	 */
	private Properties loadProperty(String filename, Class regClass) throws InternalEVerlageError {
		Properties newProps = new Properties();
		try {
			if (CAGlobal.log.isDebugEnabled()) {
				CAGlobal.log.debug("try to load " + regClass.getResource(filename).getFile());
			}
			InputStream in = new FileInputStream(regClass.getResource(filename).getFile());
			newProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		} catch (IOException e) {
			CAGlobal.log.error(e);
			throw new InternalEVerlageError(e);
		}
		return newProps;
	}

}
