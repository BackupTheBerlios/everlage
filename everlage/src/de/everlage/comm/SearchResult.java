/*
 * Created on Mar 25, 2003
 * File SearchResult.java
 * 
 */
package de.everlage.comm;

import java.io.Serializable;

/**
 * Enthält die Struktur der Suchergebnisse. Diese Struktur als Klasse könnte später mit XML
 * abgelöst werden
 * @author waffel
 */
public class SearchResult implements Serializable {

  private String documentTitle;
  private String documentAuthor;
  private String documentID;

	/**
   * Liefert den Autor des gefundenen Dokuments zurück. Wenn es keinen Autor gibt, dann null.
	 * @return Autor des gefundenen Dokuments. Wenn es keinen Autor gibt, dann null.
	 */
	public String getDocumentAuthor() {
		return documentAuthor;
	}

	/**
   * Liefert die im PA verwaltete DocumentID als String zurück. Im Moment wird der String so 
   * eingesetzt, dass er aus der <QueryID+interner documentID> besteht.
	 * @return Liefert die im PA verwaltete DocumentID als String zurück. Z.B. 2+12
	 */
	public String getDocumentID() {
		return documentID;
	}

	/**
   * Gibt den Titel des gefundenen Dokuments zurück.
	 * @return Titel des gefundenen Dokuments
	 */
	public String getDocumentTitle() {
		return documentTitle;
	}

	/**
   * Setzt den Namen des Autors für das gefundene Dokument
	 * @param string Name des Autors
	 */
	public void setDocumentAuthor(String string) {
		documentAuthor = string;
	}

	/**
   * Setzt den die interne DocumentID, damit diese dann später wieder verwendet werden kann, wenn
   * das Dokument angefordert wird.
	 * @param l interne DocumentID
	 */
	public void setDocumentID(String l) {
		documentID = l;
	}

	/**
   * Setzt den Titel für das gefundene Dokument.
	 * @param string Titel für das gefundene Dokument
	 */
	public void setDocumentTitle(String string) {
		documentTitle = string;
	}

}
