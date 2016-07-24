package localshoppingplatform.server.crawlerdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import localshoppingplatform.shared.Product;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

public class Driver {
	private static Connection connect;
	private static boolean HasData = false;
	private PreparedStatement prepair;

	void dbConnection() throws ClassNotFoundException, SQLException {
		// Setup connection
		Class.forName("org.sqlite.JDBC");
		connect = DriverManager.getConnection("jdbc:sqlite:SQLiteTest1.db");
//		 initialize();
	}

	private void initialize() throws SQLException {
		// TODO Auto-generated method stub
		if (!HasData) {
			HasData = true;
			Statement state = connect.createStatement();
			ResultSet res = state.executeQuery("SELECT name FROM "
					+ "sqlite_master WHERE type='table' AND name='user'");
			if (!res.next()) {
				System.out
						.println("Building the User table with prepopulated values.");
				// // Build Table
				Statement state2 = connect.createStatement();
				state2.execute("CREATE TABLE producttest(Pid integer, ProductTitle varchar(60),"
						+ "ProductPrice double, Key varchar(60), PlatformName varchar(60), DateOfVisit varchar(60), "
						+ "ProductUrl varchar(60), VendorName varchar(60), OriginalPage varchar(60), RelatedUrls varchar(60), primary key(Pid));");
			}
		}
	}

	public void update() {

	}

	public void insert(Map<String, Product> productMap) throws SQLException,
			ClassNotFoundException {
		// Inserting values
		if (connect == null) {
			dbConnection();
		}
		for (String key : productMap.keySet()) {
			prepair = connect
					.prepareStatement("Insert INTO product values(?,?,?,?,?,?,?);");

			if (productMap.get(key).getTitle() != null) {
				prepair.setString(2, productMap.get(key).getTitle());
				if (Double.isNaN(productMap.get(key).getPrice()) == false)
					prepair.setDouble(3, productMap.get(key).getPrice());
				if (productMap.get(key).getOriginal_key() != null) {
					prepair.setString(4, productMap.get(key).getOriginal_key());
				}
				if (productMap.get(key).getDate_of_visit() != null) {
					prepair.setString(5, productMap.get(key).getDate_of_visit());
				}
				if (productMap.get(key).getProductRelatedurls() != null) {
					String urls="";
					for(int g=0;g<productMap.get(key).getProductRelatedurls().size();g++){
						urls=urls+"\n"+productMap.get(key).getProductRelatedurls().get(g);
					}
					
					prepair.setString(6, urls);
				}
				if (productMap.get(key).getVendor_name() != null) {
					prepair.setString(7, productMap.get(key).getVendor_name());
				}
			}
			prepair.execute();
		}
	}

	public void insertTest(Map<String, Product> productMap, List<String> newProduct)
			throws SQLException, ClassNotFoundException {
		// Inserting values
		if (connect == null) {
			dbConnection();
		}
		for (String key : newProduct) {
			prepair = connect
					.prepareStatement("Insert INTO producttest values(?,?,?,?,?,?,?,?,?,?);");

			if (productMap.get(key).getTitle() != null) {
				prepair.setString(2, productMap.get(key).getTitle());
				if (Double.isNaN(productMap.get(key).getPrice()) == false)
					prepair.setDouble(3, productMap.get(key).getPrice());
				if (key != null) {
					prepair.setString(4, key);
				}
				if (productMap.get(key).getPlatform_name() != null) {
					prepair.setString(5, productMap.get(key).getPlatform_name());
				}
				if (productMap.get(key).getDate_of_visit() != null) {
					prepair.setString(6, productMap.get(key).getDate_of_visit());
				}
				if (productMap.get(key).getProductRelatedurls() != null) {
					String urls="";
					for(int g=0;g<productMap.get(key).getProductRelatedurls().size();g++){
						urls=urls+"\n"+productMap.get(key).getProductRelatedurls().get(g);
					}
					prepair.setString(7, urls);
				}
				if (productMap.get(key).getVendor_name() != null) {
					prepair.setString(8, productMap.get(key).getVendor_name());
				}
				if (productMap.get(key).getOriginalPage() != null) {
					prepair.setString(9, productMap.get(key).getOriginalPage());
				}
				if (productMap.get(key).getRelatedUrls() != null) {
					prepair.setString(10, productMap.get(key).getRelatedUrls());
				}
			}
			prepair.execute();
		}
	}

	public void delete() throws SQLException, ClassNotFoundException {
		dbConnection();
		prepair = connect.prepareStatement("DROP TABLE product");
		prepair.execute();
	}

	public ResultSet displayData() throws ClassNotFoundException, SQLException {
		if (connect == null) {
			dbConnection();
		}
		Statement state = connect.createStatement();
		ResultSet res = state.executeQuery("SELECT ProductTitle FROM "
				+ "product");
		return res;
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		// TODO Auto-generated method stub
		Driver test = new Driver();
		// test.delete();
		ResultSet rs;
//		Map<String, Product> productMap = new HashMap<String, Product>();
//		Product p = new Product();
//		p.setTitle("Einzelrose in rot");
//		p.setPrice(8.0);
//		p.setPlatform_name("https://atalanda.com");
//		p.setDate_of_visit("11.07.2016");
//		p.setLink_url("https://atalanda.com/goeppingen/products/einzelrose-in-rot?abs_pos=1192&total=1193");
//		p.setOriginalPage("sdfsdf");
//		p.setOriginal_key("fdgdfgdsg");
//		productMap.put("sdfsdf", p);
//		test.insertTest(productMap);
		 rs = test.displayData();
		 while (rs.next()) {
		 System.out.println(rs.getString("ProductTitle"));
		 }
	}

}
