package com.test;

import static org.junit.Assert.assertTrue;

import java.nio.CharBuffer;

import org.junit.Test;

import com.dzharvis.Utils;


public class TestUtils {

	@Test
	public void testSimpleLinks() {
		assertTrue("<a href=http://somesite.com>http://somesite.com</a>".equals(Utils.findURLs("http://somesite.com")));
		assertTrue("someText<a href=http://somesite.com>http://somesite.com</a> someText".equals(Utils.findURLs("someTexthttp://somesite.com someText")));
		assertTrue("http : //somesite.com".equals(Utils.findURLs("http : //somesite.com")));
	}
	
	@Test
	public void testImageLinks() {
		assertTrue("<img src=http://somesite.com.jpg width=50%>".equals(Utils.findURLs("http://somesite.com.jpg")));
		assertTrue("<img src=http://somesite.com.JPG width=50%>".equals(Utils.findURLs("http://somesite.com.JPG")));
		assertTrue("<img src=http://somesite.com.JPEG width=50%>".equals(Utils.findURLs("http://somesite.com.JPEG")));
		assertTrue("<img src=http://somesite.com.BMP width=50%>".equals(Utils.findURLs("http://somesite.com.BMP")));
		assertTrue("<img src=http://somesite.com.bmp width=50%>".equals(Utils.findURLs("http://somesite.com.bmp")));
		assertTrue("<img src=http://somesite.com.GIF width=50%>".equals(Utils.findURLs("http://somesite.com.GIF")));
		assertTrue("<img src=http://somesite.com.gIF width=50%>".equals(Utils.findURLs("http://somesite.com.gIF")));
	}
	
	@Test
	public void testBufferFromString() {
		CharBuffer cb = CharBuffer.allocate(1);
		cb.put("q");
		cb.position(0);	
		assertTrue(Utils.getBufferFromString("q").equals(cb));
		
		assertTrue(Utils.getBufferFromString("1234567").length() == 7);
		assertTrue(Utils.getBufferFromString("").length() == 0);
	}

}
