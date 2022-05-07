package searchengine;

public class Item {
	public String term;
	public float factor;
	public float tf;
	public float pagerank;
	public float score;
	
	public String url;
	public String title;
	public String excerpt;
	
	public Item(String term, float tf, String url, String title, String excerpt) {
		this.term = term;
		this.tf = tf;
		this.url = url;
		this.factor = (float)0.8;
		this.title = title;
		this.excerpt = excerpt;
	}
	
	public void addPageRank(float pagerank) {
		this.pagerank = pagerank;
		this.score = factor * tf + (1 - factor) * pagerank;
	}

	@Override
	public String toString() {
		return "Item [term=" + term + ", factor=" + factor + ", tf=" + tf + ", pagerank=" + pagerank + ", score="
				+ score + ", url=" + url + ", title=" + title + ", excerpt=" + excerpt + "]";
	}
}
