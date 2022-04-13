package com.cisco.maas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
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
	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;

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
	public void createLicenseRule() throws Exception
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
		 Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"apm-agent\",\n"
		    		+ "            \"kind\": \"AGENT_BASED\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn(null);
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleResponseNotNull() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"apm-agent\",\n"
		    		+ "            \"kind\": \"AGENT_BASED\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn("{\n"
		    				+ "    \"id\": \"4fe654cf-3d03-40b3-9e5e-9ac7b05d2435\",\n"
		    				+ "    \"version\": 0,\n"
		    				+ "    \"name\": \"Test-fabeha-check-for-license2\",\n"
		    				+ "    \"description\": \"Created by AppD On-boarding Automation\",\n"
		    				+ "    \"enabled\": true,\n"
		    				+ "    \"constraints\": [\n"
		    				+ "        {\n"
		    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\n"
		    				+ "            \"constraint_type\": \"ALLOW_SELECTED\",\n"
		    				+ "            \"match_conditions\": [\n"
		    				+ "                {\n"
		    				+ "                    \"match_type\": \"EQUALS\",\n"
		    				+ "                    \"attribute_type\": \"NAME\",\n"
		    				+ "                    \"match_string\": \"Test-fabeha-check-for-license2\"\n"
		    				+ "                }\n"
		    				+ "            ]\n"
		    				+ "        },\n"
		    				+ "        {\n"
		    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\",\n"
		    				+ "            \"constraint_type\": \"ALLOW_ALL\",\n"
		    				+ "            \"match_conditions\": []\n"
		    				+ "        }\n"
		    				+ "    ],\n"
		    				+ "    \"entitlements\": [\n"
		    				+ "        {\n"
		    				+ "            \"license_module_type\": \"NETVIZ\",\n"
		    				+ "            \"number_of_licenses\": 0\n"
		    				+ "        },\n"
		    				+ "        {\n"
		    				+ "            \"license_module_type\": \"MACHINE_AGENT\",\n"
		    				+ "            \"number_of_licenses\": 8\n"
		    				+ "        },\n"
		    				+ "        {\n"
		    				+ "            \"license_module_type\": \"SIM_MACHINE_AGENT\",\n"
		    				+ "            \"number_of_licenses\": 0\n"
		    				+ "        },\n"
		    				+ "        {\n"
		    				+ "            \"license_module_type\": \"APM\",\n"
		    				+ "            \"number_of_licenses\": 8\n"
		    				+ "        }\n"
		    				+ "    ],\n"
		    				+ "    \"account_id\": \"ff112693-e8c1-43d1-9838-2bb69b6c0e0a\",\n"
		    				+ "    \"access_key\": \"a5a923c5-5e7b-4ff6-b178-05f52a7548c4\",\n"
		    				+ "    \"total_licenses\": null,\n"
		    				+ "   \n"
		    				+ "}");
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
		assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleNoModel() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"xyz\",\n"
		    		+ "            \"kind\": \"abc_efg\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn(null);
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(null);
		assertNotNull(licensesHandler.createLicenseRule(request));
	}
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleModelKindInvalid() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"apm-agent\",\n"
		    		+ "            \"kind\": \"abc_efg\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn(null);
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(null);
		assertNotNull(licensesHandler.createLicenseRule(request));
	}
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleModelException() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenThrow(IOException.class);
		assertNotNull(licensesHandler.createLicenseRule(request));
	}
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleModelResponseNullValueTest() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{}");
		assertNotNull(licensesHandler.createLicenseRule(request));
	}
	
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleModelResponsePackageTest() throws Exception
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
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\"packages\":[]}");
		assertNotNull(licensesHandler.createLicenseRule(request));
	}
	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleAppDConnectionFailed() throws Exception
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
	    String ctrlName="ciscoeft";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"apm-agent\",\n"
		    		+ "            \"kind\": \"AGENT_BASED\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn(null);
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(null);
		assertNotNull(licensesHandler.createLicenseRule(request));
	} 

	@Test(expected=AppDOnboardingException.class)
	public void createLicenseRuleAppDConnectionException() throws Exception
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
	    String ctrlName="ciscoeft";
	    String rURL="mds/v1/license/rules/name/";		
		rURL="https://"+ctrlName+".saas.appdynamics.com/controller/"+rURL;
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 51   \n"
		 		+ "}").thenReturn("{\n"
		    		+ "    \"accountId\": 51, \n"
		    		+ "    \"packages\": [\n"
		    		+ "        {\n"
		    		+ "            \"packageName\": \"apm-agent\",\n"
		    		+ "            \"kind\": \"AGENT_BASED\",\n"
		    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
		    		+ "            \"licenseUnits\": 185,\n"
		    		+ "            \"properties\": {\n"
		    		+ "                \"licenseModel\": \"FIXED\",\n"
		    		+ "                \"edition\": \"PRO\",\n"
		    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
		    		+ "            },\n"
		    		+ "            \"agentTypes\": [\n"
		    		+ "                \"APM\"\n"
		    		+ "            ]\n"
		    		+ "        }\n"
		    		+ "    ]\n"
		    		+ "}").thenReturn(null);
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenThrow(IOException.class);
		assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test 
	public void createLicenseRuleForInfraEnterprise() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);	      
	    String content = "{\n"
	    		+ "    \"id\": \"123\",\n"
	    		+ "    \"accountId\": 186,\n"
	    		+ "    \"name\": \"test-check\",\n"
	    		+ "    \"licenseKey\": \"abc123\",\n"
	    		+ "    \"filters\": [\n"
	    		+ "        {\n"
	    		+ "            \"id\": \"xyz7\",\n"
	    		+ "            \"type\": \"APPLICATION\",\n"
	    		+ "            \"operator\": \"ID_EQUALS\",\n"
	    		+ "            \"value\": \"3736\"\n"
	    		+ "        }\n"
	    		+ "    ],\n"
	    		+ "    \"limits\": [\n"
	    		+ "        {\n"
	    		+ "            \"id\": \"abc7\",\n"
	    		+ "            \"package\": \"ENTERPRISE\",\n"
	    		+ "            \"units\": 1\n"
	    		+ "        }\n"
	    		+ "    ],\n"
	    		+ "    \"tags\": [],\n"
	    		+ "    \"createdDate\": \"2022-04-11T11:02:32Z\",\n"
	    		+ "    \"lastUpdatedDate\": \"2022-04-11T11:02:32Z\",\n"
	    		+ "    \"disabled\": false,\n"
	    		+ "    \"machineAgentPriority\": false\n"
	    		+ "}";		
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 186   \n"
		 		+ "}").thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "           \n"
		 				+ "        },\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"PREMIUM\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn("{\n"
		 						+ "    \"id\": 186  \n"
		 						+ "}");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRuleForInfraEnterpriseExceptionForAccountIdTest() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);	      
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenThrow(IOException.class);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRuleForInfraEnterpriseEmptyAccountIdTest() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);	      
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{}");
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRuleForInfraEnterpriseAccountIdIsNullTest() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);	      
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
				+ "    \"id\": null\n"
				+ "}");
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRuleForInfraEnterpriseException() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 186   \n"
		 		+ "}").thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "           \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn("{\n"
		 						+ "    \"id\": 186  \n"
		 						+ "}");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenThrow(IOException.class);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test (expected=AppDOnboardingException.class)
	public void createLicenseRuleForInfraInvalidModelKind() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 186   \n"
		 		+ "}").thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"xyz\",\n"
		 				+ "           \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn("{\n"
		 						+ "    \"id\": 186  \n"
		 						+ "}");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenThrow(IOException.class);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	} 
	@Test 
	public void createLicenseRuleForInfraPremiumException() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String content = "{\n"
	    		+ "    \"id\": \"123\",\n"
	    		+ "    \"accountId\": 186,\n"
	    		+ "    \"name\": \"test-check\",\n"
	    		+ "    \"licenseKey\": \"abc123\",\n"
	    		+ "    \"filters\": [\n"
	    		+ "        {\n"
	    		+ "            \"id\": \"xyz7\",\n"
	    		+ "            \"type\": \"APPLICATION\",\n"
	    		+ "            \"operator\": \"ID_EQUALS\",\n"
	    		+ "            \"value\": \"3736\"\n"
	    		+ "        }\n"
	    		+ "    ],\n"
	    		+ "    \"limits\": [\n"
	    		+ "        {\n"
	    		+ "            \"id\": \"abc7\",\n"
	    		+ "            \"package\": \"PREMIUM\",\n"
	    		+ "            \"units\": 1\n"
	    		+ "        }\n"
	    		+ "    ],\n"
	    		+ "    \"tags\": [],\n"
	    		+ "    \"createdDate\": \"2022-04-11T11:02:32Z\",\n"
	    		+ "    \"lastUpdatedDate\": \"2022-04-11T11:02:32Z\",\n"
	    		+ "    \"disabled\": false,\n"
	    		+ "    \"machineAgentPriority\": false\n"
	    		+ "}";	
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 186   \n"
		 		+ "}").thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"PREMIUM\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "           \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn("{\n"
		 						+ "    \"id\": 186  \n"
		 						+ "}");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		//Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("{\"account\":{\"key\":\"1234\"}}");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
	    assertNotNull(licensesHandler.createLicenseRule(request));
	}
	@Test
	public void createLicenseRuleForPremium() throws Exception
	{  	AppDOnboardingException e = null;
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rDetails=new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);	  
		try{
		    	
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn("{\n"
		 		+ "    \"id\": 186   \n"
		 		+ "}").thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"PREMIUM\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn("{\n"
		 						+ "    \"id\": 186  \n"
		 						+ "}");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
	    Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(null);
	    licensesHandler.createLicenseRule(request);
	    }
	    catch(AppDOnboardingException error){
	    	e=error;
	    }
	    assertNotNull(e);
	    verify(licensesHandler, times(1)).createLicenseRule(request);
	} 
	@Test
	public void updateLicenseRuleTestForAgentBasedModel() throws Exception {
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
				any(String.class))).thenReturn("{\n"
				 		+ "    \"id\": 186   \n"
				 		+ "}").thenReturn("{\n"
					    		+ "    \"accountId\": 186, \n"
					    		+ "    \"packages\": [\n"
					    		+ "        {\n"
					    		+ "            \"packageName\": \"apm-agent\",\n"
					    		+ "            \"kind\": \"AGENT_BASED\",\n"
					    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
					    		+ "            \"licenseUnits\": 185,\n"
					    		+ "            \"properties\": {\n"
					    		+ "                \"licenseModel\": \"FIXED\",\n"
					    		+ "                \"edition\": \"PRO\",\n"
					    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
					    		+ "            },\n"
					    		+ "            \"agentTypes\": [\n"
					    		+ "                \"APM\"\n"
					    		+ "            ]\n"
					    		+ "        }\n"
					    		+ "    ]\n"
					    		+ "}").thenReturn("{\n"
					    				+ "    \"id\": \"4fe654cf-3d03-40b3-9e5e-9ac7b05d2435\",\n"
					    				+ "    \"version\": 0,\n"
					    				+ "    \"name\": \"Test-fabeha-check-for-license2\",\n"
					    				+ "    \"description\": \"Created by AppD On-boarding Automation\",\n"
					    				+ "    \"enabled\": true,\n"
					    				+ "    \"constraints\": [\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_SELECTED\",\n"
					    				+ "            \"match_conditions\": [\n"
					    				+ "                {\n"
					    				+ "                    \"match_type\": \"EQUALS\",\n"
					    				+ "                    \"attribute_type\": \"NAME\",\n"
					    				+ "                    \"match_string\": \"Test-fabeha-check-for-license2\"\n"
					    				+ "                }\n"
					    				+ "            ]\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_ALL\",\n"
					    				+ "            \"match_conditions\": []\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"entitlements\": [\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"NETVIZ\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"SIM_MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"APM\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"account_id\": \"ff112693-e8c1-43d1-9838-2bb69b6c0e0a\",\n"
					    				+ "    \"access_key\": \"a5a923c5-5e7b-4ff6-b178-05f52a7548c4\",\n"
					    				+ "    \"total_licenses\": null,\n"
					    				+ "   \n"
					    				+ "}").thenReturn(json);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		licensesHandler.updateLicenses(request);
	}
	@Test
	public void updateLicenseRuleForInfraTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn("[{  \"name\": \"test-check\",\n"
		 								+ "    \"id\": \"3736\" \n"
		 								+ "}]").thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		
		assertEquals(true, licensesHandler.updateLicenses(request));
	}
	@Test(expected=AppDOnboardingException.class)
	public void updateLicenseRuleForInfraIOExceptionTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn("[{  \"name\": \"test-check\",\n"
		 								+ "    \"id\": \"3736\" \n"
		 								+ "}]").thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(IOException.class);
		
		 licensesHandler.updateLicenses(request);
	}
	
	@Test(expected=AppDOnboardingException.class)
	public void updateLicenseRuleForInfraNullResponseTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn("[{  \"name\": \"test-check\",\n"
		 								+ "    \"id\": \"3736\" \n"
		 								+ "}]").thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		licensesHandler.updateLicenses(request);
		verify(licensesHandler, times(1)).updateLicenses(request);
	}
	@Test
	public void updateLicenseRuleForInfraNullAllocationIdTest() throws Exception {
		AppDOnboardingException error=null;
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		try {
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn(null).thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		licensesHandler.updateLicenses(request);
		}
		catch(AppDOnboardingException e)
		{
			error=e;
		}
		assertNotNull(error);
		verify(licensesHandler, times(1)).updateLicenses(request);
	}
	@Test
	public void updateLicenseRuleForInfraAllocationIdIOExceptionTest() throws Exception {
		AppDOnboardingException error=null;
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		try {
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"ENTERPRISE\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"INFRASTRUCTURE_BASED\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenThrow(IOException.class);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
		licensesHandler.updateLicenses(request);
		}
		catch(AppDOnboardingException e)
		{
			error=e;
		}
		assertNotNull(error);
		verify(licensesHandler, times(1)).updateLicenses(request);
	}
	@Test(expected=AppDOnboardingException.class)
	public void updateLicenseRuleForInfraForInvalidModelTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"xyz\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"abc\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn("[{  \"name\": \"test-check\",\n"
		 								+ "    \"id\": \"3736\" \n"
		 								+ "}]").thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn("Done");
		
		licensesHandler.updateLicenses(request);
	}
	@Test(expected=AppDOnboardingException.class)
	public void updateLicenseRuleForInfraForIOExceptionTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails = new RetryDetails();
		rDetails.setFailureModule("LicenseHandler");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test-check");
		requestDetails.setCtrlName("devnet");
		requestDetails.setApmLicenses(5);
		request.setRetryDetails(rDetails);
		request.setRequestDetails(requestDetails);
		String responseId ="{\n"
				+ "    \"id\": \"186\" \n"
				+ "}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(responseId).thenReturn("{\n"
		 				+ "    \"accountId\": 186,\n"
		 				+ "    \"status\": \"LIMITED\",\n"
		 				+ "    \"packages\": [\n"
		 				+ "        {\n"
		 				+ "            \"packageName\": \"xyz\",\n"
		 				+ "            \"type\": \"PAID\",\n"
		 				+ "            \"kind\": \"abc\",\n"
		 				+ "   \n"
		 				+ "            \n"
		 				+ "        }\n"
		 				+ "    ]\n"
		 				+ "}").thenReturn(responseId).thenReturn("[{  \"name\": \"test-check\",\n"
		 								+ "    \"id\": \"3736\" \n"
		 								+ "}]").thenReturn(responseId);

		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class),any(String.class))).thenReturn("3736");
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(IOException.class);
		
		licensesHandler.updateLicenses(request);
	}
	
	@Test(expected=AppDOnboardingException.class)
	public void updateLicenseRuleTestForAgentExceptionTest() throws Exception {
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
				any(String.class))).thenReturn("{\n"
				 		+ "    \"id\": 186   \n"
				 		+ "}").thenReturn("{\n"
					    		+ "    \"accountId\": 186, \n"
					    		+ "    \"packages\": [\n"
					    		+ "        {\n"
					    		+ "            \"packageName\": \"apm-agent\",\n"
					    		+ "            \"kind\": \"AGENT_BASED\",\n"
					    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
					    		+ "            \"licenseUnits\": 185,\n"
					    		+ "            \"properties\": {\n"
					    		+ "                \"licenseModel\": \"FIXED\",\n"
					    		+ "                \"edition\": \"PRO\",\n"
					    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
					    		+ "            },\n"
					    		+ "            \"agentTypes\": [\n"
					    		+ "                \"APM\"\n"
					    		+ "            ]\n"
					    		+ "        }\n"
					    		+ "    ]\n"
					    		+ "}").thenReturn("{\n"
					    				+ "    \"id\": \"4fe654cf-3d03-40b3-9e5e-9ac7b05d2435\",\n"
					    				+ "    \"version\": 0,\n"
					    				+ "    \"name\": \"Test-fabeha-check-for-license2\",\n"
					    				+ "    \"description\": \"Created by AppD On-boarding Automation\",\n"
					    				+ "    \"enabled\": true,\n"
					    				+ "    \"constraints\": [\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_SELECTED\",\n"
					    				+ "            \"match_conditions\": [\n"
					    				+ "                {\n"
					    				+ "                    \"match_type\": \"EQUALS\",\n"
					    				+ "                    \"attribute_type\": \"NAME\",\n"
					    				+ "                    \"match_string\": \"Test-fabeha-check-for-license2\"\n"
					    				+ "                }\n"
					    				+ "            ]\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_ALL\",\n"
					    				+ "            \"match_conditions\": []\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"entitlements\": [\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"NETVIZ\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"SIM_MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"APM\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"account_id\": \"ff112693-e8c1-43d1-9838-2bb69b6c0e0a\",\n"
					    				+ "    \"access_key\": \"a5a923c5-5e7b-4ff6-b178-05f52a7548c4\",\n"
					    				+ "    \"total_licenses\": null,\n"
					    				+ "   \n"
					    				+ "}").thenReturn(json);
		Mockito.when(requestHandler.getUpdatedRequest(any(AppDOnboardingRequest.class))).thenReturn(request);
		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenThrow(IOException.class);
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
				any(String.class))).thenReturn("{\n"
				 		+ "    \"id\": 186   \n"
				 		+ "}").thenReturn("{\n"
					    		+ "    \"accountId\": 186, \n"
					    		+ "    \"packages\": [\n"
					    		+ "        {\n"
					    		+ "            \"packageName\": \"apm-agent\",\n"
					    		+ "            \"kind\": \"AGENT_BASED\",\n"
					    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
					    		+ "            \"licenseUnits\": 185,\n"
					    		+ "            \"properties\": {\n"
					    		+ "                \"licenseModel\": \"FIXED\",\n"
					    		+ "                \"edition\": \"PRO\",\n"
					    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
					    		+ "            },\n"
					    		+ "            \"agentTypes\": [\n"
					    		+ "                \"APM\"\n"
					    		+ "            ]\n"
					    		+ "        }\n"
					    		+ "    ]\n"
					    		+ "}").thenReturn(null);

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
				any(String.class))).thenReturn("{\n"
				 		+ "    \"id\": 186   \n"
				 		+ "}").thenReturn("{\n"
					    		+ "    \"accountId\": 186, \n"
					    		+ "    \"packages\": [\n"
					    		+ "        {\n"
					    		+ "            \"packageName\": \"apm-agent\",\n"
					    		+ "            \"kind\": \"AGENT_BASED\",\n"
					    		+ "            \"expirationDate\": \"2022-08-17T06:59:59Z\",\n"
					    		+ "            \"licenseUnits\": 185,\n"
					    		+ "            \"properties\": {\n"
					    		+ "                \"licenseModel\": \"FIXED\",\n"
					    		+ "                \"edition\": \"PRO\",\n"
					    		+ "                \"maxNumberOfLicenses\": \"185\"\n"
					    		+ "            },\n"
					    		+ "            \"agentTypes\": [\n"
					    		+ "                \"APM\"\n"
					    		+ "            ]\n"
					    		+ "        }\n"
					    		+ "    ]\n"
					    		+ "}").thenReturn("{\n"
					    				+ "    \"id\": \"4fe654cf-3d03-40b3-9e5e-9ac7b05d2435\",\n"
					    				+ "    \"version\": 0,\n"
					    				+ "    \"name\": \"Test-fabeha-check-for-license2\",\n"
					    				+ "    \"description\": \"Created by AppD On-boarding Automation\",\n"
					    				+ "    \"enabled\": true,\n"
					    				+ "    \"constraints\": [\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_SELECTED\",\n"
					    				+ "            \"match_conditions\": [\n"
					    				+ "                {\n"
					    				+ "                    \"match_type\": \"EQUALS\",\n"
					    				+ "                    \"attribute_type\": \"NAME\",\n"
					    				+ "                    \"match_string\": \"Test-fabeha-check-for-license2\"\n"
					    				+ "                }\n"
					    				+ "            ]\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"entity_type_id\": \"com.appdynamics.modules.apm.topology.impl.persistenceapi.model.MachineEntity\",\n"
					    				+ "            \"constraint_type\": \"ALLOW_ALL\",\n"
					    				+ "            \"match_conditions\": []\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"entitlements\": [\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"NETVIZ\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"SIM_MACHINE_AGENT\",\n"
					    				+ "            \"number_of_licenses\": 0\n"
					    				+ "        },\n"
					    				+ "        {\n"
					    				+ "            \"license_module_type\": \"APM\",\n"
					    				+ "            \"number_of_licenses\": 8\n"
					    				+ "        }\n"
					    				+ "    ],\n"
					    				+ "    \"account_id\": \"ff112693-e8c1-43d1-9838-2bb69b6c0e0a\",\n"
					    				+ "    \"access_key\": \"a5a923c5-5e7b-4ff6-b178-05f52a7548c4\",\n"
					    				+ "    \"total_licenses\": null,\n"
					    				+ "   \n"
					    				+ "}").thenReturn(json);

		Mockito.when(
				appdUtil.appDConnection(any(String.class), any(String.class), any(String.class), any(String.class)))
				.thenReturn(null);
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