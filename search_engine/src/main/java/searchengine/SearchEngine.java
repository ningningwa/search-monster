package searchengine;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.tartarus.snowball.ext.englishStemmer;

public class SearchEngine {
	static Logger log = LogManager.getLogger(SearchEngine.class);
	static float defaultPagerank = (float) 0.1;
	
	static int numThreads = 15;
	static int numIndexTable;
	
	static Map<String, Float> map;
	static int N;
	
	static Set<String> stopwords = StopWords.getStopWords();
	static englishStemmer stemmer = new englishStemmer();
	
	static String tablePagerank = "testPageRank_self_use";
	static String[] indexTable = {
			"te0_invertedindex",
			"te1_invertedindex",
			"te2_invertedindex"
	};
	static String[] indexTableL = {
			"te0_invertedindex",
			"te1_invertedindex",
			"te2_invertedindex",
			"te_final_invertedindex",
			"te_final2_invertedindex",
			"te_final3_invertedindex",
			"te_final4_invertedindex",
			"te_final5_invertedindex",
			"te_final6_invertedindex"
	};
	
	/**
	 * Main Function
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
        if (args.length < 1) {
            System.out.println("Usage: Search Engine [port number]");
            System.exit(1);
        }

        int myPort = Integer.valueOf(args[0]);
        port(myPort);
        
        System.out.println("Search Engine node startup, on port " + myPort);
        
        // Load Pagerank table in memory
        DBHelper helper = new DBHelper();
        map = helper.getPagerankTable(tablePagerank);
        N = map.size();
        
        registerCORS();
        registerSearchRoute();
        registerLargeResultRoute();
        registerProductSearchRoute();
        registerShutdownRoute();
	}

	
	/**
	 * Routes
	 */
	public static void registerSearchRoute() {
		
		get("/search/:query", (req, res) -> {
			long startT = System.currentTimeMillis();
			
			String query = req.params(":query");
			log.info("Received query: {}", query);
						
			List<String> queries = getQueryList(query);
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			
			if (queries.size() <= 1) {			
				// Single keyword search
				List<Item> searchRes = new LinkedList<>();
				
				if (queries.size() == 1)
					searchRes = getResultForQuery(queries.get(0), indexTable);
								
				for (Item item: searchRes) {
//					System.out.println(item);
					
					ObjectNode obj = mapper.createObjectNode();
					obj.put("title", item.title); 
					obj.put("url", item.url);
					obj.put("pagerank", item.pagerank);
					obj.put("ir", item.tf);
					obj.put("excerpt", item.excerpt);
					obj.put("score", item.score);
					
					arrayNode.add(obj);
				}
				
			} else {
				
				// Search for multiple keywords
				List<Document> searchRes = multipleTermSearch(queries, indexTable);
				
				for (Document d: searchRes) {
					ObjectNode obj = mapper.createObjectNode();
					obj.put("title", d.title); 
					obj.put("url", d.url);
					obj.put("pagerank", d.pagerank);
					obj.put("ir", d.cosSim);
					obj.put("excerpt", d.excerpt);
					obj.put("score", d.score); 
					
					arrayNode.add(obj);
				}
			}
			
			long endT = System.currentTimeMillis();
			log.info("Search time for query {}: {}, with result size: {}", query, endT - startT, arrayNode.size());
			
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
		});
	}
	
	
	public static void registerLargeResultRoute() {
		
		get("/searchL/:query", (req, res) -> {
			long startT = System.currentTimeMillis();
			
			String query = req.params(":query");
			log.info("Received query: {}", query);
						
			List<String> queries = getQueryList(query);
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			
			if (queries.size() <= 1) {			
				// Single keyword search
				List<Item> searchRes = new LinkedList<>();
				
				if (queries.size() == 1)
					searchRes = getResultForQuery(queries.get(0), indexTableL);
								
				for (Item item: searchRes) {
//					System.out.println(item);
					
					ObjectNode obj = mapper.createObjectNode();
					obj.put("title", item.title); 
					obj.put("url", item.url);
					obj.put("pagerank", item.pagerank);
					obj.put("ir", item.tf);
					obj.put("excerpt", item.excerpt);
					obj.put("score", item.score);
					
					arrayNode.add(obj);
				}
				
			} else {
				
				// Search for multiple keywords
				List<Document> searchRes = multipleTermSearch(queries, indexTableL);
				
				for (Document d: searchRes) {
					ObjectNode obj = mapper.createObjectNode();
					obj.put("title", d.title); 
					obj.put("url", d.url);
					obj.put("pagerank", d.pagerank);
					obj.put("ir", d.cosSim);
					obj.put("excerpt", d.excerpt);
					obj.put("score", d.score); 
					
					arrayNode.add(obj);
				}
			}
			
			long endT = System.currentTimeMillis();
			log.info("Search time for query {}: {}, with result size: {}", query, endT - startT, arrayNode.size());
			
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
		});
	}
	
	
	
	public static void registerProductSearchRoute() {
		get("/product/:query", (req, res) -> {
			long startT = System.currentTimeMillis();
			
			String queryRaw = req.params(":query");
			String query = getCleanQuery(queryRaw);
			
			HttpURLConnection conn = rainForestAPI(query);
			List<Product> products = getTop10Products(conn);
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			
			for (Product p: products) {
				ObjectNode obj = mapper.createObjectNode();
				obj.put("title", p.getTitle());
				obj.put("link", p.getLink());
				obj.put("price", p.getPrice());
				obj.put("image", p.getImage());
				
				arrayNode.add(obj);
			}
			
			long endT = System.currentTimeMillis();
			log.info("Search time for product {}: {}", query, endT - startT);
			
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
		});
	}
	
	
	
	public static void registerCORS() {
		
		options("/*", (request, response) -> {

            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
	}
	
	
    public static void registerShutdownRoute() {
    	
        get("/shutdown", (req, res) -> {    
			stop();
        	return "The server has been shutdown";
        });
    }
	
	
	/**
	 * Helper functions for Search & Ranking
	 */
    private static List<Item> getResultForQuery(String query, String[] tableList) {
		DBHelper helper = new DBHelper();
		List<Item> index = helper.getInvertedIndexMulti(tableList, query);
		log.info("Finished fetch index for term {}", query);
		
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		
		int len = index.size();
		int start = 0, end = len / numThreads, gap = end - start;
		
		while (start < len) {
			end = Math.min(end, len - 1);
			
			SearchRunnable r = new SearchRunnable(index, map, start, end);
			executor.execute(r);
			
			start = start + gap + 1;
			end = end + gap + 1;
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("Finished fetch pagerank for term {}", query);
		
		Collections.sort(index, new Comparator<Item>(){
			@Override
			public int compare(Item a, Item b) {
				if (a.score < b.score) return 1;
				else if (a.score > b.score) return -1;
				return 0;
			}
		});
		
		return index;
    }
    
    
    public static List<Document> multipleTermSearch(List<String> queries, String[] tableList) {
    	DBHelper helper = new DBHelper();
    	
    	int termSize = queries.size();
    	float[] termW = new float[termSize];
    	
    	// Compute term weight (idf) for each term
    	for (int i=0; i<termSize; i++) {
    		int freq = helper.getIndexSizeForTerm(indexTable, queries.get(i));
    		freq = freq == 0 ? N : freq;
    		termW[i] = (float) Math.log(N / freq);
    		
    		log.info("Term weight for {}: {}", queries.get(i), termW[i]);
    	}
    	
    	
    	// Fetch documents index
    	Map<String, Document> doc = new HashMap<>();
    	ExecutorService executor = Executors.newFixedThreadPool(termSize);
    	
    	for (int i=0; i<termSize; i++) {
    		log.info("Begin fetch index for term {}", queries.get(i));
    		
    		MultiTermRunnable r = new MultiTermRunnable(i, queries.get(i), termW, map, doc, tableList);
    		executor.execute(r);
    	}
    	
    	executor.shutdown();
		
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("Finish fetching indexes for all terms");
		
		List<Document> res = new LinkedList<>();
		for (String key: doc.keySet()) {
			Document d = doc.get(key);
			d.computeScore();
			res.add(d);
		}
    	
		Collections.sort(res, new Comparator<Document>(){
			@Override
			public int compare(Document a, Document b) {
				if (a.score < b.score) return 1;
				else if (a.score > b.score) return -1;
				else return 0;
			}
		});
		
		log.info("Finish sorting the documents");
		return res;
    }
    
    
    /**
     * Helper Functions for query processing
     */
    private static List<String> getQueryList(String raw) {
    	List<String> list = new LinkedList<>();    	    	
    	String[] allTerms = raw.split("[\\p{Punct}\\s]+");

        for (String rawTerm : allTerms) {
            String word = rawTerm.toLowerCase()
                    .replaceAll("[^\\x00-\\x7F]", "")
                    .replaceAll("\u0000", "")
                    .trim();
            
            if (word!=null && word.length() < 20) {
            	if (!word.isEmpty() && !stopwords.contains(word)) {
                    stemmer.setCurrent(word);
                    if (stemmer.stem()) {
                        word = stemmer.getCurrent();
                    }
                }
            	
                if (!word.isEmpty() && !stopwords.contains(word)) {
                    list.add(word);
                }
            }
        }
            	
    	return list;
    }
    
    
    private static String getCleanQuery(String raw) {
    	StringBuilder sb = new StringBuilder();    	
    	String[] allTerms = raw.split("[\\p{Punct}\\s]+");

        for (String rawTerm : allTerms) {
            String word = rawTerm.toLowerCase()
                    .replaceAll("[^\\x00-\\x7F]", "")
                    .replaceAll("\u0000", "")
                    .trim();
            
            if (word!=null && word.length() < 20) {
            	if (!word.isEmpty() && !stopwords.contains(word)) {
                    stemmer.setCurrent(word);
                    if (stemmer.stem()) {
                        word = stemmer.getCurrent();
                    }
                }
            	
                if (!word.isEmpty() && !stopwords.contains(word)) {
                    if (sb.length() == 0) sb.append(word);
                    else sb.append(" ").append(word);
                }
            }
        }
            	
    	return sb.toString();
    }
    
    
    /**
     * Helper functions for search Amazon products
     */
	private static List<Product> getTop10Products(HttpURLConnection conn) {
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
	
	
    private static HttpURLConnection rainForestAPI(String term) {
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
