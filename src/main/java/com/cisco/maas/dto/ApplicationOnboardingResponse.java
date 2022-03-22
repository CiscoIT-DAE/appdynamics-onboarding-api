package com.cisco.maas.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * This class contains Onboarding appD application response properties and its setters and getters.
  */
public class ApplicationOnboardingResponse {
	
	@NotNull(message = "This field cannot be null")
	private String id;
	@NotNull(message = "This field cannot be null")
	private String apmApplicationGroupName;
	@NotNull(message = "This field cannot be null")
	private List<String> alertAliases;
	@NotNull(message = "This field cannot be null")
	private List<String> adminUsers;
	@NotNull(message = "This field cannot be null")
	private List<String> viewUsers;
	@NotNull(message = "This field cannot be null")
	private int apmLicenses;
	private List<String> eumApplicationGroupNames;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getApmApplicationGroupName() {
		return apmApplicationGroupName;
	}

	public void setApmApplicationGroupName(String apmApplicationGroupName) {
		this.apmApplicationGroupName = apmApplicationGroupName;
	}

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

	@Override
	public String toString() {
		return "ApplicationOnboardingRequest [ apmApplicationGroupName = " + apmApplicationGroupName
				+ ", adminUsers=" + adminUsers
				+ ", viewUsers=" + viewUsers + ", apmLicenses=" + apmLicenses + ", eumApplicationGroupNames="
				+ eumApplicationGroupNames + "]";
	}

}
