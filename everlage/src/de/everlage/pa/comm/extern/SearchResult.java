/*
 * Created on Mar 25, 2003
 * File SearchResult.java
 * 
 */
package de.everlage.pa.comm.extern;

import java.io.Serializable;

/**
 * Enth�lt die Struktur der Suchergebnisse. Diese Struktur als Klasse k�nnte sp�ter mit XML
 * abgel�st werden
 * @author waffel
 */
public class SearchResult implements Serializable {

  private String documentTitle;
  private String documentAuthor;
  private String documentID;

	/**
   * Liefert den Autor des gefundenen Dokuments zur�ck. Wenn es keinen Autor gibt, dann null.
	 * @return Autor des gefundenen Dokuments. Wenn es keinen Autor gibt, dann null.
	 */
	public String getDocumentAuthor() {
		return documentAuthor;
	}

	/**
   * Liefert die im PA verwaltete DocumentID als String zur�ck. Im Moment wird der String so 
   * eingesetzt, dass er aus der <QueryID+interner documentID> besteht.
	 * @return Liefert die im PA verwaltete DocumentID als String zur�ck. Z.B. 2+12
	 */
	public String getDocumentID() {
		return documentID;
	}

	/**
   * Gibt den Titel des gefundenen Dokuments zur�ck.
	 * @return Titel des gefundenen Dokuments
	 */
	public String getDocumentTitle() {
		return documentTitle;
	}

	/**
   * Setzt den Namen des Autors f�r das gefundene Dokument
	 * @param string Name des Autors
	 */
	public void setDocumentAuthor(String string) {
		documentAuthor = string;
	}

	/**
   * Setzt den die interne DocumentID, damit diese dann sp�ter wieder verwendet werden kann, wenn
   * das Dokument angefordert wird.
	 * @param l interne DocumentID
	 */
	public void setDocumentID(String l) {
		documentID = l;
	}

	/**
   * Setzt den Titel f�r das gefundene Dokument.
	 * @param string Titel f�r das gefundene Dokument
	 */
	public void setDocumentTitle(String string) {
		documentTitle = string;
	}

}
