package com.main;

import com.beans.Company;
import com.beans.Customer;
import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;
import com.facade.AdminFacade;
import com.facade.CompanyFacade;
import com.facade.CustomerFacade;
import com.utilityclasses.DatabaseUtility;

import com.beans.Coupon;
import com.beans.CouponType;
import com.utilityclasses.DateGenerator;

/**
 * This is a test class that demonstrate the coupon system abilities,
 * both valid and invalid actions are tested here.
 * note that all Invalid should be uncomment so they will be active.
 * @author Administrator
 */
public class MainTest {

	public static void main(String[] args) {

		try {
			DatabaseUtility.resetAllDBTables();// ONLY FOR THE TEST AND NOT APART OF THE COUPON SYSTEM,
											   //in the "real" coupon system the DB will be created once!!!
 		} catch (DatabaseException e) {
			System.out.println(e.getMessage() + e.getCause());
			System.out.println("");
		}
		CouponSystem cs = null;
		try {
			cs = CouponSystem.getInstance();
			AdminFacade adminF = (AdminFacade) cs.login("admin", "1234", ClientType.ADMIN);
			adminF.createCompany(new Company("NOKIA", "NOKIA1234", "NOKIA@Gmail.com"));
			adminF.createCompany(new Company("SONY", "SONY1111", "SONY@Gmail.com"));
			adminF.createCompany(new Company("SAMSUNG", "SAMSUNG11", "SAMSUNG@Gmail.com"));
			adminF.createCompany(new Company("TECH", "TECH1111", "TECH@Gmail.com"));
			adminF.createCompany(new Company("ASUS", "ASUS2AS11", "ASUS@Gmail.com"));
			// Invalid - existing company
//			adminF.createCompany(new Company("ASUS", "ASUS2AS11", "ASUS@Gmail.com"));
			// Invalid - illegal Password 
//			adminF.createCompany(new Company("NEO", "234DD", "ASUS@Gmail.com"));			
			// Invalid - illegal Email
//			adminF.createCompany(new Company("CORE", "ASUS2AS11", "ASUSGmail.com"));
			
			System.out.println(adminF.getAllCompanies());

			adminF.removeCompany(new Company("TECH", "TECH1111", "TECH@Gmail.com"));
			System.out.println(adminF.getAllCompanies());
			// Invalid - Non existing company
//			adminF.removeCompany(new Company("YOKO", "YOKO1234", "YOKO@Gmail.com"));
			
			System.out.println("before:" + adminF.getCompany(1));
			adminF.updateCompany(new Company("NOKIA", "NOKIA1234", "NOKIA@nokia.com"));
			System.out.println("after:" + adminF.getCompany(1));
			// Invalid - Non existing company
//			adminF.updateCompany(new Company("YOKO", "YOKO1234", "YOKO@Gmail.com"));
			
			adminF.createCustomer(new Customer("Dani", "Dan123456"));
			adminF.createCustomer(new Customer("Mark", "Mark123456"));
			adminF.createCustomer(new Customer("Ran", "Ran23456"));
			adminF.createCustomer(new Customer("Jack", "Jack123456"));
			adminF.createCustomer(new Customer("Jhonny", "Jhon123456"));
			System.out.println(adminF.getAllCustomers());
			// Invalid - existing customer
//			adminF.createCustomer(new Customer("Mark", "Mark123456"));
			// Invalid - invalid password
//			adminF.createCustomer(new Customer("Moki", "Moki126"));	
			
			adminF.removeCustomer(new Customer("Ran", "Ran23456"));
			System.out.println(adminF.getAllCustomers());
			//Invalid - non-existing customer
//			adminF.removeCustomer(new Customer("Dudi", "Dudi23456"));
			
			adminF.updateCustomer(new Customer("Jack", "JackDanielsNo7"));
			System.out.println(adminF.getCustomer(4));
			//Invalid - non-existing customer
//			adminF.updateCustomer(new Customer("Dudi", "Dudi23456"));
			
			
			CompanyFacade compF = (CompanyFacade) cs.login("ASUS", "ASUS2AS11", ClientType.COMPANY);
			compF.createCoupon(
					new Coupon("peaches", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2019, 11, 30), 4,
							CouponType.FOOD, "This is a Message", 3, "This is an Image"));
			//Invalid - existing coupon
//			compF.createCoupon(
//					new Coupon("peaches", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2019, 11, 30), 4,
//							CouponType.FOOD, "This is a Message", 3, "This is an Image"));

			System.out.println(compF.getCoupon(1));
			//invalid - non existing coupon 
//			System.out.println(compF.getCoupon(10000));
			
			compF.createCoupon(
					new Coupon("apples", DateGenerator.genDate(2019, 11, 4), DateGenerator.genDate(2019, 12, 28), 10,
							CouponType.FOOD, "This is a Message", 2, "This is an Image"));
			compF.createCoupon(
					new Coupon("oranges", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2020, 11, 30), 12,
							CouponType.FOOD, "This is a Message", 15, "This is an Image"));
			compF.createCoupon(
					new Coupon("cheese", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2020, 11, 30), 12,
							CouponType.FOOD, "This is a Message", 15, "This is an Image"));

			System.out.println(compF.getAllCoupons());

			compF.removeCoupon(new Coupon("cheese", DateGenerator.genDate(2018, 11, 30),
					DateGenerator.genDate(2020, 11, 30), 12, CouponType.FOOD, " ", 15, " "));

			//invalid - non existing coupon 
//			compF.removeCoupon(new Coupon("Cookies", DateGenerator.genDate(2018, 11, 30),
//					DateGenerator.genDate(2020, 11, 30), 12, CouponType.FOOD, " ", 15, " "));

			compF.updateCoupon(
					new Coupon("peaches", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2019, 11, 30), 4,
							CouponType.FOOD, "This is a Message", 5555, "This is an Image"));
			//invalid - non existing coupon 
//			compF.updateCoupon(new Coupon("Cookies", DateGenerator.genDate(2018, 11, 30),
//					DateGenerator.genDate(2020, 11, 30), 12, CouponType.FOOD, " ", 15, " "));

			System.out.println(compF.getAllCoupons());
			compF = (CompanyFacade) cs.login("SONY", "SONY1111", ClientType.COMPANY);
			compF.createCoupon(
					new Coupon("TV", DateGenerator.genDate(2018, 11, 30), DateGenerator.genDate(2018, 11, 30), 4,
							CouponType.ELECTRICITY, "This is a Message", 3, "This is an Image"));
			compF.createCoupon(
					new Coupon("Cellphones", DateGenerator.genDate(2018, 11, 4), DateGenerator.genDate(2018, 12, 29),
							10, CouponType.ELECTRICITY, "This is a Message", 2, "This is an Image"));
			compF.createCoupon(
					new Coupon("Mp3Player", DateGenerator.genDate(2018, 6, 1), DateGenerator.genDate(2019, 6, 29), 10,
							CouponType.TRAVELLING, "This is a Message", 2, "This is an Image"));
			compF.createCoupon(
					new Coupon("Laptops", DateGenerator.genDate(2020, 11, 30), DateGenerator.genDate(2020, 11, 30), 40,
							CouponType.TRAVELLING, "This is a Message", 10, "This is an Image"));
			compF.createCoupon(
					new Coupon("DATE", DateGenerator.genDate(2018, 6, 29), DateGenerator.genDate(2018, 6, 29), 12,
							CouponType.FOOD, "This is a Message", 15, "This is an Image"));
			System.out.println(compF.getAllCoupons());
			compF.removeCoupon(
					new Coupon("Cellphones", DateGenerator.genDate(2018, 11, 4), DateGenerator.genDate(2018, 12, 28),
							10, CouponType.ELECTRICITY, "This is a Message", 2, "This is an Image"));
			System.out.println(compF.getAllCoupons());

			System.out.println("by type: " + compF.getAllCouponsByType(CouponType.ELECTRICITY));
			System.out
					.println("by max end date: " + compF.getAllCouponsMaxEndDate(DateGenerator.genDate(2019, 11, 29)));
			System.out.println("by max price: " + compF.getAllCouponsMaxPrice(5));

			CustomerFacade custF = (CustomerFacade) cs.login("Mark", "Mark123456", ClientType.CUSTOMER);

			custF.purchaseCoupon(new Coupon("Laptops"));
			custF.purchaseCoupon(new Coupon("apples"));
			custF.purchaseCoupon(new Coupon("oranges"));
			
			//invalid - non existing coupon
//			custF.purchaseCoupon(new Coupon("KIKI"));

			
			
			System.out.println("all:" + custF.getAllPurchasedCoupons());
			System.out.println("by price:" + custF.getAllPurchasedCouponsByPrice(3));
			System.out.println("by type:" + custF.getAllPurchasedCouponsByType(CouponType.FOOD));

		} catch (CouponSystemException | InterruptedException e) {
			e.printStackTrace();
		} finally {

			try {
				cs.shutdown();
			} catch (CouponSystemException e) {
				e.printStackTrace();//Eldar, per your instruction i've used printstacktrace() here
									// instead of using try-catch  for each.
				
			}
		}
	}
}
