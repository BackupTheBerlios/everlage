package de.everlage.ca;

import java.sql.Connection;

import de.everlage.ca.exception.extern.InternalEVerlageError;


public interface CentralAgentInt {
  
 public Connection getDBConnection() throws InternalEVerlageError;
 public void freeDBConnection(Connection con);
}
