package com.exceptions;

public class CouponPurchaseException extends CouponSystemException {

	/**
	 * CouponPurchaseException is an exception used to indicates exception which
	 * derived from the coupon purchase methods.
	 */
	private static final long serialVersionUID = 1L;

	public CouponPurchaseException() {
		super();
	}

	public CouponPurchaseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CouponPurchaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouponPurchaseException(String message) {
		super(message);
	}

	public CouponPurchaseException(Throwable cause) {
		super(cause);
	}

}
