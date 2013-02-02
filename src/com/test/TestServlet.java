package com.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class TestServlet {

	@Before
	public void prepare() {
		//setBaseUrl("http://localhost:8080/test");
	}

	@Test
	public void testSomething() {
		//Servlet s = new Servlet();
		assertTrue(true);
		// assertTrue("str"==s.getStringFromBuffer(s.getBufferFromString("str")));
	}

}
