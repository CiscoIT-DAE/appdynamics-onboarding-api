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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class EUMMetaDataTest {

	@InjectMocks
	EUMMetaData eUMMetaData;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		eUMMetaData.setAppdProjectId("test");
		eUMMetaData.setEumCreatedDate("test");
		eUMMetaData.setEumName("testApp");
		eUMMetaData.setId("1");
		eUMMetaData.getAppdProjectId();
		eUMMetaData.getEumCreatedDate();
		eUMMetaData.getEumName();
		eUMMetaData.getId();
		eUMMetaData.toString();
	}
}
