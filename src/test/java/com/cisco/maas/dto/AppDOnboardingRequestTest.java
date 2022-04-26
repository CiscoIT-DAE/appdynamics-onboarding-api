/*
 *  AppDynamics Onboarding APIs.
 *
 *  Copyright 2022 Cisco
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
