package com.toptaltest.restapitest.tests;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;

public class ProductTest extends BaseTest{

	private static Logger logger = LoggerFactory.getLogger(ProductTest.class);
	
	public ProductTest() throws IOException {
		baseUri = super.baseUri + "/PRODUCT";
	}
	
	/**
	 * Used for preparing test data for this class
	 */
	@BeforeClass(alwaysRun = true, description = "Preparing data for the tests")
	public void prepareProductsData(){
		
		/*
		 * Since deletion and addition is done through API too, 
		 * I'm not checking the status of deletion because I don't want to rely 
		 * on the product I test.
		 * In realcase scenario, this deletion (or data preparation) should be done through DB
	 	*/
		
		Object[][] o = nonexistingProductIDs();

		// first delete products that should not exist for this test
		for(Object[] o1 : o) {
			RestAssured.delete(baseUri + "/" + o1[0]);
		}
		
		// also delete objects that will be created during this test
		o = productsToBeCreatedInTestDB();
		for(Object[] o1 : o) {
			RestAssured.delete(baseUri + "/" + o1[0]);
		}
		
		// now add products that should exist for this test
		o = existingProductIDs();
		for (Object o1[] : o) {
			RestAssured
			.given()
				.contentType("application/xml")
				.body("<resource><NAME>Iron Iron</NAME><PRICE>8.5</PRICE></resource>")
			.when().put(baseUri + "/" + o1[0]);
		}
	}
	
	
	/**
	 * TestNG dataprovider method used to provide existing product IDs
	 * @return Existing product IDs in format required for TestNG dataproviders
	 */
	@DataProvider(name = "existingProductIDsDP") 
	public Object[][] existingProductIDs() {
		/*
		 * In real case scenario, here would probably go a connection to DB 
		 * and gathering of all existing product ids. 
		 * For test project, we will use some predefined ones
		 * Format is {productIS, expected status code}
		 */
		Object[][] ids = {{1},{2},{3}};
		return ids;
	}
	
	/**
	 * TestNG dataprovider method used to provide non-existing product IDs
	 * @return Non Existing product IDs in format required for TestNG dataproviders
	 */
	@DataProvider(name = "nonexistingProductIDsDP") 
	public Object[][] nonexistingProductIDs() {
		/*
		 * In real case scenario, here would probably go a connection to DB 
		 * and gathering of all nonexisting product ids. 
		 * For test project, we will use some predefined ones
		 */
		Object[][] ids = {{333653777}};
		return ids;
	}
	
	/**
	 * TestNG dataprovider method used to provide IDs of products we want to create in test
	 * @return Product IDs in format required for TestNG dataproviders
	 */
	@DataProvider(name = "productsToBeCreatedInTestDB") 
	public Object[][] productsToBeCreatedInTestDB() {
		/*
		 * In real case scenario, here would probably go a connection to DB 
		 * and gathering of all nonexisting product ids. 
		 * For test project, we will use some predefined ones
		 */
		Object[][] ids = {{995664}};
		return ids;
	}
	
	
	/**
	 * GET /PRODUCT should return Status 200 OK
	 */
	@Test(groups = {"P1"}, description = "Testing GET /PRODUCT for status 200 OK")
	public void productGetTest() {
		RestAssured
				.when().get(baseUri)
				.then().statusCode(200); // extract the response
		
	}
	
	/**
	 * GET /PRODUCT/[existing product ID] should return Status 200 OK
	 * @param productID ID of the product
	 */
	@Test(groups = {"P1"}, 
			description = "Testing GET /PRODUCT/[existing product ID] for status 200 OK",
			dataProvider = "existingProductIDsDP")
	public void existingProductGetTest(int productID) {
		RestAssured
				.when().get(baseUri + "/" + productID)
				.then().statusCode(200);
		
	}
	
	/**
	 * GET /PRODUCT/[non-existing product ID] should return Status 404 Not Found
	 * @param productID ID of the product
	 */
	@Test(groups = {"P2"}, 
			description = "Testing GET /PRODUCT/[non-existing product ID] for status 404 Not Found",
			dataProvider = "nonexistingProductIDsDP")
	public void nonexistingProductGetTest(int productID) {
		RestAssured
				.when().get(baseUri + "/" + productID)
				.then().statusCode(404);
		
	}
	
	/**
	 * POST /PRODUCT/[non-existing product ID] should return Status 404 Not Found
	 * @param productID ID of the product
	 */
	@Test(groups = {"P2"}, 
			description = "Testing POST /PRODUCT/[non-existing product ID] for status 404 Not Found",
			dataProvider = "nonexistingProductIDsDP")
	public void nonexistingProductPostTest(int productID) {
		RestAssured
				.given()
					.contentType("application/xml")
					.body("<resource><PRICE>34</PRICE></resource>")
				.when().post(baseUri + "/" + productID)
				.then().statusCode(404);
		
	}
	
	/**
	 * POST /PRODUCT/[existing product ID] with invalid content should return Status 400 Bad Request
	 * @param productID ID of the product
	 */
	@Test(groups = {"P1"}, 
			description = "Testing POST /PRODUCT/[existing product ID] for status 400 Bad Request",
			dataProvider = "existingProductIDsDP")
	public void existingProductPostTest_InvalidContent(int productID) {
		RestAssured
				.given()
					.contentType("application/json")
					.body("{lastId: 1,count: 2}")
				.when().post(baseUri + "/" + productID)
				.then().statusCode(400);
		
	}
	
	/**
	 * POST /PRODUCT/[existing product ID] with invalid resource should return Status 400 Bad Request
	 */
	@Test(groups = {"P1"}, 
			description = "Testing POST /PRODUCT/[existing product ID] for status 400 Bad Request")
	public void existingProductPostTest_InvalidResourceName() {
		
		int id = (Integer) this.existingProductIDs()[0][0];
		
		RestAssured
				.given()
					.contentType("application/xml")
					.body("<resource><BLAHBLAH>3444444444444</BLAHBLAH></resource>")
				.when().post(baseUri + "/" + id)
				.then().statusCode(400);
		
	}
	
	/**
	 * PUT /PRODUCT/[non-existing product ID] should return Status 201 Created
	 * @param productID ID of the product
	 */
	@Test(groups = {"P1"}, 
			description = "Testing PUT /PRODUCT/[non-existing product ID] for status 201 Created",
			dataProvider = "productsToBeCreatedInTestDB")
	public void nonexistingProductPutTest(int productID) {
		RestAssured
				.given()
					.contentType("application/xml")
					.body("<resource>"
							+ "<NAME>Some name for " + productID + "</NAME>"
							+ "<PRICE>34</PRICE>"
						+ "</resource>")
				.when().put(baseUri + "/" + productID)
				.then().statusCode(201);
		
		// quick check if the product is there
		RestAssured
		.when().get(baseUri + "/" + productID)
		.then().statusCode(200);
		
	}
	
	@AfterClass(alwaysRun=true, description = "cleaning up data created during this test")
	public void afterClass(){
		// clean up products created during this test
		Object[][] o = productsToBeCreatedInTestDB();
		for(Object[] o1 : o) {
			RestAssured.delete(baseUri + "/" + o1[0]);
		}
	}
}