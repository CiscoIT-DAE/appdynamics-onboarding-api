package com.cisco.maas.dto;

import java.util.List;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * This class contains onboarded appD application properties and its setters and getters. 
  */
@Document
public class AppDOnboardingRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String appdExternalId;
	private String requestType;
	private String requestCreatedDate;
	private String requestStatus;
	private String operationalStatus;
	private RequestDetails requestDetails;
	private RetryDetails retryDetails;
	private String requestModifiedDate;
	private String licenseKey;
	private List<RoleMapping> mapping;
	private String appGroupID;
	private int retryCount;
	private int requestCounter;
	private int rollbackCounter;
	private boolean retryLock;
	private boolean isResourceMove;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppdExternalId() {
		return appdExternalId;
	}

	public void setAppdExternalId(String appdExternalId) {
		this.appdExternalId = appdExternalId;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestCreatedDate() {
		return requestCreatedDate;
	}

	public void setRequestCreatedDate(String requestCreatedDate) {
		this.requestCreatedDate = requestCreatedDate;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getOperationalStatus() {
		return operationalStatus;
	}

	public void setOperationalStatus(String operationalStatus) {
		this.operationalStatus = operationalStatus;
	}

	public RequestDetails getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(RequestDetails requestDetails) {
		this.requestDetails = requestDetails;
	}

	public RetryDetails getRetryDetails() {
		return retryDetails;
	}

	public void setRetryDetails(RetryDetails retryDetails) {
		this.retryDetails = retryDetails;
	}

	public String getRequestModifiedDate() {
		return requestModifiedDate;
	}

	public void setRequestModifiedDate(String requestModifiedDate) {
		this.requestModifiedDate = requestModifiedDate;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public String getAppGroupID() {
		return appGroupID;
	}

	public void setAppGroupID(String appGroupID) {
		this.appGroupID = appGroupID;
	}

	public List<RoleMapping> getMapping() {
		return mapping;
	}

	public void setMapping(List<RoleMapping> mapping) {
		this.mapping = mapping;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", appdExternalId=" + appdExternalId + ", requestType=" + requestType
				+ ", requestCreatedDate=" + requestCreatedDate + ", requestStatus=" + requestStatus
				+ ", operationalStatus=" + operationalStatus + ", requestDetails=" + requestDetails + ", retryDetails="
				+ retryDetails + ", requestModifiedDate=" + requestModifiedDate + ", licenseKey=" + licenseKey
				+ ", mapping=" + mapping + ", appGroupID=" + appGroupID + "]";
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isResourceMove() {
		return isResourceMove;
	}

	public void setResourceMove(boolean isResourceMove) {
		this.isResourceMove = isResourceMove;
	}

	public int getRollbackCounter() {
		return rollbackCounter;
	}

	public void setRollbackCounter(int rollbackCounter) {
		this.rollbackCounter = rollbackCounter;
	}

	public int getRequestCounter() {
		return requestCounter;
	}

	public void setRequestCounter(int requestCounter) {
		this.requestCounter = requestCounter;
	}

	public boolean isRetryLock() {
		return retryLock;
	}

	public void setRetryLock(boolean retryLock) {
		this.retryLock = retryLock;
	}

}
