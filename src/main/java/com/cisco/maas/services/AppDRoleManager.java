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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.dto.UserDetail;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is for to create role, delete role, get role, update role and generates json for users.
 * */
@Service
public class AppDRoleManager extends AppDOnboardingRequestHandlerImpl {
	private static final Logger logger = LoggerFactory.getLogger(AppDRoleManager.class);
	private static final String TARGETAPPID = "$TARGET_APPLICATION_ID$";
	private static final String DB_ENTITY_ID = "$DB_ENTITY_ID$";
	private static final String ROLENAME = "$ROLE_NAME$";
	private static final String ROLE_API = "api/rbac/v1/roles/";
	private static final String USERS_RELATIVE_URL = "/users/";
	private String controllerPrefix;
	private String controller;
	private String dbEntityIdURL;
	@Autowired
	RequestDAO mcmpRequestDao;
	@Autowired
	AppDynamicsUtil appdUtil;
	@Autowired
	AppDLicensesHandler licenseHandler;
	@Autowired
	AppDUserHandler appdUserHandler;
	
	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public AppDRoleManager() throws IOException {
		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			Properties properties = new Properties();
			properties.load(input);
			controllerPrefix = properties.getProperty("appd.prefix");
			controller = properties.getProperty("appd.controller");
			dbEntityIdURL = properties.getProperty("appd.getDbEntityID.url");
		} catch (Exception e) {
			logger.info("Exception Loading Properties File", e);
		}
	}

	/**
	 * This method handle request based on request type and sets next handler.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @throws: AppDOnboardingException.
	 */
	@Override
	public void  handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException{
		logger.info("handleRequest - START");
		request.getRetryDetails().setFailureModule(Constants.RM_HANDLER);

		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Create Type");

			boolean result = this.createRole(request);
			if (result) {
				logger.info("handleRequest - Create Request Processing Successful Calling License Handler");
				request.getRetryDetails().setOperationCounter(1);
				this.setNextHandler(licenseHandler);
				super.handleRequestImpl(request);
				logger.info("handleRequest - END");
			}
		} else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Update Type");
			boolean result = this.updateProcess(request);
			if (result) {
				logger.info("handleRequest - Update Request Processing Successful Calling License Handler");
				request.getRetryDetails().setOperationCounter(1);
				this.setNextHandler(licenseHandler);
				super.handleRequestImpl(request);
			}
		}
	}
	/**
	 * Handles update Process
	 * @param request: AppD Onboarding Request
	 * @return returns boolean success flag
	 * @throws AppDOnboardingException
	 */
	public boolean updateProcess(AppDOnboardingRequest request) throws AppDOnboardingException {
	   boolean result;
	   if (request.getRequestDetails().getAdminUsers()!=null || request.getRequestDetails().getViewUsers()!=null ) {
			logger.info("updateProcess() - Updating local users");
			this.updateAdminViewUsers(request.getRequestDetails().getAdminUsers(), request.getRequestDetails().getViewUsers(), 
					request.getRequestDetails().getAppdProjectId());
			logger.info("updateProcess() - Updating users completed");
		}
		if (request.getRequestDetails().getAddEumpApps() != null) {
			
			if(!this.deleteRolesFromController(request))
				throw new AppDOnboardingException("AppDRoleManager - Delete Groups not completed", request);
				
			try {
				result = this.createRole(request);
			} catch(Exception error) {
				logger.error("updateProcess - Error - {0}",error);
				RetryDetails rDetails = new RetryDetails();
				rDetails.setFailureModule(Constants.RM_HANDLER);
				request.setRetryDetails(rDetails);
				throw new AppDOnboardingException("AppDRoleManager - Create Groups not completed", request);
			}
			
		}
		else
			result = true;
		
		return result;
	}
	/**
	 * Deletes Roles from Controller
	 * @param request: AppD Onboarding Request
	 * @return returns boolean success flag
	 */
	public boolean deleteRolesFromController(AppDOnboardingRequest request) {
		boolean status = true;
		try {
			if(request.getRequestDetails().getAdminUsers() != null && 
					this.deleteRBAC(new JSONArray(request.getRequestDetails().getAdminUsers().split(",")), request.getMapping().get(0).getAdminGroupName())) {
				logger.info("deleteRolesFromController - existing Admin roles deleted before creating EUM Application {}", request.getMapping().get(0).getAdminGroupName() );
			}
			else {
				logger.info("deleteRolesFromController - Admin roles deletion failed");
				status = false;
			}
			if(request.getRequestDetails().getViewUsers() != null && 
					this.deleteRBAC(new JSONArray(request.getRequestDetails().getViewUsers().split(",")), request.getMapping().get(0).getViewGroupName())) {
				logger.info("deleteRolesFromController - existing View roles deleted before creating EUM Application {}", request.getMapping().get(0).getViewGroupName());
			}
			else {
				logger.info("deleteRolesFromController - View roles deletion failed");
				status = false;
			}
		return status;
		} catch (Exception error) {
			logger.info("deleteRolesFromController - Failed to delete AdminGroupRole");
			logger.error("deleteRolesFromController - Error - {0}", error);
			return false;
		}
	}
	
	/**
	 * This method creates role .
	 * @param request: AppDOnboardingRequest type which contains payload for to create role.
	 * @throws: AppDOnboardingException.
	 */
	public synchronized boolean createRole(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("AppDRoleManager - createRole - START");
		RoleMapping roleMap = new RoleMapping();
		List<String> adminList = Arrays.asList(request.getRequestDetails().getAdminUsers().split(","));
		List<String> viewList = Arrays.asList(request.getRequestDetails().getViewUsers().split(","));
		try {
			List<UserDetail> userList = this.getAllUser(request.getRequestDetails().getCtrlName());
			String rURL = Constants.PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix + ROLE_API;
			int operationCounter = request.getRetryDetails().getOperationCounter();
			String json = null;
			String response;
			String result;
			List<String> eumIDList = this.getEUMApplicationIDs(request);

			if (operationCounter == 1) {
				roleMap = this.getRoles(request);
				operationCounter = 2;
				request.getRetryDetails().setOperationCounter(operationCounter);
				request.getRetryDetails().setMapping(roleMap);
			}
			if (operationCounter == 2) {
				result = this.checkIfRoleIsPresent(request.getRetryDetails().getMapping().getAdminGroupName(),
						request.getRequestDetails().getCtrlName());
				if (result != null) {
					request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
					logger.info("createRole - checkIfRoleIsPresent response is not null -  END");
					throw new AppDOnboardingException(
							"createRole -  Admin Role already exists need manual intervention", request);
				}
				json = this.prepareAdminJSON(request.getRetryDetails().getMapping().getAdminGroupName(),
						request.getAppGroupID(), eumIDList);

				logger.info("createRole - Admin json  {}", json);
				logger.info("createRole - Started creating Admin Role = {}",
						request.getRetryDetails().getMapping().getAdminGroupName());
				response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, "role");

				if (response == null) {
					logger.info("createRole - Failed creating Admin Role = {}",
							request.getRetryDetails().getMapping().getAdminGroupName());
					logger.info("createRole - END");
					throw new AppDOnboardingException("AppDRoleManager - createRole - Exception in creatingAdminRole",
							request);
				}
				logger.info("createRole - Ended creating Admin Role = {}",
						request.getRetryDetails().getMapping().getAdminGroupName());
				boolean adminResult = this.addRoleToUser(request.getRetryDetails().getMapping().getAdminGroupName(),
						request.getRequestDetails().getCtrlName(), userList, adminList);
				validateUserResult(adminResult, request, Constants.ADMIN);
				operationCounter = 3;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}
			if (operationCounter == 3) {
				result = this.checkIfRoleIsPresent(request.getRetryDetails().getMapping().getViewGroupName(),
						request.getRequestDetails().getCtrlName());
				if (result != null) {
					request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
					logger.info("createRole - checkIfRoleIsPresent response is not null - END");
					throw new AppDOnboardingException(
							"createRole -  View Role already exists need manual intervention",
							request);
				}
				json = this.prepareViewJSON(request.getRetryDetails().getMapping().getViewGroupName(),
						request.getAppGroupID(), eumIDList);
				logger.info("createRole - View json {}", json);
				logger.info("createRole - Started creating View Role = {}",
						request.getRetryDetails().getMapping().getViewGroupName());
				response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, "role");

				if (response == null) {
					logger.info("createRole - Failed creating View Role = {}",
							request.getRetryDetails().getMapping().getViewGroupName());
					logger.info("createRole -  appDConnection response is null -  END");
					throw new AppDOnboardingException("AppDRoleManager - createRole - Exception in creatingViewRole",
							request);
				}
				boolean viewResult = this.addRoleToUser(request.getRetryDetails().getMapping().getViewGroupName(),
						request.getRequestDetails().getCtrlName(), userList, viewList);
				validateUserResult(viewResult, request, Constants.VIEW);
				logger.info("createRole - Ended creating View Role = {}",
						request.getRetryDetails().getMapping().getViewGroupName());

			}
			logger.info("createRole - END");
			return true;
		} catch (AppDOnboardingException ce) {
			logger.error("createRole - AppDOnboardingException - ERROR");
			throw new AppDOnboardingException(ce.getActualMessage(), request, ce);
		} catch (Exception e) {
			logger.error("createRole - Exception - ERROR");
			throw new AppDOnboardingException("AppDRoleManager - createRole - Exception in createRole", request, e);
		}

	}

	/**
	 * This method validates user result .
	 *  @param result: boolean type true or false;
	 * @param request: AppDOnboardingRequest type which contains payload for to create role.	 
	 * @param user: String type user.
	 * @throws: AppDOnboardingException.
	 */
	private void validateUserResult(boolean result, AppDOnboardingRequest request, String user) throws AppDOnboardingException {
		logger.info("validateUserResult - START");
		if (!result) {
			if (Constants.ADMIN.equals(user)) {
				logger.info("createRole - Failed adding user to Admin Role = {}",request.getRetryDetails().getMapping().getAdminGroupName());
				logger.info("validateUserResult - END");
				throw new AppDOnboardingException("AppDRoleManager - addRoleToUser - Exception in adding user to Admin Role", request);
				}
			else {
				logger.info("createRole - Failed adding user to View Role = {}", request.getRetryDetails().getMapping().getViewGroupName());
				logger.info("validateUserResult - END");
				throw new AppDOnboardingException("AppDRoleManager - addRoleToUser - Exception in adding user to View Role", request);
				}
		}
	}

	/**
	 * This method creates json with the given details .
	 * @param roleName: String type role name.
	 * @param applicationId : String application id.
	 * @param eumIDList: List type eum application list. 
	 * @throws: IOException.
	 * @returns String
	 */
	public String prepareAdminJSON(String roleName, String applicationId, List<String> eumIDList) throws IOException {
		logger.info("prepareAdminJSON - START");
		File file;
		file = new File(getClass().getClassLoader().getResource("AdminRole.json").getFile());
		StringBuilder json=this.buildJSON(file,applicationId, roleName);
		logger.info("prepareAdminJSON - END");
		return this.prepareEUMAdminJson(json.toString(), eumIDList);
	}

	/**
	 * This method creates json with the given details.
	 * @param roleName: String type role name.
	 * @param applicationId : String application id.
	 * @param eumIDList: List type eum application list. 
	 * @throws: IOException.
	 * @returns String
	 */
	public String prepareViewJSON(String roleName, String applicationId, List<String> eumIDList) throws IOException {
		logger.info("prepareViewJSON - START");
		File file;
		file = new File(getClass().getClassLoader().getResource("ViewRole.json").getFile());
		StringBuilder json=this.buildJSON(file,applicationId, roleName);
		logger.info("prepareViewJSON - END");
		return this.prepareEUMViewJson(json.toString(), eumIDList);
	}

	/**
	 * This method builds json with the given details.
	 * @param file: File type file.
	 * @param applicationId : String application id.
	 * @param roleName :  String type rolename.	
	 * @throws: IOException.
	 * @return StringBuilder
	 */
	private StringBuilder buildJSON(File file, String applicationId, String roleName) throws IOException {
		String dbEntityId = appdUtil.getRequest(dbEntityIdURL);
		logger.info("buildJSON - START");
		StringBuilder json = new StringBuilder();
		String sCurrentLine = "";
		try (FileReader fr = new FileReader(file)) {
			try (BufferedReader buff = new BufferedReader(fr)) {
				while ((sCurrentLine = buff.readLine()) != null) {
					if (sCurrentLine.contains(TARGETAPPID)) {
						json.append(sCurrentLine.replace(TARGETAPPID, applicationId));
					} else if (sCurrentLine.contains(ROLENAME)) {
						json.append(sCurrentLine.replace(ROLENAME, roleName));
					} else if (sCurrentLine.contains(DB_ENTITY_ID)) {
						json.append(sCurrentLine.replace(DB_ENTITY_ID, dbEntityId));
					} else {
						json.append(sCurrentLine);
					}
				}
			}
		}
		logger.info("buildJSON - END");
		return json;
		
	}

	/**
	 * This method creates json with the given details.
	 * @param appJSON: String type application json.
	 * @param eumIDList : List type eum application list.
	 * @returns String	
	 */
	public String prepareEUMAdminJson(String appJSON, List<String> eumIDList) {
		logger.info("prepareEUMAdminJson - START");
		String json;

		int index = appJSON.indexOf(']');

		String appJSONSubstring = appJSON.substring(0, index);
		for (String eumId : eumIDList) {
			String eumJSON = ",{ \"entityType\": \"APPLICATION\",\"entityId\": " + eumId
					+ ",\"action\": \"CONFIG_EUM\" },{\"entityType\": \"APPLICATION\",\"entityId\":" + eumId
					+ ",\"action\": \"VIEW\" }";
			appJSONSubstring = appJSONSubstring.concat(eumJSON);
		}

		json = appJSONSubstring.concat("]}");
		logger.info("prepareEUMAdminJson - END");
		return json;
	}

	/**
	 * This method creates json with the given details.
	 * @param appJSON: String type application json.
	 * @param eumIDList : List type eum application list.
	 * @returns String	
	 */
	public String prepareEUMViewJson(String appJSON, List<String> eumIDList) {
		logger.info("prepareEUMViewJson - START");
		String json;

		int index = appJSON.indexOf(']');

		String appJSONSubstring = appJSON.substring(0, index);
		for (String eumId : eumIDList) {
			String eumJSON = ",{\"entityType\": \"APPLICATION\",\"entityId\":" + eumId + ",\"action\": \"VIEW\" }";
			appJSONSubstring = appJSONSubstring.concat(eumJSON);
		}

		json = appJSONSubstring.concat("]}");
		logger.info("prepareEUMViewJson - END");
		return json;
	}

	/**
	 * This method gets eum application id's.
	 * @param request: AppDOnboardingRequest type application details.
	 * @throw IOException	
	 * @return List
	 */
	public List<String> getEUMApplicationIDs(AppDOnboardingRequest request) throws IOException {
		logger.info("getEUMApplicationIDs - START");
		List<String> eumIDList = new ArrayList<>();
		List<String> eumAppList = new ArrayList<>();
		String rUrl = Constants.PROTO + request.getRequestDetails().getCtrlName() + controllerPrefix
				+ "rest/applications/";

		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())
				&& request.getRequestDetails().getEumApps() != null)
			eumAppList = request.getRequestDetails().getEumApps();
		else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())
				&& request.getRequestDetails().getAddEumpApps() != null)
			eumAppList = request.getRequestDetails().getAddEumpApps();

		for (String eumAppName : eumAppList) {
			String finalURL = rUrl + eumAppName + Constants.URL_PARAM_OUTPUT_JSON;
			String response = appdUtil.appDConnectionOnlyGet(finalURL, Constants.HTTP_VERB_GET,
					request.getRequestDetails().getCtrlName(), "eum");
			JSONArray responseArray = new JSONArray(response);
			String appGroupID = null;
			for (int i = 0; i < responseArray.length(); i++) {
				if (responseArray.getJSONObject(i).get("name").equals(eumAppName)) {
					appGroupID = responseArray.getJSONObject(i).get("id").toString();
					eumIDList.add(appGroupID);
				}
			}
		}
		logger.info("getEUMApplicationIDs - END");
		return eumIDList;
	}


	/**
	 * This method get roles.
	 * @param request: AppDOnboardingRequest type application details.	
	 * @return RoleMapping	 
	 */
	public RoleMapping getRoles(AppDOnboardingRequest request) {
		logger.info("getRoles - START");
		RoleMapping mapping = new RoleMapping();

		logger.info("getRoles - Inside getRoles method");

		if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
			for (RoleMapping rolemap : request.getMapping()) {
				String appGroupName = rolemap.getAppGroupName();
				if (appGroupName.equals(request.getRequestDetails().getAppGroupName())) {
					mapping.setAdminGroupName(rolemap.getAdminGroupName());
					mapping.setViewGroupName(rolemap.getViewGroupName());
				}
			}
		} else if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
			mapping.setAdminGroupName(request.getMapping().get(0).getAdminGroupName());
			mapping.setViewGroupName(request.getMapping().get(0).getViewGroupName());
		}
		logger.info("getRoles - END");
		return mapping;
	}
	
	/**
	 * This method checks if the role is present.
	 * @param roleName: String type role name.	
	 * @param ctrlName: String type controller name.
	 * @throws IOException	
	 * @returns String 
	 */
	public String checkIfRoleIsPresent(String roleName, String ctrlName) throws IOException {
		logger.info("checkIfRoleIsPresent - START");
		String rURL = ROLE_API + "name/";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + roleName + Constants.URL_PARAM_OUTPUT_JSON;
		String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, "role");
		logger.info("checkIfRoleIsPresent - Response for GET API {}", response);
		logger.info("checkIfRoleIsPresent - END");
		return response;
	}
	/**
	 * This method removes role from user.
	 * @param roleName: String type role name.	
	 * @param ctrlName: String type controller name.
	 * @param roleId: String type role id.
	 * @param  userList: List type users.
	 * @param  memberList: List type members list.
	 * @throws IOException	
	 * @returns boolean 
	 */
	public boolean removeRoleFromUser(String roleName, String roleId, String ctrlName, List<UserDetail> userList,
			List<String> memberList) throws IOException {
		logger.info("removeRoleFromUser - START");
		logger.info(roleName);
		List<Integer> userIdList = this.getUserId(userList, memberList);
		for (Integer userId : userIdList) {
			String rURL = Constants.PROTO + ctrlName + controllerPrefix + ROLE_API + roleId + USERS_RELATIVE_URL + userId;
			String response = appdUtil.appDConnectionDelete(rURL, Constants.HTTP_VERB_DELETE, ctrlName, "");
			logger.info("removeRoleFromUser - Response for DELETE API {}", response);

		}
		logger.info("removeRoleFromUser - END");
		return true;
	}

	/**
	 * This method gets all users.		
	 * @param ctrlName: String type controller name.	
	 * @throws IOException	
	 * @returns List 
	 */
	public List<UserDetail> getAllUser(String ctrlName) throws IOException {
		logger.info("getAllUser - START");
		ObjectMapper mapper = new ObjectMapper();
		String rURL = "api/rbac/v1/users/";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL;
		String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, "user");
		JSONObject userObject = new JSONObject(response);
		JSONArray userList = userObject.getJSONArray("users");
		logger.info("getAllUser - END");
		return mapper.readValue(userList.toString(), new TypeReference<List<UserDetail>>() {
		});
	}
	
	/**
	 * This method gets user id.		
	 * @param userList: List type users list.	
	 * @param userNameList: List type usernames list.	 
	 * @returns List  
	 */
	public List<Integer> getUserId(List<UserDetail> userList, List<String> userNameList) {
		logger.info("getUserId - START");
		List<Integer> userIdList = new ArrayList<>();
		for (String username : userNameList) {
			List<Integer> idList = userList.stream().filter(c -> c.getName().equals(username)).map(UserDetail::getId)
					.collect(Collectors.toList());

			if (!idList.isEmpty())
				userIdList.addAll(idList);
		}
		logger.info("getUserId - END");
		return userIdList;
	}
	
	/**
	 * This method add role to the user.
	 * @param roleName: String type role name.
	 * @param ctrlName: String type controller name.	
	 * @param userList: List type users list.	
	 * @param userNameList: List type usernames list.	
	 * @throws IOException
	 * @return boolean
	 */
	
	public boolean addRoleToUser(String roleName, String ctrlName, List<UserDetail> userList, List<String> memberList)
			throws IOException {
		logger.info("addRoleToUser - START");
		String roleId = getRoleId(roleName, ctrlName);
		logger.info("addRoleToUser - Response for roleId {}", roleId);
		if (roleId != null) {
			List<Integer> userIdList = this.getUserId(userList, memberList);
			for (Integer userId : userIdList) {
				String addRoleUrl = Constants.PROTO + ctrlName + controllerPrefix + ROLE_API + roleId + USERS_RELATIVE_URL
						+ userId;
				String apiResponse = appdUtil.appDConnection(addRoleUrl, Constants.HTTP_VERB_PUT, "", "role");
				logger.info("addRoleToUser - added user to role - Response for  API {}", apiResponse);
			}
			logger.info("addRoleToUser - END");
			return true;
		}
		logger.info("addRoleToUser - END");
		return false;
	}
	
	/**
	 * This method get role id.
	 * @param roleName: String type role name.
	 * @param ctrlName: String type controller name.	 	
	 * @throws IOException
	 * @return String	 
	 */
	public String getRoleId(String roleName, String ctrlName) throws IOException {
		logger.info("getRoleId - START");
		String rURL = ROLE_API + "name/";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + roleName + Constants.URL_PARAM_OUTPUT_JSON;
		String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, "role");
		if (response != null) {
			JSONObject res = new JSONObject(response);
			logger.info("getRoleId - END");
			return res.get("id").toString();
		}
		logger.info("getRoleId - END");
		return null;
	}
	
	/**
	 * This method Update roles to users.
	 * @param usersOfLastRequest: List type users list.
	 * @param currentRequest: List type current request.	
	 * @param tempRequest:  AppDOnboardingRequest type request details.
	 * @param userType: String type user type.
	 * @throws IOException
	 * @return boolean	 
	 */
	public boolean updateRolesToUsers(List<String> usersOfLastRequest, List<String> currentRequest,
			AppDOnboardingRequest tempRequest, String userType) throws IOException {
		logger.info("updateRolesToUsers - START");
		String groupName = "";
		List<UserDetail> userList;
		RoleMapping roleMap = this.getRoles(tempRequest);
		if (Constants.ADMIN.equals(userType)) {
			groupName = roleMap.getAdminGroupName();
		} else if (Constants.VIEW.equals(userType)) {
			groupName = roleMap.getViewGroupName();
		} else {
			logger.info("updateRolesToUsers - Other than admin or users -  END");
			return false;
		}
		List<String> usersOfCurrentRequest =new ArrayList<>(currentRequest);
		usersOfCurrentRequest.removeAll(usersOfLastRequest);
		if (!usersOfCurrentRequest.isEmpty()) {
			for (String user : usersOfCurrentRequest) {
				if (!appdUserHandler.checkIfAppDLocalUserExist(user, tempRequest.getRequestDetails().getCtrlName())
						&& (appdUserHandler.createAppDLocalUser(user, tempRequest.getRequestDetails().getCtrlName()))) {
					logger.info("updateRolesToUsers - AppD Local User created {}", user);
				}
			}
			userList = this.getAllUser(tempRequest.getRequestDetails().getCtrlName());
			boolean adminResult = this.addRoleToUser(groupName, tempRequest.getRequestDetails().getCtrlName(), userList,
					usersOfCurrentRequest);
			logger.info("updateRolesToUsers - AppD Admin User Role created added");
			if (!adminResult) {
				logger.info("updateRolesToUsers - Failed adding user to Admin Role");
				logger.info("updateRolesToUsers - admin result is empty - END");
				return false;
			}
		}
		usersOfLastRequest.removeAll(currentRequest);
		if (!usersOfLastRequest.isEmpty()) {
			String roleId = getRoleId(groupName, tempRequest.getRequestDetails().getCtrlName());
			userList = this.getAllUser(tempRequest.getRequestDetails().getCtrlName());
			logger.info("updateRolesToUsers - Response for roleId {}", roleId);
			if (!this.removeRoleFromUser(groupName, roleId, tempRequest.getRequestDetails().getCtrlName(), userList,
					usersOfLastRequest))
				logger.info("updateRolesToUsers - Role is not removed from user");

			logger.info("updateRolesToUsers - AppD Admin User Role created added");
		}
		logger.info("updateRolesToUsers - users of current request is empty -  END");
		return true;
	}
	/**
	 * This method updates admin and view users
	 * @param adminUsers : Admin users of application in payload
	 * @param viewUsers : View users of application in payload
	 * @param appDProjectId : AppD application ID
	 * @return success boolean flag
	 */
	public boolean updateAdminViewUsers(String adminUsers, String viewUsers, String appDProjectId) {
		try {
			AppDOnboardingRequest tempRequest = mcmpRequestDao.findByExternalIdAndRequestType(appDProjectId,
					Constants.REQUEST_TYPE_CREATE);
			if (tempRequest != null) {
				RequestDetails rDetails = this.setAdminViewUsers(appDProjectId);
				if(adminUsers!=null) {
					List<String> adminUsersOfLastRequest =new ArrayList<>( Arrays.asList(rDetails.getAdminUsers().split(",")));
					List<String> adminUsersOfCurrentRequest =new ArrayList<>( Arrays.asList(adminUsers.split(",")));
					if (!this.updateRolesToUsers(adminUsersOfLastRequest, adminUsersOfCurrentRequest, tempRequest, Constants.ADMIN)) {
						logger.info("updateAdminViewUsers - updateRolesToUsers for Admin failed");
					}
				}
				if(viewUsers!=null) {
					List<String> viewUsersOfLastRequest = new ArrayList<>(Arrays
							.asList(rDetails.getViewUsers().split(",")));
					List<String> viewUsersOfCurrentRequest =new ArrayList<>( Arrays.asList(viewUsers.split(",")));
					if (!this.updateRolesToUsers(viewUsersOfLastRequest, viewUsersOfCurrentRequest, tempRequest, Constants.VIEW))
						logger.info("updateAdminViewUsers - updateRolesToUsers for View failed");
				}
				return true;
			} else {
				return false;
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.info("updateAdminViewUsers - Exception in adding user to Role");
			return false;
		}
		
	}
	
	/**
	 * Sets admin and view users from recent request
	 * @param appDProjectId
	 * @return
	 */
	private RequestDetails setAdminViewUsers(String appDProjectId) {
		RequestDetails rDetails = new RequestDetails();
		Iterable<AppDOnboardingRequest> requestList = mcmpRequestDao
				.findAllByProjectId(appDProjectId);
		
		for (AppDOnboardingRequest request : requestList) {
			if(Constants.VALIDATION_RESULT_SUCCESS.equals(request.getRequestStatus())) {
				if(request.getRequestDetails().getAdminUsers()!=null)
					rDetails.setAdminUsers(request.getRequestDetails().getAdminUsers());
				
				if(request.getRequestDetails().getViewUsers()!=null)
					rDetails.setViewUsers(request.getRequestDetails().getViewUsers());	
			}
		}
		
		return rDetails;
	}
	
	/**
	 * Delete Users from Roles
	 * @param allUsers 
	 * @param roleName
	 * @return
	 */
	private boolean deleteRBAC(JSONArray allUsers, String roleName) {
		String roleId;
		try {
			roleId = this.getRoleId(roleName, controller);
		
			if(roleId == null) {
				logger.info("deleteRBAC() - Current Role Id doesnt exist");
				return true;
			}
			if(this.detachUserFromRole(allUsers, roleId) && this.deleteRole(roleId)) {
				logger.info("deleteRBAC() - Deleted {} role succesfully", roleName);
				return true;
			}
			else {
				logger.error("deleteRBAC() - Failed to delete {} role", roleName);
				return false;
			}
		} catch (IOException error) {
			logger.error("deleteRBAC() - Erroo while deleting Roles {0}",error);
			return false;
		}
		
	  }	
	
	/**
	 * Detaches all users from role
	 * @param users
	 * @param roleId
	 * @return
	 */
	private boolean detachUserFromRole(JSONArray users, String roleId){
		  String rURL = null;
		  Set<Integer> usersSet = new HashSet<>();
		  
		  try {
			  for (int j = 0; j < users.length(); j++) {
			    String user = users.getString(j);
			    rURL = Constants.PROTO + controller + controllerPrefix +"api/rbac/v1/users/name/"+user;
				String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, controller, "user");
				
				if(response != null && response.length()>0) {
					JSONObject userJSObj = new JSONObject(response);
					int userId = userJSObj.getInt("id");
					usersSet.add(userId);
			
					rURL = Constants.PROTO + controller + controllerPrefix + ROLE_API +roleId+USERS_RELATIVE_URL+userId;
					String roleResponse = appdUtil.appDConnectionDelete(rURL, Constants.HTTP_VERB_DELETE, controller, "");
					logger.info("detachUserFromRole() - Response for DELETE API {} for user {}", roleResponse, userId);
			    }
				
			  } 
			  return true;
		  } catch(Exception error) {	
			  logger.error("detachUserFromRole() - Detaching of user from Role failed with error: ",error);
			  return false;
		  }
    }
	/**
	 * Deletes role
	 * @param roleId
	 * @return
	 */
	private boolean deleteRole(String roleId) {
		String rURL = Constants.PROTO + controller + controllerPrefix + ROLE_API +roleId;
		String response;
		try {
			response = appdUtil.appDConnectionDelete(rURL, Constants.HTTP_VERB_DELETE, controller, "");
			logger.info("deleteRole() - Deleted {} role successfully",roleId);
			logger.info("deleteRole() - Response for DELETE API {}", response);
			return true;
		} catch (IOException error) {
			logger.info("deleteRole() - Failed to delete {} role",roleId);
			logger.error("deleteRole() - Deletion of role ended with Error - {0}",error);
			return false;
		}
		
	}
}
