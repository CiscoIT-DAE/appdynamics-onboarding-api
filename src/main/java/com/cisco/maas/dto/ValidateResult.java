package com.cisco.maas.dto;

import org.springframework.http.HttpStatus;
/**
 * This class contains validate result properties and its setters and getters.
  */
public class ValidateResult {

	private String validateResultStatus;
	private HttpStatus responseCode;
	private AppDError errorObject;
	private boolean resourceMoveFlag;

	public boolean isResourceMoveFlag() {
		return resourceMoveFlag;
	}

	public void setResourceMoveFlag(boolean resourceMoveFlag) {
		this.resourceMoveFlag = resourceMoveFlag;
	}

	public HttpStatus getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(HttpStatus responseCode) {
		this.responseCode = responseCode;
	}

	public AppDError getErrorObject() {
		return errorObject;
	}

	public void setErrorObject(AppDError errorObject) {
		this.errorObject = errorObject;
	}

	public String getValidateResultStatus() {
		return validateResultStatus;
	}

	public void setValidateResultStatus(String validateResultStatus) {
		this.validateResultStatus = validateResultStatus;
	}

	@Override
	public String toString() {
		return "ValidateResult [validateResultStatus=" + validateResultStatus + ", responseCode=" + responseCode
				+ ", errorObject=" + errorObject + "]";
	}

}
