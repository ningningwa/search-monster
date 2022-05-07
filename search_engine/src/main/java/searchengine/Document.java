package searchengine;

import java.util.*;

public class Document {
	String url;
	String title;
	String excerpt;
	
	float[] termW;
	float[] tf;
	float pagerank;
	
	float cosSim;
	float score;
	float factor;
	
	public Document(String url, String title, float[] termW, String excerpt) {
		this.url = url;
		this.title = title;
		tf = new float[termW.length];
		this.termW = termW;
		this.factor = (float)0.5;
		this.excerpt = excerpt;
	}
	
	public Document(String url, String title, float[] termW, float pagerank, String excerpt) {
		this.url = url;
		this.title = title;
		tf = new float[termW.length];
		this.termW = termW;
		this.pagerank = pagerank;
		this.excerpt = excerpt;
	}
	
	public void setPagerank(float pagerank) {
		this.pagerank = pagerank;
	}
	
	public void addTf(int pos, float tfScore) {
		tf[pos] = tfScore;
	}
	
	public void computeCosSim() {
		float res = 0;
		for (int i=0; i<tf.length; i++) {
			res += termW[i] * tf[i];
		}
		
		this.cosSim = res;
	}
	
	public void computeScore() {
		computeCosSim();
		this.score = this.pagerank * (1-factor) + this.cosSim * factor;
	}

	public float getScore() {
		return score;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
}
