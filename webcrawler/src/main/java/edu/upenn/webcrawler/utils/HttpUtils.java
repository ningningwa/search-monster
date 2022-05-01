package edu.upenn.webcrawler.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public abstract class HttpUtils {
	public static Map<String, List<String>> head(String headUrl) throws Exception{
		URL url = new URL(headUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();		
		conn.setDoOutput(true);		
		conn.setRequestMethod("HEAD");
		Map<String, List<String>> headerMap = conn.getHeaderFields();
		return headerMap;
	}
	
	public static String getHtml(String inurl) {
		String html = null;
		try {
			URL url=new URL(inurl);
			URLConnection conn=url.openConnection();
			InputStream is=conn.getInputStream();
			html = new String(is.readAllBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return html;
	}
}
