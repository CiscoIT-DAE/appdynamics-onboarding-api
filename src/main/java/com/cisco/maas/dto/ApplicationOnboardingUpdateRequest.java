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
