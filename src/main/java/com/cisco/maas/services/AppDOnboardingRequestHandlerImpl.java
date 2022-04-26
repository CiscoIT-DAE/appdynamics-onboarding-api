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
