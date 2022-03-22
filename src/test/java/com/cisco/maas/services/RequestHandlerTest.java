package com.cisco.maas.services;

import static org.junit.Assert.assertNotNull;
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

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.dto.ValidateResult;

public class RequestHandlerTest {
	@InjectMocks
	@Spy
	RequestHandler requestHandler;

	@Mock
	RequestDAO requestDAO;

	@Mock
	APPDMasterDAO aPPDMasterDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMockCreation() {
		assertNotNull(requestDAO);
	}

	@Test
	public void validateRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test123");
		request.setRequestType("create");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster aMaster = new APPDMaster();
		aMaster.setAlertAliases("test@cisco.com");
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(aMaster);
		Mockito.when(requestDAO.findByAppNameAndRequestType(any(String.class), any(String.class), any(String.class)))
				.thenReturn(request);
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void validateRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster aMaster = null;
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(aMaster);
		requestHandler.validateRequest(request);
	}

	@Test
	public void validateRequestTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setAppdProjectId("1234");
		request.setRequestDetails(requestDetails);
		ValidateResult validateResult = new ValidateResult();
		Mockito.doReturn(validateResult).when(requestHandler).validateUpdate(any(AppDOnboardingRequest.class));
		requestHandler.validateRequest(request);
	}

	@Test
	public void validateRequestTest5() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("sahsj-3267ds652g-26sdh");
		request.setRequestType("delete");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setAppdProjectId("1234");
		request.setRequestDetails(requestDetails);
		APPDMaster aMaster = new APPDMaster();
		aMaster.setAlertAliases("test@cisco.com");
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class))).thenReturn(null);
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void validateRequestTest6() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("ajdjs-266hdsk-217sb");
		request.setRequestType("delete");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setAppdProjectId("1234");
		request.setRequestDetails(requestDetails);
		APPDMaster aMaster = new APPDMaster();
		aMaster.setAlertAliases("test@cisco.com");
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void validateRequestTest7() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test123");
		request.setRequestType("UnknownType");
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void validateRequestDeleteExternalIdTrue() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("ajdjs-266hdsk-217sb");
		request.setRequestType("delete");
		APPDMaster aMaster = new APPDMaster();
		aMaster.setAlertAliases("test@cisco.com");
		Mockito.when(requestDAO.findAppByExternalId(any(String.class))).thenReturn(true);
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void validateRequestTestTrue() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("ajdjs-266hdsk-217sb");
		request.setRequestType("delete");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setAppdProjectId("1234");
		request.setRequestDetails(requestDetails);
		APPDMaster aMaster = new APPDMaster();
		aMaster.setAlertAliases("test@cisco.com");
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(requestDAO.findAppByExternalId(any(String.class))).thenReturn(true);
		assertNotNull(requestHandler.validateRequest(request));
	}

	@Test
	public void createMCMPRequest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.when(requestDAO.create(any(AppDOnboardingRequest.class))).thenReturn(true);
		assertNotNull(requestHandler.createRequest(request));

	}

	@Test
	public void createMCMPRequest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.when(requestDAO.create(any(AppDOnboardingRequest.class))).thenReturn(false);
		assertNotNull(requestHandler.createRequest(request));

	}

	@Test(expected = Exception.class)
	public void createMCMPRequest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.when(requestDAO.create(any(AppDOnboardingRequest.class))).thenThrow(new Exception("Unable to Connect"));
		assertNotNull(requestHandler.createRequest(request));

	}

	@Test
	public void getUpdatedRequestTest() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("hjdsnbnbds-3276hgsh-shhj");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("delete");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		Mockito.when(requestDAO.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);

		requestHandler.getUpdatedRequest(request);
	}

	@Test(expected = Exception.class)
	public void getUpdatedRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("537hdb-hs36hsh");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("delete");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");

		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenThrow(new Exception());
		requestHandler.getUpdatedRequest(request);
	}

	@Test
	public void validateUpdateTest() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("sajhdjs-msaj32");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		requestDetails.setNoOfEUMLicenses(5);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		masterDetails.setNoOfEUMLicenses(2);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);

		requestHandler.validateUpdate(request);
	}

	@Test
	public void validateUpdateTest2() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test");

		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setAppGroupName("test");
		request.setRequestDetails(requestDetails);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class))).thenReturn(null);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		Mockito.when(requestDAO.findAppByExternalId(any(String.class))).thenReturn(false);
		requestHandler.validateUpdate(request);
	}

	@Test
	public void validateUpdateTest3() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("hsdgb-2hsh2sj");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");

		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		masterDetails.setNoOfEUMLicenses(2);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);

		requestHandler.validateUpdate(request);
	}

	@Test
	public void validateUpdateTest4() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("shdjwn-we23ndsjh32");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		requestDetails.setNoOfEUMLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		masterDetails.setNoOfEUMLicenses(2);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		requestHandler.validateUpdate(request);
	}

	@Test
	public void validateUpdateTest5() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("shdjwn-we23ndsjh32");
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		requestDetails.setNoOfEUMLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);

		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");

		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		masterDetails.setNoOfEUMLicenses(2);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		Mockito.when(requestDAO.findAppByExternalId(any(String.class))).thenReturn(true);
		requestHandler.validateUpdate(request);
	}

	@Test
	public void getMCMPRequestByProjectIdTest() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("delete");
		request.setAppdExternalId("test123");
		request.setOperationalStatus("INACTIVE");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setViewUsers("rgundewa");
		requestDetails.setAdminUsers("rgundewa");
		requestDetails.setAppdProjectId("test123");
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setApmLicenses(0);
		requestDetails.setNoOfEUMLicenses(0);
		;
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		mappingList.add(roleMap);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setRequestCounter(2);
		request.setMapping(mappingList);
		requestList.add(request);
		masterDetails.setEumApps(eumApps);
		masterDetails.setAlertAliases("test@cisco.com");
		masterDetails.setNoOfEUMLicenses(1);
		masterDetails.setApmLicenses(1);
		masterDetails.setEumApps(eumApps);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);

		Mockito.when(requestDAO.findAllByProjectId(any(String.class))).thenReturn(requestList);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		requestHandler.getRequestByProjectId("test123");
	}

	@Test
	public void getUpdatedRequestResourceMoveTest() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("hgsajnc-26uds");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		request.setRequestDetails(requestDetails);
		masterDetails.setEumApps(eumApps);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)))
				.thenReturn(request);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		Mockito.when(requestDAO.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		requestHandler.getUpdatedRequestResourceMove(request);
	}

	@Test(expected = Exception.class)
	public void getUpdatedRequestResourceMoveTest1() throws Exception {
		APPDMaster masterDetails = new APPDMaster();
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("23562187-2827");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setAdminGroupName("test");
		roleMap.setViewGroupName("test");
		ArrayList<RoleMapping> mappingList = new ArrayList<RoleMapping>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		request.setRequestDetails(requestDetails);
		masterDetails.setEumApps(eumApps);
		Mockito.when(requestDAO.findByExternalIdAndRequestType(any(String.class), any(String.class)));
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(masterDetails);
		Mockito.when(requestDAO.findByProjectIdAndOpStatus(any(String.class), any(String.class))).thenReturn(request);
		requestHandler.getUpdatedRequestResourceMove(request);
	}
}
