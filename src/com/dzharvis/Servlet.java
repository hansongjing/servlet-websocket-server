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

				char cb[] = new char[1000];
				reader.read(cb);
				for (WsOutbound w : sis) {
					for (int i = 0; i < cb.length; i++) {
						if (cb[i] <= 0)
							break;
						w.writeTextData(cb[i]);
					}
					w.flush();
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
				try {
					outbound.writeTextData("A".charAt(0));
					outbound.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		return si;
	}

}
