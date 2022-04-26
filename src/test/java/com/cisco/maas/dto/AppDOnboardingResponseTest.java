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
