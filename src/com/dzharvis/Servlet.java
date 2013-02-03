package com.dzharvis;

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
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

/**
 * Servlet implementation class Servlet
 */
@WebServlet(urlPatterns = "/Servlet", asyncSupported = true)
public class Servlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<WsOutbound> sis = new ArrayList<WsOutbound>();
	private LinkedList<String> messages = new LinkedList<String>();
	private final int CHAR_BUFFER_SIZE = 1000;
	Connection conn = null;
	private Statement stmt;

	public Servlet() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			System.out.println("Driver not founsd");
			e1.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3307/test", "root", "24861793s");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (conn == null) {
			System.out.println(" NOT Connected to test NULL");
		} else {
			System.out.println("probably connected");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private void cutMessageBuffer() {
		while (messages.size() > 100) {
			messages.removeFirst();
		}
	}

	@Override
	protected boolean verifyOrigin(String origin) {
		System.out.println("Origin: {}" + origin);
		return true;
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		System.out.println("created");
		StreamInbound si = new StreamInbound() {

			@Override
			protected void onTextData(Reader reader) throws IOException {

				CharBuffer cb = CharBuffer.allocate(CHAR_BUFFER_SIZE);
				reader.read(cb);
				String str = getStringFromBuffer(cb);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cutMessageBuffer();
			}

			@Override
			protected void onBinaryData(InputStream arg0) throws IOException {
				System.out.println("bin  data");
			}

			@Override
			protected void onClose(int status) {
				System.out.println("closed");
			}

			@Override
			protected void onOpen(WsOutbound outbound) {
				super.onOpen(outbound);
				sis.add(outbound);
				System.out.println(messages.size());
				for (String str : messages) {
					try {
						str = str.replaceAll("\\s+", " ");
						outbound.writeTextMessage(getBufferFromString(str));
						outbound.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};

		return si;
	}

	private String getStringFromBuffer(CharBuffer cb) {
		StringBuilder str = new StringBuilder();
		cb.position(0);
		while (cb.hasRemaining()) {
			char c = cb.get();
			if (c == 0)
				break;
			str = (c != 0) ? (str.append(c)) : str;
		}
		return str.toString();
	}

	private synchronized String findURLs(String str) {
		StringBuffer temp = new StringBuffer();
		Pattern url = Pattern.compile("(http|https)://[^ \\s\"]+");
		Pattern imgOrURL = Pattern.compile("jpg|bmp|gif|jpeg|png");
		Matcher m = url.matcher(str);

		while (m.find()) {
			String strG = m.group();
			System.out.println(strG);

			if (imgOrURL.matcher(strG.toLowerCase()).find()) {
				strG = "<img src=" + strG + " width=50%>";
			} else {
				strG = "<a href=" + strG + ">" + strG + "</a>";
			}

			m.appendReplacement(temp, strG);
		}
		m.appendTail(temp);
		System.out.println(temp);
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
		// TODO Auto-generated method stub
		super.destroy();
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
