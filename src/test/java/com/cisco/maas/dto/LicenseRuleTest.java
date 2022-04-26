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

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class LicenseRuleTest {
	@InjectMocks
	LicenseRule rule;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		rule.setAccessKey("Test");
		rule.setJson(new JSONObject("{\"Test\":\"test\"}"));
		rule.setLicenseRuleName("Test Lic");
		rule.setNoOfApmLicenses("1");
		rule.setNoOfMALicenses("2");
		rule.getAccessKey();
		rule.getJson();
		rule.getLicenseRuleName();
		rule.getNoOfApmLicenses();
		rule.getNoOfMALicenses();
		rule.toString();
	}

}
