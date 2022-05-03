package titleindexer;

import java.io.Serializable;

public class titleEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String term;
    private double weight;
    private String url;
    private int loc;
    private String title;

    public int getId() {
        return id;
    }
    public String getUrl(){return url;}

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
    public void setUrl(String url){this.url = url;}

    public void setLoc(int loc) {
        this.loc = loc;
    }
    public int getloc(){return loc;}
    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title;}
}