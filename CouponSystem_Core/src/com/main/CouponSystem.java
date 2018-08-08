package com.main;

import com.beans.Company;
import com.beans.Customer;
import com.connectionpool.ConnectionPool;
import com.dao.CompanyDAO;
import com.dao.CouponDAO;
import com.dao.CustomerDAO;
import com.dbdao.CompanyDBDAO;
import com.dbdao.CouponDBDAO;
import com.dbdao.CustomerDBDAO;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.InvalidLoginCredentials;
import com.facade.AdminFacade;
import com.facade.CompanyFacade;
import com.facade.CouponClientFacade;
import com.facade.CustomerFacade;
import com.utilityclasses.DailyCouponExpirationTask;
/**
 * This is the coupon system Single-tone class which:
 * a.	Allows the different clients to Login into the system and perform different actions according to the Client type.
 * b.	Creating and running the ‘DailyCouponExpirationTask’.
 * c.	Loading the DAOs
 * d.	Contains the ‘shutdown ()’ method which shuts the coupon system gracefully.
 */
public class CouponSystem {

	private CompanyDAO compDao;
	private CouponDAO coupDao;
	private CustomerDAO custDao;
	private static CouponSystem coupSysInstance;
	private ConnectionPool connPool;
	private Boolean activateDailyCouponExpirationTask = true;
	private Thread t = new DailyCouponExpirationTask();

	/**
	 * loading and initiating
	 */
	private CouponSystem() throws CouponSystemException, InterruptedException {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			throw new ConnectionPoolException("Unable to load Driver!", e);
		}
		connPool = ConnectionPool.getInstance();
		loadDAOs();
		activateDailyCouponExpirationTask = true;
		runDailyCouponExpirationTask();
	}

	/**
	 * Singleton = create instance only once
	 */
	public synchronized static CouponSystem getInstance() throws CouponSystemException, InterruptedException {
		if (coupSysInstance == null) {
			coupSysInstance = new CouponSystem();
		}
		return coupSysInstance;
	}

	/**
	 * loads all the Daos and initiate them as DBDAO
	 */
	private void loadDAOs() throws CouponSystemException {

		if (connPool != null) {
			if (compDao == null) {
				compDao = new CompanyDBDAO();
			}
			if (coupDao == null) {
				coupDao = new CouponDBDAO();
			}
			if (custDao == null) {
				custDao = new CustomerDBDAO();
			}
		}
	}


	/**
	 * activates & runs the Daily Coupon Expiration Task.
	 */
	private void runDailyCouponExpirationTask() throws ConnectionPoolException, InterruptedException {
		if (activateDailyCouponExpirationTask == true) { // ADD EXCEPTION!?!!?
			t.start();
		}
	}

	/**
	 * login by client type into the Coupon System. verifies that the login credentials
	 * are valid and then grants access to the facade by client type.
	 */
	public CouponClientFacade login(String name, String password, ClientType clientType) throws CouponSystemException {

		switch (clientType) {

		case ADMIN:
			if (name.equals("admin") && password.equals("1234")) {
				AdminFacade adminF = new AdminFacade();
				return adminF;
			} else {
				throw new InvalidLoginCredentials("Invalid Username/Password");
			}

		case COMPANY:
			if (compDao.login(name, password)) {
				Company company = compDao.getCompanyByName(name);
				CompanyFacade companyF = new CompanyFacade(company);
				System.out.println("login for company '" + name + "' is confirmed");
				companyF.setLoginCompany(company);
				System.out.println(companyF.getLoginCompany());
				return companyF;
			} else {
				throw new InvalidLoginCredentials("Invalid Username/Password");
			}

		case CUSTOMER:
			if (custDao.login(name, password)) {
				Customer customer = custDao.getCustomerByName(name);
				CustomerFacade customerF = new CustomerFacade(customer);
				System.out.println("login for customer '" + name + "' is confirmed");
				return customerF;
			} else {
				throw new InvalidLoginCredentials("Invalid Username/Password");
			}
		}
		return null;
	}

	/**
	 * shuts down the Coupon System gracefully.
	 */
	public void shutdown() throws ConnectionPoolException {
		try {
			t.interrupt();
			t.join();
			System.out.println("System is shutting down...");
			connPool.closeAllConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("System shutdown completed successfully");
		System.exit(0);
	}
}
