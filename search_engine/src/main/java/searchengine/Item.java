package searchengine;

public class Item {
	public String term;
	public float factor;
	public float tf;
	public float pagerank;
	public float score;
	public String url;
	
	public Item(String term, float tf, String url) {
		this.term = term;
		this.tf = tf;
		this.url = url;
		this.factor = (float)0.5;
	}
	
	public void addPageRank(float pagerank) {
		this.pagerank = pagerank;
		this.score = factor * tf + (1 - factor) * pagerank;
	}

	@Override
	public String toString() {
		return "Item [term=" + term + ", tf=" + tf + ", pagerank=" + pagerank + ", score=" + score + ", url=" + url
				+ "]";
	}

}
