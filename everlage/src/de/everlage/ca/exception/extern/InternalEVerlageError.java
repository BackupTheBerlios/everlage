package de.everlage.ca.exception.extern;

public class InternalEVerlageError extends Exception {

	private String message = null;

	/**
	 * Constructor for InternalEVerlageError.
	 */
	public InternalEVerlageError() {
		super();
	}

	/**
	 * Constructor for InternalEVerlageError.
	 * @param message
	 */
	public InternalEVerlageError(String m) {
		super(m);
    this.message = m;
	}

	/**
	 * Constructor for InternalEVerlageError.
	 * @param message
	 * @param cause
	 */
	public InternalEVerlageError(String m, Throwable cause) {
		super(m, cause);
    this.message = m;
	}

	/**
	 * Constructor for InternalEVerlageError.
	 * @param cause
	 */
	public InternalEVerlageError(Throwable cause) {
		super(cause);
	}

	public InternalEVerlageError(Exception e) {
		this.message = e.getMessage();
	}

	public String getMessage() {
		return this.message;
	}
  

}
