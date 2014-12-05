package uk.co.stikman.regexwordsolve;

public class TookTooLongException extends Exception {

	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public TookTooLongException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TookTooLongException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public TookTooLongException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public TookTooLongException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
