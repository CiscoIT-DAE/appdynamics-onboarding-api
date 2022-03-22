package com.cisco.maas.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dto.LicenseRule;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;

public class AppDLicensesHandlerTest {
	private static final Logger logger = LoggerFactory.getLogger(AppDLicensesHandlerTest.class);

	@InjectMocks
	@Spy
	AppDLicensesHandler licensesHandler;

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
	public void prepareJSONTest() throws Exception {
		licensesHandler.prepareJSON("APMApp", "23", "8667be54-5612-408e-bd86-7266bbb4a6ff",
				"bc5b4ed6-d96a-407a-b5e4-cb731547100c");
	}

	@Test
	public void prepareJSONTest2() throws Exception {
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();
		String content = new String(Files.readAllBytes(Paths.get(path)));
		JSONObject json = new JSONObject(content);
		assertNotNull(licensesHandler.prepareJSON(json, 10, 10));
	}

	@Test
	public void readLicenseRuleTest() throws Exception {
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();
		String content = new String(Files.readAllBytes(Paths.get(path)));
		String ctrlName = "cisco1";
		String rURL = "mds/v1/license/rules/name/";
		rURL = "https://" + ctrlName + ".saas.appdynamics.com/controller/" + rURL + "APM-n";
		logger.info("AppDLicensesHandler - License Rule Read API {}", rURL);
		Mockito.when(appdUtil.appDConnectionOnlyGet(rURL, "GET", ctrlName, "license")).thenReturn(content);
		assertNotNull(licensesHandler.readLicenseRule("APM-n", ctrlName));
		assertNull(licensesHandler.readLicenseRule("APM-n", null));
	}

	@Test
	public void createLicenseRule() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APM-n");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();	      
	    String content = new String ( Files.readAllBytes(Paths.get(path)));		
	    
	    String ctrlName="cisco1";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		
		String rURL1=rURL+"Test";
		
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnectionOnlyGet(rURL1,"GET",ctrlName,"license")).thenReturn(content);
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 

	@Test 
	public void createLicenseRule1() throws Exception
	{
		
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();	      
	    String content = new String ( Files.readAllBytes(Paths.get(path)));		
	    
	    String ctrlName="cisco1";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	
	@Test 
	public void createLicenseRule2() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp1");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();	      
	    String content = new String ( Files.readAllBytes(Paths.get(path)));		
	    
	    String ctrlName="ciscoeft";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRule4() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		File file = new File(getClass().getClassLoader().getResource("TestLicenseRule.json").getFile());
		String path = file.getPath();	      
	    String content = new String ( Files.readAllBytes(Paths.get(path)));		
	    
	    String ctrlName="cisco1nonprod";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNull(licensesHandler.createLicenseRule(request));
	} 	
	
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRule5() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String ctrlName = "cisco1";
		String rURL = "mds/v1/license/rules/name/";
		rURL = "https://" + ctrlName + ".saas.appdynamics.com/controller/" + rURL;
		LicenseRule lr = new LicenseRule();
		lr.setNoOfApmLicenses("1");
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.doReturn(lr).when(licensesHandler).readLicenseRule(any(String.class),any(String.class)); 
		licensesHandler.createLicenseRule(request);
	}

	@Test
	public void updateLicenseRuleTest() throws Exception {
		String json = "{ \"name\": \"AppD-Test-New2\", \"entitlements\": [ { \"license_module_type\": \"MACHINE_AGENT\", \"number_of_licenses\": 3 },{ \"license_module_type\": \"SIM_AGENT\", \"number_of_licenses\": 3 },{ \"license_module_type\": \"NETVIZ_AGENT\", \"number_of_licenses\": 3 },{\"license_module_type\": \"APM\", \"number_of_licenses\": 3 }], \"access_key\": \"b8c3ade5-8508-4afd-bb0e-5d66616c576b\"}";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp1");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(json);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		licensesHandler.updateLicenses(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateLicenseRuleTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp1");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);

		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(null);

		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		assertNotNull(licensesHandler.updateLicenses(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateLicenseRuleTest3() throws Exception {
		String json = "{ \"name\": \"AppD-Test-New2\", \"entitlements\": [ { \"license_module_type\": \"MACHINE_AGENT\", \"number_of_licenses\": 3 },{\"license_module_type\": \"APM\", \"number_of_licenses\": 3 }], \"access_key\": \"b8c3ade5-8508-4afd-bb0e-5d66616c576b\"}";

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp1");
		requestDetails.setCtrlName("ciscoeft");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);

		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(json);

		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		assertNotNull(licensesHandler.updateLicenses(request));
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateLicenseRuleTest4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		assertNotNull(licensesHandler.updateLicenses(request));
	}

	@Test
	public void handleRequesttest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LHANDLER");
		rDetails.setOperationCounter(1);
		request.setRequestType("create");
		request.setRetryDetails(rDetails);
		Mockito.doReturn("gfasbv-653hvd-asb").when(licensesHandler).createLicenseRule(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(licensesHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(licensesHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		licensesHandler.handleRequest(request);
	}

	@Test
	public void handleRequesttest2() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LHANDLER");
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		request.setRetryDetails(rDetails);
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1");
		requestDetails.setApmLicenses(5);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(licensesHandler).updateLicenses(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(licensesHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(licensesHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		licensesHandler.handleRequest(request);
	}

	@Test
	public void handleRequesttest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LHANDLER");
		rDetails.setOperationCounter(1);
		request.setRequestType("delete");
		request.setRetryDetails(rDetails);
		Mockito.doReturn(true).when(licensesHandler).deleteLicenses(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(licensesHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(licensesHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		licensesHandler.handleRequest(request);
	}

	@Test
	public void handleRequesttest4() throws Exception {
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LHANDLER");
		rDetails.setOperationCounter(1);
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setResourceMove(true);
		request.setRequestType("update");
		request.setRetryDetails(rDetails);
		Mockito.doReturn(request).when(licensesHandler).resourceMoveUpdateLicense(any(AppDOnboardingRequest.class));
		Mockito.doNothing().when(licensesHandler).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(licensesHandler).handleRequestImpl(any(AppDOnboardingRequest.class));
		licensesHandler.handleRequest(request);
	}

	@Test
	public void deleteLicensesTest() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("Application Performance Management");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);

		String json = "{\"id\":\"2b21bf89-5f68-4388-af5a-68e0386c9e88\",\"version\":376,\"name\":\"Application Performance Management\",\"description\":\"Create By Onboarding API\",\"enabled\":true,\"constraints\":[],\"entitlements\":[],\"account_id\":\"8667be54-5612-408e-bd86-7266bbb4a6ff\",\"access_key\":\"f191a53d-2924-4117-bdb5-f02fdbccdfb1\",\"total_licenses\":null,\"peak_usage\":null}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(json);

		String response = "Done";
		Mockito.when(appdUtil.appDConnectionDelete(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);

		licensesHandler.deleteLicenses(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void deleteLicensesTest2() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("Application Performance Management");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);

		String json = "{\"version\":376,\"name\":\"Application Performance Management\",\"description\":\"Create By Onboarding API\",\"enabled\":true,\"constraints\":[],\"entitlements\":[],\"account_id\":\"8667be54-5612-408e-bd86-7266bbb4a6ff\",\"access_key\":\"f191a53d-2924-4117-bdb5-f02fdbccdfb1\",\"total_licenses\":null,\"peak_usage\":null}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(json);

		String response = "Done";
		Mockito.when(appdUtil.appDConnectionDelete(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);

		licensesHandler.deleteLicenses(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void deleteLicensesTest3() throws Exception {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("Application Performance Management");
		requestDetails.setCtrlName("cisco1nonprod");
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestDetails(requestDetails);

		String json = "{\"id\":\"2b21bf89-5f68-4388-af5a-68e0386c9e88\",\"version\":376,\"name\":\"Application Performance Management\",\"description\":\"Create By Onboarding API\",\"enabled\":true,\"constraints\":[],\"entitlements\":[],\"account_id\":\"8667be54-5612-408e-bd86-7266bbb4a6ff\",\"access_key\":\"f191a53d-2924-4117-bdb5-f02fdbccdfb1\",\"total_licenses\":null,\"peak_usage\":null}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(json);

		Mockito.when(appdUtil.appDConnectionDelete(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(null);

		licensesHandler.deleteLicenses(request);
	}

	@Test
	public void resourceMoveUpdateLicenseTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		requestDetails.setOldAppGroupName("APM");
		request.setRequestDetails(requestDetails);
		String response = "{ \"id\": \"0ab9a8ae-ba2b-4667-8818-8b44bfdf17b7\",  \"name\": \"AppD-Test\",\"constraints\":"
				+ " [ { \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\","
				+ "\"match_conditions\": []},{\"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\"match_conditions\":"
				+ " [ {\"match_type\": \"EQUALS\", \"attribute_type\": \"NAME\", \"match_string\": \"AppD-Test\" }] } ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		licensesHandler.resourceMoveUpdateLicense(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateLicenseTest1() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		requestDetails.setOldAppGroupName("APM");
		request.setRequestDetails(requestDetails);
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(null);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		licensesHandler.resourceMoveUpdateLicense(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void resourceMoveUpdateLicenseTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		requestDetails.setOldAppGroupName("APM");
		request.setRequestDetails(requestDetails);
		String response = "{ \"id\": \"0ab9a8ae-ba2b-4667-8818-8b44bfdf17b7\",  \"name\": \"AppD-Test\",\"constraints\":"
				+ " [ { \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\","
				+ "\"match_conditions\": []},{\"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\"match_conditions\":"
				+ " [ {\"match_type\": \"EQUALS\", \"attribute_type\": \"NAME\", \"match_string\": \"AppD-Test\" }] } ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		licensesHandler.resourceMoveUpdateLicense(request);
	}

	@Test(expected = Exception.class)
	public void resourceMoveUpdateLicenseTest3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		requestDetails.setOldAppGroupName("APM");
		request.setRequestDetails(requestDetails);
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class)));
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		licensesHandler.resourceMoveUpdateLicense(request);
	}
}