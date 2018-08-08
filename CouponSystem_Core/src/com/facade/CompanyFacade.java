package com.facade;

import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import com.beans.Company;
import com.beans.Coupon;
import com.beans.CouponType;
import com.dao.CompanyDAO;
import com.dao.CouponDAO;
import com.dbdao.CompanyDBDAO;
import com.dbdao.CouponDBDAO;
import com.exceptions.CouponSystemException;
import com.exceptions.DataNotFoundException;
import com.main.ClientType;

import com.exceptions.InvalidInputException;

/**
 * Sets the business logic and actions for the Company client.
 * 
 * @author SegevSinay
 */
public class CompanyFacade implements CouponClientFacade {

	private CompanyDAO compDao;
	private CouponDAO coupDao;

	private Company loginCompany;

	/**
	 * Default constructor
	 */
	public CompanyFacade() {

	}

	/**
	 * CompanyFacade Constructor. sets Dao's to DBDAO. sets the loginCompany to the
	 * credentials that were used in the CS login.
	 */
	public CompanyFacade(Company company) throws CouponSystemException {
		this.coupDao = new CouponDBDAO();
		this.compDao = new CompanyDBDAO();
		this.loginCompany = company;
	}

	/**
	 * Creates a specific coupon record in the Coupons table.
	 */
	public void createCoupon(Coupon coupon) throws CouponSystemException {
		Coupon coup = coupDao.getCouponByTitle(coupon.getTitle());
		if (coup != null) {
			throw new InvalidInputException(
					"Unable to create Coupon '" + coupon.getTitle() + "\ncause: Coupon already exist in Databse!");
		}
		if (couponCheck(coupon)) {
			coupDao.createCoupon(coupon);
			System.out.println(coupon.getTitle() + " Coupon record has been successfully created within Coupons table");
			coupDao.updateCompanyCoupon(coupon, loginCompany);
			System.out.println(
					coupon.getTitle() + " Coupon record has been successfully created within Company_Coupons table");
		}
	}

	/**
	 * Removes a specific coupon record coupon from the Coupons table.
	 */
	public void removeCoupon(Coupon coupon) throws CouponSystemException {
		Coupon coup = coupDao.getCouponByTitle(coupon.getTitle());
		if (coup == null) {
			throw new DataNotFoundException(
					"Unable to remove coupon '" + coupon.getTitle() + "' \ncause: Coupon not found!");
		}
		coupDao.removeCouponFromCustomerCoupon(coupon);
		System.out
				.println("Coupon '" + coupon.getTitle() + "' has been successfully removed from CustomerCoupon table");
		coupDao.removeCouponFromCompanyCoupon(coupon);
		System.out.println("Coupon '" + coupon.getTitle() + "' has been successfully removed from CompanyCoupon table");
		coupDao.removeCoupon(coupon);
		System.out.println("Coupon '" + coupon.getTitle() + "' has been successfully removed from Coupons table");
		System.out.println("Coupon '" + coupon.getTitle() + "' has been successfully removed from Database");
	}

	/**
	 * Updates a specific coupon record in Coupons table.
	 * 
	 * NOTE: In order to allow a fully adjustable database Coupon update, instead of
	 * constraining the updateCompany() to price and end date only , i've used the
	 * original Coupon Data for the Original values which should NOT be allowed to
	 * update. Once the web will be operational, the update constrain will be via
	 * the UI fields which will be accessible so the client can update.
	 */
	public void updateCoupon(Coupon coupon) throws CouponSystemException {

		Coupon originalCoup = coupDao.getCouponByTitle(coupon.getTitle());

		if (originalCoup == null) {
			throw new DataNotFoundException(
					"Unable to update Coupon '" + coupon.getTitle() + "'\ncause: Coupon not found!");
		}
		if (couponCheck(coupon)) {
			Coupon updatedCoupon = new Coupon(originalCoup.getTitle(), originalCoup.getStartDate(), coupon.getEndDate(),
					originalCoup.getAmount(), originalCoup.getType(), originalCoup.getMessage(), coupon.getPrice(),
					originalCoup.getImage());

			coupDao.updateCoupon(updatedCoupon);
			System.out
					.println("Coupon '" + coupon.getTitle() + "' End Date and/or Price has been successfully updated");
		}
	}

	/**
	 * allows to get the login company .
	 */
	public Company getLoginCompany() throws CouponSystemException {
		if (loginCompany == null) {
			throw new DataNotFoundException("login company is null");
		}
		return loginCompany;
	}

	/**
	 * allows to set the login company .
	 */
	public void setLoginCompany(Company loginCompany) {
		this.loginCompany = loginCompany;
	}

	// ----------------------------
	// >>>>get Coupons methods<<<<
	// ----------------------------

	/**
	 * Retrieves a specific coupon using it's ID.
	 */
	public Coupon getCoupon(long id) throws CouponSystemException {
		Coupon coupon = coupDao.getCoupon(id);
		if (coupon == null) {
			throw new DataNotFoundException("Unable to retrive coupon \ncause: coupon ID#:" + id + " was not found!");
		} else {
			return coupDao.getCoupon(id);
		}
	}

	/**
	 * Retrieves a specific coupon using it's ID after Validating that the
	 * requesting company is the owner.
	 * 
	 * @throws CouponSystemException
	 */
	public Coupon getCouponAfterOwnershipValidation(long compId, long coupId) throws CouponSystemException {
		if (!couponOwnershipValidation(compId, coupId)) {
			throw new InvalidInputException(
					"Unable to retrive coupon \ncause: The requesting company is not the owner of coupon ID#:"
							+ coupId);
		} else {
			return coupDao.getCoupon(coupId);
		}
	}

	/**
	 * Retrieves all coupons for the specific login company.
	 */
	public Collection<Coupon> getAllCoupons() throws CouponSystemException {
		return compDao.getCoupons(this.getLoginCompany());
	}

	/**
	 * Retrieves a collection of all coupons by the requested type for the specific
	 * login company.
	 */
	public Collection<Coupon> getAllCouponsByType(CouponType coupontype) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> companyCoupons = getAllCoupons();
		Iterator<Coupon> it = companyCoupons.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getType() != coupontype) {
				it.remove();
			}
			if (companyCoupons.isEmpty()) {
				throw new DataNotFoundException("No data found for the requested coupon type");
			}
		}
		return companyCoupons;
	}

	/**
	 * Retrieves a collection of all coupons by the requested price range (0 to max
	 * price) for the specific login company.
	 */
	public Collection<Coupon> getAllCouponsMaxPrice(double price) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> companyCoupons = this.getAllCoupons();
		Iterator<Coupon> it = companyCoupons.iterator();
		while (it.hasNext()) {
			coupon = it.next();
			if (coupon.getPrice() > price) {
				it.remove();
			}
			if (companyCoupons.isEmpty()) {
				throw new DataNotFoundException("No data was found for the requested price range");
			}
		}
		return companyCoupons;
	}

	/**
	 * Retrieves a collection of all coupons by the requested date range for the
	 * specific login company.
	 */
	public Collection<Coupon> getAllCouponsMaxEndDate(Date maxEndDate) throws CouponSystemException {
		Coupon coupon = null;
		Collection<Coupon> companyCoupons = this.getAllCoupons();
		Iterator<Coupon> it = companyCoupons.iterator();
		if (maxEndDate != null) {
			while (it.hasNext()) {
				coupon = it.next();
				if (coupon.getEndDate().after(maxEndDate)) {
					it.remove();
				}
			}
			return companyCoupons;
		} else {
			throw new InvalidInputException("Date cannot be set to null");
		}
	}

	/**
	 * Validates that all coupon credential are valid and return true if: (1) price
	 * is non-negative. (2) date is not set to the past. (3) start date is not after
	 * end date. (4) amount is non-negative.(5) message is not empty. (6) title is
	 * not empty.
	 */
	private Boolean couponCheck(Coupon coupon) throws InvalidInputException {
		// ELDAR YOU'LL NEED TO NEUTRLIZE THE 2 FIRST DATE RESTRICTIONS IN ORDER TO
		// ALLOW CREATION OF EXPIRED COUPON IN THE TEST WITHOUT HAVEING TO WAIT "ONLINE"
		// UNTIL NEXT DAY (=TURN LINES 236-245 TO A COMMENT).

		// if (Calendar.getInstance().getTime().after(coupon.getStartDate())
		// || coupon.getStartDate().after(coupon.getEndDate())) {
		// throw new InvalidInputException(
		// "Date cannot be set to the past and/or End Date must be later than start
		// Date!");
		// }
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

	/**
	 * Validates that the company requesting the coupons is the coupon owner.
	 * returns true if the company own the coupon or false if not.
	 * 
	 * @throws CouponSystemException
	 */

	private Boolean couponOwnershipValidation(long compId, long coupId) throws CouponSystemException {
		Long checkOwnership = compDao.getCouponIdByCompanyId(compId, coupId);
		if (checkOwnership != null) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * NOT IN USE - login process performed in the CouponSystem class
	 */
	@Override
	public CouponClientFacade login(String name, String password, ClientType clienttype) throws CouponSystemException {
		return null;
	}

}
