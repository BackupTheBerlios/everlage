/*
 * Created on Mar 5, 2003
 * File UserNotExistsException.java
 * 
 */
package de.everlage.ca.userManager.exception.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class UserNotExistsException extends Exception implements Serializable {

  private long userID=-1;
  
  public UserNotExistsException(long userIDVar) {
    this.userID = userIDVar;
  }
  
  public String getMessage() {
    String res = "";
    if (userID > 0) {
      res = "userID: "+this.userID;
    }
    return res;
  }

}
