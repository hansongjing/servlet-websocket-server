package com.dzharvis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class MyDBConnector {
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
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3307/test", "root", "24861793s");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (conn == null) {
			Utils.log(" NOT Connected to test NULL");
		} else {
			Utils.log("probably connected");
		}
	}

	public void writeToDB(String str) {
		try {
			stmt.executeUpdate("insert into messages (message) values ('" + str
					+ "')");
		} catch (SQLException e) {
			Utils.log("DB update is impossible");
			e.printStackTrace();
		}
	}

	public void fillMessages(Collection<String> data) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("create table if not exists messages ("
					+ "id int not null," + "message text" + ");");
			ResultSet rs = stmt
					.executeQuery("select * from messages order by id");
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
