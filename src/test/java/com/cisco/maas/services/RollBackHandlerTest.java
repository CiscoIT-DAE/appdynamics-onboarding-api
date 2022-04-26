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
import org.mockito.Spy;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;
@SuppressWarnings("unchecked")
public class RollBackHandlerTest {

	@InjectMocks
	@Spy
	RollBackHandler rollBackHandler;
	@Mock
	AppDLicensesHandler licenseHandler;
	@Mock
	AppDAlertsHandler alertHandler;
	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;

	@Mock
	AppDRoleManager roleManager;

	@Mock
	RequestDAO mcmpRequestDao;
	@Mock
	DBHandler dbHandler;
	@Mock
	DBOperationHandler operationHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void rollbackForCreate() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		rDetails.setFailureModule("AD_HANDLER");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test
	public void rollbackForCreateTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(3);
		rDetails.setFailureModule("AD_HANDLER");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test
	public void rollbackForCreate3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRollbackCounter(1);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		rDetails.setDbFlagRollback("success");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test
	public void rollbackForCreateExceptionTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRollbackCounter(1);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		rDetails.setDbFlagRollback("success");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test(expected = Exception.class)
	public void rollbackForCreateException() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRollbackCounter(1);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		rDetails.setDbFlagRollback("failed");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test(expected = Exception.class)
	public void rollbackForCreate4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("ALE_HANDLER");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForCreate(request);

	}

	@Test
	public void rollbackForUpdate() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		rDetails.setFailureModule("AD_HANDLER");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForUpdate(request);

	}

	@Test
	public void rollbackForUpdateTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(3);
		rDetails.setFailureModule("AD_HANDLER");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForUpdate(request);

	}

	@Test
	public void rollbackForUpdate3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRollbackCounter(1);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		rDetails.setDbFlagRollback("success");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForUpdate(request);

	}

	@Test(expected = Exception.class)
	public void rollbackForUpdateException() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRollbackCounter(1);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		rDetails.setDbFlagRollback("failed");
		request.setRetryDetails(rDetails);
		rollBackHandler.rollbackForUpdate(request);

	}

	@Test
	public void handleRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("rollbackError");
		rollBackHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("rollbackfailed");
		request.setRequestType("create");
		Mockito.doReturn(true).when(mcmpRequestDao).updateRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(rollBackHandler).rollbackForCreate(any(AppDOnboardingRequest.class));
		rollBackHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("rollbackfailed");
		request.setRequestType("update");
		Mockito.doReturn(true).when(mcmpRequestDao).updateRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(rollBackHandler).rollbackForUpdate(any(AppDOnboardingRequest.class));
		rollBackHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest3() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");
		Mockito.doThrow(new AppDOnboardingException("error message")).when(rollBackHandler)
				.rollbackForCreate(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(mcmpRequestDao).updateRequest(any(AppDOnboardingRequest.class));
		rollBackHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest4() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("rollbackfailed");
		request.setRequestType("create");
		Mockito.doThrow(new AppDOnboardingException("unable to connect")).when(rollBackHandler)
				.rollbackForCreate(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(mcmpRequestDao).updateRequest(any(AppDOnboardingRequest.class));
		rollBackHandler.handleRequest(request);
	}

	@Test
	public void checkIfAppDeletedUpdateTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rDetails = new RequestDetails();
		rDetails.setCtrlName("cisco1nonprod");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		rDetails.setAddEumpApps(eumApps);
		request.setRequestDetails(rDetails);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		rollBackHandler.checkIfAppDeletedUpdate(request);
	}

	@Test(expected = Exception.class)
	public void checkIfAppDeletedUpdateTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rDetails = new RequestDetails();
		rDetails.setCtrlName("cisco1nonprod");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		rDetails.setAddEumpApps(eumApps);
		request.setRequestDetails(rDetails);
		Mockito.doReturn(false).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class),
				any(String.class));
		rollBackHandler.checkIfAppDeletedUpdate(request);
	}

	@Test
	public void checkIfApplicationDeletedTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setOperationCounter(2);
		request.setRetryDetails(retryDetails);
		rollBackHandler.checkIfApplicationDeleted(request, true);
	}

	@Test
	public void checkIfApplicationDeletedTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setOperationCounter(5);
		request.setRetryDetails(retryDetails);
		RequestDetails rDetails = new RequestDetails();
		rDetails.setCtrlName("cisco1nonprod");
		rDetails.setAppGroupName("testApp");
		request.setRequestDetails(rDetails);
		Mockito.doReturn(true).when(operationHandler).checkIfAPMApplicationNotExist(any(String.class),
				any(String.class));
		rollBackHandler.checkIfApplicationDeleted(request, true);
	}

	@Test
	public void checkIfApplicationDeletedTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setOperationCounter(8);
		request.setRetryDetails(retryDetails);
		RequestDetails rDetails = new RequestDetails();
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		eumApps.add("emu App1");
		rDetails.setEumApps(eumApps);
		rDetails.setCtrlName("cisco1nonprod");
		rDetails.setAppGroupName("testApp");
		request.setRequestDetails(rDetails);
		Mockito.doReturn(true).when(operationHandler).checkIfAPMApplicationNotExist(any(String.class),
				any(String.class));

		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		rollBackHandler.checkIfApplicationDeleted(request, true);
	}

	@Test(expected = AppDOnboardingException.class)
	public void checkIfApplicationDeletedTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setOperationCounter(8);
		request.setRetryDetails(retryDetails);
		RequestDetails rDetails = new RequestDetails();
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		eumApps.add("emu App1");
		rDetails.setEumApps(eumApps);
		rDetails.setCtrlName("cisco1nonprod");
		rDetails.setAppGroupName("testApp");
		request.setRequestDetails(rDetails);
		Mockito.doReturn(true).when(operationHandler).checkIfAPMApplicationNotExist(any(String.class),
				any(String.class));
		Mockito.doReturn(false).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class),
				any(String.class));
		rollBackHandler.checkIfApplicationDeleted(request, false);
	}
}
