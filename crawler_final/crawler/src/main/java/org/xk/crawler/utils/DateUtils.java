package org.xk.crawler.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateUtils {
	public static String nowFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result =  sdf.format(new Date()).replace(" ", "T");
		return result;
	}

	public static String getCurrentPath(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
		String result =  sdf.format(new Date()).replace(" ", "T");
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentPath());
	}
}
