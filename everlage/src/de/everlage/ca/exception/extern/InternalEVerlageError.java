/**
 * $Id: InternalEVerlageError.java,v 1.2 2003/01/22 16:48:59 waffel Exp $
 */
package de.everlage.ca.exception.extern;

public class InternalEVerlageError extends Exception {

	private String message = null;

	/**
	 * Default Konstruktor für die Exception.
	 */
	public InternalEVerlageError() {
		super();
	}

	/**
	 * Konstruktor mit der Möglichkeit der Übergabe einer Nachricht. Die Nachricht wird nicht
	 * lokalisiert.
	 * @param message Nachricht, welche in die Exception eingetragen werden soll. Die Nachricht wird
	 * nicht lokalisiert.
	 */
	public InternalEVerlageError(String m) {
		super(m);
    this.message = m;
	}

	/**
	 * Konstruktor, welchem eine Nachricht und eine andere Exception als Source übergeben werden kann.
	 * @param message Nachricht für diese Exception
	 * @param cause Source einer anderen Exception
	 */
	public InternalEVerlageError(String m, Throwable cause) {
		super(m, cause);
    this.message = m;
	}

	/**
	 * Konstruktor mit dem Source einer anderen Exception
	 * @param cause Source einer anderen Exception
	 */
	public InternalEVerlageError(Throwable cause) {
		super(cause);
	}

	/**
	 * Konstruktor mit dem Source einer anderen Exception. Dabei wird nur die Message der anderen
	 * Exception übernommen und als eigener ExceptionText eingetragen.
	 * @param e Source einer anderen Exception, von welcher der Nachrichtentext genommen wird
	 */
	public InternalEVerlageError(Exception e) {
		this.message = e.getMessage();
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return this.message;
	}
  

}
