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
import java.util.List;

import javax.validation.constraints.NotNull;


/**
 * This class contains appd application request details and its setters and getters.
  */
public class RequestDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String trackingId;
	private String appdProjectId;
	@NotNull(message = "This field cannot be null")
	private String ctrlName;
	@NotNull(message = "This field cannot be null")
	private String appGroupName;
	@NotNull(message = "This field cannot be null")
	private String alertAliases;
	@NotNull(message = "This field cannot be null")
	private String adminUsers;
	@NotNull(message = "This field cannot be null")
	private String viewUsers;
	@NotNull(message = "This field cannot be null")
	private int apmLicenses;
	private int noOfEUMLicenses;
	private List<String> eumApps;
	private List<String> addEumpApps;
	private List<String> deleteEumpApps;
	private String oldAppGroupName;
	private List<String> oldEumApps;

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
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

	public String getAlertAliases() {
		return alertAliases;
	}

	public void setAlertAliases(String alertAliases) {
		this.alertAliases = alertAliases;
	}

	public String getAdminUsers() {
		return adminUsers;
	}

	public void setAdminUsers(String adminUsers) {
		this.adminUsers = adminUsers;
	}

	public String getViewUsers() {
		return viewUsers;
	}

	public void setViewUsers(String viewUsers) {
		this.viewUsers = viewUsers;
	}

	public int getApmLicenses() {
		return apmLicenses;
	}

	public void setApmLicenses(int apmLicenses) {
		this.apmLicenses = apmLicenses;
	}

	public String getAppdProjectId() {
		return appdProjectId;
	}

	public void setAppdProjectId(String appdProjectId) {
		this.appdProjectId = appdProjectId;
	}

	public List<String> getEumApps() {
		return eumApps;
	}

	public void setEumApps(List<String> eumApps) {
		this.eumApps = eumApps;
	}

	public List<String> getAddEumpApps() {
		return addEumpApps;
	}

	public void setAddEumpApps(List<String> addEumpApps) {
		this.addEumpApps = addEumpApps;
	}

	public List<String> getDeleteEumpApps() {
		return deleteEumpApps;
	}

	public void setDeleteEumpApps(List<String> deleteEumpApps) {
		this.deleteEumpApps = deleteEumpApps;
	}

	public int getNoOfEUMLicenses() {
		return noOfEUMLicenses;
	}

	public void setNoOfEUMLicenses(int noOfEUMLicenses) {
		this.noOfEUMLicenses = noOfEUMLicenses;
	}

	public String getOldAppGroupName() {
		return oldAppGroupName;
	}

	public void setOldAppGroupName(String oldAppGroupName) {
		this.oldAppGroupName = oldAppGroupName;
	}

	public List<String> getOldEumApps() {
		return oldEumApps;
	}

	public void setOldEumApps(List<String> oldEumApps) {
		this.oldEumApps = oldEumApps;
	}

	@Override
	public String toString() {
		return "RequestDetails [trackingId=" + trackingId + ", appdProjectId=" + appdProjectId + ", ctrlName="
				+ ctrlName + ", appGroupName=" + appGroupName + ", adminUsers=" + adminUsers + ", viewUsers=" + viewUsers + ", apmLicenses="
				+ apmLicenses  + ", eumApps=" + eumApps + ", addEumpApps=" + addEumpApps + ", deleteEumpApps="
				+ deleteEumpApps + "]";
	}
}
