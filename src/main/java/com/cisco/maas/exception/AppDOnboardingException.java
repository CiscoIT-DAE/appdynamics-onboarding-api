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

