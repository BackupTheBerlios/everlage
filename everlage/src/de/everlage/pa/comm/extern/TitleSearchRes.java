/**
 *  File: TitleSearchRes.java    
 * Created on Jan 31, 2003
 *
*/
package de.everlage.pa.comm.extern;

import java.io.Serializable;
import java.util.Map;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public final class TitleSearchRes implements Serializable {
  
  public final static int EMPTY=0;
  public final static int FULL=10; 
  
  private int emptyFlag;
  
  private Map authors;
  private String title;
  private Map highlightTitle;
  private String year;
  
	/**
	 * @return Map
	 */
	public Map getAuthors() {
		return authors;
	}

	/**
	 * @return Map
	 */
	public Map getHighlightTitle() {
		return highlightTitle;
	}

	/**
	 * @return String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return String
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Sets the authors.
	 * @param authors The authors to set
	 */
	public void setAuthors(Map authors) {
		this.authors = authors;
	}

	/**
	 * Sets the highlightTitle.
	 * @param highlightTitle The highlightTitle to set
	 */
	public void setHighlightTitle(Map highlightTitle) {
		this.highlightTitle = highlightTitle;
	}

	/**
	 * Sets the title.
	 * @param title The title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the year.
	 * @param year The year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return int
	 */
	public int getEmptyFlag() {
		return emptyFlag;
	}

	/**
	 * Sets the emptyFlag.
	 * @param emptyFlag The emptyFlag to set
	 */
	public void setEmptyFlag(int emptyFlag) {
		this.emptyFlag = emptyFlag;
	}

}
