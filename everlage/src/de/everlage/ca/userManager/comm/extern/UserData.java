/**
 * $Id: UserData.java,v 1.3 2003/02/11 15:35:00 waffel Exp $  
 * File: UserData.java    Created on Jan 10, 2003
 *
*/

package de.everlage.ca.userManager.comm.extern;

import java.io.Serializable;

/**
 * Enthält bestimmte Daten zu einem Nutzer von eVerlage
 * @author waffel
 *
 * 
 */
public final class UserData implements Serializable {
  
  public String firstName;
  public String lastName;
  public String title;
  

}
