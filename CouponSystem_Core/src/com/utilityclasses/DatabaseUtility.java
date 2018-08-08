package com.utilityclasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;
/**
 * this is a utility class that creates the initial database tables
 * and not a part of the coupon system.
 *
 */
public class DatabaseUtility extends CouponSystemException {

	
	private static final long serialVersionUID = 1L;

	private static String url;
	private static String driverUrl;
	private static File urlFile = new File("files/dbUrl.txt");
	private static File driverFile = new File("files/driverUrl.txt");

	public static String getUrl() {
		return url;
	}
	public static String getDriverurl() {
		return driverUrl;
	}

	
	/**
	 * static initializer, will run on class load once, when class is loaded.
	 */
	static {
		try (Scanner sc = new Scanner(driverFile);) { // loads the driver upon class load
			driverUrl = sc.nextLine();
			Class.forName(driverUrl);
			System.out.println("Driver has been successfully loaded");
		} catch (FileNotFoundException | ClassNotFoundException e) {
			System.err.println("Driver had not been loaded" + e.getMessage());
		}
		try (Scanner sc = new Scanner(urlFile);) {
			url = sc.nextLine() + ";create=true";
			System.out.println("Database connection to:" + "\n" + url + ", has been successfully created.");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			if (e.getCause() != null) {
				e.getCause();
			}
		}
	}

	/**
	 * creates the company table in the DB
	 */
	public static void createCompaniesTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "CREATE TABLE companies (";
			sql += "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),";
			sql += "COMP_NAME VARCHAR(20) UNIQUE NOT NULL,";
			sql += "PASSWORD VARCHAR(10) NOT NULL,";
			sql += "EMAIL VARCHAR(30) NOT NULL";
			sql += ")";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);

			System.out.println("Companies Table has been successfully created");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Companies Table", e);
		}
	}

	/**
	 * removes the company table from the DB
	 */
	public static void removeCompaniesTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "DROP TABLE companies";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Companies Table has been successfully removed");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove Companies Table", e);
		}
	}

	/**
	 * creates the customer table in the DB
	 */
	public static void createCustomersTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "CREATE TABLE customers(";
			sql += "ID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),";
			sql += "CUST_NAME VARCHAR(20) UNIQUE NOT NULL,";
			sql += "PASSWORD VARCHAR(10) NOT NULL";
			sql += ")";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Customers Table has been successfully created");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Customers Table", e);
		}
	}

	/**
	 * removes the customer table from the DB
	 */
	public static void removeCustomersTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "DROP TABLE customers";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Customers Table has been successfully removed");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove Customers Table", e);
		}
	}

	/**
	 * creates the coupon table in the DB
	 */
	public static void createCouponsTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {
			String sql = "CREATE TABLE coupons(";
			sql += "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),";
			sql += "TITLE VARCHAR(30) UNIQUE NOT NULL,"; // header - short coupon description
			sql += "START_DATE DATE NOT NULL,"; // coupon valid from date
			sql += "END_DATE DATE NOT NULL,";// coupon expired date
			sql += "AMOUNT INTEGER NOT NULL,";// coupon quantity in stock
			sql += "TYPE VARCHAR(30) NOT NULL,"; // coupon category - a closed list (ENUM).
			sql += "MESSAGE VARCHAR(200) NOT NULL,"; // coupon detailed description
			sql += "PRICE FLOAT NOT NULL,"; // coupon listed price (for customers)
			sql += "IMAGE VARCHAR(10000)"; // link or relevant picture location
			sql += ")";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Coupons Table has been successfully created");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Coupons Table", e);
		}
	}

	/**
	 * removes the coupon table from the DB
	 */
	public static void removeCouponsTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {
			String sql = "DROP TABLE coupons";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Coupons Table has been successfully removed");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Coupons Table", e);
		}
	}

	/**
	 * create the Customer_Coupon table in the DB
	 */
	public static void createCustomer_CouponTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "CREATE TABLE customer_coupon(";
			sql += "CUST_ID BIGINT NOT NULL,";
			sql += "COUPON_ID BIGINT NOT NULL,";
			sql += "PRIMARY KEY(CUST_ID, COUPON_ID )";
			sql += ")";

			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Customer_Coupon Table has been successfully created");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Customer_Coupon Table", e);
		}
	}

	/**
	 * removes the Customer_Coupon table from the DB
	 */
	public static void removeCustomer_CouponTable() throws DatabaseException {
		try (Connection con = DriverManager.getConnection(getUrl());) {

			String sql = "DROP TABLE customer_coupon";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Customer_Coupon Table has been successfully removed");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove Customer_Coupon Table", e);
		}
	}

	/**
	 * create the Company_Coupon Table table in the DB
	 */
	public static void createCompany_CouponTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {
			String sql = "CREATE TABLE company_coupon(";
			sql += "COMP_ID BIGINT NOT NULL,";
			sql += "COUPON_ID BIGINT NOT NULL, ";
			sql += "PRIMARY KEY(COMP_ID, COUPON_ID)";
			sql += ")";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Company_Coupon Table has been successfully created");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Company_Coupon Table", e);
		}
	}

	/**
	 * removes the Company_Coupon Table table from the DB
	 */
	public static void removeCompany_CouponTable() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {

			String sql = "DROP TABLE company_coupon";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("Company_Coupon Table has been successfully removed");

		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove Company_Coupon Table", e);
		}
	}

	/**
	 * removes all tables from the DB
	 */
	public static void removeAllDBTables() throws DatabaseException {
		try (Connection conn = DriverManager.getConnection(getUrl());) {
			removeCompaniesTable();
			removeCouponsTable();
			removeCompany_CouponTable();
			removeCustomersTable();
			removeCustomer_CouponTable();

		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove Database Tables! ", e);
		}

		System.out.println();
		System.out.println("--------------------------------------------------");
		System.out.println("- Database tables has been successfully Removed! -");
		System.out.println("--------------------------------------------------");
	}
	/**
	 * creates all tables in the DB
	 */
	public static void createAllDBTables() throws DatabaseException {
		try (Connection con = DriverManager.getConnection(getUrl());) {
			createCompaniesTable();
			createCouponsTable();
			createCompany_CouponTable();
			createCustomersTable();
			createCustomer_CouponTable();

		} catch (SQLException e) {
			throw new DatabaseException("Unable to create Database Tables! \ncause: ", e);
		}
		System.out.println();
		System.out.println("---------------------------------------------------");
		System.out.println("-  Database tables has been successfully Created! -");
		System.out.println("---------------------------------------------------");
	}
	/**
	 * removes tables if exists and then creates all tables in the DB
	 */
	public static void resetAllDBTables() throws DatabaseException {
		try (Connection con = DriverManager.getConnection(getUrl());) {
			try {
				createCompaniesTable();
			} catch (DatabaseException e) {
				removeCompaniesTable();
				createCompaniesTable();
			}
			try {
				createCouponsTable();
			} catch (DatabaseException e) {
				removeCouponsTable();
				createCouponsTable();
			}
			try {
				createCompany_CouponTable();
			} catch (DatabaseException e) {
				removeCompany_CouponTable();
				createCompany_CouponTable();
			}
			try {
				createCustomersTable();
			} catch (DatabaseException e) {
				removeCustomersTable();
				createCustomersTable();
			}
			try {
				createCustomer_CouponTable();
			} catch (DatabaseException e) {
				removeCustomer_CouponTable();
				createCustomer_CouponTable();
			}

		} catch (SQLException e) {
			throw new DatabaseException("Unable to reset Database Tables! \ncause:", e);
		}
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println("- Database has been successfully Reseted! -");
		System.out.println("------------------------------------------");
	}

	
}
