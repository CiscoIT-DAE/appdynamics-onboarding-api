package com.cisco.maas.services;


import org.springframework.stereotype.Service;

import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
/**
 * This class is to handle current handler and set next handler.
 * */


@Service
public abstract class AppDOnboardingRequestHandlerImpl {	

	protected AppDOnboardingRequestHandlerImpl handler;

	public void setNextHandler(AppDOnboardingRequestHandlerImpl handler) {
		this.handler = handler;
	}

	public abstract void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException;

	public final void handleRequestImpl(AppDOnboardingRequest request) throws AppDOnboardingException {
		this.handler.handleRequest(request);
	}

}
