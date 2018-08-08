package com.exceptions;

public class CouponSystemException extends Exception {

	/**
	 * CouponSystemException is the Parent Class of all the custom made exception
	 * that been used in this coupon system which in turn inherits from the Exception class.
	 */
	private static final long serialVersionUID = 1L;

	public CouponSystemException() {
		super();
	}

	public CouponSystemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CouponSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouponSystemException(String message) {
		super(message);
	}

	public CouponSystemException(Throwable cause) {
		super(cause);
	}

}
