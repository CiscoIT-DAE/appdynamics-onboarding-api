package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class RequestDetailsTest {
	@InjectMocks
	AppDOnboardingRequest appDOnboardingRequest;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers("rpatta");
		requestDetails.setAlertAliases("rgundewa@cisco.com");
		requestDetails.setAppdProjectId("123");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setApmLicenses(10);
		requestDetails.setViewUsers("jjadav");
		requestDetails.setTrackingId("test-1234");
		requestDetails.setOldAppGroupName("TestApp");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldEumApps(eumApps);
		requestDetails.getOldEumApps();
		requestDetails.getOldAppGroupName();
		requestDetails.getAdminUsers();
		requestDetails.getAlertAliases();
		requestDetails.getAppdProjectId();
		requestDetails.getAppGroupName();
		requestDetails.getCtrlName();
		requestDetails.getEumApps();
		requestDetails.getApmLicenses();
		requestDetails.getViewUsers();
		requestDetails.getTrackingId();
		requestDetails.toString();
	}
}
