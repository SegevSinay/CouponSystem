package com.dbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.beans.Coupon;
import com.beans.CouponType;
import com.beans.Customer;
import com.connectionpool.ConnectionPool;
import com.dao.CustomerDAO;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;

/**
 * CustomerDBDAO class implements the CustomerDAO interface.Its main purpose is
 * to supply the mechanism that allows data transit from and to the database.
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
public class CustomerDBDAO implements CustomerDAO {

	private ConnectionPool connPool;

	/**
	 * Default Constructor
	 */
	public CustomerDBDAO() throws ConnectionPoolException {
		this.connPool = ConnectionPool.getInstance();
	}

	/**
	 * Creates a specific customer in customers table. (Exports the detailed data of
	 * the record we want to create from Java to the database for the requested
	 * customer).
	 */
	@Override
	public void createCustomer(Customer customer) throws CouponSystemException {
		Connection conn = connPool.getConnection();
		String sql = "INSERT INTO customers (CUST_NAME, PASSWORD) VALUES(?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, customer.getCustName());
			pstmt.setString(2, customer.getPassword());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to create customer '" + customer.getCustName() + "'", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific customer from customers table.
	 */
	@Override
	public void removeCustomer(Customer customer) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long custId = getCustomerId(customer.getCustName());
		String sql = "DELETE FROM customers WHERE ID =?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, custId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			new DatabaseException("unable to remove customer '" + customer.getCustName() + "'", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific customer coupons from the database. i've chosen to add
	 * this method since it allows the removal of all customer's coupon record from
	 * the customer_coupon table "in a single shot". Although a customer removal
	 * requires a removal of all its coupons from customer_coupon table ,in order to
	 * maintain the coupon system fully adjustable and to keep database method
	 * "clean" from any business logic (which is introduced and implemented in the
	 * facade),I've chosen to use a separate method instead of a single method
	 * although it is less performance efficient.
	 */
	@Override
	public void removeCustomerFromCustomerCoupon(Customer customer) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long custId = getCustomerId(customer.getCustName());
		String sql = "DELETE FROM customer_coupon WHERE CUST_ID =?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, custId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			new DatabaseException("unable to remove customer '" + customer.getCustName() + "'", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Updates a specific customer record within the Customer table. (Exports the
	 * detail for the record we want to update within the database for the requested
	 * customer.)
	 */
	@Override
	public void updateCustomer(Customer customer) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long custId = getCustomerId(customer.getCustName());
		String sql = "UPDATE customers SET CUST_NAME=?, PASSWORD=? WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, customer.getCustName());
			pstmt.setString(2, customer.getPassword());
			pstmt.setLong(3, custId);// Since update can also alter Name, I've used ID as the "anchor"!
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to update customer details \ncause:" + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific customer data from customers table using customer ID as
	 * the identifier. (Imports the detail of the requested customer record from DB
	 * to Java then it creates the customer Object using these details)
	 */
	@Override
	public Customer getCustomer(long id) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Customer customer = null;
		String sql = "SELECT * FROM customers WHERE ID=?";
		try {
			customer = new Customer();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				customer.setId(id);
				customer.setCustName(rs.getString("CUST_NAME"));
				customer.setPassword(rs.getString("PASSWORD"));
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive date!", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return customer;
	}

	/**
	 * Retrieves all customers data from customers table. (Imports the detail of the
	 * full customers Table from DB to Java and then using these details it creates
	 * the all customers list)
	 */
	@Override
	public Collection<Customer> getAllCustomer() throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Customer customer = null;
		List<Customer> allcustomers = new ArrayList<>();
		String sql = "SELECT * FROM customers";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				customer = new Customer();
				customer.setId(rs.getLong("ID"));
				customer.setCustName(rs.getString("CUST_NAME"));
				customer.setPassword(rs.getString("PASSWORD"));
				allcustomers.add(customer);
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive data! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return allcustomers;
	}

	/**
	 * Retrieves a specific customer coupons by creating a joined table of both
	 * coupons and customer_coupon tables, once table are joined it extracts all
	 * customer's coupons records using the customer's ID identifier. (Imports the
	 * detail of the full customer coupons from DB to Java and then creates the
	 * coupon list using these details)
	 */
	@Override
	public Collection<Coupon> getCoupons(Customer customer) throws ConnectionPoolException, DatabaseException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		List<Coupon> customerCoupons = new ArrayList<>();
		long custId = getCustomerId(customer.getCustName());
		String sql = "SELECT * FROM coupons INNER JOIN customer_coupon ON coupons.id=customer_coupon.coupon_id WHERE CUST_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, custId);
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
				customerCoupons.add(coupon);
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive data! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return customerCoupons;
	}

	/**
	 * Checks for the combine key of customer name and password, if such a
	 * combination exist within the customers records it will return true else it
	 * will return false
	 */
	@Override
	public boolean login(String custName, String password) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		String userCredetials = custName + password;
		String dbCredetials = null;
		String sql = "SELECT * FROM customers WHERE CUST_NAME=? AND PASSWORD=? ";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, custName);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				dbCredetials = rs.getString("CUST_NAME") + rs.getString("PASSWORD");
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to login! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
		if (!(userCredetials.equals(dbCredetials))) {
			return false;
		}
		System.out.println("login for customer '" + custName + "' is confirmed");
		return true;
	}

	/**
	 * Creates a new record in Customer_Coupon table. (Exports the detail for the
	 * purchase record we want to create within the customer_coupon table for the
	 * requested customer and coupon using their Id.)
	 */
	@Override
	public void purchaseCoupon(Customer customer, Coupon coupon) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long custId = getCustomerId(customer.getCustName());
		String sql = "INSERT INTO customer_coupon (CUST_ID,COUPON_ID) VALUES (?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, custId);
			pstmt.setLong(2, coupon.getId());
			pstmt.executeUpdate();
			pstmt.close();
			System.out.println(
					customer.getCustName() + " purchase of coupon " + coupon.getTitle() + " was updated successfuly");
		} catch (SQLException e) {
			throw new DatabaseException(
					"unable to update " + customer.getCustName() + " purchase of coupon '" + coupon.getTitle() + "' !",
					e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific customer ID using customer Name. Although the most
	 * reliable unique key column is the customer's ID column, since customer name
	 * is also a unique value, it enable us to use it as an identifier. BUT! since
	 * customer_coupon is based on the CUST_ID, the ID identifier is required for
	 * the retrieval of the customer coupon ID and vice versa. personally, I find
	 * that the usage of customer name is easier to implement than extracting the ID
	 * using the "RETURN_GENERATED_KEYS" option, therefore i've chosen to add and
	 * implement the following Method.
	 */
	@Override
	public long getCustomerId(String custName) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long custId = 0;
		String sql = "SELECT ID FROM customers WHERE CUST_NAME=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, custName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				custId = rs.getLong("ID");
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to retrive customer Id", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return custId;
	}

	/**
	 * Retrieves a specific customer using customer Name. Although the most reliable
	 * unique key column is the customer's ID column, since customer name is also a
	 * unique value, it enable us to use it as an identifier. personally, I find
	 * that the usage of customer name easier to implement than extracting the ID
	 * using the "RETURN_GENERATED_KEYS" option, therefore i've chosen to add and
	 * implement the following Method.
	 */
	@Override
	public Customer getCustomerByName(String name) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Customer customer = null;
		String sql = "SELECT * FROM customers WHERE CUST_NAME=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				customer = new Customer();
				customer.setCustName(rs.getString("CUST_NAME"));
				customer.setPassword(rs.getString("PASSWORD"));
			}
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive date!", e);
		} finally {
			connPool.returnConnection(conn);
		}
		return customer;
	}

}
