package com.cisco.maas.services;

import static org.junit.Assert.assertEquals;
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
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;

public class ProcessRequestTest {

	@InjectMocks
	@Spy
	ProcessRequest pr;
	@Mock
	RequestDAO requestDao;
	@Mock
	APPDMasterDAO appDMasterDao;
	@Mock
	AppDUserHandler appDUserHandler;
	@Mock
	AppDLicensesHandler licenseHandler;
	@Mock
	AppDAlertsHandler alertHandler;
	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	@Mock
	RequestHandler requestHandler;
	@Mock
	AppDRoleManager roleManager;

	@Mock
	DBHandler dBHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void asyncProcessRequestTest() throws AppDOnboardingException {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");
		request.setRequestStatus("pending");
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		Mockito.doNothing().when(appDUserHandler).handleRequest(any(AppDOnboardingRequest.class));
		pr.asyncProcessRequest(request);
		verify(pr, times(1)).asyncProcessRequest(request);

	}

	@Test
	public void asyncProcessRequestTest2() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("create");
		request.setRequestStatus("failed");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("Alert_handler");
		request.setRetryDetails(rDetails);
		Mockito.doNothing().when(pr).retryMap(any(String.class), any(AppDOnboardingRequest.class));
		pr.asyncProcessRequest(request);
		verify(pr, times(1)).asyncProcessRequest(request);

	}

	@Test
	public void asyncProcessRequestTest3() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("success");
		pr.asyncProcessRequest(request);
		verify(pr, times(1)).asyncProcessRequest(request);

	}

	@Test
	public void asyncProcessRequestException() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("create");
		request.setRequestStatus("pending");
		Mockito.doThrow(new AppDOnboardingException("")).when(appDUserHandler)
				.handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(pr).errorHandler(any(Exception.class), any(AppDOnboardingRequest.class));
		pr.asyncProcessRequest(request);
		verify(pr, times(1)).asyncProcessRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		request.getRequestDetails().setAdminUsers("admin");
		request.getRequestDetails().setViewUsers("view");
		Mockito.doReturn(1).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		Mockito.doReturn(2).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(alertHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		Mockito.doReturn(3).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(licenseHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		Mockito.doReturn(4).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDUserHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest6() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		request.getRequestDetails().setAdminUsers("admin");
		request.getRequestDetails().setViewUsers("view");
		Mockito.doReturn(5).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestTest5() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("failed");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("Alert_handler");
		request.setRetryDetails(rDetails);
		Mockito.doNothing().when(pr).retryMap(any(String.class), any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestFailed() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("create");
		request.setRequestStatus("success");
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void asyncProcessUpdateRequestException() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		Mockito.doReturn(4).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doThrow(new AppDOnboardingException("")).when(appDUserHandler)
				.handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(pr).errorHandler(any(Exception.class), any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}
	@Test
	public void asyncProcessUpdateRequestTestCounter6() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rqDetails = new RequestDetails();
		rqDetails.setTrackingId("1234");
		request.setRequestDetails(rqDetails);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		request.getRequestDetails().setAdminUsers("admin");
		request.getRequestDetails().setViewUsers("view");
		Mockito.doReturn(6).when(pr).handleUpdate(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(pr).initializeUpdateRequest(request);
		pr.asyncProcessUpdateRequest(request);
		verify(pr, times(1)).asyncProcessUpdateRequest(request);

	}

	@Test
	public void handleUpdateTest() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setApmLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setLicenseKey("652hgjdsad");
		request.setRequestType("delete");
		assertEquals(5, pr.handleUpdate(request));
	}

	@Test
	public void handleUpdateTestResourceMoveTrue() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setResourceMove(true);
		assertEquals(6, pr.handleUpdate(request));
	}

	@Test
	public void handleUpdateTest2() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(0);
		request.setRequestDetails(requestDetails);
		assertEquals(0, pr.handleUpdate(request));
	}

	@Test
	public void handleUpdateTestZeroServices() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(0);
		request.setRequestDetails(requestDetails);
		assertEquals(0, pr.handleUpdate(request));
	}
	@Test
	public void handleUpdateTestCounter6() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers("xyz");
		requestDetails.setViewUsers("abc");
		request.setRequestDetails(requestDetails);
		assertEquals(4, pr.handleUpdate(request));
	}
	@Test
	public void handleUpdateTestADdminViewNull() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers(null);
		requestDetails.setViewUsers(null);
		request.setRequestDetails(requestDetails);
		assertEquals(0, pr.handleUpdate(request));
	}
	@Test
	public void handleUpdateTestAdminNull() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers(null);
		requestDetails.setViewUsers("xyz");
		request.setRequestDetails(requestDetails);
		assertEquals(4, pr.handleUpdate(request));
	}
	@Test
	public void retryMapTest() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.doNothing().when(appDUserHandler).handleRequest(request);
		Mockito.doNothing().when(licenseHandler).handleRequest(request);
		Mockito.doNothing().when(alertHandler).handleRequest(request);
		Mockito.doNothing().when(appDApplicationCreationHandler).handleRequest(request);
		Mockito.doNothing().when(roleManager).handleRequest(request);
		Mockito.doNothing().when(dBHandler).handleRequest(request);
		pr.retryMap("AD_HANDLER", request);
		pr.retryMap("APP_CREATION_HANDLER", request);
		pr.retryMap("RM_HANDLER", request);
		pr.retryMap("LIC_HANDLER", request);
		pr.retryMap("ALERT_HANDLER", request);
		pr.retryMap("DB_HANDLER", request);
		pr.retryMap("Quota_ESP_HANDLER", request);
		verify(pr, times(1)).retryMap("AD_HANDLER", request);
		verify(pr, times(1)).retryMap("APP_CREATION_HANDLER", request);
		verify(pr, times(1)).retryMap("RM_HANDLER", request);
		verify(pr, times(1)).retryMap("LIC_HANDLER", request);
		verify(pr, times(1)).retryMap("ALERT_HANDLER", request);
		verify(pr, times(1)).retryMap("DB_HANDLER", request);
		verify(pr, times(1)).retryMap("Quota_ESP_HANDLER", request);

	}

	@Test
	public void errorHandlerTest() throws Exception {

		AppDOnboardingException ce = new AppDOnboardingException("unable to connect");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("ERROR");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("Test");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("");
		request.setRequestDetails(requestDetails);
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		ce.setStackTrace(stackTrace);
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		pr.errorHandler(new AppDOnboardingException("unable to connect"), request);
	}

	@Test
	public void errorHandlerTest2() throws Exception {

		AppDOnboardingException ce = new AppDOnboardingException("unable to connect");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("Test");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		pr.errorHandler(ce, request);
	}

	@Test
	public void errorHandlerTest4() throws Exception {

		AppDOnboardingException ce = new AppDOnboardingException("unable to connect");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("DB_HANDLER");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		pr.errorHandler(ce, request);
	}

	@Test
	public void errorHandlerTest3() throws Exception {

		Exception ce = new Exception("unable to connect");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("Test");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(dBHandler).handleRequest(any(AppDOnboardingRequest.class));
		pr.errorHandler(ce, request);
	}
	
	@Test(expected=AppDOnboardingException.class)
	public void  initializeUpdateRequestException() throws AppDOnboardingException {
		
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("123");
		request.setRequestType("create");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("");
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(request).when(requestDao).findByExternalIdAndRequestType("123", "create");	 
		pr.initializeUpdateRequest(request);
		 
	}
}
