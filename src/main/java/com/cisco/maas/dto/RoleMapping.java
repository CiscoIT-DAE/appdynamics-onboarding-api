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

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class contains RoleMapping details and its setters and getters.
  */
@Document
public class RoleMapping implements Serializable{	
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String ctrlName;
	private String appGroupName;
	private String adminGroupName;
	private String viewGroupName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCtrlName() {
		return ctrlName;
	}
	public void setCtrlName(String ctrlName) {
		this.ctrlName = ctrlName;
	}
	public String getAppGroupName() {
		return appGroupName;
	}
	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}
	public String getAdminGroupName() {
		return adminGroupName;
	}
	public void setAdminGroupName(String adminGroupName) {
		this.adminGroupName = adminGroupName;
	}
	public String getViewGroupName() {
		return viewGroupName;
	}
	public void setViewGroupName(String viewGroupName) {
		this.viewGroupName = viewGroupName;
	}
	
}
