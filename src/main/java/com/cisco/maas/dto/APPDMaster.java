package com.cisco.maas.dto;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * This class contains AppD application master properties and its setters and getters. 
  */

@Document
public class APPDMaster {
	@Id
	private String id;
	private String ctrlName;
	private int appGroupId;
	private String appGroupName; 
	private String licenseKey;
	private int apmLicenses;
	private int noOfEUMLicenses;	
	private List<String> eumApps;
	private String alertAliases;	
	
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
	public int getAppGroupId() {
		return appGroupId;
	}
	public void setAppGroupId(int appGroupId) {
		this.appGroupId = appGroupId;
	}
	public String getAppGroupName() {
		return appGroupName;
	}
	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}
	public String getLicenseKey() {
		return licenseKey;
	}
	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
	public int getApmLicenses() {
		return apmLicenses;
	}
	public void setApmLicenses(int apmLicenses) {
		this.apmLicenses = apmLicenses;
	}

	public int getNoOfEUMLicenses() {
		return noOfEUMLicenses;
	}
	public void setNoOfEUMLicenses(int noOfEUMLicenses) {
		this.noOfEUMLicenses = noOfEUMLicenses;
	}
	
	public List<String> getEumApps() {
		return eumApps;
	}
	public void setEumApps(List<String> eumApps) {
		this.eumApps = eumApps;
	}
	
	public String getAlertAliases() {
		return alertAliases;
	}
	public void setAlertAliases(String alertAliases) {
		this.alertAliases = alertAliases;
	}
	@Override
	public String toString() {
		return "APPDMaster [id=" + id + ", ctrlName=" + ctrlName + ", appGroupId=" + appGroupId
				+ ", appGroupName=" + appGroupName + ", licenseKey=" + licenseKey
				+ ", apmLicenses=" + apmLicenses + ", noOfEUMLicenses=" + noOfEUMLicenses 
				+ ", EUMApps=" + eumApps + ", alertAliases=" + alertAliases + "]";
	}
}
