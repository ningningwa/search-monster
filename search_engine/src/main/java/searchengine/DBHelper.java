package searchengine;
import java.sql.*;
import java.util.*;

public class DBHelper {
	
	Connection conn;
	private static float defaultPagerank;

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
    
    
    public int getIndexSizeForTerm(String[] table, String term) {
		try {
			Statement st = conn.createStatement();
			
			int cnt = 0;
			
			for (String t: table) {
	            String query = String.format("SELECT COUNT(*) as cnt FROM %s WHERE term=\'%s\';", t, term);
				
				ResultSet rs = st.executeQuery(query);
				rs.next();	
				
				cnt += rs.getInt("cnt");
			}
				
			return cnt;
			
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
    
    
    public void printInvertedIndex(String[] table, String term) {		
		try {
			Statement st = conn.createStatement();
            
			for (String t: table) {
				String query = String.format("SELECT * FROM %s WHERE term=\'%s\'", t, term);
				
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
				Item item = new Item(rs.getString("term"), rs.getFloat("weight"), 
						rs.getString("url"), rs.getString("title"), rs.getString("excerpt"));
				list.add(item);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
    }
    
    
    public List<Item> getInvertedIndexMulti(String[] table, String term) {	
    	List<Item> list = new LinkedList<>();
    	
		try {
			Statement st = conn.createStatement();
			Set<String> seen = new HashSet<>();
			
			for (String t: table) {
	            String query = String.format("SELECT * FROM %s WHERE term=\'%s\'", t, term);
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					if (seen.contains(rs.getString("title"))) continue;

					Item item = new Item(rs.getString("term"), rs.getFloat("weight"), 
							rs.getString("url"), rs.getString("title"), rs.getString("excerpt"));
					
					list.add(item);
					seen.add(rs.getString("title"));
				}
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
				return defaultPagerank;
			} else {
				return rs.getFloat("score");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return defaultPagerank;
    }
    
    
    public Map<String, Float> getPagerankTable(String table) {
    	Map<String, Float> map = new HashMap<>();
    	
		try {
			Statement st = conn.createStatement();
            String query = String.format("SELECT url, score FROM %s", table);
			
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				float score = rs.getFloat("score");
				
				// TODO: tuen the score normalization
				if (score > 3) {
					score = score / 30 + 2;
				}
				
				map.put(rs.getString("url"), score);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return map;
    }
}
