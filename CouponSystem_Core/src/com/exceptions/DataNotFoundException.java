package com.exceptions;

public class DataNotFoundException extends CouponSystemException {

	/**
	 * DataNotFoundException is an exception used to let the user know that the data
	 * he've requested was not found. 
	 * a specific cause is added and give the user additional information regarding. 
	 * e.g company/coupon/customer not found, company/coupon/customer id not found, etc. 
	 */
	private static final long serialVersionUID = 1L;

	public DataNotFoundException() {
		super();
	}

	public DataNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

}
