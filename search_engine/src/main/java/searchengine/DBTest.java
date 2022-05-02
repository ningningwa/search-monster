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
import java.util.concurrent.LinkedBlockingQueue;

public class DBTest {

	public static void main(String[] args) {
		DBHelper helper = new DBHelper();
//		int size = helper.getTableSize("Test_titleindex");
//		System.out.println(size);
		
//		Set<String> terms = helper.getCorpus("Test_titleindex");
//		System.out.println(terms.size());
//		for (String term: terms) System.out.println(term);
		
//		helper.printInvertedIndex("Test_titleindex", "");
//		helper.printSampleData("TestPageRankDemo", 100);
		
		List<Item> index = helper.getInvertedIndex("Test_titleindex", "wikipedia");
		
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
		
		for (Item item: index) {
			System.out.println(item);
		}
	}
}
