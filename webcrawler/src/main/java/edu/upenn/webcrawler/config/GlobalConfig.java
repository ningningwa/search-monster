package edu.upenn.webcrawler.config;

import edu.upenn.webcrawler.entity.Document;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class GlobalConfig {
    public static BlockingDeque<Document> CRAWLER_QUEUE = new LinkedBlockingDeque<Document>();
    public static Map<String,Boolean> ACCESS_MAP = new ConcurrentHashMap<>();
    public static final String USER_AGENT = "cis455crawler";

    public static void main(String[] args){
        CRAWLER_QUEUE.add(new Document());
    }
}
