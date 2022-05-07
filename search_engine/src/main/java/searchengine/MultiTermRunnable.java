package searchengine;

import java.util.List;
import java.util.Map;

public class MultiTermRunnable implements Runnable {
	
	int termPos;
	String term;
	float[] termW;
 	Map<String, Float> pagerank;
	Map<String, Document> doc;
	
	static String[] indexTable = {
			"te0_invertedindex",
			"te1_invertedindex",
			"te2_invertedindex"
	};
	
	public MultiTermRunnable(int termPos, String term, float[] termW,
			Map<String, Float> pagerank,
			Map<String, Document> doc) {
		
		this.termPos = termPos;
		this.term = term;
		this.termW = termW;
		this.pagerank = pagerank;
		this.doc = doc;
	}
	

	@Override
	public void run() {
		DBHelper helper = new DBHelper();
		List<Item> index = helper.getInvertedIndexMulti(indexTable, term);
		
		for (Item item: index) {
//			Document d = doc.getOrDefault(item.url, new Document(item.url, item.title, termW));
			
			Document d;
			if (doc.containsKey(item.url)) {
				d = doc.get(item.url);
			} else {
				d =  new Document(item.url, item.title, termW, item.excerpt);
				d.setPagerank(pagerank.getOrDefault(item.url, (float) 0.01));
			}
			
			d.addTf(termPos, item.tf);
			doc.put(item.url, d);
		}
	}
}
