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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.maas.dto.LicenseRule;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;
/**
 * This class contains methods to read license, update license and delete license.
 */
@Service
public class AppDLicensesHandler extends AppDOnboardingRequestHandlerImpl {
	private static final Logger logger = LoggerFactory.getLogger(AppDLicensesHandler.class);
	private String controllerPrefix;
	private String licenseAccountIdURL;
	private String  appdAccountIdURL;
	private static final String PROTO = "https://";
	private static final String ACCESS_KEY = "access_key";
	private static final String ENTITLEMENTS = "entitlements";
	private static final String LICENSE_MODULE_TYPE = "license_module_type";
	private static final String NUMBER_OF_LICENSES = "number_of_licenses";
	private static final String UTF_8 = "UTF-8";
	private static final String FAILUREMODULE = "LIC_HANDLER";
	private static final String LICENSE_RULE_BASE_URL = "mds/v1/license/rules";
	private static final String LICENSE_RULE_NAME_URL = "mds/v1/license/rules/name/";
	private static final String LICENSE="license";
	private static final String LICENSE_MODEL_ENTERPRISE="ENTERPRISE";
	private static final String LICENSE_MODEL_PREMIUM="PREMIUM";
	private static final String LICENSE_RULE_NAME= "$LICENSE_RULE_NAME$";
	private static final String LICENSE_MODEL_APM_AGENT ="apm-agent";
	private static final String LICENSE_INFRA_URL="licensing/v1/account/"; 

	@Autowired
	AppDynamicsUtil appdUtil;
	@Autowired
	AppDAlertsHandler aHandler;
	@Autowired
	DBHandler dbHandler;
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	
	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public AppDLicensesHandler() throws IOException {
		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			Properties properties = new Properties();
			properties.load(input);
			controllerPrefix = properties.getProperty("appd.prefix");
		    licenseAccountIdURL = properties.getProperty("appd.getLicenseAccountID.url");
		    appdAccountIdURL = properties.getProperty("appd.getAppAccountID.url");
		}
	}
	
	/**
	 * This method handle request based on request type and sets next handler.
	 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation.
	 * @throws: AppDOnboardingException.
	 */

	public void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("handleRequest - START");
		request.getRetryDetails().setFailureModule(FAILUREMODULE);
		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Create Type");
			String licenseKey = this.createLicenseRule(request);
			logger.info("handleRequest - Create Request Processing Successful Calling Alert Handler");
			request.setLicenseKey(licenseKey);
			request.getRetryDetails().setOperationCounter(1);
			this.setNextHandler(aHandler);
			super.handleRequestImpl(request);
			logger.info("handleRequest - create - END");
		} else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Update Type");
			if (request.isResourceMove()) {
				request = this.resourceMoveUpdateLicense(request);
				logger.info("handleRequest - Update Request Processing Successful Calling DB Handler");
				request.getRetryDetails().setOperationCounter(1);
				this.setNextHandler(dbHandler);
				super.handleRequestImpl(request);
				logger.info("handleRequest - update - resource is moved -  END");
			} else if (!request.isResourceMove()) {
				boolean result = this.updateLicenses(request);
				if (result) {
					logger.info("handleRequest - Update Request Processing Successful Calling Alert Handler");
					request.getRetryDetails().setOperationCounter(1);
					this.setNextHandler(aHandler);
					super.handleRequestImpl(request);
					logger.info("handleRequest - update - resource is not moved -  END");
				}
			}
		} 
	}

	
	/**
	 * This method reads appD application license rule information.
	 * @param appGroupName: String type application group name.
	 * @param ctrlName: String type controller name.
	 * @throws: IOException.
	 * @returns : LicenseRule.
	 */
	public LicenseRule readLicenseRule(String appGroupName, String ctrlName) throws IOException {
		logger.info("readLicenseRule - START");
		LicenseRule licenseRule = new LicenseRule();
		
		 String rURL = PROTO + ctrlName + controllerPrefix + LICENSE_RULE_NAME_URL + appGroupName;
		logger.info("readLicenseRule - License Rule Read API :: {}", rURL);

		String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, LICENSE);

		if (response != null) {
			JSONObject res = new JSONObject(response);

			licenseRule.setJson(res);
			licenseRule.setLicenseRuleName(res.get("name").toString());
			licenseRule.setAccessKey(res.get(ACCESS_KEY).toString());

			JSONArray entitlements = res.getJSONArray(ENTITLEMENTS);

			for (int i = 0; i < entitlements.length(); i++) {
				if (("APM").equals(entitlements.getJSONObject(i).get(LICENSE_MODULE_TYPE).toString())) {
					licenseRule.setNoOfApmLicenses(entitlements.getJSONObject(3).get(NUMBER_OF_LICENSES).toString());
					logger.info("readLicenseRule - Setting No of APM Licenses {}", licenseRule.getNoOfApmLicenses());
				} else if (("MACHINE_AGENT")
						.equals(entitlements.getJSONObject(i).get(LICENSE_MODULE_TYPE).toString())) {
					licenseRule.setNoOfMALicenses(entitlements.getJSONObject(3).get(NUMBER_OF_LICENSES).toString());
					logger.info("readLicenseRule - Setting No of MA Licenses {}", licenseRule.getNoOfMALicenses());
				}
			}
			logger.info("readLicenseRule - License Rule Details Retrieved {}", licenseRule);
			logger.info("readLicenseRule - END");
			return licenseRule;
		}
		logger.info("readLicenseRule - response is null - END");
		return null;
	}
	
	/**
	 * This method creates license rule for appD application.
	 * @param AppDOnboardingRequest: AppDOnboardingRequest type which contains appD application information.	 
	 * @throws: AppDOnboardingException.
	 * @returns: String type access key.
	 */	

	public String createLicenseRule(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("createLicenseRule - START");
			logger.info("createLicenseRule - Started License Rule Create API");
			String appGroupName = URLEncoder.encode(request.getRequestDetails().getAppGroupName(), UTF_8);
			String licenseResponse = this.getLicenseModel(request.getRequestDetails().getCtrlName());
			int noOfLicenses = request.getRequestDetails().getApmLicenses();
			if(licenseResponse==null) {
		           logger.error("createLicenseRule - license model package does not exist -  END");
					logger.info("createLicenseRule - Creation of License failed");
					throw new AppDOnboardingException(
							"AppDLicensesHandler - createLicenseRule - Create License Rule failed - license model package does not exist ",
							request);
			}
			if ((LICENSE_MODEL_ENTERPRISE).equals(licenseResponse) || (LICENSE_MODEL_PREMIUM).equals(licenseResponse)) {
				return (this.createLicenseRuleForInfra(request, appGroupName, noOfLicenses, licenseResponse));
			} else {
				String accountId;
				String accountConstant = "account";
				String accountIdResponse = appdUtil.getRequest(licenseAccountIdURL);
				JSONObject accountIdResponseJSON = new JSONObject(accountIdResponse);
				if (accountIdResponseJSON.length() != 0 && accountIdResponseJSON.getJSONObject(accountConstant) != null
						&& accountIdResponseJSON.getJSONObject(accountConstant).getString("key") != null) {
					accountId = accountIdResponseJSON.getJSONObject(accountConstant).getString("key");
				} else {
					logger.error("createLicenseRule - Unable to fetch controller account id. "
							+ "Response returned from controller is {0}", accountIdResponseJSON);
					throw new AppDOnboardingException("AppDLicensesHandler - createLicenseRule - "
							+ "Fetching of contoller id unsuccessful. Create License Rule Failed");
				}
				String accessKey = UUID.randomUUID().toString();
				String rURL = PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix
						+ LICENSE_RULE_BASE_URL;
				logger.info("createLicenseRule - License Rule Create API {}", rURL);
				LicenseRule lr = this.readLicenseRule(appGroupName, request.getRequestDetails().getCtrlName());
				if (lr == null) {
					String json = this.prepareJSON(appGroupName, String.valueOf(noOfLicenses), accountId, accessKey);
					String response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, LICENSE);
					if (response != null) {
						JSONObject res = new JSONObject(response);
						logger.info("createLicenseRule - License Rule Create API Success with AccessKey {}",
								res.get(ACCESS_KEY));
						logger.info("createLicenseRule - response not null - END");
						return res.get(ACCESS_KEY).toString();
					}
				} else {
					logger.error("createLicenseRule - license rule is not null -  END");
					logger.info("createLicenseRule - LR already exists");
					request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
					throw new AppDOnboardingException(
							"AppDLicensesHandler - createLicenseRule - License Rule already exists need manual intervention",
							request);
				}
				logger.error("createLicenseRule - license rule is null and response also null -  END");
				throw new AppDOnboardingException(
						"AppDLicensesHandler - createLicenseRule - Create License Rule Failed", request);
			}
			
		} catch (Exception e) {
			logger.error("createLicenseRule - Exception -  ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - createLicenseRule - Create License Rule Failed",
					request, e);
		}
	}
	/**
	 * This method creates license rule for appD application for INFRASTRUCTURE based licensing model.
	 * @param AppDOnboardingRequest: AppDOnboardingRequest type which contains appD application information.	 
	 * @throws: AppDOnboardingException.
	 * @returns: String type license key.
	 */	
	private String createLicenseRuleForInfra(AppDOnboardingRequest request, String appGroupName, int noOfLicenses, String licenseResponse) throws AppDOnboardingException {
		try{
		String infraURL = PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix + LICENSE_INFRA_URL+this.getappdAccountId(request.getRequestDetails().getCtrlName())+ "/allocation";
		String applicationId=appDApplicationCreationHandler.getAppID(request.getRequestDetails().getCtrlName(),request.getRequestDetails().getAppGroupName());
		String jsonForInfra = this.prepareJSONForInfra(appGroupName, String.valueOf(noOfLicenses), applicationId,licenseResponse );
		String responseForInfra = appdUtil.appDConnection(infraURL, Constants.HTTP_VERB_POST,  jsonForInfra,
				LICENSE);
			if (responseForInfra != null) {
				JSONObject res = new JSONObject(responseForInfra);
				logger.info("createLicenseRule - License Rule Create API Success with AccessKey {}",
						res.get("licenseKey"));
				logger.info("createLicenseRule - response not null - END");
				return res.get("licenseKey").toString();
			}else {
				logger.error("createLicenseRule - license rule is not null -  END");
				logger.info("createLicenseRule - LR already exists");
				request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
				throw new AppDOnboardingException(
						"AppDLicensesHandler - createLicenseRuleForInfra- License Rule already exists need manual intervention",
						request);
			}
			}catch (IOException e) {
				logger.error("createLicenseRuleForInfra - Exception -  ERROR");
				throw new AppDOnboardingException("AppDLicensesHandler - createLicenseRuleForInfra - Create License Rule Failed",
						request, e);
			}
		
	}

	/**
	 * This method creates Json object with given params.
	 * @param appGroupName: String type which application group name.	 
	 * @param noOfLicenses: String type license count.
	 * @param applicationId: String type application ID.
	 * @param packageName: String type package name of license
	 * @throws: IOException.
	 * @returns: String form of json to be sent as payload.
	 */
	private String prepareJSONForInfra(String appGroupName, String noOfLicenses, String applicationId,
			String packageName) throws IOException {
		logger.info("prepareJSONForInfra - START");
		String sCurrentLine = "";
		StringBuilder json = new StringBuilder();
		File file = new File(getClass().getClassLoader().getResource("createLicenseRuleForInfra.json").getFile());
		try (FileReader fr = new FileReader(file); BufferedReader buff = new BufferedReader(fr)) {
			while ((sCurrentLine = buff.readLine()) != null) {

				if (sCurrentLine.contains(LICENSE_RULE_NAME)) {
					json.append(sCurrentLine.replace(LICENSE_RULE_NAME, URLDecoder.decode(appGroupName, UTF_8)));
				} else if (sCurrentLine.contains("$APPLICATION_ID$")) {
					json.append(sCurrentLine.replace("$APPLICATION_ID$", applicationId));
				} else if (sCurrentLine.contains("$PACKAGE_NAME$")) {
					json.append(sCurrentLine.replace("$PACKAGE_NAME$", packageName));
				} else if (sCurrentLine.contains("$NO_OF_UNITS$")) {
					json.append(sCurrentLine.replace("$NO_OF_UNITS$", String.valueOf(noOfLicenses)));
				} else {
					json.append(sCurrentLine);
				}

			}
		}
		logger.info("prepareJSONForInfra - END");
		return json.toString();
	}


	/**
	 * This method gets the Licensing Model used by the AppDynamics Controller.
	 * @param controller: name of AppDynamics controller 
	 * @throws: AppDOnboardingException: if AppDynamics connection fails
	 * @returns: String: the license model being followed by controller and null if the license model followed does not has agents for APM
	 */
	private String getLicenseModel(String controller) throws AppDOnboardingException {
		try {
		logger.info("getLicenseModel- START");
		String accountId = getappdAccountId(controller);
		String rURL = PROTO + controller + controllerPrefix + LICENSE_INFRA_URL + accountId +"/info";
		String licenseModelResponse= appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, controller, LICENSE);
		JSONObject licenseModelResponseJSON = new JSONObject(licenseModelResponse);
		if(licenseModelResponseJSON.length()!=0 && ((JSONArray) licenseModelResponseJSON.get("packages")).length()!=0) {
			JSONArray packages= (JSONArray) licenseModelResponseJSON.get("packages");

		if (checkKeyInPackage(packages, LICENSE_MODEL_ENTERPRISE)!=null) {
			  return  LICENSE_MODEL_ENTERPRISE;
		}else if (checkKeyInPackage(packages, LICENSE_MODEL_PREMIUM)!=null) {
			  return  LICENSE_MODEL_PREMIUM;
		}else if (checkKeyInPackage(packages,LICENSE_MODEL_APM_AGENT)!=null) {
			return LICENSE_MODEL_APM_AGENT;
		}

		}
		} catch (IOException e) {
			logger.error("getLicenseModel- Exception -  ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - getLicenseModel - Create License Rule Failed", e);
		}
		return null;
	}

	/**
	 * This method check key in package name of Licensing API payload and returns the Model followed
	 * @param key: String type which contains the packageName to be checked 
	 * @param packages: JSONArray of packages followed by AppD Controller
	 * @returns: String key if found in package else returns null if key is not present
	 */
	private String checkKeyInPackage(JSONArray packages, String key) {
		for (int i = 0; i < packages.length(); i++) {
			String packageName = packages.getJSONObject(i).get("packageName").toString();
			if (packageName.equals(key)) {
				logger.info("checkKeyInPackage - key is in package {}", key);
				if (key.equals(LICENSE_MODEL_APM_AGENT)) {
					if ("AGENT_BASED".equals(packages.getJSONObject(i).get("kind").toString())) {
						logger.info("checkKeyInPackage - type of licensing model is {}",
								packages.getJSONObject(i).get("kind"));
						return key;
					}
				} else {
					if ("INFRASTRUCTURE_BASED".equals(packages.getJSONObject(i).get("kind").toString())) {
						logger.info("checkKeyInPackage - type of licensing model is {}",
								packages.getJSONObject(i).get("kind"));
						return key;
					}
				}
			} 
		} 
      return null;

	}

	/**
	 * This method gets account Id for AppDynamics controller.
	 * @param ctrlName : The AppDynamics controller for which accound Id is required
	 * @throws: AppDOnboardingException:if appDConnection throws IOException or response received is null or getting id from JSONObject throws JSONException
	 * @returns: String: the account Id of the controller
	 */	
	private String getappdAccountId(String controller) throws AppDOnboardingException {
		try {
		String rURL = PROTO + controller + controllerPrefix + appdAccountIdURL;
		String accountIdResponse= appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, controller, "licenseQuota");
		JSONObject accountIdResponseJSON = new JSONObject(accountIdResponse);
		if(accountIdResponseJSON.length()!=0 && accountIdResponseJSON.get("id")!= null) {
			String accountId = accountIdResponseJSON.get("id").toString();
			logger.info("getappdAccountId - account Id is {}",accountId);
			return accountId;
			}
		else {
			logger.error("getappdAccountId - Unable to fetch controller account id. "
					+ "Response returned from controller is {0}",accountIdResponseJSON);
			throw new AppDOnboardingException("AppDLicensesHandler - getappdAccountId - Fetching of contoller id unsuccessful");
		}
		} catch (Exception e) {
			logger.error("createLicenseRule - Exception -  ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - getappdAccountId -  Getting AppDynamics Account Id unsuccessful - getappdAccountId Failed due to exception", e);
		}
	
	}

	/**
	 * This method creates Json object with given params.
	 * @param appGroupName: String type which application group name.	 
	 * @param noOfLicenses: String type license count.
	 * @param accountId: String type account ID.
	 * @param accessKey: String type access key.
	 * @throws: IOException.
	 * @returns: String.
	 */

	public String prepareJSON(String appGroupName, String noOfLicenses, String accountId, String accessKey)
			throws IOException {
		    logger.info("prepareJSON - START");
			String sCurrentLine = "";
			StringBuilder json = new StringBuilder();
			File file = new File(getClass().getClassLoader().getResource("createLicenseRule.json").getFile());
			try (FileReader fr = new FileReader(file); BufferedReader buff = new BufferedReader(fr)) {
				while ((sCurrentLine = buff.readLine()) != null) {

					if (sCurrentLine.contains("$LICENSE_RULE_UUID$")) {
						json.append(sCurrentLine.replace("$LICENSE_RULE_UUID$", UUID.randomUUID().toString()));
					} else if (sCurrentLine.contains( LICENSE_RULE_NAME)) {
						json.append(
								sCurrentLine.replace( LICENSE_RULE_NAME, URLDecoder.decode(appGroupName, UTF_8)));
					} else if (sCurrentLine.contains("$APP_NAME$")) {
						json.append(sCurrentLine.replace("$APP_NAME$", URLDecoder.decode(appGroupName, UTF_8)));
					} else if (sCurrentLine.contains("$NETVIZ_AGENT_LICENSE_COUNT$")) {
						json.append(sCurrentLine.replace("$NETVIZ_AGENT_LICENSE_COUNT$", String.valueOf(0)));
					} else if (sCurrentLine.contains("$MACHINE_AGENT_LICENSE_COUNT$")) {
						json.append(
								sCurrentLine.replace("$MACHINE_AGENT_LICENSE_COUNT$", String.valueOf(noOfLicenses)));
					} else if (sCurrentLine.contains("$SIM_AGENT_LICENSE_COUNT$")) {
						json.append(sCurrentLine.replace("$SIM_AGENT_LICENSE_COUNT$", String.valueOf(0)));
					} else if (sCurrentLine.contains("$APM_AGENT_LICENSE_COUNT$")) {
						json.append(sCurrentLine.replace("$APM_AGENT_LICENSE_COUNT$", String.valueOf(noOfLicenses)));
					} else if (sCurrentLine.contains("$ACCESS_KEY_UUID$")) {
						json.append(sCurrentLine.replace("$ACCESS_KEY_UUID$", accessKey));
					} else if (sCurrentLine.contains("$ACCOUNT_ID$")) {
						json.append(sCurrentLine.replace("$ACCOUNT_ID$", accountId));
					} else {
						json.append(sCurrentLine);
					}
				}
			}
			logger.info("prepareJSON - END");
			return json.toString();
	}

	/**
	 * This method Updates license for given appD application.
	 * @param AppDOnboardingRequest: AppDOnboardingRequest type which contains appD application information.	 
	 * @throws: AppDOnboardingException.
	 * @returns: boolean.
	 */	
	public boolean updateLicenses(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("updateLicenses - START");
			logger.info("updateLicenses - Started Updating licenses ");

			if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
				request.getRetryDetails().setFailureModule(FAILUREMODULE);
				request = requestHandler.getUpdatedRequest(request);
			}
			String licenseResponse= this.getLicenseModel(request.getRequestDetails().getCtrlName());
			if(licenseResponse!=null && (LICENSE_MODEL_APM_AGENT.equals(licenseResponse))){
				String rURL = PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix + LICENSE_RULE_BASE_URL;
				logger.info("updateLicenses - License Rule Update API {}", rURL);
				LicenseRule lr = this.readLicenseRule(request.getRequestDetails().getAppGroupName(), request.getRequestDetails().getCtrlName());
				if (lr != null) {
					logger.info("updateLicenses - Update licenses");
					JSONObject existingLicenseData = lr.getJson();
					int noOfLicenses = request.getRequestDetails().getApmLicenses();
					String updatedLicenseData = this.prepareJSON(existingLicenseData, noOfLicenses, noOfLicenses);
					String response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_PUT,
							updatedLicenseData, LICENSE);
					if (response != null) {
						logger.info("updateLicenses - Ended Updating licenses ");
						logger.info("updateLicenses -  response is not null - END");
						return true;
					} else {
						logger.info("updateLicenses - response is null - END");
						logger.info("updateLicenses - Failed Updating licenses ");
						throw new AppDOnboardingException(
								"AppDLicensesHandler - updateLicenses - Update License Rule Failed", request);
					}
				}
				logger.error("AppDLicensesHandler - updateLicenses - lr is null - ERROR");
				throw new AppDOnboardingException("AppDLicensesHandler - updateLicenses - License Rule Does not Exist",
						request);
		}else if  ((LICENSE_MODEL_ENTERPRISE).equals(licenseResponse)|| ( LICENSE_MODEL_PREMIUM).equals(licenseResponse)){
			String applicationId=appDApplicationCreationHandler.getAppID(request.getRequestDetails().getCtrlName(),request.getRequestDetails().getAppGroupName());
			return this.updateLicensesForInfra(request, applicationId, licenseResponse);
		}else {
			logger.info("updateLicenses - Failed Updating licenses ");
			throw new AppDOnboardingException(
					"AppDLicensesHandler - updateLicenses - License model does not support apm agents licenses ", request);
		}
		 } catch (IOException e) {
			logger.error("updateLicenses - exception - ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - updateLicenses - Update License Rule Failed due to IOException",
					request, e);
		}
		
	}
	

	/**
	 * This method creates Json object with given params.
	 * @param jsonOb: JSONObject type json object.	 
	 * @param machineAgentCount: Integer type machine agent count.
	 * @param apmAgentCount: Integer type apm agent count.	 
	 * @returns: String.
	 */
	public String prepareJSON(JSONObject jsonOb, int machineAgentCount, int apmAgentCount) {
		logger.info("prepareJSON - START");
		final String licCount = NUMBER_OF_LICENSES;
		final String licType = LICENSE_MODULE_TYPE;
		JSONArray ja = jsonOb.getJSONArray(ENTITLEMENTS);
		JSONObject jo;
		for (int i = 0; i < ja.length(); i++) {
			jo = (JSONObject) ja.get(i);
			if (("MACHINE_AGENT").equals(jo.get(licType).toString())) {
				jo.put(licCount, machineAgentCount);
				ja.put(i, jo);
			}

			else if (("APM").equals(jo.get(licType).toString())) {
				jo.put(licCount, apmAgentCount);
				ja.put(i, jo);
			}
		}
		jsonOb.put(ENTITLEMENTS, ja);
		logger.info("prepareJSON - END");
		return jsonOb.toString();
	}

	/**
	 * This method updates resourceMoveLicense.
	 * @param AppDOnboardingRequest: AppDOnboardingRequest type which contains appD application information.
	 * @throws 	AppDOnboardingException.
	 * @returns: AppDOnboardingRequest.
	 */
	public AppDOnboardingRequest resourceMoveUpdateLicense(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("resourceMoveUpdateLicense - START");
			String ctrlName = request.getRequestDetails().getCtrlName();
			String oldAppName = request.getRequestDetails().getOldAppGroupName();
			logger.info("resourceMoveUpdateLicense - Started Updating licenses ");

			String rURL = PROTO + ctrlName + controllerPrefix + LICENSE_RULE_NAME_URL + oldAppName;
			logger.info("resourceMoveUpdateLicense - License Rule Read API :: {}", rURL);

			String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, LICENSE);

			if (response != null) {
				JSONObject licenseData = new JSONObject(response);
				licenseData.put("name", request.getRequestDetails().getAppGroupName());

				JSONArray constraintsData = licenseData.getJSONArray("constraints");
				String newCondition = "{\"match_type\": \"EQUALS\",\"attribute_type\": \"NAME\", \"match_string\": \""
						+ request.getRequestDetails().getAppGroupName() + "\" }";
				JSONObject newConditionObject = new JSONObject(newCondition);
				int constraintObjectIndex = 0;

				for (int i = 0; i < constraintsData.length(); i++) {
					JSONObject constraintObject = constraintsData.getJSONObject(i);
					if ("com.appdynamics.modules.apm.topology.impl.persistenceapi.model.ApplicationEntity"
							.equals(constraintObject.get("entity_type_id").toString())) {
						constraintObjectIndex = i;
					}
				}
				JSONObject constraint = constraintsData.getJSONObject(constraintObjectIndex);
				JSONArray conditionData = constraint.getJSONArray("match_conditions");
				conditionData.put(newConditionObject);
				constraint.put("match_conditions", conditionData);
				constraintsData.remove(constraintObjectIndex);
				constraintsData.put(constraint);
				licenseData.put("constraints", constraintsData);
				rURL = PROTO + ctrlName + controllerPrefix + LICENSE_RULE_BASE_URL;
				logger.info("resourceMoveUpdateLicense - license rule json {}", licenseData);
				logger.info("resourceMoveUpdateLicense - License Rule Update API {}", rURL);
				response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_PUT, licenseData.toString(), LICENSE);
				if (response != null) {
					logger.info("resourceMoveUpdateLicense - Ended Updating licenses API");
					logger.info("resourceMoveUpdateLicense - response is not null - END");
					return request;
				} else {
					logger.info("resourceMoveUpdateLicense - END");
					logger.info("resourceMoveUpdateLicense - Failed Updating licenses ");
					throw new AppDOnboardingException(
							"AppDLicensesHandler - resourceMoveUpdateLicense - Update License Rule Failed", request);
				}

			}
			logger.error("resourceMoveUpdateLicense - response is null - ERROR");
			throw new AppDOnboardingException(
					"AppDLicensesHandler - resourceMoveUpdateLicense - License Rule Does not Exist", request);
		} catch (Exception e) {
			logger.error("resourceMoveUpdateLicense - ERROR");
			throw new AppDOnboardingException(
					"AppDLicensesHandler - resourceMoveUpdateLicense - Update License Rule Failed", request, e);
		}

	}

	/**
	 * This method Updates license for given appD application.
	 * @param AppDOnboardingRequest: AppDOnboardingRequest type which contains appD application information.	 
	 * @throws: AppDOnboardingException.
	 * @returns: boolean.
	 */	
	public boolean updateLicensesForInfra(AppDOnboardingRequest request,String applicationId, String licenseResponse) throws AppDOnboardingException {
		try {
			logger.info("updateLicensesForInfra - START");
			logger.info("updateLicensesForInfra - Started Updating licenses ");	
			String allocationId = this.getLicenseRuleAllocationId(request.getRequestDetails().getAppGroupName(),
					request.getRequestDetails().getCtrlName());
			if (allocationId != null) {
				logger.info("updateLicensesForInfra - Update licenses");
				String jsonForInfra = this.prepareJSONForInfra(request.getRequestDetails().getAppGroupName(),  String.valueOf(request.getRequestDetails().getApmLicenses()), applicationId,licenseResponse);
				String infraURL = PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix + LICENSE_INFRA_URL+this.getappdAccountId(request.getRequestDetails().getCtrlName())+ "/allocation/"+allocationId;
				logger.info("updateLicensesForInfra - License Rule Update API {}", infraURL);
				String response = appdUtil.appDConnection(infraURL, Constants.HTTP_VERB_PUT, jsonForInfra, LICENSE);
				if (response != null) {
					logger.info("updateLicensesForInfra - Ended Updating licenses ");
					logger.info("updateLicensesForInfra -  response is not null - END");
					return true;
				} else {
					logger.info("updateLicensesForInfra - response is null - END");
					logger.info("updateLicensesForInfra - Failed Updating licenses ");
					throw new AppDOnboardingException(
							"AppDLicensesHandler - updateLicenses - Update License Rule Failed", request);
				}
			}
			logger.error("AppDLicensesHandler - updateLicensesForInfra - lr is null - ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - updateLicensesForInfra - License Rule Does not Exist",
					request);
		} catch (IOException e) {
			logger.error("updateLicensesForInfra - exception - ERROR");
			throw new AppDOnboardingException("AppDLicensesHandler - updateLicensesForInfra - Update License Rule Failed",
					request, e);
		}
	}
	/**
	 * This method gets allocation Id for license rule for given appD application.
	 * @param appGroupName: The name of the license rule	
	 * @param ctrlName : The AppDynamics controller in which license allocation needs to be updated
	 * @throws: AppDOnboardingException.
	 * @returns: String: the allocationId of the license rule nd null if no such license exists
	 */	
	private String getLicenseRuleAllocationId(String appGroupName, String ctrlName) throws AppDOnboardingException {
		try {
			logger.info("getLicenseRuleAllocationId - START");
			String infraURL = PROTO + ctrlName + controllerPrefix + LICENSE_INFRA_URL + this.getappdAccountId(ctrlName)
					+ "/allocation";
			logger.info("getLicenseRuleAllocationId - API to get all License Rule:: {}", infraURL);
			String response = appdUtil.appDConnectionOnlyGet(infraURL, Constants.HTTP_VERB_GET, ctrlName, LICENSE);
			if (response != null && response.length() != 0) {
				JSONArray result = new JSONArray(response);
				for (int i = 0; i < result.length(); i++) {
					if ((appGroupName).equals(result.getJSONObject(i).get("name").toString())) {
						logger.info("getLicenseRuleAllocationId - Found allocation for {} - END", appGroupName);
						return (String) result.getJSONObject(i).get("id");
					}
				}
			}
		} catch (IOException e) {
			logger.error("updateLicensesForInfra - exception - ERROR");
			throw new AppDOnboardingException(
					"AppDLicensesHandler - updateLicensesForInfra - Update License Rule Failed", e);
		}
		logger.info("getLicenseRuleAllocationId - END");
		return null;
	}
}
