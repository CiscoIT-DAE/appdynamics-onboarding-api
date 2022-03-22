package com.cisco.maas.services;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.client.RestTemplate;

import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RoleMapping;

public class LicenseUsageCheckHandlerTest {

	@InjectMocks
	@Spy
	LicenseUsageCheckHandler licenseUsageCheckHandler;

	@Mock
	RestTemplate api;
	@Mock
	LicenseQuotaHandler licenseQuotaHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		Mockito.doReturn(true).when(licenseUsageCheckHandler).processCallBack((any(AppDOnboardingRequest.class)));
		licenseUsageCheckHandler.handleRequest(request);
	}

	@Test
	public void processCallBackTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		request.setRequestType("create");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(licenseUsageCheckHandler).processLicenseQuotaCheck(any(AppDOnboardingRequest.class));
		licenseUsageCheckHandler.processCallBack(request);
	}

	@Test
	public void processCallBackTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(licenseUsageCheckHandler).processLicenseQuotaCheck(any(AppDOnboardingRequest.class));
		licenseUsageCheckHandler.processCallBack(request);
	}

	@Test
	public void processCallBackTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		request.setRequestType("create");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(licenseUsageCheckHandler).processLicenseQuotaCheck(any(AppDOnboardingRequest.class));
		licenseUsageCheckHandler.processCallBack(request);
	}

	@Test
	public void processCallBackTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		request.setRequestType("create");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.doNothing().when(licenseUsageCheckHandler).processLicenseQuotaCheck(any(AppDOnboardingRequest.class));
		licenseUsageCheckHandler.processCallBack(request);
	}

	@Test
	public void processLicenseQuotaCheck() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		request.setRequestType("create");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.when(licenseQuotaHandler.getLicenseUsage(any(String.class))).thenReturn((float) 76.5);
		licenseUsageCheckHandler.processLicenseQuotaCheck(request);
	}

	@Test
	public void processLicenseQuotaCheck2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");
		request.setOperationalStatus("INACTIVE");
		request.setRequestType("create");
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("test");
		mapping.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("test");
		requestDetails.setCtrlName("ciscoeft");
		request.setRequestDetails(requestDetails);
		Mockito.when(licenseQuotaHandler.getLicenseUsage(any(String.class))).thenThrow(new IOException());
		licenseUsageCheckHandler.processLicenseQuotaCheck(request);
	}
}
