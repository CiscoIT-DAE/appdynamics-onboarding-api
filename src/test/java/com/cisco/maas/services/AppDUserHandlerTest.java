package com.cisco.maas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dao.RoleMappingDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;

public class AppDUserHandlerTest {
	@InjectMocks
	@Spy
	AppDUserHandler appDUserHandler;
	@Mock
	RoleMappingDAO RoleMappingDAO;
	@Mock
	RequestDAO mcmpRequestDao;
	@Mock
	RequestHandler requestHandler;
	@Mock
	APPDMasterDAO aPPDMasterDAO;
	@Mock
	AppDynamicsUtil appdUtil;

	@Mock
	JSONObject jsonObject;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMockCreation() {
		assertNotNull(RoleMappingDAO);
	}

	@Test
	public void handleRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("create");

		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(appDUserHandler).buildAppDetails(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDUserHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDUserHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDUserHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		rdDetails.setAddEumpApps(addEUMapps);
		request.setRequestType("update");
		request.setRequestStatus("pending");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		rDetails.setFailureModule("CGHANDLER");
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rDetails);

		Mockito.doReturn(request).when(appDUserHandler).eumUpdate(any(AppDOnboardingRequest.class));
		Mockito.doReturn(request).when(appDUserHandler).buildAppDetails(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(appDUserHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDUserHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		appDUserHandler.handleRequest(request);
	}

	@Test
	public void handleRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("delete");
		request.setRequestStatus("pending");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setOperationCounter(1);
		rDetails.setFailureModule("APP_CREATION_HANDLER");
		request.setRetryDetails(rDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-GATS-3DIT-Orchestration-na");
		mapping.setViewGroupName("AppD-GATS-3DIT-Orchestration-nv");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.doNothing().when(appDUserHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(appDUserHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		appDUserHandler.handleRequest(request);
	}

	@Test
	public void prepareRoleMappingTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("Application Performance Management");
		rdDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setRequestType("create");
		Mockito.doReturn(true).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareRoleMappingTest5() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("Application Performance Management");
		rdDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setRequestType("create");
		Mockito.doReturn(true).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		Mockito.doReturn("0e387be4-c4fd-31c9-9ed2-787d5a264846").when(appDUserHandler).getADbySO(any(String.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareRoleMappingTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("Application Performance Management");
		rdDetails.setCtrlName("cisco1");
		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pa");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pv");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setRequestType("create");
		Mockito.doReturn(true).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		Mockito.doReturn("0e387be4-c4fd-31c9-9ed2-787d5a264846").when(appDUserHandler).getADbySO(any(String.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareRoleMappingTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1");

		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("Application Performance Management");
		rdDetails.setAddEumpApps(addeumAppList);

		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pa");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pv");
		mappingList.add(mapping);
		mapping.setAppGroupName("Application Performance Management");
		mapping.setCtrlName("cisco1");
		request.setMapping(mappingList);
		request.setRequestType("update");
		Mockito.doReturn(true).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		Mockito.doReturn("0e387be4-c4fd-31c9-9ed2-787d5a264846").when(appDUserHandler).getADbySO(any(String.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareRoleMappingTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("cisco1nonprod");
		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("Application Performance Management");
		rdDetails.setAddEumpApps(addeumAppList);
		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		mappingList.add(mapping);
		mapping.setAppGroupName("Application Performance Management");
		mapping.setCtrlName("cisco1nonprod");
		request.setMapping(mappingList);
		request.setRequestType("update");
		Mockito.doReturn(false).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		Mockito.doReturn("0e387be4-c4fd-31c9-9ed2-787d5a264846").when(appDUserHandler).getADbySO(any(String.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareRoleMappingTest4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setCtrlName("ciscoeft");

		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("Application Performance Management");
		rdDetails.setAddEumpApps(addeumAppList);

		request.setRequestDetails(rdDetails);
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pa");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-pv");
		mappingList.add(mapping);
		mapping.setAppGroupName("Application Performance Management");
		mapping.setCtrlName("cisco1");
		request.setMapping(mappingList);
		request.setRequestType("update");
		Mockito.doReturn(true).when(appDUserHandler).createAppDLocalUsersIfNotExists(any(AppDOnboardingRequest.class));
		Mockito.doReturn("0e387be4-c4fd-31c9-9ed2-787d5a264846").when(appDUserHandler).getADbySO(any(String.class));
		appDUserHandler.prepareRoleMapping(request);
	}

	@Test
	public void prepareOrUpdateMappingListTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		List<String> eumAppList = new ArrayList<>();
		eumAppList.add("Application Performance Management");
		rdDetails.setEumApps(eumAppList);
		request.setRequestDetails(rdDetails);
		request.setRequestType("create");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		mappingList.add(mapping);
		mapping.setAppGroupName("Application Performance Management");
		mapping.setCtrlName("cisco1nonprod");
		request.setMapping(mappingList);
		appDUserHandler.prepareOrUpdateMappingList(request, mapping);
	}

	@Test
	public void prepareOrUpdateMappingListTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("Application Performance Management");
		rdDetails.setAddEumpApps(addeumAppList);
		request.setRequestDetails(rdDetails);
		request.setRequestType("update");

		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		mapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		mappingList.add(mapping);
		mapping.setAppGroupName("Application Performance Management");
		mapping.setCtrlName("cisco1nonprod");
		request.setMapping(mappingList);
		appDUserHandler.prepareOrUpdateMappingList(request, mapping);
	}

	@Test
	public void eumUpdateTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		List<String> eumAppList = new ArrayList<>();
		eumAppList.add("EUM1");
		rdDetails.setEumApps(eumAppList);
		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("EUM3");
		rdDetails.setAddEumpApps(addeumAppList);
		List<String> deleteeumAppList = new ArrayList<>();
		deleteeumAppList.add("EUM2");
		rdDetails.setAddEumpApps(deleteeumAppList);
		request.setRequestDetails(rdDetails);
		APPDMaster aPPDMaster = new APPDMaster();
		aPPDMaster.setAppGroupName("Test-AppGroupName");
		aPPDMaster.setCtrlName("cisco1");
		List<String> oldEumAppList = new ArrayList<>();
		oldEumAppList.add("EUM1");
		aPPDMaster.setEumApps(oldEumAppList);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(aPPDMaster);
		appDUserHandler.eumUpdate(request);
	}

	@Test
	public void eumUpdateTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		List<String> eumAppList = new ArrayList<>();
		eumAppList.add("EUM1");
		rdDetails.setEumApps(eumAppList);
		List<String> deleteeumAppList = new ArrayList<>();
		deleteeumAppList.add("EUM2");
		rdDetails.setAddEumpApps(deleteeumAppList);
		request.setRequestDetails(rdDetails);
		APPDMaster aPPDMaster = new APPDMaster();
		aPPDMaster.setAppGroupName("Test-AppGroupName");
		aPPDMaster.setCtrlName("cisco1");
		List<String> oldEumAppList = new ArrayList<>();
		oldEumAppList.add("EUM1");
		oldEumAppList.add("EUM2");
		aPPDMaster.setEumApps(oldEumAppList);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(aPPDMaster);
		appDUserHandler.eumUpdate(request);
	}

	@Test
	public void eumUpdateTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		List<String> eumAppList = new ArrayList<>();
		eumAppList.add("EUM1");
		eumAppList.add("EUM2");
		rdDetails.setEumApps(eumAppList);
		List<String> addeumAppList = new ArrayList<>();
		addeumAppList.add("EUM3");
		rdDetails.setAddEumpApps(addeumAppList);
		request.setRequestDetails(rdDetails);
		APPDMaster aPPDMaster = new APPDMaster();
		aPPDMaster.setAppGroupName("Test-AppGroupName");
		aPPDMaster.setCtrlName("cisco1");
		List<String> oldEumAppList = new ArrayList<>();
		oldEumAppList.add("EUM1");
		aPPDMaster.setEumApps(oldEumAppList);
		Mockito.when(aPPDMasterDAO.findByApp(any(String.class), any(String.class))).thenReturn(aPPDMaster);
		appDUserHandler.eumUpdate(request);

	}

	@Test
	public void getADbySOTest() throws Exception {
		String groupName = "Aditya APM Test";
		assertEquals("d1d8a64b-ca2b-3301-8389-77acd631fedf", appDUserHandler.getADbySO(groupName));
	}

	@Test
	public void checkIfAppDLocalUserExistTest() throws Exception {
		Mockito.doReturn("Test").when(appdUtil).appDConnectionOnlyGet(any(String.class), any(String.class),
				any(String.class), any(String.class));
		appDUserHandler.checkIfAppDLocalUserExist("test", "test");

	}

	@Test
	public void checkIfAppDLocalUserExistTest1() throws Exception {
		Mockito.doReturn(null).when(appdUtil).appDConnectionOnlyGet(any(String.class), any(String.class),
				any(String.class), any(String.class));
		appDUserHandler.checkIfAppDLocalUserExist("test", "test");

	}

	@Test
	public void createAppDLocalUsersIfNotExistsTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		rdDetails.setAdminUsers("test,test1");
		rdDetails.setViewUsers("test,test1");
		request.setRequestDetails(rdDetails);
		appDUserHandler.createAppDLocalUsersIfNotExists(request);

	}

	@Test
	public void createAppDLocalUsersIfNotExistsTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		rdDetails.setAdminUsers("");
		rdDetails.setViewUsers("test,test1");
		request.setRequestDetails(rdDetails);
		Mockito.doReturn(false).when(appDUserHandler).checkIfAppDLocalUserExist(any(String.class), any(String.class));
		appDUserHandler.createAppDLocalUsersIfNotExists(request);

	}

	@Test
	public void createAppDLocalUsersIfNotExistsTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		rdDetails.setAdminUsers("test,test1");
		rdDetails.setViewUsers("");
		request.setRequestDetails(rdDetails);
		Mockito.doReturn(true).when(appDUserHandler).checkIfAppDLocalUserExist(any(String.class), any(String.class));
		appDUserHandler.createAppDLocalUsersIfNotExists(request);

	}

	@Test
	public void createAppDLocalUserTest() throws Exception {
		Mockito.doReturn(null).when(appdUtil).appDConnection(any(String.class), any(String.class), any(String.class),
				any(String.class));
		appDUserHandler.createAppDLocalUser("test", "test");

	}

	@Test
	public void createAppDLocalUserTest1() throws Exception {
		Mockito.doReturn("{id:test}").when(appdUtil).appDConnection(any(String.class), any(String.class),
				any(String.class), any(String.class));
		appDUserHandler.createAppDLocalUser("test", "test");

	}

	@Test
	public void buildAppDetailsTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails rdDetails = new RequestDetails();
		RetryDetails rtryDetails = new RetryDetails();
		rtryDetails.setOperationCounter(1);
		rdDetails.setAppGroupName("APMApp");
		rdDetails.setCtrlName("cisco1");
		rdDetails.setAdminUsers("test,test1");
		rdDetails.setViewUsers("");
		request.setRequestDetails(rdDetails);
		request.setRetryDetails(rtryDetails);
		request.setRequestType(Constants.REQUEST_TYPE_CREATE);
		appDUserHandler.buildAppDetails(request);

	}

	@Test
	public void buildAppDetailsTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("AppD-GATS-3DIT-Orchestration-na");
		mapping.setViewGroupName("AppD-GATS-3DIT-Orchestration-nv");
		mappingList.add(mapping);
		RequestDetails rdDetails = new RequestDetails();
		RetryDetails rtryDetails = new RetryDetails();
		rtryDetails.setOperationCounter(2);
		request.setRequestDetails(rdDetails);
		request.setRequestType("update");
		request.setRetryDetails(rtryDetails);
		request.setMapping(mappingList);
		request.setRequestType(Constants.REQUEST_TYPE_CREATE);
		appDUserHandler.buildAppDetails(request);

	}

}