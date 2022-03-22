package com.cisco.maas.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class contains EUM application metadata properties and its setters and getters.
  */

@Document
public class EUMMetaData {

	@Id
	private String id;
	private String appdProjectId;
	private String eumName;
	private String eumCreatedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppdProjectId() {
		return appdProjectId;
	}

	public void setAppdProjectId(String appdProjectId) {
		this.appdProjectId = appdProjectId;
	}

	public String getEumName() {
		return eumName;
	}

	public void setEumName(String eumName) {
		this.eumName = eumName;
	}

	public String getEumCreatedDate() {
		return eumCreatedDate;
	}

	public void setEumCreatedDate(String eumCreatedDate) {
		this.eumCreatedDate = eumCreatedDate;
	}

}
