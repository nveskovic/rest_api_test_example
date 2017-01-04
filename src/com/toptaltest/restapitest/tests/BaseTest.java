package com.toptaltest.restapitest.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;

import io.restassured.RestAssured;


public class BaseTest {

	private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
	
	private static Properties prop = new Properties();
	protected String baseUri = "";
	private final String defaultConfigPropertiesFile = "resources/config/test.properties";
	
	public BaseTest() throws IOException {
		loadProperties(defaultConfigPropertiesFile);
		baseUri = prop.getProperty("rest.api.url.base");
	}
	
	private void loadProperties(String path) throws FileNotFoundException, IOException {
		File configProperties = new File(defaultConfigPropertiesFile);
		if(!configProperties.exists() || !configProperties.isFile())
			logger.error("config-file does not exist or is not a file!");
		
		// load properties
		prop.load(new FileInputStream(defaultConfigPropertiesFile));
	}
	
	/**
	 * GET root / should return Status 200 OK
	 * <br>Prerequisite for all other tests.
	 */
	@BeforeTest(description = "Testing GET status code 200 for base URL as a prerequisite for all tests", 
			alwaysRun = true)
	private final void baseUrlTest_StatusCode200() {
		RestAssured
				.when().get(baseUri)
				.then().statusCode(200);
	}
}
