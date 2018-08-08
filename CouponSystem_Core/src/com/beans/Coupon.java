package com.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * Sets the "Blueprint" for a Coupon type objects.
 * @author SegevSinay
 */

@XmlRootElement
public class Coupon {

	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;

	/**
	 * Default constructor.
	 */
	public Coupon() {
	}
	
	/**
	 *Partial constructor used mainly for customer coupon purchase purpose 
	 */
	public Coupon(String title) {
		this.title = title;
	}
	
	/**
	 *Partial constructor used mainly for coupon purchase amount update 
	 */
	public Coupon(String title, int amount) {
		this.title = title;
		this.amount = amount;
	}

	/**
	 * Partial constructor used mainly for coupon creation.
	 */
	public Coupon(String title, Date startDate, Date endDate, int amount, CouponType type, String message, double price,
			String image) {
		this.title = title;
		this.startDate=startDate;
		this.endDate=endDate;
		this.amount = amount;
		this.type = type;
		this.message=message;
		this.price=price;
		this.image=image;
	}

	/**
	 * Full constructor. used upon coupon extraction from DB.
	 */
	public Coupon(long id, String title, Date startDate, Date endDate, int amount, CouponType type, String message,
			double price, String image) {
		this.id = id;
		this.title = title;
		this.startDate=startDate;
		this.endDate=endDate;
		this.amount = amount;
		this.type = type;
		this.message=message;
		this.price=price;
		this.image=image;
	}

	/**
	 * allows to get the coupons's ID.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * allows to set the coupons's ID.
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * allows to get the coupons's Title. 
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * allows to set the coupons's Title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * allows to get the coupons's StartDate.
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * allows to set the coupons's StartDate.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * allows to get the coupons's EndDate.
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * allows to set the coupons's EndDate.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * allows to get the coupons's Amount.
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * allows to set the coupons's Amount.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 * allows to get the coupons's Type.
	 */
	public CouponType getType() {
		return type;
	}
	
	/**
	 * allows to set the coupons's Type.
	 */
	public void setType(CouponType type) {
		this.type = type;
	}
	
	/**
	 * allows to get the coupons's Message.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * allows to set the coupons's Message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * allows to get the coupons's Price.
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * allows to set the coupons's Price.
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * allows to get the coupons's Image.
	 */
	public String getImage() {
		return image;
	}
	
	/**
	 * allows to set the coupons's Image.
	 */
	public void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * Returns a custom textual representation of the object. in this case it will
	 * display coupon details.
	 */
	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", amount=" + amount + ", type=" + type + ", message=" + message + ", price=" + price + ", image="
				+ image + "]";
	}


	

	

}
