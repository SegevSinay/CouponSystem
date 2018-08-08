package com.facade;

import java.util.Collection;
import java.util.Iterator;

import com.beans.Company;
import com.beans.Coupon;
import com.beans.Customer;
import com.dao.CompanyDAO;
import com.dao.CouponDAO;
import com.dao.CustomerDAO;
import com.dbdao.CompanyDBDAO;
import com.dbdao.CouponDBDAO;
import com.dbdao.CustomerDBDAO;
import com.exceptions.CouponSystemException;
import com.exceptions.DataNotFoundException;
import com.exceptions.InvalidInputException;
import com.main.ClientType;

/**
 * Sets the business logic and actions for the Admin client.
 * 
 * @author SegevSinay
 */
public class AdminFacade implements CouponClientFacade {

	private CompanyDAO compDao;
	private CustomerDAO custDao;
	private CouponDAO coupDao;

	/**
	 * AdminFacade Constructor. sets the Dao's to DBDAO's.
	 */
	public AdminFacade() throws CouponSystemException {
		this.custDao = new CustomerDBDAO();
		this.coupDao = new CouponDBDAO();
		this.compDao = new CompanyDBDAO();
	}

	// ---------------------------
	// Companies related methods
	// ---------------------------

	/**
	 * Creates a new company in the companies table under the following
	 * RESTRICTIONS: 1. Can't create a company with the same name; 2. Password: must
	 * have at least eight characters,consists of only letters and digits, must
	 * contain at least two digits. 3. Email: must be a legal Email format. if a
	 * restriction is violated it throws InvalidInputException.
	 */
	public void createCompany(Company company) throws CouponSystemException {
		String compEmail = company.getEmail();
		Company compName = compDao.getCompanyByName(company.getCompName());
		String compPassword = company.getPassword();
		if (compName != null) {
			throw new InvalidInputException("Unable to create '" + company.getCompName()
					+ "' company\ncause: company already exists within the Database!");
		} else if (!passwordValidation(compPassword)) {
			throw new InvalidInputException(
					"Invalid password, please make sure that:" + "\n1. password length is 8-10 characters."
							+ "\n2. password contains letters and digits only."
							+ "\n3. password contains at least two digits.");
		} else if (!validateEmailAvaliability(compEmail)) {
			throw new InvalidInputException(
					"Requested Email address is already taken! please enter another Email address");

			// DEPRICIATED : EMAIL VALIDATION IS DONE USING ANGULAR FORM 'email' RESERVED
			// WORD
			// } else if (!validateEmail(compEmail)) {
			// throw new InvalidInputException(
			// "Invalid Email , please enter a valid Email format e.g
			// username@example.com");
		}
		compDao.createCompany(company);
		System.out.println("'" + company.getCompName() + " company record has been successfully Created in database");

	}

	/**
	 * Removes a company and all its coupons from the database. Removing a company
	 * with this method impacts the following database tables: (a) Company_Coupon
	 * table: if any company's coupon record has been created. (b) Customer_Coupon
	 * (d) Companies table. It is imperative we will remove all company's related
	 * data from these tables in order to be able to remove the company data. once
	 * we clear all related data it is safe to remove the company from the companies
	 * table.
	 */
	public void removeCompany(Company company) throws CouponSystemException {
		long compId = compDao.getCompanyId(company.getCompName());
		if (compId == 0) {
			throw new DataNotFoundException(
					"Unable to remove '" + company.getCompName() + "' company \ncause: company not found!");
		}
		Collection<Coupon> couponsToDelete = compDao.getCoupons(company);
		Iterator<Coupon> it = couponsToDelete.iterator();
		while (it.hasNext()) {
			for (Coupon coupon : couponsToDelete) {
				System.out.println(coupon);
				coupDao.removeCouponFromCustomerCoupon(coupon);
				System.out.println(coupon + " has been successfully removed from customer_coupon table");
				coupDao.removeCoupon(coupon);
				System.out.println(coupon + " has been successfully removed from coupons table");
			}
		}
		compDao.removeCompanyFromCompanyCoupon(company);// seems more efficient than removing each coupon...
		System.out.println(company.getCompName() + " company has been successfully Removed from company_coupon table ");
		compDao.removeCompany(company);
		System.out.println(company.getCompName() + " company has been successfully removed from companied table");
		System.out.println(company.getCompName() + " company has been successfully Removed from database");
	}

	/**
	 * Updates an existing company in the companies table under the following
	 * RESTRICTIONS: 1. Can't update a company that has not been created first. 2.
	 * Password: must have at least eight characters,consists of only letters and
	 * digits, must contain at least two digits. 3. Email: must be a legal Email
	 * format.if a restriction is violated it throws InvalidInputException.
	 */
	public void updateCompany(Company company) throws CouponSystemException {
		long compId = compDao.getCompanyId(company.getCompName());
		String compPassword = company.getPassword();
		String compEmail = company.getEmail();
		if (compId == 0) {
			throw new DataNotFoundException("Unable to update '" + company.getCompName()
					+ "' company \ncause: company not found within the Database!");
		}
		if (!passwordValidation(compPassword)) {
			throw new InvalidInputException("Invalid password, please make sure that:"
					+ "\n1. password length is 8-10 characters." + "\n2. password contains letters and digits only."
					+ "\n3. password contains at least two digits.");
		}
//		if (!validateEmailAvaliability(compEmail)) {
//			throw new InvalidInputException(
//					"Requested Email address is already taken! please try a different email address");
//		}
		// DEPRICIATED : EMAIL VALIDATION IS DONE USING ANGULAR FORM 'email' RESERVED
		// WORD
		// } else if (!validateEmail(compEmail)) {
		// throw new InvalidInputException(
		// "Invalid Email , please enter a valid Email format e.g
		// username@example.com");
		System.out.println("'" + company.getCompName() + "' has been successfully updated ");
		compDao.updateCompany(company);
	}

	/**
	 * Retrieves a specific company using its ID. if retrieved company is null, it
	 * throws a CouponSystemException.
	 */
	public Company getCompany(long id) throws CouponSystemException {
		Company company = compDao.getCompany(id);
		if (company == null) {
			throw new DataNotFoundException("Unable to retrive compay data for company '"
					+ compDao.getCompany(id).getCompName() + "'\ncause: Company ID not found!");
		}
		return company;
	}

	/**
	 * Retrieves a specific company using its ID. if retrieved company is null, it
	 * throws a CouponSystemException.
	 */
	public Collection<Company> getAllCompanies() throws CouponSystemException {
		if (compDao.getAllCompanies() == null) {
			throw new DataNotFoundException("No data to retrive!");
		}
		System.out.println("companies data retrived successfully");
		return compDao.getAllCompanies();
	}

	// --------------------------
	// Customers related methods
	// --------------------------

	/**
	 * Creates a new customer in the customers table under the following
	 * RESTRICTIONS: 1. Can't create a customer with the same name; 2. Password:
	 * must have at least eight characters,consists of only letters and digits, must
	 * contain at least two digits. if a restriction is violated it throws
	 * InvalidInputException.
	 */
	public void createCustomer(Customer customer) throws CouponSystemException {
		Customer custName = custDao.getCustomerByName(customer.getCustName());
		String custPassword = customer.getPassword();
		if (custName != null) {
			throw new InvalidInputException("\"Unable to create Customer '" + customer.getCustName()
					+ "'\ncause: customer ' " + customer.getCustName() + "' already exists within the Database!");
		}
		if (!passwordValidation(custPassword)) {
			throw new InvalidInputException("Invalid password, please make sure that:"
					+ "\n1. password length is 8-10 characters." + "\n2. password contains letters and digits only."
					+ "\n3. password contains at least two digits.");
		}
		custDao.createCustomer(customer);
		System.out.println("'" + customer.getCustName() + "' has been successfully added to customers table");
	}

	/**
	 * Remove a specific customer and all of its coupons. Removing a customer with
	 * this method impacts the following : (a) customer_coupon table: if a coupon
	 * has been purchased by this customer. (b)customers table. first,
	 * customer_coupon record will be removed then the customer will removed from
	 * the customers table. if company is not found it throws InvalidInputException.
	 */
	public void removeCustomer(Customer customer) throws CouponSystemException {
		long custId = custDao.getCustomerId(customer.getCustName());
		if (custId == 0) {
			throw new DataNotFoundException(
					"Unable to remove Customer: '" + customer.getCustName() + "'\nCause: Customer not found!");
		}
		custDao.removeCustomerFromCustomerCoupon(customer);// seems more efficient than removing each coupon...
		custDao.removeCustomer(customer);
		System.out.println("Customer has been successfully removed from companied table");
	}

	/**
	 * Updates an existing customer in the customers table under the following
	 * RESTRICTIONS: 1. Can't create a customer with the same name; 2. Password:
	 * must have at least eight characters,consists of only letters and digits, must
	 * contain at least two digits. if customer is not found it throws
	 * DatabaseException. if a restriction is violated it throws
	 * InvalidInputException.
	 */
	public void updateCustomer(Customer customer) throws CouponSystemException {
		Customer custName = custDao.getCustomerByName(customer.getCustName());
		String custPassword = customer.getPassword();
		if (custName == null) {
			throw new DataNotFoundException(
					"Unable to create '" + customer.getCustName() + "' customer \ncause: Customer not found!");
		}
		if (!passwordValidation(custPassword)) {
			throw new InvalidInputException("Invalid password, please make sure that:"
					+ "\n1. password length is 8-10 characters." + "\n2. password contains letters and digits only."
					+ "\n3. password contains at least two digits.");
		}
		custDao.updateCustomer(customer);
	}

	/**
	 * Retrieves a specific customer using its ID. if retrieved customer is null, it
	 * throws a InvalidInputException.
	 */
	public Customer getCustomer(long id) throws CouponSystemException {
		Customer customer = custDao.getCustomer(id);
		if (customer.getCustName() == null) {
			throw new DataNotFoundException("Unable to update Customer '" + custDao.getCustomer(id).getCustName()
					+ "' \ncause: customer not found!");
		}
		return customer;
	}

	/**
	 * Retrieves a all customers using its ID.
	 */
	public Collection<Customer> getAllCustomers() throws CouponSystemException {
		Collection<Customer> allCust = custDao.getAllCustomer();
		System.out.println("Customers data retrived");
		return allCust;
	}

	/**
	 * NOT IN USE - login process performed in the CouponSystem class
	 */
	@Override
	public AdminFacade login(String name, String password, ClientType clienttype) throws CouponSystemException {
		return null;
	}

	/**
	 * Validates that the password is in the correct format. Returns true if and
	 * only if the password: a. at least eight characters length b. consists of only
	 * letters and digits. c. contains at least two digits.
	 */
	private static boolean passwordValidation(String password) {
		if (password.length() < 8 || password.length() > 10) {
			return false;
		} else {
			char c;
			int count = 1;
			for (int i = 0; i < password.length() - 1; i++) {
				c = password.charAt(i);
				if (!Character.isLetterOrDigit(c)) {
					return false;
				} else if (Character.isDigit(c)) {
					count++;
					if (count < 2) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * DEPRICIATED : EMAIL VALIDATION IS DONE USING ANGULAR FORM 'email' RESERVED
	 * WORD Validates that the Email is in the correct Email format. e.g
	 * "Alias@domain.com";
	 * 
	 * @throws CouponSystemException
	 */
	// private boolean validateEmail(String email) {
	// Pattern inputPattern;
	// Matcher inputMatcher;
	// inputPattern =
	// Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
	// inputMatcher = inputPattern.matcher(email);
	// if (inputMatcher.matches()) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	private boolean validateEmailAvaliability(String email) throws CouponSystemException {
		String checkEmail = compDao.emailAvaliabilityCheck(email);
		if (checkEmail != null) {
			return false;
		} else {
			return true;
		}
	}
}
