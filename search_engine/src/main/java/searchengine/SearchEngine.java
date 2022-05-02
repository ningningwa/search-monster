package searchengine;

import static spark.Spark.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SearchEngine {
	static Logger log = LogManager.getLogger(SearchEngine.class);
	static float defaultPagerank = (float) 0.01;
	
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
        
        registerCORS();
        registerSearchRoute();
        registerShutdownRoute();
	}

	
	/**
	 * Routes
	 */
	public static void registerSearchRoute() {
		
		get("/search/:query", (req, res) -> {
			String query = req.params(":query");
			log.info("Received query: {}", query);
						
			List<String> queries = getQueryList(query);
			List<Item> combined;
			
			if (queries.size() == 0) {
				
				// No valid keyword
				combined = new LinkedList<>();			
			} else if (queries.size() == 1) {
				
				// Single keyword search
				combined = getResultForQuery(queries.get(0));
				sortItem(combined);
				
			} else {
				
				// Search for multiple keywords
				Map<String, List<Item>> indexes = new HashMap<>();
				
				for (String q: queries) {
					List<Item> index = getResultForQuery(q);
					indexes.put(q, index);
				}
				
				combined = getCombinedResult(indexes);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			
			for (Item item: combined) {
//				System.out.println(item);
				
				ObjectNode obj = mapper.createObjectNode();
				obj.put("title", item.term); // TODO: change it to title
				obj.put("url", item.url);
				obj.put("pagerank", item.pagerank);
				obj.put("ir", item.tf);
				
				arrayNode.add(obj);
			}
			
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
    private static List<Item> getResultForQuery(String query) {
		DBHelper helper = new DBHelper();
		List<Item> index = helper.getInvertedIndex("Test_titleindex", query);
		
		for (Item item: index) {
			float pagerank = helper.getPagerankForUrl("TestPageRankDemo", item.url);
			if (pagerank == 0) pagerank = defaultPagerank; 
			
			item.addPageRank(pagerank);
		}
		
		return index;
    }
    
    
    private static void sortItem(List<Item> items) {
		Collections.sort(items, new Comparator<Item>(){
			@Override
			public int compare(Item a, Item b) {
				if (a.score < b.score) return 1;
				else return -1;
			}
		});
    }
    
    
    private static List<Item> getCombinedResult(Map<String, List<Item>> indexes) {  	
    	List<Item> res = new LinkedList<>();
    	DBHelper helper = new DBHelper();
    	return res;
    }
    
    
    /**
     * Helper Functions for query processing
     */
    private static List<String> getQueryList(String raw) {
    	List<String> list = new LinkedList<>();
    	String[] queries = raw.split(" ");
    	
    	// TODO: filter out stop words
    	for (String q: queries) list.add(q);
    	return list;
    }
}
