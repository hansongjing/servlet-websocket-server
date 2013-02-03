package com.dzharvis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

/**
 * Servlet implementation class Servlet
 */
@WebServlet(urlPatterns = "/Servlet", asyncSupported = true)
public class Servlet extends WebSocketServlet {
	private static final int MESSAGES_QUERY_SIZE = 100;
	private static final long serialVersionUID = 1L;
	private ArrayList<WsOutbound> sis = new ArrayList<WsOutbound>();
	private LinkedList<String> messages = new LinkedList<String>();
	Connection conn = null;
	private Statement stmt;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			Log("Driver not found");
			e1.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3307/test", "root", "24861793s");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (conn == null) {
			Log(" NOT Connected to test NULL");
		} else {
			Log("probably connected");
		}
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("create table if not exists messages ("
					+ "id int not null," + "message text" + ");");

			ResultSet rs = stmt
					.executeQuery("select * from messages order by id");

			while (rs.next()) {
				String str = rs.getString("message");
				messages.add(str);
			}
			cutMessageBuffer();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Log(String str) {
		System.out.println(new Date().toString() + ": " + str);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.service(req, resp);
		Log("Ip connected: " + req.getRemoteAddr());
	}

	private void cutMessageBuffer() {
		while (messages.size() > MESSAGES_QUERY_SIZE) {
			messages.removeFirst();
		}
	}

	@Override
	protected boolean verifyOrigin(String origin) {
		Log("Origin: " + origin);
		return true;
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		StreamInbound si = new StreamInbound() {

			@Override
			protected void onTextData(Reader reader) throws IOException {
				BufferedReader br = new BufferedReader(reader);
				StringBuilder sb = new StringBuilder();
				String str = "";
				while ((str = br.readLine()) != null)
					sb.append(str);
				str = sb.toString();
				str = findURLs(str);
				putNewMessage(str);
				for (int i = 0; i < sis.size(); i++) {
					if (sis.get(i) != null)
						writeStringToBuffer(str, sis.get(i));
				}
			}

			private void putNewMessage(String str) {
				messages.add(str);
				try {
					stmt.executeUpdate("insert into messages (message) values ('"
							+ str + "')");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				cutMessageBuffer();
			}

			@Override
			protected void onBinaryData(InputStream arg0) throws IOException {
				Log("bin  data");
			}

			@Override
			protected void onClose(int status) {
				Log("closed");
				removeListener(getWsOutbound());
			}

			@Override
			protected void onOpen(WsOutbound outbound) {
				super.onOpen(outbound);
				sis.add(outbound);
				for (String str : messages) {
					try {
						str = str.replaceAll("\\s+", " ");
						outbound.writeTextMessage(getBufferFromString(str));
						outbound.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		};

		return si;
	}

	private synchronized String findURLs(String str) {
		StringBuffer temp = new StringBuffer();
		Pattern url = Pattern.compile("(http|https)://[^ \\s\"]+");
		Pattern imgOrURL = Pattern.compile("jpg|bmp|gif|jpeg|png");
		Matcher m = url.matcher(str);

		while (m.find()) {
			String strG = m.group();
			if (imgOrURL.matcher(strG.toLowerCase()).find()) {
				strG = "<img src=" + strG + " width=50%>";
			} else {
				strG = "<a href=" + strG + ">" + strG + "</a>";
			}
			m.appendReplacement(temp, strG);
		}
		m.appendTail(temp);
		Log(temp.toString());
		return temp.toString();
	}

	private CharBuffer getBufferFromString(String str) {
		CharBuffer cb = CharBuffer.allocate(str.length());
		cb.put(str);
		cb.position(0);
		return cb;
	}

	private void writeStringToBuffer(String str, WsOutbound outbound) {
		CharBuffer cb = getBufferFromString(str);
		cb.position(0);
		try {
			outbound.writeTextMessage(cb);
			outbound.flush();
		} catch (IOException e) {
			removeListener(outbound);
		}
	}

	private void removeListener(WsOutbound out) {
		sis.remove(out);
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
