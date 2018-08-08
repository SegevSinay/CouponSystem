package com.exceptions;

public class DailyTaskException extends CouponSystemException {

	/**
	 * DailyTaskException is an exception used to indicates exception which
	 * derived from the daily expired coupon task.
	 */
	private static final long serialVersionUID = 1L;

	public DailyTaskException() {
		super();
	}

	public DailyTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DailyTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public DailyTaskException(String message) {
		super(message);
	}

	public DailyTaskException(Throwable cause) {
		super(cause);
	}
	

}
