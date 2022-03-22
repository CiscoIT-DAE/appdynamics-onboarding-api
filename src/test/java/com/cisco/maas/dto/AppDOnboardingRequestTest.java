package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDOnboardingRequestTest {
	@InjectMocks
	AppDOnboardingRequest appDOnboardingRequest;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void allPojoTest() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("123");
		request.setAppGroupID("456");
		request.setId("123");
		request.setLicenseKey("adfghguyijkj123");
		request.setRetryCount(1);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping=new RoleMapping();
		mapping.setAdminGroupName("Test");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setOperationalStatus("1");
		request.setRequestCreatedDate("11-11-2020");
		RequestDetails requestDetails=new RequestDetails();
		request.setRequestDetails(requestDetails);
		request.setRequestModifiedDate("21-11-2020");
		request.setRequestStatus("success");
		request.setRequestType("create");
		RetryDetails retryDetails=new RetryDetails();
		request.setRetryDetails(retryDetails);
		request.setRequestCounter(1);
		request.setRollbackCounter(1);
		request.getRollbackCounter();
		request.getRequestCounter();
		request.getAppdExternalId();
		request.getAppGroupID();
		request.getAppGroupID();
		request.getId();
		request.getLicenseKey();
		request.getMapping();
		request.getOperationalStatus();
		request.getRequestCreatedDate();
		request.getRequestDetails();
		request.getRequestModifiedDate();
		request.getRequestStatus();
		request.getRequestType();
		request.getRetryDetails();
		request.toString();
		request.getRetryCount();
		
	}

}
