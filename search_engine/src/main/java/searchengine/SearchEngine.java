package searchengine;

import static spark.Spark.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SearchEngine {
	static Logger log = LogManager.getLogger(SearchEngine.class);
	
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
			DBHelper helper = new DBHelper();
			
			List<Item> index = helper.getInvertedIndex("Test_titleindex", query);
			
			for (Item item: index) {
				item.addPageRank(helper.getPagerankForUrl("TestPageRankDemo", item.url));
			}
			
			Collections.sort(index, new Comparator<Item>(){
				@Override
				public int compare(Item a, Item b) {
					if (a.score < b.score) return 1;
					else return -1;
				}
			});
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			
			for (Item item: index) {
				System.out.println(item);
				
				ObjectNode obj = mapper.createObjectNode();
				obj.put("title", item.term);
				obj.put("url", item.url);
				obj.put("description", item.term);
				obj.put("pagerank", item.pagerank);
				obj.put("ir", item.tf);
				
				arrayNode.add(obj);
			}
			
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
//			String results = buildMockResults();
			return json;
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
	 * Helper functions
	 */
	private static String buildMockResults() {
		
		String json = "";
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			ObjectNode res1 = mapper.createObjectNode();
			res1.put("title", "University of Pennsylvania");
			res1.put("url", "https://www.upenn.edu");
			res1.put("description", "Equal Opportunity and Nondiscrimination at Penn. The University of Pennsylvania values diversity and seeks talented students, faculty and staff from diverse ...");
			res1.put("pagerank", "0.28");
			res1.put("ir", "0.87");
			
			ObjectNode res2 = mapper.createObjectNode();
			res2.put("title", "University of Pennsylvania - Wikipedia");
			res2.put("url", "https://en.wikipedia.org/wiki/University_of_Pennsylvania");
			res2.put("description", "The University of Pennsylvania (Penn or UPenn) is a private Ivy League research university in Philadelphia, Pennsylvania. The university, established as the ..");
			res2.put("pagerank", "0.28");
			res2.put("ir", "0.87");
			
			ObjectNode res3 = mapper.createObjectNode();
			res3.put("title", "University of Pennsylvania - Profile, Rankings and Data");
			res3.put("url", "https://www.usnews.com/best-colleges/university-of-pennsylvania-3378");
			res3.put("description", "University of Pennsylvania is a private institution that was founded in 1740. It has a total undergraduate enrollment of 9,872 (fall 2020), its setting is ...");
			res3.put("pagerank", "0.28");
			res3.put("ir", "0.87");
			
			ArrayNode arrayNode = mapper.createArrayNode();
			arrayNode.addAll(Arrays.asList(res1, res2, res3));
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
}
