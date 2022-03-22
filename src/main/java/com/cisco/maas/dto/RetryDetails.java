package com.cisco.maas.dto;

import java.io.Serializable;
import java.util.List;

/**
 * This class contains appd application retry details and its setters and getters.
  */
public class RetryDetails implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String failureModule;
	private int operationCounter;
	private String lastRetryTime;
	private RoleMapping mapping;
	private String appDApplicationID;
	private String targetApplicationID;
	private String adminUsers;
	private String viewUsers;
	private List<RoleMapping> mappingList;
	private List<String> pendingList;
	private String dbFlagRollback;
	private String adminGroupRollback;

	public String getFailureModule() {
		return failureModule;
	}

	public void setFailureModule(String failureModule) {
		this.failureModule = failureModule;
	}

	public int getOperationCounter() {
		return operationCounter;
	}

	public void setOperationCounter(int operationCounter) {
		this.operationCounter = operationCounter;
	}

	public String getLastRetryTime() {
		return lastRetryTime;
	}

	public void setLastRetryTime(String lastRetryTime) {
		this.lastRetryTime = lastRetryTime;
	}

	public RoleMapping getMapping() {
		return mapping;
	}

	public void setMapping(RoleMapping mapping) {
		this.mapping = mapping;
	}

	public String getAppDApplicationID() {
		return appDApplicationID;
	}

	public void setAppDApplicationID(String appDApplicationID) {
		this.appDApplicationID = appDApplicationID;
	}

	public String getTargetApplicationID() {
		return targetApplicationID;
	}

	public void setTargetApplicationID(String targetApplicationID) {
		this.targetApplicationID = targetApplicationID;
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

	public List<RoleMapping> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<RoleMapping> mappingList) {
		this.mappingList = mappingList;
	}

	public List<String> getPendingList() {
		return pendingList;
	}

	public void setPendingList(List<String> pendingList) {
		this.pendingList = pendingList;
	}

	public String getDbFlagRollback() {
		return dbFlagRollback;
	}

	public void setDbFlagRollback(String dbFlagRollback) {
		this.dbFlagRollback = dbFlagRollback;
	}

	public String getAdminGroupRollback() {
		return adminGroupRollback;
	}

	public void setAdminGroupRollback(String adminGroupRollback) {
		this.adminGroupRollback = adminGroupRollback;
	}

	@Override
	public String toString() {
		return "RetryDetails [failureModule=" + failureModule + ", operationCounter=" + operationCounter
				+ ", lastRetryTime=" + lastRetryTime + "]";
	}

}
