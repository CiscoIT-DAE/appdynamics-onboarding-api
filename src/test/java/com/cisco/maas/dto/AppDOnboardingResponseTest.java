package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;



import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDOnboardingResponseTest {
	@InjectMocks
	ApplicationOnboardingResponse applicationOnboardingResponse;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	
	@Test
	public void allPojoTest() {
		ApplicationOnboardingResponse request = new ApplicationOnboardingResponse();
		request.setId("123");
		request.setApmApplicationGroupName("Test");
		List<String> mappingList = new ArrayList<>();
		request.setAlertAliases(mappingList);
		request.setAdminUsers(mappingList);		
		request.setViewUsers(mappingList);
		request.setApmLicenses(1);
		request.setEumApplicationGroupNames(mappingList);
		request.getId();
		request.getApmApplicationGroupName();
		request.getAlertAliases();
		request.getAdminUsers();
		request.getViewUsers();
		request.getApmLicenses();
		request.getEumApplicationGroupNames();
		request.toString();
		
		
	}

}
