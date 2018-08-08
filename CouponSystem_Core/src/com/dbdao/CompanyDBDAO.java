package com.dbdao;

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

import java.sql.Connection;

import com.dao.CompanyDAO;
import com.exceptions.ConnectionPoolException;
import com.exceptions.CouponSystemException;
import com.exceptions.DatabaseException;

/**
 * CompanyDBDAO class implements the CompanyDAO interface. Its main purpose is
 * to supply the mechanism that allows data transit from and to the database.
 * 
 * Methods within this class are based on following pattern: (1) Acquiring
 * connection from connection pool. (2) performing SQL queries and/or updates
 * using prepared statements. (3) Returning connection back to connection pool
 * 
 * NOTE : (a) every method that requires a connection must request a connection
 * from the connection pool. once an action gets terminated or completed the
 * connection must be returned to the connection pool. In addition, (b) ID
 * columns were defined as IDENTITY column (in the database) therefore, ID will
 * be assigned by the auto increment upon creation.
 * 
 * @author SegevSinay
 */
public class CompanyDBDAO implements CompanyDAO {

	private ConnectionPool connPool;

	/**
	 * Default Constructor
	 */
	public CompanyDBDAO() throws CouponSystemException {
		this.connPool = ConnectionPool.getInstance();
	}

	/**
	 * Exports the detailed data of the record we want to create from Java to the
	 * database for the requested company
	 */
	@Override
	public void createCompany(Company company) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		String sql = "INSERT INTO Companies (COMP_NAME,PASSWORD,EMAIL) VALUES (?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, company.getCompName());
			pstmt.setString(2, company.getPassword());
			pstmt.setString(3, company.getEmail());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to create company" + company.getCompName(), e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Removes a specific company from the database by removing it from the
	 * companies table.
	 */
	@Override
	public void removeCompany(Company company) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long compId = getCompanyId(company.getCompName());
		String sql = "DELETE FROM companies WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, compId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to remove Company '" + company.getCompName() + "' records!", e);
		} finally {
			connPool.returnConnection(conn);// must return connection once terminated or done
		}
	}

	/**
	 * Removes a specific company coupons from the database. i've chosen to add this
	 * method since it allows the removal of all company's coupon record from the
	 * company_coupon table "in a single shot". Although a company removal requires
	 * a removal of all its coupons from company_coupon table ,in order to maintain
	 * the coupon system fully adjustable and to keep database method "clean" from
	 * any business logic (which is introduced and implemented in the facade),I've
	 * chosen to use a separate method instead of a single method although it is
	 * less performance efficient.
	 */
	@Override
	public void removeCompanyFromCompanyCoupon(Company company) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long compId = getCompanyId(company.getCompName());
		String sql = "DELETE FROM company_coupon WHERE COMP_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, compId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to remove Company '" + company.getCompName() + "' records!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * updates a specific company data.
	 */
	@Override
	public void updateCompany(Company company) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long compId = getCompanyId(company.getCompName());
		String sql = "UPDATE Companies SET PASSWORD=?, EMAIL=? WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, company.getPassword());
			pstmt.setString(2, company.getEmail());
			pstmt.setLong(3, compId);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new DatabaseException("unable to update " + company.getCompName() + " records!", e);
		} finally {
			connPool.returnConnection(conn);// must return connection once terminated or done
		}
	}

	/**
	 * Retrieves a specific company record from companies table. (Imports the
	 * details of each company record from DB to Java and then creates each company
	 * Object using these details)
	 */
	@Override
	public Company getCompany(long id) throws DatabaseException, ConnectionPoolException {
		Company company = null;
		Connection conn = connPool.getConnection();
		String sql = "SELECT * FROM companies WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				company = new Company();
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
			}
			pstmt.close();
		} catch (SQLException e) {
			System.out.println(
					"unable to retrive '" + company.getCompName() + "'company records! \ncause: " + e.getMessage());
		} finally {
			connPool.returnConnection(conn);
		}
		return company;
	}

	/**
	 * Retrieves all companies record from companies table. (Imports the detail of
	 * the full companies Table from DB to Java and then using these details it
	 * creates the companies List)
	 */
	@Override
	public Collection<Company> getAllCompanies() throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Company company = null;
		List<Company> allCompanies = new ArrayList<>();
		String sql = "SELECT * FROM companies";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				company = new Company();
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
				allCompanies.add(company);
			}
			pstmt.close();
			return allCompanies;
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive Data!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific company coupons by creating a joined table of both
	 * coupons and company_coupon tables, once table are joined it extracts all
	 * company's coupons records using the company's ID identifier. (Imports the
	 * detail of the full company's coupons from DB to Java and then creates the
	 * coupon list using these details)
	 */
	@Override
	public Collection<Coupon> getCoupons(Company company) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Coupon coupon = null;
		List<Coupon> allCoupons = new ArrayList<>();
		long compId = getCompanyId(company.getCompName());
		String sql = "SELECT * FROM coupons INNER JOIN company_coupon ON coupons.id=company_coupon.coupon_id WHERE COMP_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, compId);
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
			return allCoupons;
		} catch (SQLException e) {
			throw new DatabaseException("unable to retrive Data!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Checks for the combine key of company's name and password, if such a
	 * combination exist within the companies records it will return true else it
	 * will return false.
	 */
	@Override
	public boolean login(String compName, String password) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		String userCredetials = compName + password;
		String dbCredetials = null;
		Long id = getCompanyId(compName);
		String sql = "SELECT * FROM companies WHERE ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				dbCredetials = rs.getString("COMP_NAME") + rs.getString("PASSWORD");
			}
			pstmt.close();
			if (!(userCredetials.equals(dbCredetials))) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			throw new DatabaseException("Unable to login! ", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific company ID using Company Name. Although the most
	 * reliable unique key column is the Company's ID column, since Company's name
	 * is also a unique value, it enable us to use Company's name as an identifier.
	 * BUT! since company_coupon is based on the COMP_ID, the ID identifier is
	 * required for the retrieval of the Company's coupon ID and vice versa.
	 * personally, I find that the usage of company's name is easier to implement
	 * than extracting the ID using the "RETURN_GENERATED_KEYS" option, therefore
	 * i've chosen to add and implement the following Method.
	 */
	@Override
	public long getCompanyId(String compName) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		long companyId = 0;
		String sql = "SELECT ID FROM companies WHERE COMP_NAME=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, compName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				companyId = rs.getLong("ID");
			}
			pstmt.close();
			return companyId;

		} catch (SQLException e) {
			throw new DatabaseException("Unable to retrive company Id", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Retrieves a specific company using Company Name. Although the most reliable
	 * unique key column is the Company's ID column, since Company's name is also a
	 * unique value, it enable us to use Company's name as an identifier.
	 * personally, I found the usage of company's name easier to implement than
	 * extracting the ID using the "RETURN_GENERATED_KEYS" option, so i've chosen to
	 * add and implement the following Method.
	 */
	@Override
	public Company getCompanyByName(String compName) throws DatabaseException, ConnectionPoolException {
		Connection conn = connPool.getConnection();
		Company company = null;
		String sql = "SELECT * FROM companies WHERE COMP_NAME=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, compName);
			ResultSet rs = pstmt.executeQuery();
			// while we have results from this query, we use Company's advance CTOR to
			// create company object using company record
			while (rs.next()) {
				company = new Company();
				company.setId(rs.getLong("ID"));
				company.setCompName(rs.getString("COMP_NAME"));
				company.setPassword(rs.getString("PASSWORD"));
				company.setEmail(rs.getString("EMAIL"));
			}
			pstmt.close();
			return company;
		} catch (SQLException e) {
			throw new DatabaseException("Unable to retrive company data!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}

	/**
	 * Query a specific Email address. returns the actual Email address if email
	 * exist or null if doesn't. this allow to perform an Email availability check
	 * upon company creation and/or update in the company Facade.
	 */
	@Override
	public String emailAvaliabilityCheck(String email) throws CouponSystemException {
		Connection conn = connPool.getConnection();
		String reqEmail = null;
		String sql = "SELECT EMAIL FROM companies WHERE EMAIL=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				reqEmail = rs.getString("EMAIL");
			}
			pstmt.close();
			return reqEmail;
		} catch (SQLException e) {
			throw new DatabaseException("Unable to retrive email data!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}
	
	/**
	 * Retrieves requesting company Coupon Id according to the requesting company
	 * Id; returns the requested coupon Id if exist or null if it doesn't.
	 */
	@Override
	public Long getCouponIdByCompanyId(long compId, long coupId) throws CouponSystemException {
		Connection conn = connPool.getConnection();
		Long reqCoupId = null; 
		String sql = "SELECT COUPON_ID FROM company_coupon WHERE COMP_ID=? AND COUPON_ID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, compId);
			pstmt.setLong(2, coupId);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				reqCoupId = rs.getLong("COUPON_ID");
			}
			pstmt.close();
			return reqCoupId;
		}catch (SQLException e) {
			throw new DatabaseException("Unable to retrive coupon ID data!", e);
		} finally {
			connPool.returnConnection(conn);
		}
	}
}
