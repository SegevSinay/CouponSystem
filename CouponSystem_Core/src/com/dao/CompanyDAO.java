package com.dao;

import java.util.Collection;

import com.exceptions.CouponSystemException;

import com.beans.Company;
import com.beans.Coupon;

/**
 * Data Access Object is a design pattern that provides an abstract interface to
 * the database. The DAO provides some specific data operations without exposing
 * the realization mechanism. The companyDAO mainly defines the C.R.U.D
 * operations that can be performed on a Company Object in the Database.
 * implementation is done by Concrete classes such as the CompanyDBDAO class
 * which interacts with the Driver and with the Database thought SQL queries.
 * 
 * @author SegevSinay
 *
 */
public interface CompanyDAO {
	/**
	 * Exports the detailed data of the record we want to create from Java to the
	 * database for the requested company
	 */
	public void createCompany(Company company) throws CouponSystemException;

	/**
	 * Removes a specific company from the database by removing it from the
	 * companies table.
	 */
	public void removeCompany(Company company) throws CouponSystemException;

	/**
	 * updates a specific company data.
	 */
	public void updateCompany(Company company) throws CouponSystemException;

	/**
	 * Retrieves a specific company record from companies table. (Imports the
	 * details of each company record from DB to Java and then creates each company
	 * Object using these details)
	 */
	public Company getCompany(long id) throws CouponSystemException;

	/**
	 * Retrieves all companies record from companies table. (Imports the detail of
	 * the full companies Table from DB to Java and then using these details it
	 * creates the companies List)
	 */
	public Collection<Company> getAllCompanies() throws CouponSystemException;

	/**
	 * Retrieves a specific company coupons by creating a joined table of both
	 * coupons and company_coupon tables, once table are joined it extracts all
	 * company's coupons records using the company's ID identifier. (Imports the
	 * detail of the full company's coupons from DB to Java and then creates the
	 * coupon list using these details)
	 */
	public Collection<Coupon> getCoupons(Company company) throws CouponSystemException;

	/**
	 * Checks for the combine key of company's name and password, if such a
	 * combination exist within the companies records it will return true else it
	 * will return false.
	 */
	public boolean login(String compName, String password) throws CouponSystemException;

	/**
	 * Retrieves a specific company ID using Company Name. Although the most
	 * reliable unique key column is the Company's ID column, since Company's name
	 * is also a unique value, it enable us to use Company's name as an identifier.
	 * BUT! since company_coupon is based on the COMP_ID, the ID identifier is
	 * required for the retrieval of the Company's coupon ID and vice versa.
	 * personally, I find that the usage of company's name is easier to implement
	 * than extracting the ID using the "RETURN_GENERATED_KEYS" option, therefore
	 * i've chosen to add and implement the following Method.
	 */
	public long getCompanyId(String compname) throws CouponSystemException;

	/**
	 * Retrieves a specific company using Company Name. Although the most reliable
	 * unique key column is the Company's ID column, since Company's name is also a
	 * unique value, it enable us to use Company's name as an identifier.
	 * personally, I found the usage of company's name easier to implement than
	 * extracting the ID using the "RETURN_GENERATED_KEYS" option, so i've chosen to
	 * add and implement the following Method.
	 */
	public Company getCompanyByName(String compname) throws CouponSystemException;

	/**
	 * Removes a specific company coupons from the database. i've chosen to add this
	 * method since it allows the removal of all company's coupon record from the
	 * company_coupon table "in a single shot". Although a company removal requires
	 * a removal of all its coupons from company_coupon table ,in order to maintain
	 * the coupon system fully adjustable and to keep database method "clean" from
	 * any business logic (which is introduced and implemented in the facade),I've
	 * chosen to use a separate method instead of a single method although it is
	 * less performance efficient.
	 */
	void removeCompanyFromCompanyCoupon(Company company) throws CouponSystemException;

	/**
	 * Query a specific Email address. returns the actual Email address if email
	 * exist or null if doesn't. this allow to perform an Email availability check
	 * upon company creation and/or update.
	 */
	public String emailAvaliabilityCheck(String email) throws CouponSystemException;

	/**
	 * Retrieves requesting company Coupon Id according to the requesting company
	 * Id; returns the requested coupon Id if exist or null if it doesn't.
	 */
	public Long getCouponIdByCompanyId(long compId, long coupId) throws CouponSystemException;
}
