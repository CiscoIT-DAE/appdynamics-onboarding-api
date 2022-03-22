package com.cisco.maas.dto;

/**
 * This class contains get/view appdynamics response properties and its setters and getters.
  */
public class ViewAppdynamicsResponse extends ApplicationOnboardingResponse {

	private String adminRoleName;
	private String viewRoleName;
	private String operation;
	private String status;
	private String licenseKey;

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdminRoleName() {
		return adminRoleName;
	}

	public void setAdminRoleName(String adminRoleName) {
		this.adminRoleName = adminRoleName;
	}

	public String getViewRoleName() {
		return viewRoleName;
	}

	public void setViewRoleName(String viewRoleName) {
		this.viewRoleName = viewRoleName;
	}
	
}
