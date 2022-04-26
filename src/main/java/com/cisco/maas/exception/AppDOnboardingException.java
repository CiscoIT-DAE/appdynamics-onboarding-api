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

package com.cisco.maas.exception;

import com.cisco.maas.dto.AppDOnboardingRequest;

/**
 * This class contains appd application onboarding exception details.
  */
public class AppDOnboardingException extends Exception{	

	private static final long serialVersionUID = 1L;	
	private final AppDOnboardingRequest request ;
	private final String actualMessage;
	private final String reqAction;
	private final Throwable error;
	
	
	/**
	 * Initializing exception properties.
	 */
	public AppDOnboardingException(String errorMessage) {
		  super(errorMessage);
		  this.actualMessage=errorMessage;	 
		  this.request=null;
		  this.reqAction=null;
		  this.error=null;
	}
	
	/**
	 * This method creates AppDOnboardingException with the given error message and request object.
	 * @param String type errorMessage.
	 * @param AppDOnboardingRequest type request.
	 * @returns AppDOnboardingException.
	 */
	public AppDOnboardingException(String errorMessage, AppDOnboardingRequest request)
	{
		super(errorMessage);
		this.actualMessage=errorMessage;
		this.request=request;
		 this.reqAction=null;
		 this.error=null;
	}
	
	/**
	 * This method creates AppDOnboardingException with the given error message , request object and throwable error.
	 * @param String type errorMessage.
	 * @param AppDOnboardingRequest type request.
	 * @param Throwable type err.
	 * @returns AppDOnboardingException.
	 */
	
	public AppDOnboardingException(String errorMessage, AppDOnboardingRequest request, Throwable err)
	{
		super(errorMessage);
		this.actualMessage=errorMessage;
		this.request=request;
		error=err;
		this.reqAction=null;
	}
	  
	
	
    public AppDOnboardingRequest getRequest() {
		return request;
	}
    
    /**
	 * This method creates AppDOnboardingException with the given error message and throwable error.
	 * @param String type errorMessage.
	 * @param Throwable type err.
	 * @returns AppDOnboardingException.
	 */

	public AppDOnboardingException(String errorMessage, Throwable err) {
      super(errorMessage);
      this.actualMessage=errorMessage;
      this.request=null;
      this.reqAction=null;
      this.error=null;
    } 
	
	public String getReqAction() {
		return reqAction;
	}
	
	public String getActualMessage() {
		return actualMessage;
	}

	public Throwable getError() {
		return error;
	}

}

