/**
 *  File: PAData.java    
 * Created on Jan 30, 2003
 *
*/
package de.everlage.ca.componentManager.comm.extern;

import java.io.Serializable;

import de.everlage.pa.ProviderAgentInt;

/**
 * New Class
 * @author waffel
 *
 * 
 */
public class PAData implements Serializable {
	public long providerAgentID;
  public long paSessionID;
  public String paRMIAddress;
  public ProviderAgentInt providerAgent;
  
  public PAData(long l_providerAgentID, long l_paSessionID, String l_paRMIAddress, ProviderAgentInt pa) {
    this.providerAgentID = l_providerAgentID;
    this.paSessionID = l_paSessionID;
    this. paRMIAddress = l_paRMIAddress;
    this.providerAgent = pa;
  }
}
