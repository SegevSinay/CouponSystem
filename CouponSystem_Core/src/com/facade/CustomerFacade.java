package com.facade;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import com.beans.Coupon;
import com.beans.CouponType;
import com.beans.Customer;
import com.dao.CouponDAO;
import com.dao.CustomerDAO;
import com.dbdao.CouponDBDAO;
import com.dbdao.CustomerDBDAO;
import com.exceptions.CouponPurchaseException;
import com.exceptions.CouponSystemException;
import com.exceptions.DataNotFoundException;
import com.exceptions.InvalidInputException;
import com.main.ClientType;

/**
 * Sets the business logic and actions for the Customer client.
 * 
 * @author SegevSinay
 */
public class CustomerFacade implements CouponClientFacade {

	private CustomerDAO custDao;
	private CouponDAO coupDao;
	private Customer logincustomer;

	/**
	 * Default constructor
	 */
	public CustomerFacade() {
	}

	/**
	 * CustomerFacade Constructor. sets Dao's to DBDAO. sets the loginCustomer to
	 * the credentials that were used in the CS login.
	 */
	public CustomerFacade(Customer loginCustomer) throws CouponSystemException {
		this.custDao = new CustomerDBDAO();
		this.coupDao = new CouponDBDAO();
		this.logincustomer = loginCustomer;
	}

	/**
	 * Purchasing process is restricted by the following: (1) if coupon exist within
	 * this customer purchased coupons history it cannot be purchased again. (2) if
	 * the coupon is out of stock, it cannot be purchased. (3) if the coupon is due
	 * its expire date it cannot be purchased. all three must be validated versus
	 * Database current data.
	 */
	public void purchaseCoupon(Coupon coupon) throws CouponSystemException {
		Coupon originalCoup = coupDao.getCouponByTitle(coupon.getTitle());

		// if coupon is expired (since it got deleted upon daily thread run) or not
		// exists
		if (originalCoup == null) {
			throw new CouponPurchaseException("Unable to complete purchasing process \ncause: coupon not found");
		}
		// if coupon was already purchased by this customer
		Collection<Coupon> custCoup = getAllPurchasedCoupons();
		Iterator<Coupon> it = custCoup.iterator();
		while (it.hasNext()) {
			Coupon coup = it.next();
			if (coup.getId() == originalCoup.getId()) {
				throw new CouponPurchaseException(
						"Unable to complete purchasing process \ncause: coupon already purchased by this customer");
			}
		}
		// if coupon is out of stock
		if (originalCoup.getAmount() <= 0) {
			throw new CouponPurchaseException("Unable to complete purchasing process \ncause: coupon is out of stock");
		}
		// --------------------------
		// purchasing update process
		// --------------------------
		Coupon couponCurrentData = new Coupon(originalCoup.getId(), originalCoup.getTitle(),
				originalCoup.getStartDate(), originalCoup.getEndDate(), (originalCoup.getAmount() - 1),
				originalCoup.getType(), originalCoup.getMessage(), originalCoup.getPrice(),
				originalCoup.getImage());

		// add record to customer_coupon table
		custDao.purchaseCoupon(this.getLoginCustomer(), couponCurrentData);
		// update new amount in coupons table

		if (couponCheck(couponCurrentData)) {
			coupDao.updateCoupon(couponCurrentData);
			System.out.println("Coupon '" + coupon.getTitle() + "' Amount has been successfully updated");

		}
	}
	
	/**
	 * MY ADD-ON : Retrieves all coupons so the customer will be able to choose which coupon to purchase.
	 */
	public Collection<Coupon> getAllCoupons() throws CouponSystemException {
		return coupDao.getAllCoupons();
	}

	/**
	 * MY ADD-ON: Retrieves a collection of all coupons by the requested type from all companies
	 */
	public Collection<Coupon> getAllCouponsByType(CouponType coupontype) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> allCoupons = getAllCoupons();
		Iterator<Coupon> it = allCoupons.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getType() != coupontype) {
				it.remove();
			}
			if (allCoupons.isEmpty()) {
				throw new DataNotFoundException("No data found for the requested coupon type");
			}
		}
		return allCoupons;
	}
	
	/**
	 *  MY ADD-ON: Retrieves all coupons by requested max price.
	 */
	public Collection<Coupon> getAllCouponsByPrice(double price) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> couponsByPrice = custDao.getCoupons(this.getLoginCustomer());
		Iterator<Coupon> it = couponsByPrice.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getPrice() > price) {
				it.remove();
			}
		}
		return couponsByPrice;
	}
	/**
	 * Retrieves a specific customer's purchased coupons.
	 */
	public Collection<Coupon> getAllPurchasedCoupons() throws CouponSystemException {
		Collection<Coupon> allPurchasedcoupons = custDao.getCoupons(this.getLoginCustomer());
		return allPurchasedcoupons;
	}

	
	/**
	 * Retrieves a specific customer's purchased coupons by requested coupon type.
	 */
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType coupontype) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> couponsByType = custDao.getCoupons(this.getLoginCustomer());
		Iterator<Coupon> it = couponsByType.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getType() != coupontype) {
				it.remove();
			}
		}
		return couponsByType;
	}

	/**
	 * Retrieves a specific customer's purchased coupons by requested max price.
	 */
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> couponsByPrice = custDao.getCoupons(this.getLoginCustomer());
		Iterator<Coupon> it = couponsByPrice.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getPrice() > price) {
				it.remove();
			}
		}
		return couponsByPrice;
	}

	/**
	 * allows to get the login customer
	 */
	public Customer getLoginCustomer() {
		return logincustomer;
	}

	/**
	 * allows to set the login customer
	 */
	public void setLoginCustomer(Customer logincustomer) {
		this.logincustomer = logincustomer;
	}

	/**
	 * NOT IN USE - login process performed in the CouponSystem class
	 */
	@Override
	public CouponClientFacade login(String name, String password, ClientType clienttype) throws CouponSystemException {
		return null;
	}

	/**
	 * Validates that all coupon credential are valid and return true if: (1) price
	 * is non-negative. (2) date is not set to the past. (3) start date is not after
	 * end date. (4) amount is non-negative.(5) message is not empty. (6) title is
	 * no empty.
	 */
	private Boolean couponCheck(Coupon coupon) throws InvalidInputException {
		// ELDAR YOU'LL NEED TO UNEBLE THE 2 FIRST DATE RESTRICTIONS IN ORDER TO
		// ALLOW CREATION OF EXPIRED COUPON IN THE TEST WITHOUT HAVEING TO WAIT "ONLINE"
		// UNTIL NEXT DAY (=TURN LINES 152-160 TO A COMMENT)
		// (currently I'VE USED 10 miliSec interval just to show the thread works)

		if (Calendar.getInstance().getTime().after(coupon.getStartDate())
				|| coupon.getStartDate().after(coupon.getEndDate())) {
			throw new InvalidInputException(
					"Date cannot be set to the past and/or End Date must be later than start Date!");
		}
		if (Calendar.getInstance().getTime().after(coupon.getEndDate())) {
			throw new InvalidInputException(
					"Date cannot be set to the past and/or End Date must be later than start Date!");
		}

		if (coupon.getPrice() < 0) {
			throw new InvalidInputException("Invalid price ! Price cannot be negative!");
		}
		if (coupon.getAmount() < 0) {
			throw new InvalidInputException("Invalid amount ! Amount cannot be negative!");
		}
		if (coupon.getMessage().length() == 0) {
			throw new InvalidInputException("Message cannot be empty ! please add a short coupon description!");
		}
		if (coupon.getTitle().length() == 0) {
			throw new InvalidInputException("Title cannot be empty ! please add a coupon title!");
		} else {
			return true;
		}
	}

}
