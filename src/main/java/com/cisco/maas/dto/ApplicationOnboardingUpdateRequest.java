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

import java.util.List;


/**
 * This class contains AppD application Update request properties and its setters and getters.
  */
public class ApplicationOnboardingUpdateRequest {
	
	// @NotNull(message = "This field cannot be null")
	private List<String> alertAliases;
	// @NotNull(message = "This field cannot be null")
	private List<String> adminUsers;
	// @NotNull(message = "This field cannot be null")
	private List<String> viewUsers;
	// @NotNull(message = "This field cannot be null")
	private int apmLicenses;
	private List<String> eumApplicationGroupNames;


	public List<String> getAlertAliases() {
		return alertAliases;
	}

	public void setAlertAliases(List<String> alertAliases) {
		this.alertAliases = alertAliases;
	}

	public List<String> getAdminUsers() {
		return adminUsers;
	}

	public void setAdminUsers(List<String> adminUsers) {
		this.adminUsers = adminUsers;
	}

	public List<String> getViewUsers() {
		return viewUsers;
	}

	public void setViewUsers(List<String> viewUsers) {
		this.viewUsers = viewUsers;
	}

	public int getApmLicenses() {
		return apmLicenses;
	}

	public void setApmLicenses(int apmLicenses) {
		this.apmLicenses = apmLicenses;
	}

	public List<String> getEumApplicationGroupNames() {
		return eumApplicationGroupNames;
	}

	public void setEumApplicationGroupNames(List<String> eumApplicationGroupNames) {
		this.eumApplicationGroupNames = eumApplicationGroupNames;
	}

	public String toString() {
		return "ApplicationOnboardingRequest [ adminUsers=" + adminUsers
				+ ", viewUsers=" + viewUsers + ", apmLicenses=" + apmLicenses + ", eumApplicationGroupNames="
				+ eumApplicationGroupNames + "]";
	}

}
