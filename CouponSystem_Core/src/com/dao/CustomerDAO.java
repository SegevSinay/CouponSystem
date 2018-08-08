package com.dao;

import java.util.Collection;

import com.beans.Coupon;
import com.beans.Customer;
import com.exceptions.CouponSystemException;

/**
 * Data Access Object is a design pattern that provides an abstract interface to
 * the database. The DAO provides some specific data operations without exposing
 * the realization mechanism. The CustomerDAO mainly defines the C.R.U.D
 * operations that can be performed on a Customer Object in the Database.
 * implementation is done by Concrete classes such as the CustomerDBDAO class
 * which interacts with the Driver and with the Database thought SQL queries.
 * 
 * @author SegevSinay
 *
 */
public interface CustomerDAO {
	/**
	 * Creates a specific customer in customers table. (Exports the detailed data of
	 * the record we want to create from Java to the database for the requested
	 * customer).
	 */
	public void createCustomer(Customer customer) throws CouponSystemException;
	/**
	 * Removes a specific customer from customers table.
	 */
	public void removeCustomer(Customer customer) throws CouponSystemException;
	/**
	 * Updates a specific customer record within the Customer table. (Exports the
	 * detail for the record we want to update within the database for the requested
	 * customer.)
	 */
	public void updateCustomer(Customer customer) throws CouponSystemException;
	/**
	 * Retrieves a specific customer data from customers table using customer ID as
	 * the identifier. (Imports the detail of the requested customer record from DB
	 * to Java then it creates the customer Object using these details)
	 */
	public Customer getCustomer(long id) throws CouponSystemException;
	/**
	 * Retrieves all customers data from customers table. (Imports the detail of the
	 * full customers Table from DB to Java and then using these details it creates
	 * the all customers list)
	 */
	public Collection<Customer> getAllCustomer() throws CouponSystemException;
	/**
	 * Retrieves a specific customer coupons by creating a joined table of both
	 * coupons and customer_coupon tables, once table are joined it extracts all
	 * customer's coupons records using the customer's ID identifier. (Imports the
	 * detail of the full customer coupons from DB to Java and then creates the
	 * coupon list using these details)
	 */
	public Collection<Coupon> getCoupons(Customer customer) throws CouponSystemException;
	/**
	 * Checks for the combine key of customer name and password, if such a
	 * combination exist within the customers records it will return true else it
	 * will return false
	 */
	public boolean login(String custName, String password) throws CouponSystemException;

	// my additions:
	/**
	 * Creates a new record in Customer_Coupon table. (Exports the detail for the
	 * purchase record we want to create within the customer_coupon table for the
	 * requested customer and coupon using their Id.)
	 */
	public void purchaseCoupon(Customer customer, Coupon coupon) throws CouponSystemException;
	/**
	 * Retrieves a specific customer ID using customer Name. Although the most
	 * reliable unique key column is the customer's ID column, since customer name
	 * is also a unique value, it enable us to use it as an identifier. BUT! since
	 * customer_coupon is based on the CUST_ID, the ID identifier is required for
	 * the retrieval of the customer coupon ID and vice versa. personally, I find
	 * that the usage of customer name is easier to implement than extracting the ID
	 * using the "RETURN_GENERATED_KEYS" option, therefore i've chosen to
	 * add and implement the following Method.
	 */
	public long getCustomerId(String name) throws CouponSystemException;
	/**
	 * Retrieves a specific customer using customer Name. Although the most reliable
	 * unique key column is the customer's ID column, since customer name is also a
	 * unique value, it enable us to use it as an identifier.
	 */
	public Customer getCustomerByName(String name) throws CouponSystemException;
	/**
	 * Removes a specific customer coupons from the database. i've chosen to add
	 * this method since it allows the removal of all customer's coupon record from
	 * the customer_coupon table "in a single shot". Although a customer removal
	 * requires a removal of all its coupons from customer_coupon table ,in order to
	 * maintain the coupon system fully adjustable and to keep database method
	 * "clean" from any business logic (which is introduced and implemented in the
	 * facade),I've chosen to use a separate method instead of a single method
	 * although it is less performance efficient.
	 */
	void removeCustomerFromCustomerCoupon(Customer customer) throws CouponSystemException;
}
