package bodyindexer;

import java.io.Serializable;

public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String term;
    private double weight;
    private String url;
    private int loc;

    public void setLoc(int loc){this.loc=loc;}
    public int getLoc(){return loc;}

    public int getId() {
        return id;
    }
    public String getUrl(){
    	return url;
    }

    public String getTerm() {
        return term;
    }

    public double getWeight() {
        return weight;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    public void setUrl(String url){
    	this.url = url;
    }

}