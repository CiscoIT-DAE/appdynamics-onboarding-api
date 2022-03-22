package com.cisco.maas.configuration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class MongoConfigTest {
	
	
	@InjectMocks
	MongoConfig mongoConfig;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void getJavaMailSenderTest() throws Exception {
		mongoConfig.mongoTemplate();
	}

}
