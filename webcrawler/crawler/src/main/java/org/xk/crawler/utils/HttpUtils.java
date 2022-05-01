package org.xk.crawler.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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
			return null;
		}
		return html;
	}

	public static String doGet(String path) {
		if(!path.startsWith("http")) {
			path = "http://"+path;
		}
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(path);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				return EntityUtils.toString(responseEntity,"UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String doPost(String path, String body) {
		if(!path.startsWith("http")) {
			path = "http://"+path;
		}
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(path);
		CloseableHttpResponse response = null;
		httpPost.addHeader("Content-Type", "application/json");
		if(body!=null) {
			StringEntity postingString = new StringEntity(body, "utf-8");
			httpPost.setEntity(postingString);
		}
		try {
			response = httpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getHtml("https://crawltest.cis.upenn.edu/"));
	}
}
