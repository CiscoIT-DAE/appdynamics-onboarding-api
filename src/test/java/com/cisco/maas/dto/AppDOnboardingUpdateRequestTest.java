package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDOnboardingUpdateRequestTest {
	@InjectMocks
	ApplicationOnboardingUpdateRequest applicationOnboardingUpdateRequest;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void allPojoTest() {
		ApplicationOnboardingUpdateRequest request = new ApplicationOnboardingUpdateRequest();
		List<String> mappingList = new ArrayList<>();
		request.setAlertAliases(mappingList);		
		request.setAlertAliases(mappingList);
		request.setAdminUsers(mappingList);		
		request.setViewUsers(mappingList);
		request.setApmLicenses(1);
		request.setEumApplicationGroupNames(mappingList);	
		request.getAlertAliases();
		request.getAdminUsers();
		request.getViewUsers();
		request.getApmLicenses();
		request.getEumApplicationGroupNames();
		request.toString();
		
	}

}
