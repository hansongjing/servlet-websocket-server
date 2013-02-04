package com.dzharvis;

import java.nio.CharBuffer;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static void log(String str) {
		System.out.println(new Date().toString() + ": " + str);
	}

	public static String findURLs(String str) {
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
		log(temp.toString());
		return temp.toString();
	}

	public static CharBuffer getBufferFromString(String str) {
		CharBuffer cb = CharBuffer.allocate(str.length());
		cb.put(str);
		cb.position(0);
		return cb;
	}

}
