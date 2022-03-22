package com.cisco.maas.dto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDOnboardingErrorTest {
	@InjectMocks
	ApplicationOnboardingError applicationOnboardingError;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void allPojoTest() {
		ApplicationOnboardingError request = new ApplicationOnboardingError();
		request.setMessage("test");
		request.setCode("456");
		request.getMessage();
		request.getCode();
	}

}
