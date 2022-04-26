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

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
//import com.cisco.maas.dto.MCMPRequest;
import com.cisco.maas.dto.RequestDetails;

public class RetryHandlerTest {

	@InjectMocks
	RetryHandler retryHandler;
	
	@Mock
	RequestDAO requestDao;
	
	@Mock
	ProcessRequest pr;

	@Mock
	RollBackHandler rollBackHandler;
	
	@Mock
	RequestDetails requestDetails;
	
	@Before
	 public void setup()
	 {		
		MockitoAnnotations.initMocks(this);		
	 }	

	
	@Test
	public void retryFailedRequestsTest() throws Exception
	{
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		AppDOnboardingRequest request1 = new AppDOnboardingRequest();
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		String trackingId = "23836128312637812";		
		requestDetails = new RequestDetails();		
		requestDetails.setTrackingId(trackingId);
		request.setRequestDetails(requestDetails);
		request1.setRequestDetails(requestDetails);
		request2.setRequestDetails(requestDetails);		
		request.setRequestType("create");
		request1.setRequestType("update");
		request2.setRequestType("delete");
		request.setRetryCount(2);
		request1.setRetryCount(2);
		request2.setRetryCount(2);
		requestList.add(request);
		requestList.add(request1);
		requestList.add(request2);
		Mockito.when(requestDao.findFailedRequests()).thenReturn(requestList);
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));		
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class)); 
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class)); 
		retryHandler.retryFailedRequests();
	}
	
	
	@Test
	public void retryFailedRequestsTest1() throws Exception
	{
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		AppDOnboardingRequest request1 = new AppDOnboardingRequest();
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		String trackingId = "23836128312637812";		
		requestDetails = new RequestDetails();		
		requestDetails.setTrackingId(trackingId);
		request.setRequestDetails(requestDetails);
		request1.setRequestDetails(requestDetails);
		request2.setRequestDetails(requestDetails);		
		request.setRequestType("create");
		request1.setRequestType("update");
		request2.setRequestType("delete");
		request.setRetryCount(2);
		request1.setRetryCount(2);
		request2.setRetryCount(2);
		requestList.add(request);
		requestList.add(request1);
		requestList.add(request2);
		Mockito.when(requestDao.findFailedRequests()).thenReturn(null);
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class)); 
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class)); 
		retryHandler.retryFailedRequests();
	}
	
	
	@Test
	public void retryFailedRequestsTest2() throws Exception
	{
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		AppDOnboardingRequest request1 = new AppDOnboardingRequest();
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		String trackingId = "23836128312637812";		
		requestDetails = new RequestDetails();		
		requestDetails.setTrackingId(trackingId);
		request.setRequestDetails(requestDetails);
		request1.setRequestDetails(requestDetails);
		request2.setRequestDetails(requestDetails);		
		request.setRequestType("create");
		request1.setRequestType("update");
		request2.setRequestType("delete");
		request.setRetryCount(2);
		request1.setRetryCount(2);
		request2.setRetryCount(2);
		requestList.add(request);
		requestList.add(request1);
		requestList.add(request2);
		Mockito.when(requestDao.findFailedRequests()).thenReturn(null);
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));		
		retryHandler.retryFailedRequests();
	}
	
	@Test
	public void createHandlerTest() throws Exception
	{  
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(5);	
		request.setRollbackCounter(1);
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		retryHandler.createHandler(request);		
	}

	@Test
	public void createHandlerTest1() throws Exception
	{  
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(109);	
		request.setRollbackCounter(1);
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		retryHandler.createHandler(request);		
	}
	
	@Test
	public void createHandlerTest2() throws Exception
	{  
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(109);	
		request.setRollbackCounter(37); 
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		retryHandler.createHandler(request);		
	}
	
	
	@Test
	public void updateHandlerTest() throws Exception
	{  
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(5);	
		request.setRollbackCounter(1);		
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));  
		retryHandler.updateHandler(request);		
	}	
	
	@Test
	public void updateHandlerTest1() throws Exception
	{  
		List<String> requestList = new ArrayList<String>();	
		requestList.add("Test1");
		requestList.add("Test2");
		requestDetails = new RequestDetails();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(109);	
		requestDetails.setAddEumpApps(requestList);
		request.setRequestDetails(requestDetails);
		request.setRollbackCounter(1);		
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));  
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		retryHandler.updateHandler(request);		
	}
	
	@Test
	public void updateHandlerTest2() throws Exception
	{  
		requestDetails = new RequestDetails();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryCount(109);	
		requestDetails.setAddEumpApps(null);
		request.setRequestDetails(requestDetails);
		request.setRollbackCounter(37);		
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));  
		Mockito.doNothing().when(rollBackHandler).handleRequest(any(AppDOnboardingRequest.class));
		retryHandler.updateHandler(request);		
	}

}

