import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Row;

import com.google.common.collect.Iterables;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class PageRank {
	  private static final Pattern SPACES = Pattern.compile("\\s+");

	  private static class Sum implements Function2<Double, Double, Double> {

		public Double call(Double v1, Double v2) throws Exception {
			// TODO Auto-generated method stub
			return v1+v2;
		}

		
	  }

//    private static Connection getRemoteConnection() throws SQLException, ClassNotFoundException {
//        // String dbName = "documents";
//        String userName = "cis555";
//        String password = "cis555final";
////        Class.forName("com.mysql.cj.jdbc.Driver");   
//        Connection con = DriverManager.getConnection("jdbc:mysql://cis555final.cru751xzhha3.us-east-2.rds.amazonaws.com:3306/documents?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=true", userName, password);
//        
//        return con;
//      }
    //handle convergence
    private static boolean checkConvergence(JavaPairRDD<String, Double> a, JavaPairRDD<String, Double> b) {
    	List<Tuple2<String, Double>> aa = a.collect();
    	List<Tuple2<String, Double>> bb = b.collect();
    	for (int i = 0; i<aa.size(); i++) {
    		if (Math.abs(aa.get(i)._2()-bb.get(i)._2())>0.00001) {
    			return false;
    		}
    	}
    	return true;
    }
    public static void main(String[] args) throws Exception {
//        Connection connection = getRemoteConnection();
//        Statement statement = connection.createStatement();
//        List<String> array = new ArrayList();
//        Map<String, String> urls = new HashMap();
    	//String query = "SELECT m.access_url as url, n.access_url as curl FROM (SELECT b.id, b.access_url, a.cid FROM t_doc_rel a JOIN t_document b WHERE a.id=b.id) m JOIN t_document n WHERE m.cid=n.id";
//        ResultSet resultSet=statement.executeQuery(query);
        
        String url = "jdbc:mysql://cis555final.cru751xzhha3.us-east-2.rds.amazonaws.com:3306/documents?user=cis555&password=cis555final";
        
//        while (resultSet.next()) {
//        	String url=resultSet.getString("url");
//        	String curl=resultSet.getString("curl");
//        	array.add(url+" "+curl);
//        }
//        statement.close();
//        
//        String url = 

  	    SparkSession spark = SparkSession
  	      .builder()
  	      .appName("PageRank")
  	      .getOrCreate();
  	    Dataset<Row> df = spark.read()
				.format("jdbc")
				.option("url", url)
				.option("driver", "com.mysql.cj.jdbc.Driver")
				.option("dbtable", "NEWREF")
				.load();

  	    JavaRDD<Row> lines = df.toJavaRDD();

  	    // Loads all URLs from input file and initialize their neighbors.
  	    JavaPairRDD<String, Iterable<String>> links = lines.mapToPair(row -> {
  	      String a = row.getAs("url");
  	      String b = row.getAs("curl");
  	      return new Tuple2<>(a, b);
  	    }).distinct().groupByKey().cache();

  	    // Loads all URLs with other URL(s) link to from input file and initialize scores of them to one.
  	    JavaPairRDD<String, Double> scores = links.mapValues(rs -> 1.0);

  	    // Calculates and updates URL scores continuously using Pagescore algorithm.
  	    //referenced from official Spark PageRank;
  	    JavaPairRDD<String, Double> prevscores=null;
  	    int iter=0;
  	    while (prevscores==null || !checkConvergence(prevscores, scores)) {
  	      // Calculates URL contributions to the score of other URLs.
  	    	if (scores!=null) {
    	    	  prevscores=scores;
    	      }
  	      JavaPairRDD<String, Double> contribs = links.join(scores).values()
  	        .flatMapToPair(s -> {
  	          int urlCount = Iterables.size(s._1());
  	          List<Tuple2<String, Double>> results = new ArrayList<>();
  	          for (String n : s._1) {
  	            results.add(new Tuple2<>(n, s._2() / urlCount));
  	          }
  	          return results.iterator();
  	        });
  	      // Re-calculates URL scores based on neighbor contributions.
  	      scores = contribs.reduceByKey(new Sum()).mapValues(sum -> 0.15 + sum * 0.85);
  	      iter+=1;
  	    }
  	    JavaRDD<Score> newScores = scores.map(pair -> {
			Score entry = new Score();
			entry.setUrl(pair._1());
			entry.setScore(pair._2());
			return entry;
		});
  	    Dataset<Row> result= spark.createDataFrame(newScores, Score.class);
	  	result.write()
			.format("jdbc")
			
			.option("url", url)
			.option("driver", "com.mysql.cj.jdbc.Driver")
			.option("dbtable", "testPageRank_self_use")
			.option("truncate", true)
			.mode("overwrite")
			.save();

  	    // Collects all URL scores and dump them to console.
//  	    statement = connection.createStatement();
//  	    String createTable = "CREATE TABLE TestPageRankDemo (url longtext, score double);";
//  	    statement.addBatch(createTable);
//  	    List<Tuple2<String, Double>> output = scores.collect();
//  	    for (Tuple2<String, Double> tuple : output) {
//  	      System.out.println(tuple._1() + " has score: " + tuple._2());
  	      //statement.addBatch(String.format("INSERT INTO TestPageRankDemo (url, score) VALUES (\"%s\", %f);", tuple._1().replace("'", "\\'"), tuple._2()));
//  	    }
//	  	statement.executeBatch();
//	    statement.close();
  	    System.out.println("urlCount: "+scores.collect().size());
  	    System.out.println("iter until convergence: "+iter);

  	    spark.stop();
    }
    
    public static class Score implements Serializable {
  	  /**
  	 * 
  	 */
	  	private static final long serialVersionUID = 1L;
	  	private String url;
	  	  private Double score;
	  	  public String getUrl() {
	  		  return url;
	  	  }
	  	  public void setUrl(String url) {
	  		  this.url=url;
	  	  }
	  	  public Double getScore() {
	  		  return score;
	  	  }
	  	  public void setScore(Double score) {
	  		  this.score=score;
	  	  }
  }  
}