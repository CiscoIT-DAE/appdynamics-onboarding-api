package com.cisco.maas.dto;

import org.json.JSONObject;

/**
 * This class contains license rule properties and its setters and getters.
  */
public class LicenseRule {
	private String licenseRuleName;
	private String noOfApmLicenses;
	private String noOfMALicenses;
	private String accessKey;
	private JSONObject json;

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public String getLicenseRuleName() {
		return licenseRuleName;
	}

	public void setLicenseRuleName(String licenseRuleName) {
		this.licenseRuleName = licenseRuleName;
	}

	public String getNoOfApmLicenses() {
		return noOfApmLicenses;
	}

	public void setNoOfApmLicenses(String noOfApmLicenses) {
		this.noOfApmLicenses = noOfApmLicenses;
	}

	public String getNoOfMALicenses() {
		return noOfMALicenses;
	}

	public void setNoOfMALicenses(String noOfMALicenses) {
		this.noOfMALicenses = noOfMALicenses;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	@Override
	public String toString() {
		return "LicenseRule [licenseRuleName=" + licenseRuleName + ", noOfApmLicenses=" + noOfApmLicenses
				+ ", noOfMALicenses=" + noOfMALicenses + ", accessKey=" + accessKey + "]";
	}

}
