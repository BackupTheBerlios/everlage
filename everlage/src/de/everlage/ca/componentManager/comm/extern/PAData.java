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
public final class PAData implements Serializable {
	public long providerAgentID;
  public long paSessionID;
  public String paRMIAddress;
  public ProviderAgentInt providerAgent;
  
  public PAData(long extProviderAgentID, long extPaSessionID, String extPaRMIAddress, ProviderAgentInt pa) {
    this.providerAgentID = extProviderAgentID;
    this.paSessionID = extPaSessionID;
    this. paRMIAddress = extPaRMIAddress;
    this.providerAgent = pa;
  }
}
