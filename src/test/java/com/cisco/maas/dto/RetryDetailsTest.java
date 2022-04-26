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

public class RetryDetailsTest {
	@InjectMocks
	RetryDetails rDetails;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		List<RoleMapping> mappingList = new ArrayList<>();
		List<String> mappingList1 = new ArrayList<>();
		rDetails.setFailureModule("Test");
		rDetails.getFailureModule();
		rDetails.setOperationCounter(1);
		rDetails.getOperationCounter();
		rDetails.setPendingList(mappingList1);
		rDetails.getPendingList();
		rDetails.setLastRetryTime("test");
		rDetails.getLastRetryTime();
		rDetails.setMappingList(mappingList);
		rDetails.getMappingList();
		rDetails.setAdminGroupRollback("test");
		rDetails.setDbFlagRollback("test");
		rDetails.getAdminGroupRollback();
		rDetails.getDbFlagRollback();
		rDetails.getPendingList();
		RoleMapping mapping = new RoleMapping();
		rDetails.setMapping(mapping);
		rDetails.getMapping();
		rDetails.setAppDApplicationID("1234");
		rDetails.getAppDApplicationID();
		rDetails.setTargetApplicationID("12345");
		rDetails.getTargetApplicationID();
		rDetails.setAdminUsers("test");
		rDetails.getAdminUsers();
		rDetails.setViewUsers("Test");
		rDetails.getViewUsers();
		rDetails.toString();

	}

}
