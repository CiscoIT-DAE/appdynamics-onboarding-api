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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.EUMMetaDataDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dao.RoleMappingDAO;
import com.cisco.maas.dto.EUMMetaData;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.exception.AppDOnboardingException;
import com.mongodb.MongoException;
@SuppressWarnings("unchecked")
public class DBHandlerTest {

	@InjectMocks
	@Spy
	DBHandler dBHandler;

	@Mock
	RequestDAO requestDao;

	@Mock
	APPDMasterDAO appDMasterDao;

	@Mock
	RoleMappingDAO RoleMappingDAO;

	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;

	@Mock
	EUMMetaDataDAO eUMMetaDataDAO;

	@Mock
	RequestHandler requestHandler;
	@Mock
	DBOperationHandler operationHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		Mockito.doReturn(true).when(dBHandler).checkRequestType(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("success");
		request.setRequestType("create");
		Mockito.doReturn(true).when(dBHandler).createApplication(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.doReturn(true).when(dBHandler).updateApplication(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("success");
		request.setRequestType("delete");
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest5() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("ERROR");
		request.setRequestType("delete");
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}

	@Test
	public void handleRequestResourceMoveRequest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("success");
		request.setRequestType("update");
		request.setResourceMove(true);
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateApplication(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
		verify(dBHandler, times(1)).handleRequest(request);
	}

	@Test
	public void handleRequestTestInvalidRequesType() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("success");
		request.setRequestType("xyz");
		Mockito.doNothing().when(dBHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(dBHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		dBHandler.handleRequest(request);
	}
	@Test(expected = AppDOnboardingException.class)
	public void handleRequestTestException() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		Mockito.doThrow(AppDOnboardingException.class).when(dBHandler).checkRequestType(request);
		dBHandler.handleRequest(request);
	}


	@Test
	public void createApplicationTest() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("nakmx-ancyr");
		request.setRetryDetails(rDetails);
		request.setRequestCreatedDate("10-12-2011");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.doReturn(true).when(operationHandler).persistAppDMetadata(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(operationHandler).persistMappings(any(List.class));
		Mockito.doReturn(true).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplication_invalidOpCounter() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(5);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryDetails(rDetails);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplication_opCounter1ResponseIsFalse() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("nakmx-ancyr");
		request.setRetryDetails(rDetails);
		Mockito.doReturn(false).when(operationHandler).persistAppDMetadata(any(AppDOnboardingRequest.class));
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplication_opCounter4ResponseIsFalse() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(4);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("nakmx-ancyr");
		request.setRetryDetails(rDetails);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplication_persistEUMMetaDataIsFalse() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(3);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("nakmx-ancyr");
		request.setRetryDetails(rDetails);
		request.setRequestCreatedDate("10-12-2011");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.doReturn(false).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createApplication_persistMappingIsFalse() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(2);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("nakmx-ancyr");
		request.setRetryDetails(rDetails);
		request.setRequestCreatedDate("10-12-2011");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.doReturn(false).when(operationHandler).persistMappings(any(List.class));
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.createApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateApplicationInvalidOpCounter() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(7);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("tgd-26hdv");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setCtrlName("ciscoeft");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		dBHandler.updateApplication(request);
	}

	@Test
	public void updateApplicationTestEumExists() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("tgd-26hdv");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(1);
		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		requestDetails.setAlertAliases("test@cisco.com");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(false).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class),
				any(String.class));
		Mockito.doReturn(true).when(operationHandler).updateAppDMetadata(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(operationHandler).deleteEUMMetaData(any(String.class), any(List.class));
		Mockito.doReturn(true).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		Mockito.doReturn(true).when(operationHandler).persistMappings(any(List.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.updateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateApplicationTest6() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(false).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class),
				any(String.class));
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.updateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateApplicationTest7() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(6);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		Mockito.doReturn(request).when(dBHandler).firstUpdateOperations(any(AppDOnboardingRequest.class),
				any(Integer.class));

		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.updateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void firstUpdateOperationsOpCounterOneException() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(1);
		request.setRetryDetails(rdetails);
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(false).when(operationHandler).updateAppDMetadata(any(AppDOnboardingRequest.class));
		dBHandler.firstUpdateOperations(request, 1);
	}

	@Test
	public void firstUpdateOperationsOpCounterOne() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(1);
		request.setRetryDetails(rdetails);
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(operationHandler).updateAppDMetadata(any(AppDOnboardingRequest.class));
		dBHandler.firstUpdateOperations(request, 1);
	}

	@Test
	public void firstUpdateOperations_opCounter2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(2);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(operationHandler).persistMappings(any(List.class));
		dBHandler.firstUpdateOperations(request, 2);
	}

	@Test
	public void firstUpdateOperations_opCounter3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(3);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		dBHandler.firstUpdateOperations(request, 3);
	}

	@Test
	public void firstUpdateOperations_opCounter4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(4);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(operationHandler).deleteEUMMetaData(any(String.class), any(List.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		dBHandler.firstUpdateOperations(request, 4);
	}

	@Test
	public void firstUpdateOperations_opCounter5() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(5);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		request.setMapping(mappingList);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.firstUpdateOperations(request, 5);
	}

	@Test
	public void checkRequestTypeTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		AppDOnboardingRequest request1 = new AppDOnboardingRequest();
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		AppDOnboardingRequest request3 = new AppDOnboardingRequest();
		AppDOnboardingRequest request4 = new AppDOnboardingRequest();
		RetryDetails rDetails1 = new RetryDetails();
		rDetails1.setFailureModule("RM_HANDLER");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		request.setRetryDetails(rDetails);
		request.setRequestType("create");
		request1.setRequestType("update");
		request4.setRequestType("update");
		request1.setRetryDetails(rDetails);
		request2.setRequestType("delete");
		request2.setRetryDetails(rDetails);
		request4.setRetryDetails(rDetails);
		request3.setRetryDetails(rDetails1);
		request3.setRequestType("create");
		request4.setResourceMove(true);
		Mockito.doReturn(true).when(dBHandler).createApplication(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(dBHandler).updateApplication(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateApplication(any(AppDOnboardingRequest.class));
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.checkRequestType(request);
		dBHandler.checkRequestType(request2);
		dBHandler.checkRequestType(request1);
		dBHandler.checkRequestType(request3);
		dBHandler.checkRequestType(request4);
	}

	@Test
	public void checkRequestTypeTestFalse() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		AppDOnboardingRequest request1 = new AppDOnboardingRequest();
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		AppDOnboardingRequest request3 = new AppDOnboardingRequest();
		AppDOnboardingRequest request4 = new AppDOnboardingRequest();
		RetryDetails rDetails1 = new RetryDetails();
		rDetails1.setFailureModule("RM_HANDLER");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("xyz");
		request.setRetryDetails(rDetails);
		request.setRequestType("create");
		request1.setRequestType("update");
		request4.setRequestType("update");
		request1.setRetryDetails(rDetails);
		request2.setRequestType("delete");
		request2.setRetryDetails(rDetails);
		request4.setRetryDetails(rDetails);
		request3.setRetryDetails(rDetails1);
		request3.setRequestType("create");
		request4.setResourceMove(true);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.checkRequestType(request);
		dBHandler.checkRequestType(request2);
		dBHandler.checkRequestType(request1);
		dBHandler.checkRequestType(request3);
		dBHandler.checkRequestType(request4);
	}

	@Test(expected = AppDOnboardingException.class)
	public void checkRequestTypeThrowsException() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		request.setRetryDetails(rDetails);
		request.setRequestType("create");
		Mockito.doThrow(AppDOnboardingException.class).when(dBHandler)
				.createApplication(any(AppDOnboardingRequest.class));
		dBHandler.checkRequestType(request);

	}

	@Test
	public void modifyAppNameTest() {
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test-app-old");
		roleMap.setCtrlName("ciscoeft");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		dBHandler.modifyAppName(mappingList, "test-app-old", "test-app-new");
	}

	@Test
	public void resourceMoveUpdateRoleMappingTest() throws Exception {
		List<String> eumList = new ArrayList<>();
		eumList.add("test1");
		Mockito.when(RoleMappingDAO.resourceMoveUpdate(any(String.class), any(String.class), any(String.class)))
				.thenReturn(true);
		dBHandler.resourceMoveUpdateRoleMapping("test-app-old", "test-app-new", "ciscoeft", eumList);
	}

	@Test
	public void resourceMoveUpdateRoleMappingTest1() throws Exception {

		Mockito.when(RoleMappingDAO.resourceMoveUpdate(any(String.class), any(String.class), any(String.class)))
				.thenReturn(true);
		dBHandler.resourceMoveUpdateRoleMapping("test-app-old", "test-app-new", "ciscoeft", null);
	}

	@Test
	public void resourceMoveUpdateEUMMetaDataTest() throws AppDOnboardingException {
		String oldAppName = "test-app-old";
		String newAppName = "test-app-new";
		String appdProjectId = "a0d24fc8-3031";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setAppdProjectId("test");
		eumData.setEumName("test-eum");
		ArrayList<EUMMetaData> eumList = new ArrayList<EUMMetaData>();
		eumList.add(eumData);
		Mockito.when(eUMMetaDataDAO.findByProjectID(any(String.class))).thenReturn(eumList);
		Mockito.when(eUMMetaDataDAO.resourceMoveUpdateEUM(any(String.class), any(String.class), any(String.class)))
				.thenReturn(true);
		dBHandler.resourceMoveUpdateEUMMetaData(appdProjectId, oldAppName, newAppName);
	}

	@Test
	public void resourceMoveUpdateEUMMetaDataTestEmptyEUMList() throws AppDOnboardingException {
		String oldAppName = "test-app-old";
		String newAppName = "test-app-new";
		String appdProjectId = "a0d24fc8-3031";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setAppdProjectId("test");
		eumData.setEumName("test-eum");
		EUMMetaData eumData2 = null;
		ArrayList<EUMMetaData> eumList = new ArrayList<EUMMetaData>();
		eumList.add(eumData);
		eumList.add(eumData2);
		Mockito.when(eUMMetaDataDAO.findByProjectID(any(String.class))).thenReturn(eumList);
		Mockito.when(eUMMetaDataDAO.resourceMoveUpdateEUM(any(String.class), any(String.class), any(String.class)))
				.thenReturn(true);
		dBHandler.resourceMoveUpdateEUMMetaData(appdProjectId, oldAppName, newAppName);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateEUMMetaDataTest2() throws AppDOnboardingException {
		String oldAppName = "test-app-old";
		String newAppName = "test-app-new";
		String appdProjectId = "a0d24fc8-3031";
		Mockito.when(eUMMetaDataDAO.findByProjectID(any(String.class))).thenReturn(null);
		Mockito.when(eUMMetaDataDAO.resourceMoveUpdateEUM(any(String.class), any(String.class), any(String.class)))
				.thenReturn(true);
		dBHandler.resourceMoveUpdateEUMMetaData(appdProjectId, oldAppName, newAppName);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateApplicationTest() throws AppDOnboardingException {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("tgd-26hdv");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(1);
		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		requestDetails.setAlertAliases("test@cisco.com");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		Mockito.doReturn(true).when(operationHandler).updateAppDMetadata(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(operationHandler).deleteEUMMetaData(any(String.class), any(List.class));
		Mockito.doReturn(true).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		Mockito.doReturn(true).when(operationHandler).persistMappings(any(List.class));
		Mockito.doThrow(AppDOnboardingException.class).when(dBHandler)
				.firstUpdateOperations(any(AppDOnboardingRequest.class), any(Integer.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.updateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateApplication_invalidOpCounter() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(7);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("tgd-26hdv");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setCtrlName("ciscoeft");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		dBHandler.updateApplication(request);
	}

	@Test
	public void updateApplicationTestOpCounterSix() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(6);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("tgd-26hdv");
		request.setRequestCreatedDate("20-10-1201");
		request.setRequestModifiedDate("20-12-2013");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(1);

		RoleMapping mapping = new RoleMapping();
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		requestDetails.setAlertAliases("test@cisco.com");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doReturn(true).when(operationHandler).checkIfEUMApplicationNotExist(any(List.class), any(String.class));
		Mockito.doReturn(true).when(operationHandler).updateAppDMetadata(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(operationHandler).deleteEUMMetaData(any(String.class), any(List.class));
		Mockito.doReturn(true).when(operationHandler).persistEUMMetaData(any(String.class), any(List.class),
				any(String.class));
		Mockito.doReturn(true).when(operationHandler).persistMappings(any(List.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.updateApplication(request);
	}

	@Test(expected = MongoException.class)
	public void resourceMoveUpdateApplicationTestMongoException() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twdb-2yhds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(1);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenThrow(new MongoException(""));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.resourceMoveUpdateApplication(request);
	}

	@Test
	public void resourceMoveUpdateApplicationTest() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twdb-2yhds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(5);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenReturn(true);
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.resourceMoveUpdateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateApplicationTestException() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twdb-2yhds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(1);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenReturn(true);
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.resourceMoveUpdateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateApplicationTestFalse() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twdb-2yhds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(5);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenReturn(true);
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.resourceMoveUpdateApplication(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateApplicationTestFalseForOpCounterSix() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twdb-2yhds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(6);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenReturn(true);
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		Mockito.doReturn(true).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		Mockito.when(requestDao.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		dBHandler.resourceMoveUpdateApplication(request);
	}

	@Test
	public void resourceMoveFirstSetOfOperations_opCounter1() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(1);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(appDMasterDao.updateNewAppName(any(AppDOnboardingRequest.class), any(String.class)))
				.thenReturn(false);
		dBHandler.resourceMoveFirstSetOfOperations(request, 1);
	}

	@Test
	public void resourceMoveFirstSetOfOperations_opCounter2() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(2);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(false).when(dBHandler).resourceMoveUpdateRoleMapping(any(String.class), any(String.class),
				any(String.class), any(List.class));
		dBHandler.resourceMoveFirstSetOfOperations(request, 2);
	}

	@Test
	public void resourceMoveFirstSetOfOperations_opCounter3() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(3);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(false).when(dBHandler).resourceMoveUpdateEUMMetaData(any(String.class), any(String.class),
				any(String.class));
		dBHandler.resourceMoveFirstSetOfOperations(request, 3);
	}

	@Test
	public void resourceMoveFirstSetOfOperationsOpCounterFour() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("twebd-262hdsj");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		rdetails.setOperationCounter(4);
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		request.setAppdExternalId("ysjn-27sg73-shh");
		requestDetails.setAppdProjectId("a0d24fc8-3031");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		Mockito.when(requestDao.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDao.updateCreateRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		dBHandler.resourceMoveFirstSetOfOperations(request, 4);
	}

}
