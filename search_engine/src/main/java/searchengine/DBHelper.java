package searchengine;
import java.sql.*;
import java.util.*;

public class DBHelper {
	
	Connection conn;

	public DBHelper() {
		conn = getRemoteConnection();
	}
	
    private static Connection getRemoteConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbName = "documents";
            String userName = "cis555";
            String password = "cis555final";
            String hostname = "cis555final.cru751xzhha3.us-east-2.rds.amazonaws.com";
            String port = "3306";
            String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password + "&useSSL=false";

            Connection con = DriverManager.getConnection(jdbcUrl);
            return con;
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
	    return null;
	}
    
    
    public int getTableSize(String table) {
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT COUNT(*) as cnt FROM %s;", table);
			
			ResultSet rs = st.executeQuery(query);
			rs.next();			
			return rs.getInt("cnt");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
    }
    
    
    public void printSampleData(String table, int limit) {		
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT * FROM %s LIMIT %s;", table, "" + limit);
			
			ResultSet rs = st.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
				}
				System.out.println("");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    public void printInvertedIndex(String table, String term) {		
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT * FROM %s WHERE term=\'%s\'", table, term);
			
			ResultSet rs = st.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
				}
				System.out.println("");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    public List<Item> getInvertedIndex(String table, String term) {	
    	List<Item> list = new LinkedList<>();
    	
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT * FROM %s WHERE term=\'%s\'", table, term);
			
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				Item item = new Item(rs.getString("term"), rs.getFloat("weight"), rs.getString("url"));
				list.add(item);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
    }
    
    
    public Set<String> getCorpus(String table) {
    	Set<String> res = new HashSet<>();
    	
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT DISTINCT term FROM %s", table);
			
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				res.add(rs.getString("term"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
    }
    
    
    public float getPagerankForUrl(String table, String url) {
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT score FROM %s WHERE url=\'%s\'", table, url);
			
			ResultSet rs = st.executeQuery(query);
			
			if (!rs.next()) {
				return 0;
			} else {
				return rs.getFloat("score");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
    }
}
