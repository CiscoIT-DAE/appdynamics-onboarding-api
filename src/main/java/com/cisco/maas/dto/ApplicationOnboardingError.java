package com.cisco.maas.dto;


/**
 * This class contains ApplicationOnboarding Error properties and its setters and getters. 
  */

public class ApplicationOnboardingError {
	
	private String message;
	private String code;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}