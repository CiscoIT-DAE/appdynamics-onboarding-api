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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class contains EUM application metadata properties and its setters and getters.
  */

@Document
public class EUMMetaData {

	@Id
	private String id;
	private String appdProjectId;
	private String eumName;
	private String eumCreatedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppdProjectId() {
		return appdProjectId;
	}

	public void setAppdProjectId(String appdProjectId) {
		this.appdProjectId = appdProjectId;
	}

	public String getEumName() {
		return eumName;
	}

	public void setEumName(String eumName) {
		this.eumName = eumName;
	}

	public String getEumCreatedDate() {
		return eumCreatedDate;
	}

	public void setEumCreatedDate(String eumCreatedDate) {
		this.eumCreatedDate = eumCreatedDate;
	}

}
