package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class RequestDetailsBodyTest {

	@InjectMocks
	ApplicationOnboardingRequest rDetails;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	 }
	
	@Test
	public void allPojoTest() {
		
		List<String> testData = new ArrayList<>();
		rDetails.setAdminUsers(testData);
		rDetails.setAlertAliases(testData);
		rDetails.setApmApplicationGroupName("test");
		rDetails.setEumApplicationGroupNames(testData);
		rDetails.setApmLicenses(1);
		rDetails.setViewUsers(testData);
		rDetails.getAdminUsers();
		rDetails.getAlertAliases();
		rDetails.getApmApplicationGroupName();
		rDetails.getEumApplicationGroupNames();
		rDetails.getApmLicenses();
		rDetails.getViewUsers();
		rDetails.toString();
	}
}
