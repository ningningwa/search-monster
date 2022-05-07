package searchengine;

import java.util.List;
import java.util.Map;

public class SearchRunnable implements Runnable {
	
	List<Item> index;
	int start, end;
	Map<String, Float> pagerank;
	
	public SearchRunnable(List<Item> index, Map<String, Float> pagerank, int start, int end) {
		this.index = index;
		this.pagerank = pagerank;
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		for (int i=start; i<=end; i++) {
			Item item = index.get(i);
			item.addPageRank(pagerank.getOrDefault(item.url, (float) 0.01));
		}
	}
}
