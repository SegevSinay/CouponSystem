package com.utilityclasses;

import java.util.Calendar;
import java.util.Date;

/**
 * a helper class which provides a date generator that generates a date format
 * that allow us to work with the database.
 */
public class DateGenerator {

	/**
	 * generates a date format that allows to work with the database
	 */
	public static Date genDate(int year, int month, int day) {
		Date myDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		myDate = cal.getTime();
		return myDate;
	}
}
