package com.facade;

import com.exceptions.CouponSystemException;
import com.main.ClientType;

/**
 * CouponClientFacade Interface provides the login process data that be entered
 * so the system will be able to determine which function will be available to
 * the logged in client account. All concrete Facade classes must implement this
 * interface.
 * 
 * @author SegevSinay
 */
public interface CouponClientFacade {
	/**
	 * Login method determines the current login client type. According to these
	 * details it will return the proper Facade and within it the business functions
	 * that this Client has access to perform.
	 */
	public CouponClientFacade login(String name, String password, ClientType clienttype) throws CouponSystemException;

}
