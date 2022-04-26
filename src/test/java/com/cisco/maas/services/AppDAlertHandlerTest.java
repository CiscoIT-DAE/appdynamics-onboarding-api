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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
@SuppressWarnings("unchecked")
public class AppDAlertHandlerTest {

	@InjectMocks
	@Spy
	AppDAlertsHandler appDAlertHandler;

	@Mock
	AppDynamicsUtil appdUtil;

	@Mock
	APPDMasterDAO appdMasterDao;
	@Mock
	RequestHandler requestHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleRequestCreateRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(true).when(appDAlertHandler).createAlerts(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDAlertHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDAlertHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDAlertHandler.handleRequest(request);
	}

	@Test
	public void handleRequestUpdateRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(rdDetails);
		Mockito.doReturn(true).when(appDAlertHandler).updateAction(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDAlertHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDAlertHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDAlertHandler.handleRequest(request);
	}

	@Test
	public void handleRequestUpdateRequestAlertAliasNullTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RequestDetails rdDetails = new RequestDetails();
		request.setRequestDetails(rdDetails);
		Mockito.doReturn(true).when(appDAlertHandler).updateAction(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDAlertHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDAlertHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDAlertHandler.handleRequest(request);
	}

	@Test
	public void handleRequestInvalidRequestTypeTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("delete");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(rdDetails);
		Mockito.doReturn(true).when(appDAlertHandler).updateAction(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDAlertHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDAlertHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDAlertHandler.handleRequest(request);
	}

	@Test
	public void getExistingHealthRuleIdTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");

		String str = "[{\"id\": 11085,\"name\": \"Agent Down - A Cisco default health rule\" }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules?output=JSON",
				"GET", "cisco1nonprod", "healthRule")).thenReturn(str);

		String response = "Done";
		when(appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(response);

		when(appdUtil.appDConnectionDelete(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(response);

		assertNotNull(appDAlertHandler.getExistingHealthRuleId(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void getExistingHealthRuleIdTest2() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(2);
		request.setRetryDetails(rDetails);
		String str = "[{\"id\": 11085,\"name\": \"Agent Down - A Cisco default health rule\" }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules?output=JSON",
				"GET", "cisco1nonprod", "healthRule")).thenReturn(str);

		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules/11085",
				"DELETE", "cisco1nonprod", "healthRule")).thenReturn(null);

		assertNull(appDAlertHandler.getExistingHealthRuleId(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void getExistingHealthRuleId_ioException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(2);
		request.setRetryDetails(rDetails);
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules?output=JSON",
				"GET", "cisco1nonprod", "healthRule")).thenThrow(new IOException());

		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules/11085",
				"DELETE", "cisco1nonprod", "healthRule")).thenReturn(null);

		assertNull(appDAlertHandler.getExistingHealthRuleId(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void getExistingHealthRuleId_parseException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");

		String str = "{\"id\": 11085\"name\": \"Agent Down - A Cisco default health rule\" }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/health-rules?output=JSON",
				"GET", "cisco1nonprod", "healthRule")).thenReturn(str);

		String response = "Done";
		when(appdUtil.appDConnectionDelete(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(response);

		assertNotNull(appDAlertHandler.getExistingHealthRuleId(request));
	}

	@Test
	public void createHealthRulesTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");

		String str = "[{\"id\":11067,\"name\":\"Agent Down - A Cisco default health rule\"}]";

		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(str);
		assertNotNull(appDAlertHandler.createHealthRules(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createHealthRulesTest2() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		assertNotNull(appDAlertHandler.createHealthRules(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createHealthRules_IOException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(new IOException());
		assertNotNull(appDAlertHandler.createHealthRules(request));
	}

	@Test
	public void createActionsTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(3);
		request.setRetryDetails(rDetails);
		String str = "[{\"id\":1230,\"actionType\":\"EMAIL\",\"emails\":[\"test@cisco.com\"]}]";
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(str);
		assertNotNull(appDAlertHandler.createActions(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createActionsTest2() throws Exception {

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(3);
		request.setRetryDetails(rDetails);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		assertNotNull(appDAlertHandler.createActions(request));
	}

	@Test
	public void createDynamicActionTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		String str = "[{\"id\":1230,\"actionType\":\"EMAIL\",\"emails\":[\"test@cisco.com\"]}]";
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(str);
		assertNotNull(appDAlertHandler.createDynamicAction(request, 1234, "rgundewa@cisco.com"));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createDynamicActionTest2() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		assertNotNull(appDAlertHandler.createDynamicAction(request, 1234, "rgundewa@cisco.com"));
	}

	@Test(expected = Exception.class)
	public void createDynamicActionTest3() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		assertNotNull(appDAlertHandler.createDynamicAction(request, 1234, "rgundewa@cisco.com"));
	}

	@Test
	public void createPoliciesTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(7);
		request.setRetryDetails(rDetails);
		String str = "[{\"id\":6663,\"name\":\"Agent stopped reporting - A Cisco default alert\"}]";

		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(str);

		assertNotNull(appDAlertHandler.createPolicies(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createPoliciesTest2() throws Exception {

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(7);
		request.setRetryDetails(rDetails);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);

		assertNull(appDAlertHandler.createPolicies(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createPolicies_ioException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(7);
		request.setRetryDetails(rDetails);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(new IOException());

		assertNotNull(appDAlertHandler.createPolicies(request));
	}

	@Test
	public void deleteActionByIdTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);

		String str = "[ { \"id\": 1273, \"name\": \"test@cisco.com\",\"actionType\": \"EMAIL\" },{\"name\":\"newtest@cisco.com\"}]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions?output=JSON",
				"GET", "cisco1nonprod", "action")).thenReturn(str);

		String response = "Done";
		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions/1273",
				"DELETE", "cisco1nonprod", "action")).thenReturn(response);

		assertNotNull(appDAlertHandler.deleteActionById(request, 442105, "test@cisco.com"));
	}

	@Test(expected = AppDOnboardingException.class)
	public void deleteActionById_ioException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions?output=JSON",
				"GET", "cisco1nonprod", "action")).thenThrow(new IOException());
		String response = "Done";
		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions/1273",
				"DELETE", "cisco1nonprod", "action")).thenReturn(response);
		assertNotNull(appDAlertHandler.deleteActionById(request, 442105, "test@cisco.com"));
	}

	@Test(expected = AppDOnboardingException.class)
	public void deleteActionById_parseException() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		String str = " { \"id\": 1273, \"name\": \"test@cisco.com\",\"actionType\": \"EMAIL\" },{\"name\":\"newtest@cisco.com\"}]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions?output=JSON",
				"GET", "cisco1nonprod", "action")).thenReturn(str);
		String response = "Done";
		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions/1273",
				"DELETE", "cisco1nonprod", "action")).thenReturn(response);
		assertNotNull(appDAlertHandler.deleteActionById(request, 442105, "test@cisco.com"));
	}

	@Test(expected = AppDOnboardingException.class)
	public void deleteActionByIdTest3() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		String str = "[ { \"id\": 1273, \"name\": \"test@cisco.com\",\"actionType\": \"EMAIL\" }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions?output=JSON",
				"GET", "cisco1nonprod", "action")).thenReturn(str);
		when(appdUtil.appDConnectionDelete(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/actions/1273",
				"DELETE", "cisco1nonprod", "action")).thenReturn(null);
		assertNotNull(appDAlertHandler.deleteActionById(request, 442105, "test@cisco.com"));
	}

	@Test
	public void removeActionFromPolicyTest() throws Exception {
		String str = "[{\"actionName\": \"test@cisco.com\",\"actionType\": \"EMAIL\"}]";
		JSONParser parser = new JSONParser();
		JSONArray data = (JSONArray) parser.parse(str);
		assertNotNull(appDAlertHandler.removeActionFromPolicy(data, "test@cisco.com"));
	}

	@Test
	public void updateActionTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("AppDAlerthandler");
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");

		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("APMApp");
		appdDetails.setAppGroupId(442105);
		appdDetails.setAlertAliases("rgundewa@cisco.com,sample@cisco.com");
		appdDetails.setCtrlName("cisco1nonprod");
		Mockito.when(appdMasterDao.findByApp("APMApp", "cisco1nonprod")).thenReturn(appdDetails);
		String response = "Done";
		Mockito.doReturn(response).when(appDAlertHandler).createDynamicAction(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		Mockito.doReturn(response).when(appDAlertHandler).deleteActionById(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		Mockito.doReturn(true).when(appDAlertHandler).updatePolicies(any(AppDOnboardingRequest.class), any(int.class),
				any(List.class), any(List.class));
		assertNotNull(appDAlertHandler.updateAction(request));
	}

	@Test
	public void updateAction_alertAliasIsNull() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("AppDAlerthandler");
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");

		assertNotNull(appDAlertHandler.updateAction(request));
	}

	@Test
	public void updateAction_opCounter3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("AppDAlerthandler");
		rDetails.setOperationCounter(3);
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("APMApp");
		appdDetails.setAppGroupId(442105);
		appdDetails.setAlertAliases("rgundewa@cisco.com,sample@cisco.com");
		appdDetails.setCtrlName("cisco1nonprod");
		Mockito.when(appdMasterDao.findByApp("APMApp", "cisco1nonprod")).thenReturn(appdDetails);
		String response = "Done";
		Mockito.doReturn(response).when(appDAlertHandler).createDynamicAction(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		Mockito.doReturn(response).when(appDAlertHandler).deleteActionById(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		Mockito.doReturn(false).when(appDAlertHandler).updatePolicies(any(AppDOnboardingRequest.class), any(int.class),
				any(List.class), any(List.class));
		assertNotNull(appDAlertHandler.updateAction(request));
	}

	@Test
	public void updateAction_opCounter7() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("AppDAlerthandler");
		rDetails.setOperationCounter(7);
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("442105");
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("APMApp");
		appdDetails.setAppGroupId(442105);
		appdDetails.setAlertAliases("rgundewa@cisco.com,sample@cisco.com");
		appdDetails.setCtrlName("cisco1nonprod");
		Mockito.when(appdMasterDao.findByApp("APMApp", "cisco1nonprod")).thenReturn(appdDetails);
		String response = "Done";
		Mockito.doReturn(request).when(requestHandler).getUpdatedRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(response).when(appDAlertHandler).createDynamicAction(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		Mockito.doReturn(response).when(appDAlertHandler).deleteActionById(any(AppDOnboardingRequest.class),
				any(int.class), any(String.class));
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		Mockito.doReturn(true).when(appDAlertHandler).updatePolicies(any(AppDOnboardingRequest.class), any(int.class),
				any(List.class), any(List.class));
		assertNotNull(appDAlertHandler.updateAction(request));
	}

	@Test
	public void updatePoliciesTest() throws Exception {
		JSONParser parser = new JSONParser();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		String policyList = "[{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies?output=JSON",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyList);
		String policyObject = "{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies/5006",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyObject);

		String actionList = "[{\"actionName\": \"appd-nonprod-alerts@cisco.com\", \"actionType\": \"EMAIL\" }]";
		JSONArray updatedActionList = (JSONArray) parser.parse(actionList);
		Mockito.doReturn(updatedActionList).when(appDAlertHandler).removeActionFromPolicy(any(JSONArray.class),
				any(String.class));
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		assertNotNull(appDAlertHandler.updatePolicies(request, 442105, addList, removeList));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updatePolicies_ioException() throws Exception {
		JSONParser parser = new JSONParser();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies?output=JSON",
				"GET", "cisco1nonprod", "policy")).thenThrow(new IOException());
		String policyObject = "{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies/5006",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyObject);
		String actionList = "[{\"actionName\": \"appd-nonprod-alerts@cisco.com\", \"actionType\": \"EMAIL\" }]";
		JSONArray updatedActionList = (JSONArray) parser.parse(actionList);
		Mockito.doReturn(updatedActionList).when(appDAlertHandler).removeActionFromPolicy(any(JSONArray.class),
				any(String.class));
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		assertNotNull(appDAlertHandler.updatePolicies(request, 442105, addList, removeList));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updatePolicies_parseException() throws Exception {
		JSONParser parser = new JSONParser();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		List<String> removeList = new ArrayList<>();
		removeList.add("sample@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		String policyList = "{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies?output=JSON",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyList);
		String policyObject = "{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies/5006",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyObject);

		String actionList = "[{\"actionName\": \"appd-nonprod-alerts@cisco.com\", \"actionType\": \"EMAIL\" }]";
		JSONArray updatedActionList = (JSONArray) parser.parse(actionList);

		Mockito.doReturn(updatedActionList).when(appDAlertHandler).removeActionFromPolicy(any(JSONArray.class),
				any(String.class));
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");

		assertNotNull(appDAlertHandler.updatePolicies(request, 442105, addList, removeList));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updatePolicies_responseIsNull() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("rgundewa@cisco.com,rpatta@cisco.com");
		request.setRequestDetails(requestDetails);
		List<String> removeList = new ArrayList<>();
		removeList.add("appd@cisco.com");
		List<String> addList = new ArrayList<>();
		addList.add("rpatta@cisco.com");
		String policyList = "[{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }]";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies?output=JSON",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyList);
		String policyObject = "{\"id\": 5006,\"name\": \"Agent stopped reporting - A Cisco default alert\",\"actions\": [{ \"actionName\": \"sample@cisco.com\", \"actionType\": \"EMAIL\"}], \"events\": { \"anomalyEvents\": [] } }";
		when(appdUtil.appDConnectionOnlyGet(
				"https://cisco1nonprod.saas.appdynamics.com/controller/alerting/rest/v1/applications/442105/policies/5006",
				"GET", "cisco1nonprod", "policy")).thenReturn(policyObject);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);

		assertNotNull(appDAlertHandler.updatePolicies(request, 442105, addList, removeList));
	}

	@Test
	public void removeActionFromPolicy() throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("actionType", "EMAI");
		obj.put("actionName", "t");
	}

	@Test
	public void createAlerts_OpCounter1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(false).when(appDAlertHandler).getExistingHealthRuleId(any(AppDOnboardingRequest.class));
		appDAlertHandler.createAlerts(request);
	}

	@Test
	public void createAlerts_OpCounter2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(2);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(false).when(appDAlertHandler).createHealthRules(any(AppDOnboardingRequest.class));
		appDAlertHandler.createAlerts(request);
	}

	@Test
	public void createAlertsTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		RetryDetails rDetails1 = new RetryDetails();
		rDetails1.setOperationCounter(5);
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		request2.setRetryDetails(rDetails1);
		Mockito.doReturn(true).when(appDAlertHandler).getExistingHealthRuleId(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDAlertHandler).createHealthRules(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDAlertHandler).createPolicies(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request2).when(appDAlertHandler).createActions(any(AppDOnboardingRequest.class));
		assertNotNull(appDAlertHandler.createAlerts(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createAlertsTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doThrow(new AppDOnboardingException("")).when(appDAlertHandler)
				.getExistingHealthRuleId(any(AppDOnboardingRequest.class));
		assertNotNull(appDAlertHandler.createAlerts(request));
	}

	@Test
	public void createAlertsTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(4);
		request.setRetryDetails(rDetails);
		RetryDetails rDetails1 = new RetryDetails();
		rDetails1.setOperationCounter(6);
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		request2.setRetryDetails(rDetails1);
		Mockito.doReturn(request2).when(appDAlertHandler).createActions(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDAlertHandler).createPolicies(any(AppDOnboardingRequest.class));
		appDAlertHandler.createAlerts(request);
	}

	@Test
	public void createAlertsTest4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(4);
		request.setRetryDetails(rDetails);
		RetryDetails rDetails1 = new RetryDetails();
		rDetails1.setOperationCounter(7);
		AppDOnboardingRequest request2 = new AppDOnboardingRequest();
		request2.setRetryDetails(rDetails1);
		Mockito.doReturn(request2).when(appDAlertHandler).createActions(any(AppDOnboardingRequest.class));
		Mockito.doReturn(true).when(appDAlertHandler).createPolicies(any(AppDOnboardingRequest.class));
		appDAlertHandler.createAlerts(request);
	}

	@Test
	public void createAlerts_invalidOpCounter() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(8);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(true).when(appDAlertHandler).createPolicies(any(AppDOnboardingRequest.class));
		appDAlertHandler.createAlerts(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void createActions_opCounter5() throws Exception {

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com,test@cisco.com,test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(5);
		request.setRetryDetails(rDetails);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(new IOException());
		assertNotNull(appDAlertHandler.createActions(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void createActions_opCounter6() throws Exception {

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAlertAliases("test@cisco.com,test@cisco.com,test@cisco.com");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("1234");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(6);
		request.setRetryDetails(rDetails);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(new IOException());
		assertNotNull(appDAlertHandler.createActions(request));
	}

	@Test
	public void removeActionFromPolicyTest2() throws Exception {
		String str = "[{\"actionName\": \"test@cisco.com\",\"actionType\": \"\"}]";
		JSONParser parser = new JSONParser();
		JSONArray data = (JSONArray) parser.parse(str);
		assertNotNull(appDAlertHandler.removeActionFromPolicy(data, "test@cisco.com"));
	}

}