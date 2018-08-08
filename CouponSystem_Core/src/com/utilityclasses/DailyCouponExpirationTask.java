package com.utilityclasses;

import java.util.Iterator;
import java.util.List;

import com.beans.Coupon;
import com.dao.CouponDAO;
import com.dbdao.CouponDBDAO;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.DailyTaskException;
import com.exceptions.DatabaseException;

/**
 * DailyCouponExperationTask extends Thread class (which in turn implements
 * runnable) the thread is implemented by the singleton Class "CouponSystem";
 * it's purpose is to set the coupon system daily thread operation.
 * 
 * @author Segev Sinay
 */
public class DailyCouponExpirationTask extends Thread {

	private CouponDAO coupDao;
	private Boolean quit;

	/**
	 * DailyCouponExperationTask Constructor. initiates the Dao and quit variable
	 * value.
	 */
	public DailyCouponExpirationTask() throws ConnectionPoolException {
		coupDao = new CouponDBDAO();
		quit = false;
	}

	/**
	 * defines the DailyCouponExperationTask activity and puts the Thread to sleep
	 * for 24 hours.
	 * 
	 * Sets the DailyCouponExperationTask thread mechanism. The
	 * DailyCouponExperationTask will check the coupons table (in the database) and
	 * collect to a coupon list all the expired coupons based on their end date.
	 * Once it reviews the full table records, it will start removing them one at
	 * the time stating which expired coupons were removed. In addition, it throws
	 * an DailyTaskException in case not all expired coupons were removed
	 * successfully or in case it was unable to complete it's task.
	 */
	@Override
	public void run() {
		while (quit == false) {
			try {
				Thread.sleep(60*60*24*1000);// should be 60*60*24*1000 =864000000 = 24 hrs in milliseconds.
									// BUT for test purpose ONLY i've used 10 in order to demonstrate it works
			} catch (InterruptedException e) {
				break;
			}
			try {
				try {
					List<Coupon> expiredCoupons = coupDao.collectExpiredCoupons();
					int expiredCounter = expiredCoupons.size();
					int removedCounter = 0;
					if (expiredCoupons.size() > 0) {
						System.out.println("COUPON_CLEANER: Total expired coupons found: " + expiredCoupons.size());
						Iterator<Coupon> it = expiredCoupons.iterator();
						Coupon coupon = null;
						try {
							while (it.hasNext()) {
								coupon = it.next();
								coupDao.removeCouponFromCompanyCoupon(coupon);
								coupDao.removeCouponFromCustomerCoupon(coupon);
								coupDao.removeCoupon(coupon);
								removedCounter++;
								System.out.println("COUPON_CLEANER: coupon '" + coupon.getTitle()
										+ "' has been removed successfuly");
							}
						} catch (DatabaseException | ConnectionPoolException e) {
							throw new DailyTaskException(
									"COUPON_CLEANER: unable to remove coupon'" + coupon.getTitle() + "'", e);
						}
						System.out.println("COUPON_CLEANER: Total of " + removedCounter
								+ " expired coupons were removed successfully");
						if ((expiredCounter - removedCounter) != 0) {
							throw new DailyTaskException("COUPON_CLEANER: Total of " + (expiredCounter - removedCounter)
									+ " expired coupons were not removed !");
						}
					}
					System.out.println("COUPON_CLEANER: No expired coupons to remove");
				} catch (DatabaseException e) {
					throw new DailyTaskException("Daily expired coupon removal process has failed!", e);
				}

			} catch (CouponSystemException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();// for Debug purposes only
			}
		}
		System.out.println("Daily Task is Stopped!");
	}

	// NO NEED FOR THE FOLLOWING ACCORDING TO ELDAR:
	public Boolean getQuit() {
		return quit;
	}

	public void stopTask(Boolean quit) {
		this.quit = quit;
	}

	public boolean isQuit() {
		return quit;
	}
}
