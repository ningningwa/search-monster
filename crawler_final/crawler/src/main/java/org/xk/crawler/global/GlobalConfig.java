package org.xk.crawler.global;

import org.xk.crawler.entity.Document;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class GlobalConfig {
    public static Queue<Document> CRAWLER_QUEUE = new LinkedBlockingDeque<Document>();
    public static Map<String,Boolean> ACCESS_MAP = new ConcurrentHashMap<>();
    public static final String USER_AGENT = "cis455crawler";
    public static int count = 0;
    public static void main(String[] args){
        CRAWLER_QUEUE.add(new Document());
    }
    public static String BASE_PATH = "E:\\crawler";
}
