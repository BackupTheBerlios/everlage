/*
 * Created on Feb 27, 2003
 * File AgentAlradyLoggedOutException.java
 * 
 */
package de.everlage.ca.componentManager.exception.extern;

import java.io.Serializable;

/**
 * @author waffel
 */
public class AgentAlradyLoggedOutException extends Exception implements Serializable {

  public AgentAlradyLoggedOutException(String msg) {
    super(msg);
  }

}
