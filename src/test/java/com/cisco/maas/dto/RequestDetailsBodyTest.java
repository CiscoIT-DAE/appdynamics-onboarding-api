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
