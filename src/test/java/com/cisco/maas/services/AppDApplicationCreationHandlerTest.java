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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;
@SuppressWarnings("unchecked")
public class AppDApplicationCreationHandlerTest {
	@InjectMocks
	@Spy
	AppDApplicationCreationHandler appDApplicationCreationHandler;

	@Mock
	APPDMasterDAO appdMasterDao;

	@Mock
	AppDynamicsUtil appdUtil;

	@Mock
	RestTemplate api;

	@Mock
	RequestHandler requestHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(appDApplicationCreationHandler).createApplication(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDApplicationCreationHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTestForUpdate() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		rdDetails.setAddEumpApps(addEUMapps);
		List<String> deleteEUMapps = new ArrayList<>();
		deleteEUMapps.add("Test2");
		rdDetails.setDeleteEumpApps(deleteEUMapps);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRequestType("update");
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(appDApplicationCreationHandler).updateEUMApplication(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDApplicationCreationHandler).deleteEUMApplication(any(List.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDApplicationCreationHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTestForUpdateEmpty() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		List<String> addEUMapps = new ArrayList<>();
		rdDetails.setAddEumpApps(addEUMapps);
		List<String> deleteEUMapps = new ArrayList<>();
		rdDetails.setDeleteEumpApps(deleteEUMapps);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRequestType("update");
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(appDApplicationCreationHandler).updateEUMApplication(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDApplicationCreationHandler).deleteEUMApplication(any(List.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDApplicationCreationHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTestForUpdateNull() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAddEumpApps(null);
		rdDetails.setDeleteEumpApps(null);
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRequestType("update");
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(appDApplicationCreationHandler).updateEUMApplication(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDApplicationCreationHandler).deleteEUMApplication(any(List.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDApplicationCreationHandler.handleRequest(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplicationTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1nonprod");
		rdDetails.setAppGroupName("Test");
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		rdDetails.setEumApps(eumList);
		request.setAppGroupID("12345");
		rDetails.setAppDApplicationID(" 2345");
		rDetails.setOperationCounter(1);
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getStatusCodeValue()).thenReturn(204);
		Mockito.when(appdUtil.setBTSettigsInAppDynamics("12345")).thenReturn(true);
		appDApplicationCreationHandler.createApplication(request);
	}

	@Test
	public void getAppIDTest() throws Exception {

		String content = "[{\"name\": \"APM-Java-Template\",\"id\": 2812350},{\"name\": \"APM-JavaTemplate\",\"id\": 2812350}]";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(content);
		appDApplicationCreationHandler.getAppID("cisco1nonprod", "APM-Java-Template");
	}

	@Test
	public void getAppID_responseIsNull() throws Exception {

		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(null);
		appDApplicationCreationHandler.getAppID("cisco1nonprod", "APM-Java-Template");
	}

	@Test
	public void updateEUMApplicationTest() throws Exception {

		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1nonprod");
		rdDetails.setAppGroupName("Test");
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		rdDetails.setAddEumpApps(addEUMapps);
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		assertNotNull(appDApplicationCreationHandler.updateEUMApplication(request));
	}

	@Test
	public void updateEUMApplicationTestOpCounterLessThanOne() throws Exception {

		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(0);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1nonprod");
		rdDetails.setAppGroupName("Test");
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		rdDetails.setAddEumpApps(addEUMapps);
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		assertNotNull(appDApplicationCreationHandler.updateEUMApplication(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateEUMApplicationTestException() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1nonprod");
		rdDetails.setAppGroupName("Test");
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		rdDetails.setAddEumpApps(addEUMapps);
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);
		request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
		AppDOnboardingException e = new AppDOnboardingException(
				"AppDConfigExporterHandler - updateEUMApplication - EUM application already exists need manual intervention",
				request);
		Mockito.doThrow(e).when(appDApplicationCreationHandler).createEUMApplication(any(AppDOnboardingRequest.class),
				any(Integer.class), any(List.class));
		assertNotNull(appDApplicationCreationHandler.updateEUMApplication(request));
	}

	@Test
	public void checkIfRenameIsCompleteTest() throws Exception {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}

	@Test
	public void checkIfRenameIsCompleteTest2() throws Exception {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		Mockito.doReturn("id=67").when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(ctrlName, "Test1");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}

	@Test
	public void checkIfRenameIsCompleteTest3() throws Exception {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		Mockito.doReturn("id=67").when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		Mockito.doReturn("id=67").when(appDApplicationCreationHandler).getAppID(ctrlName, "Test1");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}

	@Test
	public void checkIfRenameIsCompleteTest4() throws Exception {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = null;
		Mockito.doReturn("id=67").when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		Mockito.doReturn("id=67").when(appDApplicationCreationHandler).getAppID(ctrlName, "Test1");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}

	@Test(expected = Exception.class)
	public void checkIfRenameIsCompleteTest5() throws Exception {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		Mockito.doThrow(new Exception()).when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createEUMApplicationTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		List<String> eumList = new ArrayList<String>();
		AppDOnboardingException e = new AppDOnboardingException(
				"\"AppDConfigExporterHandler - updateEUMApplication - EUM application already exists need manual intervention",
				request);
		eumList.add("Test1");
		eumList.add("Test2");
		Mockito.doThrow(e).when(appdUtil).createApplicationInAppDynamics(any(String.class), any(String.class));
		assertNotNull(appDApplicationCreationHandler.createEUMApplication(request, 1, eumList));
	}

	@Test(expected = AppDOnboardingException.class)
	public void checkIfRenameIsCompleteTestIOException() throws IOException, AppDOnboardingException {
		String appGroupName = "APM-n";
		String ctrlName = "ciscoeft";
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		Mockito.doThrow(new IOException()).when(appDApplicationCreationHandler).getAppID(ctrlName, "APM-n");
		appDApplicationCreationHandler.checkIfRenameIsComplete(appGroupName, ctrlName, eumList);
	}
}
