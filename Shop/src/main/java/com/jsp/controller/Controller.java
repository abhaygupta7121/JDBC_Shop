package com.jsp.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Properties;
import org.postgresql.Driver;
import com.jsp.model.Product;
import com.jsp.model.Shop;

public class Controller {
	
	static String dbUrl = "jdbc:postgresql://localhost:5432/Shop";
	static Connection connection = null;
	static {
		try {
			//register
			
			Driver pgDriver = new Driver();
			DriverManager.registerDriver(pgDriver);
			
			//establish connection
			
			FileInputStream fileInputStream = new FileInputStream("Dbconfig.properties");
			Properties properties = new Properties();
			properties.load(fileInputStream);
			connection = DriverManager.getConnection(dbUrl, properties);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	
	public int addShop(Shop shop) {
		try {
			PreparedStatement prepareStatement = connection.prepareStatement
					("INSERT INTO shop VALUES(?,?,?,?,?,?);");
			prepareStatement.setInt(1, shop.getId());
			prepareStatement.setString(2, shop.getShopName());
			prepareStatement.setString(3, shop.getAddress());
			prepareStatement.setString(4,shop.getGst());
			prepareStatement.setLong(5, shop.getContact());
			prepareStatement.setString(6, shop.getOwnerName());
			return prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public Shop isShopExist() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM shop;");
			Shop isShopExist = new Shop();
			while (resultSet.next()) {
				isShopExist.setId(resultSet.getInt(1));
				isShopExist.setShopName(resultSet.getString(2));
				isShopExist.setAddress(resultSet.getString(3));
				isShopExist.setGst(resultSet.getString(4));
				isShopExist.setContact(resultSet.getLong(5));
				isShopExist.setOwnerName(resultSet.getString(6));	
			}
			return isShopExist;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addProducts(Shop shop, List<Product> products) {

		for (Product product : products) {
			try {
				//Insert product into product table
				PreparedStatement prepareStatement = connection.prepareStatement
						("INSERT INTO product VALUES(?,?,?,?,?);");
				prepareStatement.setInt(1, product.getId());
				prepareStatement.setString(2, product.getProductName());
				prepareStatement.setDouble(3, product.getPrice());
				prepareStatement.setInt(4,product.getQuantity());
				prepareStatement.setBoolean(5, product.isAvailability());
				prepareStatement.executeUpdate();
				//Insert shopID and ProductID into Shop-Product table
				PreparedStatement prepareStatement2 = connection.prepareStatement
						("INSERT INTO shop_id_product_id VALUES (?,?)");
				prepareStatement2.setInt(1, shop.getId());
				prepareStatement2.setInt(2, product.getId());
				prepareStatement2.executeUpdate();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int removeProduct(int productId){
		try {
			CallableStatement removeFromProduct = connection.prepareCall("call remove_procedure(?,?,?)");
			//
			removeFromProduct.registerOutParameter(1, Types.INTEGER);
			removeFromProduct.setInt(2, productId);
			removeFromProduct.registerOutParameter(3, Types.INTEGER);
			removeFromProduct.executeUpdate();
			int int1 = removeFromProduct.getInt(1);
			int int2 = removeFromProduct.getInt(3);
			if (int1!=int2) {
				return 2;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public ResultSet FetchAllProducts() {
		
		try {
			Statement statement = connection.createStatement();
			return checkProduct(statement.executeQuery("SELECT * FROM product;"));
//			byte count=0;
//			while(products.next()) {
//				if(++count>0) {
//					break;
//				}
//			}
//			if (count == 1) {
//				return statement.executeQuery("select * from product;");
//			} else {
//				return null;
//			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Product fetchParticularProduct(int id) {
		try {
			PreparedStatement prepareStatement = connection.prepareStatement
					("SELECT * FROM  product WHERE id=?;");
			prepareStatement.setInt(1, id);
			ResultSet productResultSet = checkProduct(prepareStatement.executeQuery());
			//return that particular product
			Product product = new Product();
			while(productResultSet.next()) {
				if(productResultSet.next()) {
					product.setId(productResultSet.getInt(1));
					product.setProductName(productResultSet.getString(2));
					product.setPrice(productResultSet.getDouble(3));
					product.setQuantity(productResultSet.getInt(4));
					product.setAvailability(productResultSet.getBoolean(5));
					
				}
			}
			return product;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ResultSet checkProduct(ResultSet resultSet) {
		try {
			Statement statement = connection.createStatement();
			byte count = 0;
			while(resultSet.next()) {
				if(++count > 0) {
					break;
				}
			}
			if (count == 1) {
				return statement.executeQuery("select * from product;");
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closeConnetion() {
		if (connection!=null) {
			try {
				connection.close();
				System.out.println("closed");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
