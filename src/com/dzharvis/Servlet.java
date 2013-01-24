package com.dzharvis;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.servlet.AsyncContext;
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
	private final int CHAR_BUFFER_SIZE = 200;

	// TODO remove if not debug
	public Servlet() {
		String str = getStringFromBuffer(getBufferFromString("str"));
		System.out.println("str" == str);
	}

	@Override
	protected boolean verifyOrigin(String origin) {
		System.out.println("Origin: {}" + origin);
		return true;
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		System.out.println("created");
		StreamInbound si = new StreamInbound() {

			@Override
			protected void onTextData(Reader reader) throws IOException {

				CharBuffer cb = CharBuffer.allocate(CHAR_BUFFER_SIZE);
				reader.read(cb);
				String str = getStringFromBuffer(cb);
				for (int i=0; i< sis.size(); i++) {
					if(sis.get(i)!=null) writeStringToBuffer(str, sis.get(i));
				}
			}

			@Override
			protected void onBinaryData(InputStream arg0) throws IOException {
				// TODO Auto-generated method stub
				System.out.println("bin  data");
			}

			@Override
			protected void onClose(int status) {
				// TODO Auto-generated method stub
				System.out.println("closed");
			}

			@Override
			protected void onOpen(WsOutbound outbound) {
				// TODO Auto-generated method stub
				super.onOpen(outbound);
				sis.add(outbound);
			}

		};

		return si;
	}

	// TODO make private
	public String getStringFromBuffer(CharBuffer cb) {
		String str = "";
		cb.position(0);
		while (cb.hasRemaining()) {
			char c = cb.get();
			if(c == 0) return str;
			str = (c != 0) ? (str + c) : str;
		}
		return str;
	}

	// TODO make private
	public CharBuffer getBufferFromString(String str) {
		CharBuffer cb = CharBuffer.allocate(CHAR_BUFFER_SIZE);
		cb.put(str);
		return cb;
	}

	// TODO make private
	public synchronized void writeStringToBuffer(String str, WsOutbound outbound) {
		CharBuffer cb = getBufferFromString(str);
		cb.position(0);
		try {
			outbound.writeTextMessage(cb);
			outbound.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			removeListener(outbound);
		}

	}
	private synchronized void removeListener(WsOutbound out) {
		sis.remove(out);
	}

}
