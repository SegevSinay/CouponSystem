package com.beans;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sets the "Blueprint" for a Customer type objects.
 * 
 * @author SegevSinay
 */

@XmlRootElement
public class Customer {

	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons;

	/**
	 * Default constructor.
	 */
	public Customer() {
	}

	/**
	 * Partial Constructor. used mainly for customer creation.
	 */
	public Customer(String custName, String password) {
		this.custName = custName;
		this.password = password;
	}

	/**
	 * Full Constructor. used mainly upon customer extraction from DB.
	 */
	public Customer(long id, String custName, String password) {
		this.id = id;
		this.custName = custName;
		this.password = password;
	}
	
	/**
	 * allows to get the customer's Id.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * allows to set the customer's Id.
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * allows to get the customer's Name.
	 */
	public String getCustName() {
		return custName;
	}
	
	/**
	 * allows to set the customer's Name.
	 */
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	/**
	 * allows to get the customer's Password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * allows to set the customer's Password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * allows to get the customer's Coupon list.
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}
	
	/**
	 * allows to set the customer's Coupon collection.
	 */
	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}
	
	/**
	 * allows to add a single coupon to the coupons collection
	 */
	public void addCustCoupons(Coupon coupon) {
		this.coupons.add(coupon);
	}
	
	/**
	 * Returns a custom textual representation of the object. in this case it will
	 * display customer details.
	 */
	@Override
	public String toString() {
		return "Customer [id=" + id + ", custName=" + custName + ", password=" + password +  "]";
	}

}
