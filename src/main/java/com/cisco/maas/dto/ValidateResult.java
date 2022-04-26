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
