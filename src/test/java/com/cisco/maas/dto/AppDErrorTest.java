package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDErrorTest {

	@InjectMocks
	AppDError appDError;

	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void allPojoTest()
	{
		appDError.setCode(100);
		appDError.setMsg("test");
		appDError.setRetry("test");
		appDError.setType("test");
		appDError.getCode();
		appDError.getMsg();
		appDError.getRetry();
		appDError.getType();
	}
}
