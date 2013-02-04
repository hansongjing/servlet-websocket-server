package com.dzharvis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class MyDBConnector {
	private static final String HOST = "localhost";
	private static final String PORT = "3307";
	private static final String PASSWORD = "24861793s";
	private static final String USER = "root";
	private static final String DATA_BASE = "test";
	Connection conn = null;
	private Statement stmt;

	public MyDBConnector() {

	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			Utils.log("Driver not found");
			e1.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":"
					+ PORT + "/" + DATA_BASE, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (conn == null) {
			Utils.log("NOT Connected to test NULL");
		} else {
			Utils.log("probably connected");
		}
	}

	public void writeToDB(String str) {
		try {
			stmt.executeUpdate(
					"INSERT INTO messages (message) " +
							"VALUES ('" + str + "')");
		} catch (SQLException e) {
			Utils.log("DB update is impossible");
			e.printStackTrace();
		}
	}

	public void fillMessages(Collection<String> data) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(
					"CREATE TABLE IF NOT EXISTS messages ("
						+ "ID INT NOT NULL," 
						+ "message TEXT"
					+ ");");
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM messages ORDER BY ID");
			while (rs.next()) {
				String str = rs.getString("message");
				data.add(str);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
