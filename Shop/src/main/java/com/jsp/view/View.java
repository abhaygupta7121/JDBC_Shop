package com.jsp.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.jsp.controller.Controller;
import com.jsp.model.Product;
import com.jsp.model.Shop;

public class View {
	static Scanner myInput = new Scanner(System.in);
	static Controller controller = new Controller();
	static Shop shop = new Shop();
	static {
		//ask shop details for first 1st run of application
		//for 2nd run onward check if shop exists, if yes than use existing
		Shop shopExist = controller.isShopExist();
		if(shopExist.getId() != 0) {
			// Maintain only one reference for futher referance
			shop = shopExist;
			System.out.println("Welcome back to Shop");
			System.out.println("Shop Details :" );
			System.out.println("Enter ID :"+ shop.getId());
			System.out.println("Enter Shop Name :" + shop.getShopName());
			System.out.println("Enter Shop Address :"+ shop.getAddress());
			System.out.println("Enter GST :"+ shop.getGst());
			System.out.println("Enter contact number :"+ shop.getContact());
			System.out.println("Enter Shop owner Name :" + shop.getOwnerName());
		} else {
			System.out.println("Welcome to Shop");
			System.out.print("Enter ID :");
			shop.setId(myInput.nextInt());
			myInput.nextLine();
			System.out.print("Enter Shop Name :");
			shop.setShopName(myInput.nextLine());
			System.out.print("Enter Shop Address :");
			shop.setAddress(myInput.nextLine());
			System.out.print("Enter GST :");
			shop.setGst(myInput.nextLine());
			System.out.print("Enter contact number :");
			shop.setContact(myInput.nextLong());
			myInput.nextLine();
			System.out.print("Enter Shop owner Name :");
			shop.setOwnerName(myInput.nextLine());
			if (controller.addShop(shop)!=0) {
				System.out.println("Shop Added\n");
			}
		}
	}
	
	public static void main(String[] args) {
		do {
			System.out.print("Select operation to perform :");
			System.out.println("1.Add product/s \n2.Remove Product \n 0.Exit");
			System.out.print("Enter digit respective to desired option :");
			byte userchoice = myInput.nextByte();
			myInput.nextLine();
			switch (userchoice) {
			case 1: // 1. Add product/s
				List<Product> products = new ArrayList();
				boolean continueToAdd=true;
				do {
					Product product = new Product();
					System.out.print("Enter product ID :");
					product.setId(myInput.nextInt());
					myInput.nextLine();
					System.out.print("Enter product name :");
					product.setProductName(myInput.nextLine());
					System.out.print("Enter product price :");
					product.setPrice(myInput.nextDouble());
					myInput.nextLine();
					System.out.print("Enter product quantity :");
					int quantity = myInput.nextInt();
					myInput.nextLine();
					product.setQuantity(quantity);
					if(quantity>0) {
						//set availability true
						product.setAvailability(true);
					} else {
						//set availability false
						product.setAvailability(false);
					}
					products.add(product);
					System.out.println("Continue to add product ? y/n :");
					String tocontinue = myInput.nextLine();
					if(tocontinue.equalsIgnoreCase("n")) {
						continueToAdd = false;
					}
				} 
				while (continueToAdd);
//				for(Product product2 : products) {
//					System.out.println(product2);
//				}
				controller.addProducts(shop, products);
				System.out.println("Product Added");
				break;
			
			case 2://fetch product
				ResultSet productResultSet = controller.FetchAllProducts();				
				if (productResultSet == null) {
					System.out.println("No Product exist, No remove operation can be performed");
				} else {
					System.out.println("Available Products in shop :");
					System.out.println("_________________________");
					System.out.println("| id | product name | Price | Quantity | Availblility");
				try {
					while(productResultSet.next()) {
						System.out.println(productResultSet.getInt(1)+"        ");
						System.out.println(productResultSet.getString(2)+"        ");
						System.out.println(productResultSet.getDouble(3)+"        ");
						System.out.println(productResultSet.getInt(4)+"        ");
						System.out.println(productResultSet.getBoolean(5)+"        ");
					}
					System.out.println("``````````````````````````````````");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
			case 3: //update Product
				ResultSet fetchAllProduct = controller.FetchAllProducts();
				if(fetchAllProduct == null) {
					System.out.println("No product exist");
				} else {
					System.out.println("id | Product Name");
					try {
						while (fetchAllProduct.next())	;
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
			} 
		}while(true);

	}
}

