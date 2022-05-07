package searchengine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TermRunnable implements Runnable {
	
	Map<String, List<Item>> indexes;
	String term;
	Map<String, Float> pagerank;
	
	public TermRunnable(Map<String, List<Item>> indexes, String term,
			Map<String, Float> pagerank) {
		this.indexes = indexes;
		this.term = term;
		this.pagerank = pagerank;
	}

	@Override
	public void run() {
		DBHelper helper = new DBHelper();
		List<Item> index = helper.getInvertedIndex("Test_titleindex", term);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		int len = index.size();
		int start = 0, end = len / 10, gap = end - start;
		
		while (start < len) {
			end = Math.min(end, len - 1);			
			SearchRunnable r = new SearchRunnable(index, pagerank, start, end);
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
		
		indexes.put(term, index);
	}
}
