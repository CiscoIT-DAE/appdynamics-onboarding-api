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
