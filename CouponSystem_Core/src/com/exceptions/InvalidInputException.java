package com.exceptions;

public class InvalidInputException extends CouponSystemException {

	/**
	 * InvalidInputException is an exception used to indicates exception which
	 * derived from invalid inputs usage such as invalid Email format, date that has
	 * been set to the past, negative amount, negative price etc.
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException() {
		super();
	}

	public InvalidInputException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	}

}
