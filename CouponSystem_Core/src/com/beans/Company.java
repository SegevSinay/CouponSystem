package com.beans;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sets the "Blueprint" for a Company type objects.
 * @author SegevSinay
 */

@XmlRootElement
public class Company {

	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<Coupon> coupons;

	/**
	 * Default constructor.
	 */
	public Company() {
		super();
	}
	
	/**
	 * Partial Constructor. used mainly for company creation in DB
	 */
	public Company(long id, String compName, String password, String email) {
		super();
		this.id = id;
		this.compName = compName;
		this.password = password;
		this.email = email;
	}
	
	/**
	 * Full Constructor. used mainly upon company extraction from DB 
	 */
	public Company(String compName, String password, String email) {
		super();
		this.compName = compName;
		this.password = password;
		this.email = email;
	}
	/**
	 * allows to get the company's ID.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * allows to set company's ID.
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * allows to get the company's Name.
	 */
	public String getCompName() {
		return compName;
	}

	/**
	 * allows to set company's Name.
	 */
	public void setCompName(String compName) {
		this.compName = compName;
	}

	/**
	 * allows to get the company's Password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * allows to set company's Password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * allows to get the company's Email .
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * allows to set company's Email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * allows to get the company's coupons collection.
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * allows to add a single coupon to the coupons collection
	 */
	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;

	}
	
	/**
	 * Returns a custom textual representation of the object. in this case it will
	 * display company's details.
	 */
	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", password=" + password + ", email=" + email + "]";
	}
}
