/*
 * Created on Feb 21, 2003
 * File TestGlobal.java
 * 
 */
package de.everlage;

import de.everlage.ca.core.db.DBMediator;

/**
 * @author waffel
 */
public class TestGlobal {

  public static DBMediator dbMediator;
  public static String uaRMIAddress = "//127.0.0.1/TestUA";
  public static String componentManagerRMI = "//127.0.0.1/ComponentManager";
  public static String userManagerRMI = "//127.0.0.1/UserManager";
}
