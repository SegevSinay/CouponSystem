package com.exceptions;

public class InvalidLoginCredentials extends CouponSystemException {

	/**
	 * InvalidLoginCredentials is an exception related to login issues, such as
	 * wrong password or user name (or both).
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLoginCredentials() {
		super();
	}

	public InvalidLoginCredentials(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidLoginCredentials(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLoginCredentials(String message) {
		super(message);
	}

	public InvalidLoginCredentials(Throwable cause) {
		super(cause);
	}

}
