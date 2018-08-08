package com.dbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.beans.Company;
import com.beans.Coupon;
import com.beans.CouponType;
import com.connectionpool.ConnectionPool;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;
import com.dao.CouponDAO;

/**
 * CouponDBDAO class implements the CompanyDAO interface. Its main purpose is to
 * supply the mechanism that allows data transit from and to the database.
 * 
 * Methods within this class based on following pattern: (1) Acquiring
 * connection from connection pool. (2) performing SQL queries and/or updates
 * using prepared statements. (3) Returning connection back to connection pool.
 * 
 * NOTE : (a) every method that requires a connection must request a connection
 * from the connection pool. once an action gets terminated or completed the
 * connection must be returned to the connection pool. In addition, (b) ID
 * column was defined as IDENTITY column (in the database) therefore, ID will be
 * assigned by the auto increment upon creation.
 * 
 * @author SegevSinay
 */
public class CouponDBDAO implements CouponDAO {

	private ConnectionPool connPool;

	/**
	 * Default Constructor
	 */
	public CouponDBDAO() throws ConnectionPoolException {
		this.connPool = ConnectionPool.getInstance();
	}

	/**
	 * Creates a coupon within the coupons table. (Exports the detailed data of the
	 * record we want to create from Java to the database for the requested coupon)
	 */
	@Override
	public void createCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		String sql = "INSERT INTO coupons (TITLE, START_DATE, END_DATE, AMOUNT, TYPE, MESSAGE, PRICE,IMAGE) VALUES (?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, new java.sql.Date(coupon.getStartDate().getTime()));
			pstmt.setDate(3, new java.sql.Date(coupon.getEndDate().getTime()));
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().toString());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setDouble(7, coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create coupon", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Inserts a new company and coupon record to Company_Coupon table. (since each
	 * coupon record thats get created within the coupons table needs to have a
	 * parallel record entry within the join_company_coupon table)
	 */
	@Override
	public void updateCompanyCoupon(Coupon coupon, Company company) throws CouponSystemException {
		Connection conn = connPool.getConnection();
		CompanyDBDAO compDbDao = new CompanyDBDAO();
		long coupId = getCouponId(coupon.getTitle());
		long compId = compDbDao.getCompanyId(company.getCompName());
		String sql = "INSERT INTO company_coupon (COMP_ID, COUPON_ID) VALUES (?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, compId);
			pstmt.setLong(2, coupId);
			pstmt.executeUpdate();
			pstmt.close();
			System.out.println("Coupon '" + coupon.getTitle()
					+ "' record has been successfully created within company_coupon table");
		} catch (SQLException e) {
			throw new DatabaseException("unable to create '" + coupon.getTitle() + "' coupon record!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific coupon record from coupons table.
	 */
	@Override
	public void removeCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long coupId = getCouponId(coupon.getTitle());
		String sql = "DELETE FROM coupons WHERE ID=? ";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, coupId);
			pstmt.executeUpdate();
			System.out.println("Coupon '" + coupon.getTitle() + "' (id #" + coupId
					+ ") has been successfully deleted from coupons table");
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to remove '" + coupon.getTitle() + "' coupon record!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific coupon record from customer_coupons joined table.
	 */
	@Override
	public void removeCouponFromCustomerCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long coupId = getCouponId(coupon.getTitle());
		String sql = "DELETE FROM customer_coupon WHERE COUPON_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, coupId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove coupon '" + coupon.getTitle() + "' from customer_coupon",
					e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific coupon record from company_coupons joined table.
	 */
	@Override
	public void removeCouponFromCompanyCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long coupId = getCouponId(coupon.getTitle());
		String sql = "DELETE FROM company_coupon WHERE COUPON_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, coupId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to remove '" + coupon.getTitle() + "' coupon from customer_coupon",
					e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Updates a specific coupon record within the coupons table. Exports the detail
	 * for the record we want to update within the database for the requested coupon
	 */
	@Override
	public void updateCoupon(Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long coupId = 0;
		String sql = "SELECT * FROM Coupons WHERE TITLE=?"; // I'm able to used it only since title is a unique value!
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupId = rs.getLong("ID");
			}
			sql = "UPDATE Coupons SET TITLE=?,START_DATE =?, END_DATE=?, AMOUNT=?, TYPE=?, MESSAGE=?, PRICE=? ,IMAGE=? WHERE ID=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, new java.sql.Date(coupon.getStartDate().getTime()));
			pstmt.setDate(3, new java.sql.Date(coupon.getEndDate().getTime()));
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().toString());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setDouble(7, coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.setDouble(9, coupId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException(
					"unable to update" + coupon.getTitle() + " coupon record! \ncause:" + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific coupon record from the coupons table. (Imports the
	 * detail of the requested coupon record from DB to Java then it creates the
	 * coupon Object using these details)
	 */
	@Override
	public Coupon getCoupon(long id) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		String sql = "SELECT * FROM coupons WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
			}
			pstmt.close();
			return coupon;
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive coupon record! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific coupon using Coupon Name. Although the most reliable
	 * unique key column is the Coupon ID column, since the coupon name is also a
	 * unique value, it enable us to use Coupon name as an identifier. personally, I
	 * find that the usage of coupon title easier to implement than extracting the
	 * ID using the "RETURN_GENERATED_KEYS" option, therefore i've chosen to add and
	 * implement the following Method.
	 */
	@Override
	public Coupon getCouponByTitle(String title) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		String sql = "SELECT * FROM coupons WHERE TITLE=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
			}
			pstmt.close();
			return coupon;
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive coupon record! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific coupon using Coupon Name. Although the most reliable
	 * unique key column is the Coupon ID column, since the coupon name is also a
	 * unique value, it enable us to use Coupon name as an identifier. BUT! since
	 * company_coupon is based on the COMP_ID, the ID identifier is required for the
	 * retrieval of the Company's coupon ID and vice versa. personally, I find that
	 * the usage of coupon title easier to implement than extracting the ID using
	 * the "RETURN_GENERATED_KEYS" option, therefore i've chosen to add and
	 * implement the following Method.
	 */
	@Override
	public long getCouponId(String title) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long coupId = 0;
		String sql = "SELECT ID FROM coupons WHERE TITLE=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupId = rs.getLong("ID");
			}
			pstmt.close();
			return coupId;
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive coupon '" + title + " id ", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves all coupon record for coupons table. (Imports the detail of the
	 * full coupons Table from DB to Java and then using these details it creates
	 * the coupons List)
	 */
	@Override
	public Collection<Coupon> getAllCoupons() throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		List<Coupon> allCoupons = new ArrayList<>();
		String sql = "SELECT * FROM coupons";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setMessage(rs.getString("MESSAGE"));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
				allCoupons.add(coupon);
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive data! \ncause:", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return allCoupons;
	}

	/**
	 * Retrieves all coupon record for coupons table by coupon Type. (Imports the
	 * detail of the full coupons from DB to Java and then creates the coupon list
	 * using these details)
	 */
	@Override
	public Collection<Coupon> getCouponByType(CouponType coupontype) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		List<Coupon> couponsByType = new ArrayList<>();
		String sql = "SELECT * FROM coupons WHERE TYPE = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
				couponsByType.add(coupon);
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive data! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return couponsByType;
	}

	/**
	 * Checks the coupons table (in the database) and collect to a coupon list all
	 * the expired coupons based on their end date.
	 */
	@Override
	public List<Coupon> collectExpiredCoupons() throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		List<Coupon> expiredCoupons = new ArrayList<>();

		String sql = "SELECT * FROM coupons WHERE END_DATE<CURRENT_DATE";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong("ID"));
				coupon.setTitle(rs.getString("TITLE"));
				coupon.setStartDate(rs.getDate("START_DATE"));
				coupon.setEndDate(rs.getDate("END_DATE"));
				coupon.setAmount(rs.getInt("AMOUNT"));
				coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
				coupon.setPrice(rs.getDouble("PRICE"));
				coupon.setImage(rs.getString("IMAGE"));
				expiredCoupons.add(coupon);
			}
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive data! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return expiredCoupons;
	}
}
