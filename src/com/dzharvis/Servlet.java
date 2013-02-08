package com.dzharvis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.ServletException;
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
	private static final int MESSAGES_QUERY_SIZE = 100;
	private static final long serialVersionUID = 1L;
	private ArrayList<WsOutbound> connectedUsers = new ArrayList<WsOutbound>();
	private LinkedList<String> messagesQuery = new LinkedList<String>();
	private MyDBConnector dbConnector = new MyDBConnector();

	@Override
	public void init() throws ServletException {
		super.init();
		dbConnector.connect();
		dbConnector.fillMessages(messagesQuery);
		cutMessageBuffer();
	}

	private void cutMessageBuffer() {
		while (messagesQuery.size() > MESSAGES_QUERY_SIZE) {
			messagesQuery.removeFirst();
		}
	}

	@Override
	protected boolean verifyOrigin(String origin) {
		Utils.log("Origin: " + origin);
		return true;
	}

	@Override
	protected StreamInbound createWebSocketInbound(String str,
			HttpServletRequest req) {
		Utils.log("Ip connected: " + req.getRemoteAddr());
		return new MyStreamInbound(req.getRemoteAddr());
	}

	private void removeListener(WsOutbound out) {
		connectedUsers.remove(out);
	}

	@Override
	public void destroy() {
		super.destroy();
		dbConnector.close();
	}

	class MyStreamInbound extends StreamInbound {

		private String addr;

		public MyStreamInbound(String addr) {
			this.addr = addr;
		}

		@Override
		protected void onTextData(Reader reader) throws IOException {
			BufferedReader br = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String str = "";
			while ((str = br.readLine()) != null)
				sb.append(str);
			str = Utils.findURLs(sb.toString());
			putNewMessageInQuery(str);
			dispatchMessage(str);
		}

		private void dispatchMessage(String str) throws IOException {
			for (int i = 0; i < connectedUsers.size(); i++) {
				if (connectedUsers.get(i) != null)
					writeToSocket(getWsOutbound(), str);
			}
		}

		private void putNewMessageInQuery(String str) {
			messagesQuery.add(str);
			dbConnector.writeToDB(str);
			cutMessageBuffer();
		}

		private void writeToSocket(WsOutbound outbound, String str)
				throws IOException {
			outbound.writeTextMessage(Utils.getBufferFromString(str));
			outbound.flush();
		}

		@Override
		protected void onBinaryData(InputStream arg0) throws IOException {
			Utils.log("binary  data");
		}

		@Override
		protected void onClose(int status) {
			Utils.log("IP Closed: " + addr);
			removeListener(getWsOutbound());
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			super.onOpen(outbound);
			connectedUsers.add(outbound);
			for (String str : messagesQuery) {
				try {
					str = str.replaceAll("\\s+", " ");
					writeToSocket(outbound, str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
