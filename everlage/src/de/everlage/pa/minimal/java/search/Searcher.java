/*
 * Created on Mar 21, 2003
 * File Searcher.java
 * 
 */
package de.everlage.pa.minimal.java.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import de.everlage.ca.componentManager.comm.extern.PASearchRequestRecord;
import de.everlage.pa.comm.extern.SearchResult;

/**
 * @author waffel
 */
public class Searcher {

	private List resultList;

	public void startSearch(PASearchRequestRecord searchRec) {
		try {
      System.out.println("Start search for "+searchRec.getSearchString());
			WebConversation wc = new WebConversation();
			WebRequest req = new GetMethodWebRequest("http://www11.in.tum.de/Java/");
			WebResponse res = wc.getResponse(req);
      System.out.println("get response "+res);
			WebForm form = res.getForms()[0];
			form.setParameter("search_term", searchRec.getSearchString());
			form.submit();
			res = wc.getCurrentPage();
			WebLink[] searchResults = res.getLinks();
			resultList = new ArrayList(searchResults.length);
      long documentID = 0;
			for (int i = 0; i < searchResults.length; i++) {
        SearchResult sRes = new SearchResult();
        sRes.setDocumentTitle(searchResults[i].asText());
        sRes.setDocumentID(searchRec.getQuestionID()+"+"+documentID);
        sRes.setDocumentAuthor(null);
				resultList.add(sRes);
        documentID++;
			}
		} catch (IOException e) {
      e.printStackTrace();
			// leere ResultList erzeugen
			resultList = new ArrayList();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Liste mit Suchergebnissen, dies ist im Moment einfach nur der URL-String
	 * @TODO Dies Ergebnisliste müsste eine Liste mit bestimmten Kommunikationsobjekten sein,
	 * welche eine ID und einen Suchergebnisstring beinhalten. Diese Liste könnte in einer Datenbank
	 * abgespeichert sein, wo sie hier rausgeholt und zusammengebaut werden kann. Dabei sollte eine
	 * bestimmte Query-ID mit angegeben werden, wonach die Ergebnisse zusammengetragen werden können.
	 */
	public List getSearchList() {
		return resultList;
	}

}
