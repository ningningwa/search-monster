package searchengine;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.englishStemmer;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import scala.Tuple2;

public class DBTest {
	
	static String[] indexTable = {
			"te0_invertedindex",
			"te1_invertedindex",
			"te2_invertedindex"
	};

	public static void main(String[] args) {
		DBHelper helper = new DBHelper();
//		int size = helper.getTableSize("te_final6_invertedindex");
//		System.out.println(size);
		
//		Set<String> terms = helper.getCorpus("Test_titleindex");
//		System.out.println(terms.size());
//		for (String term: terms) System.out.println(term);
		
//		helper.printInvertedIndex(indexTable, "pennsylvania");
//		helper.printSampleData("te_final1_invertedindex", 100);
		
//		long startT = System.currentTimeMillis();
//		System.out.println(helper.getIndexSizeForTerm("body0_invertedindex", "philadelphia"));	
//		System.out.println("Time needed: " + (System.currentTimeMillis() - startT) / 1000);
		
//		classOf[org.apache.commons.lang3.SystemUtils].getResource("SystemUtils.class")
		
//		Map<String, Float> map = helper.getPagerankTable("testPageRank_self_use");
//		System.out.println(map.getOrDefault("https://www.upenn.edu/academics/graduate", (float) -1));
		
//		Set<String> largeUrl = new HashSet<>();
//		for (String key: map.keySet()) {
//			if (map.get(key) > 10) {
//				largeUrl.add(key);
//			}
//		}
//		
//		for (String url: largeUrl) {
//			System.out.println(url + " : " + map.get(url));
//		}
	}
}
