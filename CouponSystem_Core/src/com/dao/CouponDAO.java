package com.dao;

import java.util.Collection;
import java.util.List;

import com.beans.Company;
import com.beans.Coupon;
import com.beans.CouponType;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;

/**
 * Data Access Object is a design pattern that provides an abstract interface to
 * the database. The DAO provides some specific data operations without exposing
 * the realization mechanism. The CouponDAO mainly defines the C.R.U.D
 * operations that can be performed on a Coupon Object in the Database.
 * implementation is done by Concrete classes such as the CouponDBDAO class
 * which interacts with the Driver and with the Database thought SQL queries.
 * 
 * @author SegevSinay
 *
 */
public interface CouponDAO {
	/**
	 * Creates a coupon within the coupons table. (Exports the detailed data of the
	 * record we want to create from Java to the database for the requested coupon)
	 */
	void createCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException;

	/**
	 * Removes a specific coupon record from coupons table.
	 */
	void removeCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException;

	/**
	 * Updates a specific coupon record within the coupons table. (Exports the detail
	 * for the record we want to update within the database for the requested coupon)
	 */
	void updateCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException;

	/**
	 * Retrieves a specific coupon record from the coupons table. (Imports the detail
	 * of the requested coupon record from DB to Java then it creates the coupon
	 * Object using these details)
	 */
	Coupon getCoupon(long id) throws DatabaseException, ConnectionPoolException;

	/**
	 * Retrieves all coupon record for coupons table. (Imports the detail of the
	 * full coupons Table from DB to Java and then using these details it creates
	 * the coupons List)
	 */
	Collection<Coupon> getAllCoupons() throws DatabaseException, ConnectionPoolException;

	/**
	 * Retrieves all coupon record for coupons table by coupon Type. (Imports the
	 * detail of the full coupons from DB to Java and then creates the coupon list
	 * using these details)
	 */
	Collection<Coupon> getCouponByType(CouponType coupontype) throws DatabaseException, ConnectionPoolException;

	// my addition :
	
	/**
	 * Inserts a new company and coupon record to Company_Coupon table. (each coupon
	 * record thats get created within the coupons table needs to have parallel
	 * record entry within the join_company_coupon table)
	 */
	void updateCompanyCoupon(Coupon coupon, Company company) throws CouponSystemException;

	/**
	 * Removes a specific coupon record from customer_coupons joined table.
	 */
	void removeCouponFromCustomerCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException;

	/**
	 * Retrieves a specific coupon using Coupon Name. Although the most reliable
	 * unique key column is the Coupon ID column, since the coupon name is also a
	 * unique value, it enable us to use Coupon name as an identifier. BUT! since
	 * company_coupon is based on the COMP_ID, the ID identifier is required for the
	 * retrieval of the Company's coupon ID and vice versa. personally, I find that
	 * the usage of company's name is easier to implement than extracting the ID
	 * using the "RETURN_GENERATED_KEYS" option, therefore i've chosen to
	 * add and implement the following Method.
	 */
	long getCouponId(String title) throws DatabaseException, ConnectionPoolException;

	/**
	 * Removes a specific coupon record from company_coupons joined table.
	 */
	void removeCouponFromCompanyCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException;

	/**
	 * Retrieves a specific coupon using Coupon Name. Although the most reliable
	 * unique key column is the Coupon ID column, since the coupon name is also a
	 * unique value, it enable us to use Coupon name as an identifier. personally, I
	 * find that the usage of company's name is easier to implement than extracting
	 * the ID using the "RETURN_GENERATED_KEYS" option, therefore i've
	 * chosen to add and implement the following Method.
	 */
	Coupon getCouponByTitle(String title) throws DatabaseException, ConnectionPoolException;

	/**
	 * Checks the coupons table (in the database) and collect to a coupon list all
	 * the expired coupons based on their end date. 
	 */
	List<Coupon> collectExpiredCoupons() throws DatabaseException, ConnectionPoolException;

}
