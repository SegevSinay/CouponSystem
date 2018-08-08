package com.connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import com.exceptions.ConnectionPoolException;

/**
 * The ConnectionPool Class is a singleton class which manage the connections
 * allowed simultaneously. it has 2 synchronized methods: getConnection() and
 * returnConnection() that manages the connection requests from the threads
 * using wait() and notiftyAll() Object class methods. Once the connections have
 * reached the Max simultaneously connection allowed, any additional thread that
 * will request a connection via getConnection method will have to "wait()"
 * until the returnConnection() method will "notifyAll()" that there are
 * available connections in the ConnectionPool.
 */
public class ConnectionPool {

	// Maximum connections that can be active simultaneously.
	private final static int MAX_CONN = 10;

	// the ConnectionPool one instance
	private static ConnectionPool instance;

	// Set of connections, its size is limited by Maximum connections allowed
	private Set<Connection> connections = new HashSet<>();

	
	/**
	 * Private Constructor (SingelTone design pattern). only one Instance can be
	 * created, and only within the ConnectionPool Class. other classes can interact
	 * with this instance via getInstance() Method.
	 */
	private ConnectionPool() throws ConnectionPoolException {
		try {
			DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
			System.out.println("Driver has been successfuly loaded");
		} catch (SQLException e) {
			throw new ConnectionPoolException("Unable to load Driver!", e);
		}
		for (int i = 1; i <= MAX_CONN; i++) {
			try {
				Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/coupon_sys_db");
				connections.add(conn);
			} catch (SQLException e) {
				throw new ConnectionPoolException("Unable to create connection pool!", e);
			}
		}
	}

	
	/**
	 * allows to get the single instance variable that was created in the singleton
	 * class.
	 */
	public synchronized static ConnectionPool getInstance() throws ConnectionPoolException {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	private int i = MAX_CONN;
	private int c = 1;

	/**
	 * Grant a connection if available and forces threads to wait if the connections
	 * have reach the Maximum connections allowed simultaneously.
	 */
	public synchronized Connection getConnection() throws ConnectionPoolException {
		System.out.println("INFO: " + i + " avaliable connections");
		while (connections.isEmpty()) {
			try {
				System.out.println("ALERT: connection pool is empty...");
				wait(); // wait while there are no available connections...
			} catch (InterruptedException e) {
				throw new ConnectionPoolException("wait has been interrupted! \ncause:", e);
			}
		}
		Iterator<Connection> it = connections.iterator();
		Connection conn = it.next();// moves the iterator to the current position (since it starts at -1 position).
		it.remove();// removes the connection that has been acquired by a thread.
		i--;
		System.out.println("INFO: " + c++ + " connection taken");
		return conn;
	}

	/**
	 * returns the connection once it has been released and notifying all that there is an
	 * available connection within the connection pool
	 */
	public synchronized void returnConnection(Connection conn) throws ConnectionPoolException {
		connections.add(conn);
		System.out.println("INFO: 1 connection returned");
		i++;
		c--;
		System.out.println("INFO: " + i + " avaliable connections");
		notifyAll();
	}

	/**
	 * closeAllConnection() closes all connections and is used during the system
	 * Shutdown
	 */
	public synchronized void closeAllConnection() throws ConnectionPoolException {
		int conn_counter = 0; // will increment each time a connection will be closed.
		while (conn_counter < MAX_CONN) {// while we haven't closed all connections
			while (connections.isEmpty()) {// while we have an ongoing connections
				try {
					wait(); // wait for the ongoing connections to return to connection pool
				} catch (InterruptedException e) {
					throw new ConnectionPoolException("wait has been interrupted!", e);
				}
			}
			while (!connections.isEmpty()) {// while we have a connection within the connection set that we can
											// close
				try {
					Iterator<Connection> it = connections.iterator();
					it.next().close(); // close the open connection
					it.remove();
					System.out.println("1 connnection been closed");
				} catch (SQLException e) {
					throw new ConnectionPoolException("Unable to close the current connection!", e);
				}
				conn_counter++;// Increment the closed connection counter
			}
		}
	}
}
