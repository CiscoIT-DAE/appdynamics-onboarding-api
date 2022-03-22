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
