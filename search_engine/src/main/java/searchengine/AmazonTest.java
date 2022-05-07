package searchengine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.*;

public class AmazonTest {
	static Logger log = LogManager.getLogger(AmazonTest.class);
	
	public static void main(String[] args) {
		HttpURLConnection conn = rainForestAPI("memory card");
		List<Product> products = getTop10Products(conn);
		
		System.out.println(products.get(0));
	}
	
	static List<Product> getTop10Products(HttpURLConnection conn) {
		List<Product> result = new LinkedList<>();
		
		BufferedReader br = null;		
		try {
			if (conn.getResponseCode() == 200) {
			    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} 
			
			String resp = br.lines().collect(Collectors.joining());
			System.out.println("finishe br to string");
			JSONObject obj = new JSONObject(resp);
			
			JSONArray array = obj.getJSONArray("search_results");
			int max = Math.min(10, array.length());
			
			for (int i=0; i<max; i++) {
				JSONObject o = array.getJSONObject(i);
				result.add(new Product(o.getString("title"), o.getString("link"), 
						o.getJSONObject("price").getString("raw"), o.getString("image")));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
    static HttpURLConnection rainForestAPI(String term) {
    	URL url;
    	HttpURLConnection conn = null;
    	
    	StringBuilder query = new StringBuilder("https://api.rainforestapi.com/request?");
    	
    	String[] terms = term.split(" ");
    	String search_term = "";
    	
    	if (terms.length >= 1) search_term += terms[0];
    	
    	if (terms.length >= 2) {
    		for (int i=1; i<terms.length; i++) {
    			search_term += "_";
    			search_term += terms[i];
    		}
    	}
    	
    	String api_key = "EFD6FE60F8F94ECEBE9D067E6683B8D6";
    	String type = "search";
    	String amazon_domain = "amazon.com";
    	String sort_by = "average_review";
    	String page = "1";
    	
    	try {
    		query.append("api_key=").append(api_key);
    		query.append("&type=").append(type);
    		query.append("&amazon_domain=").append(amazon_domain);
    		query.append("&search_term=").append(search_term);
    		query.append("&sort_by=").append(sort_by);
    		query.append("&page=").append(page);
    		
    		url = new URL(query.toString());
        	
        	conn = (HttpURLConnection)url.openConnection();
        	conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept-Charset", "UTF-8");
    	} catch (Exception e) {
    		log.info("IOException when sending workerstatus to master");
    	}
    	
    	return conn;
    }
}
