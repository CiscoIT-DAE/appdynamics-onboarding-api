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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDError;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.ValidateResult;
import com.cisco.maas.dto.ViewAppdynamicsResponse;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;
import com.mongodb.MongoException;
/**
 * This class contains methods to create, validate and update request.
 * */

@Service
public class RequestHandler {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	@Autowired
	RequestDAO requestDao;

	@Autowired
	APPDMasterDAO appdMasterDao;
	@Autowired
	AppDRoleManager appDRoleManager;

	/**
	 * This method is used to create validate request .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @return ValidateResult
	 */
	public ValidateResult validateRequest(AppDOnboardingRequest request){
		logger.info("validateRequest - START");
		ValidateResult validateResult = new ValidateResult();
		AppDError appDError = new AppDError();

		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
			logger.info("Validating Create Request");
			if (appdMasterDao.findByApp(request.getRequestDetails().getAppGroupName(),
					request.getRequestDetails().getCtrlName()) != null) {
				AppDOnboardingRequest tempRequest = requestDao.findByAppNameAndRequestType(
						request.getRequestDetails().getAppGroupName(),
						request.getRequestDetails().getCtrlName(), Constants.REQUEST_TYPE_CREATE);
				logger.info("validateRequest - Create Request is not Valid");
				validateResult.setValidateResultStatus(Constants.REQUEST_STATUS_FAILED);
				validateResult.setResponseCode(HttpStatus.CONFLICT);
				appDError.setCode(HttpStatus.CONFLICT.value());
				appDError.setMsg("project " + tempRequest.getAppdExternalId() + " contains active AppDynamics");
				validateResult.setErrorObject(appDError);
				logger.info("validateRequest - appmasterDao result is true - END");
				return validateResult;
			} else {
				logger.info("validateRequest - Create Request is Valid");
				validateResult.setValidateResultStatus(Constants.VALIDATION_RESULT_SUCCESS);
				validateResult.setResponseCode(HttpStatus.ACCEPTED);
				logger.info("validateRequest - appmasterDao result is false - END");
				return validateResult;
			}
		} else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
			logger.info("validateRequest -  request type equals to update - END");
			return this.validateUpdate(request);
		} else if (Constants.REQUEST_TYPE_DELETE.equals(request.getRequestType())) {
			AppDOnboardingRequest tempRequest = requestDao
					.findByExternalIdAndRequestType(request.getAppdExternalId(), Constants.REQUEST_TYPE_CREATE);
			if (tempRequest == null || requestDao.findAppByExternalId(request.getAppdExternalId())) {
				logger.info("validateRequest - Request is not Valid ");
				validateResult.setValidateResultStatus(Constants.REQUEST_STATUS_FAILED);
				validateResult.setResponseCode(HttpStatus.NOT_FOUND);
				appDError.setCode(404);
				appDError.setMsg("project UUID does not exist:");
				validateResult.setErrorObject(appDError);
				logger.info("validateRequest - temp request is null - END");
				return validateResult;
			} else {

				logger.info("validateRequest - Request is Valid");
				validateResult.setValidateResultStatus(Constants.VALIDATION_RESULT_SUCCESS);
				validateResult.setResponseCode(HttpStatus.ACCEPTED);
				logger.info("validateRequest - temp request is not null - END");
				return validateResult;
			}
		} else {
			appDError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			appDError.setMsg("Error while processing request");
			validateResult.setErrorObject(appDError);
			validateResult.setValidateResultStatus(Constants.REQUEST_STATUS_FAILED);
			logger.info("validateRequest - request type is not create or update - END");
			return validateResult;
		}
	}
	
	/**
	 * This method is used to update validate request .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @return ValidateResult
	 */
	public ValidateResult validateUpdate(AppDOnboardingRequest request) {
		logger.info("validateUpdate - START");
		ValidateResult validateResult = new ValidateResult();
		validateResult.setResourceMoveFlag(false);
		AppDError appDError = new AppDError();
		AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(request.getAppdExternalId(),
				Constants.REQUEST_TYPE_CREATE);
		if (tempRequest != null && !requestDao.findAppByExternalId(request.getAppdExternalId())) {
		
			if (request.getRequestDetails().getNoOfEUMLicenses() != 0) {
				APPDMaster masterDetails = appdMasterDao.findByApp(tempRequest.getRequestDetails().getAppGroupName(),
						tempRequest.getRequestDetails().getCtrlName());
				if (request.getRequestDetails().getNoOfEUMLicenses() < masterDetails.getNoOfEUMLicenses()) {
					logger.info(
							"validateUpdate - Request is not Valid : No Of EUM Licenses lesser than actual usage");
					validateResult.setValidateResultStatus(Constants.REQUEST_STATUS_FAILED);
					validateResult.setResponseCode(HttpStatus.BAD_REQUEST);
					appDError.setCode(HttpStatus.BAD_REQUEST.value());
					appDError.setMsg(
							"Number of estimated page views is invalid. Please set it to a range equal to or greater than '> "
									+ (masterDetails.getNoOfEUMLicenses() * 10 - 10) + "M - <= "
									+ masterDetails.getNoOfEUMLicenses() * 10 + "M'.");
					validateResult.setErrorObject(appDError);
					logger.info("validateUpdate - request no of eum licenses < master details no of eum licenses -  END");
					return validateResult;
				} else {
					logger.info(
							"validateUpdate -   Request Valid : AppD ProjectId exists and No Of EUM Licenses are more than actual usage");
					validateResult.setValidateResultStatus(Constants.VALIDATION_RESULT_SUCCESS);
					validateResult.setResponseCode(HttpStatus.ACCEPTED);
					logger.info("validateUpdate - request - no of eum licenses > master details - no of eum licenses -  END");
					return validateResult;
				}
			} else {
				logger.info("validateUpdate -   Request Valid : AppD ProjectId exists");
				validateResult.setValidateResultStatus(Constants.VALIDATION_RESULT_SUCCESS);
				validateResult.setResponseCode(HttpStatus.ACCEPTED);
				logger.info("validateUpdate - No of EUM licenses is 0 - END");
				return validateResult;
			}
		} else {
			logger.info(
					"validateUpdate -   Request is not Valid : AppD ProjectId does not exist");
			validateResult.setValidateResultStatus(Constants.REQUEST_STATUS_FAILED);
			validateResult.setResponseCode(HttpStatus.NOT_FOUND);
			appDError.setCode(HttpStatus.NOT_FOUND.value());
			appDError.setMsg("project UUID does not exist:");
			validateResult.setErrorObject(appDError);
			logger.info("validateUpdate - temp request or requestDao response is false - END");
			return validateResult;
		}
	}

	/**
	 * This method is used to create request .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @return boolean
	 */
	public boolean createRequest(AppDOnboardingRequest request) {
		boolean result = false;
		logger.info("createRequest: Start Create  Request");
		try {
			result = requestDao.create(request);
			if (result) {
				logger.info("createRequest: Request Created Status {}", result);
				logger.info("createRequest: End Create - result- Request");
				return true;
			} else {
				logger.info("createRequest: End Create  Request");
				return false;
			}
		} catch (Exception e) {
			logger.info("createRequest: Exception in Create Request");
			throw new MongoException(e.getMessage(), e);
		}
	}
	
	/**
	 * This method is used to get AppDOnboardingRequest details and update ViewAppdynamicsResponse .
	 * @param appdExternalId: String type request application external id.	
	 * @return ViewAppdynamicsResponse
	 */
	public ViewAppdynamicsResponse getRequestByProjectId(String appdExternalId) {
		logger.info("getRequestByProjectId - START");
		int requestCounter = 0;

		AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(appdExternalId,
				Constants.REQUEST_TYPE_CREATE);

		if (tempRequest != null) {
			APPDMaster masterDetails = appdMasterDao.findByApp(tempRequest.getRequestDetails().getAppGroupName(),
					tempRequest.getRequestDetails().getCtrlName());
			AppDOnboardingRequest latestRequest = new AppDOnboardingRequest();
			Iterable<AppDOnboardingRequest> requestList = requestDao
					.findAllByProjectId(tempRequest.getAppdExternalId());

			for (AppDOnboardingRequest request : requestList) {
				if (request.getRequestCounter() > requestCounter) {
					requestCounter = request.getRequestCounter();
					latestRequest = request;
				}
			}

			ViewAppdynamicsResponse viewResponse = this.getUpdatedViewResponse(tempRequest, latestRequest, masterDetails);
			logger.info("getRequestByProjectId - Final View JSON for ={}", viewResponse);
			logger.info(
					"getRequestByProjectId - Started processing request in getRequest for AppD External ID ={}",
					appdExternalId);
			logger.info("getRequestByProjectId -  temp request is not null - END");
			return viewResponse;
		} else {
			logger.info("getRequestByProjectId - END");
			return null;
		}
	}

	/**
	 * This method is used to update AppDOnboardingRequest with required properties .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @return AppDOnboardingRequest
	 */
	public AppDOnboardingRequest getUpdatedRequest(AppDOnboardingRequest request) {
		
		logger.info("getUpdatedRequest - START");
		AppDOnboardingRequest tempRequest = requestDao
				.findByExternalIdAndRequestType(request.getAppdExternalId(), Constants.REQUEST_TYPE_CREATE);
		if (tempRequest != null) {
			logger.info("getUpdatedRequest - Started Updating Request from create");
			request.setAppGroupID(tempRequest.getAppGroupID());
			request.getRequestDetails().setAppGroupName(tempRequest.getRequestDetails().getAppGroupName());
			request.getRequestDetails().setCtrlName(tempRequest.getRequestDetails().getCtrlName());
			request.getRequestDetails().setAdminUsers(tempRequest.getRequestDetails().getAdminUsers());
			request.getRequestDetails().setViewUsers(tempRequest.getRequestDetails().getViewUsers());

			if (Constants.REQUEST_TYPE_DELETE.equals(request.getRequestType())) {
				logger.info("getUpdatedRequest - Started Updating Master Details");
				APPDMaster masterDetails = appdMasterDao.findByApp(
						tempRequest.getRequestDetails().getAppGroupName(),
						tempRequest.getRequestDetails().getCtrlName());
				request.getRequestDetails().setEumApps(masterDetails.getEumApps());
				request.getRequestDetails().setAlertAliases(masterDetails.getAlertAliases());
				logger.info("getUpdatedRequest - Completed Updating Master Details");
			}

		}
		tempRequest = requestDao.findByProjectIdAndOpStatus(request.getAppdExternalId(),
				Constants.OPERATIONAL_STATUS_ACTIVE);
		if (tempRequest != null && request.getMapping() == null) {
			request.setMapping(tempRequest.getMapping());
		}		
		logger.info("getUpdatedRequest - END");
		return request;
	}
	
	/**
	 * This method is used to convert string to array.
	 * @param str: String type string.	
	 * @return List
	 */
	public List<String> convertToArray(String str) {
		return Arrays.asList(str);
	}
	
	/**
	 * This method is used to update moved resource requestwith id, old app group name, ctrl name.
	 * @param request: AppDOnboardingRequest type request details.
	 * @throws AppDOnboardingException	 	
	 * @return AppDOnboardingRequest
	 */

	public AppDOnboardingRequest getUpdatedRequestResourceMove(AppDOnboardingRequest request) throws AppDOnboardingException{
		try {
			logger.info("getUpdatedRequestResourceMove - Started processing request in getUpdatedRequest");
			AppDOnboardingRequest tempRequest = requestDao
					.findByExternalIdAndRequestType(request.getAppdExternalId(), Constants.REQUEST_TYPE_CREATE);

			if (tempRequest != null) {
				logger.info("getUpdatedRequestResourceMove - Started Updating Request from Rename");

				request.getRequestDetails().setOldAppGroupName(tempRequest.getRequestDetails().getAppGroupName());
				request.getRequestDetails().setCtrlName(tempRequest.getRequestDetails().getCtrlName());
				logger.info("getUpdatedRequestResourceMove - Started Updating Master Details");
				APPDMaster masterDetails = appdMasterDao.findByApp(tempRequest.getRequestDetails().getAppGroupName(),
						tempRequest.getRequestDetails().getCtrlName());
				request.getRequestDetails().setOldEumApps(masterDetails.getEumApps());

			}
			tempRequest = requestDao.findByProjectIdAndOpStatus(request.getAppdExternalId(),
					Constants.OPERATIONAL_STATUS_ACTIVE);
			if (tempRequest != null && request.getMapping() == null) {
				request.setMapping(tempRequest.getMapping());
			}
			logger.info(
					"getUpdatedRequestResourceMove - Ended Updating request in getUpdatedRequest");
			return request;
		} catch (Exception e) {
			throw new AppDOnboardingException(
					"getUpdatedRequestResourceMove - Error while updating request", request, e);
		}
	}
	
	
	/**
	 * This method is used to update view response with id, admin users, view users, admin role and view roles.
	 * @param tempRequest: AppDOnboardingRequest type request details.
	 * @param latestRequest: AppDOnboardingRequest type request details.
	 * @param masterDetails: APPDMaster type master details.	
	 * @return ViewAppdynamicsResponse
	 */
	public ViewAppdynamicsResponse getUpdatedViewResponse(AppDOnboardingRequest tempRequest, AppDOnboardingRequest latestRequest,
			APPDMaster masterDetails) {
		logger.info("getUpdatedViewResponse - START");
		ViewAppdynamicsResponse viewResponse = new ViewAppdynamicsResponse();
		
		viewResponse.setId(tempRequest.getAppdExternalId());
		viewResponse.setLicenseKey(tempRequest.getLicenseKey());
		viewResponse.setAdminUsers(Arrays.asList(tempRequest.getRequestDetails().getAdminUsers().split(",")));
		viewResponse.setViewUsers(Arrays.asList(tempRequest.getRequestDetails().getViewUsers().split(",")));
		viewResponse.setApmApplicationGroupName(tempRequest.getRequestDetails().getAppGroupName());

		viewResponse.setOperation(latestRequest.getRequestType());
		viewResponse.setStatus(latestRequest.getOperationalStatus());
		
		if (tempRequest.getMapping() != null) {
			viewResponse.setAdminRoleName(tempRequest.getMapping().get(0).getAdminGroupName());
			viewResponse.setViewRoleName(tempRequest.getMapping().get(0).getViewGroupName());
		}
		
		if (Constants.REQUEST_TYPE_UPDATE.equals(latestRequest.getRequestType())) {
			logger.info("getUpdatedViewResponse - update - END");
			return (this.updateViewResponse(latestRequest, masterDetails, viewResponse));
			
		}

		viewResponse.setAlertAliases(this.convertToArray(latestRequest.getRequestDetails().getAlertAliases()));
		
		if (Constants.REQUEST_TYPE_CREATE.equals(latestRequest.getRequestType())) {
			List<String> eumList = new ArrayList<>();
			viewResponse.setApmLicenses(latestRequest.getRequestDetails().getApmLicenses());
			List<String> eumApps = (latestRequest.getRequestDetails().getEumApps() == null) ? eumList
					: latestRequest.getRequestDetails().getEumApps();
			viewResponse.setEumApplicationGroupNames(eumApps);
			
		} else {
			viewResponse.setApmLicenses(masterDetails.getApmLicenses());
			viewResponse.setEumApplicationGroupNames(masterDetails.getEumApps());
		}
		logger.info("getUpdatedViewResponse - END");
		return viewResponse;
	}

	
	/**
	 * This method is used to update view response with alert alias, apm licenses, num of eum apps
	 * @param latestRequest: AppDOnboardingRequest type request details.
	 * @param masterDetails: APPDMaster type master details.
	 * @param viewResponse: ViewAppdynamicsResponse type response details.	
	 * @return ViewAppdynamicsResponse
	 */
	private ViewAppdynamicsResponse updateViewResponse(AppDOnboardingRequest latestRequest, APPDMaster masterDetails, ViewAppdynamicsResponse viewResponse) {
		
		logger.info("updateViewResponse - START");
		String result = (latestRequest.getRequestDetails().getAlertAliases() == null)
				? masterDetails.getAlertAliases()
				: latestRequest.getRequestDetails().getAlertAliases();
		
		viewResponse.setAlertAliases(this.convertToArray(result));
		
		int val = (latestRequest.getRequestDetails().getApmLicenses() == 0)
				? masterDetails.getApmLicenses()
				: latestRequest.getRequestDetails().getApmLicenses();
		viewResponse.setApmLicenses(val);

		List<String> eumApps = (latestRequest.getRequestDetails().getEumApps() == null)
				? masterDetails.getEumApps()
				: latestRequest.getRequestDetails().getEumApps();

		viewResponse.setEumApplicationGroupNames(eumApps);
		logger.info("updateViewResponse - END");
		return viewResponse;
		
	}
	
	
	/**
	 * This method is used to update admin and view users.
	 * @param adminUsers: String type admin users.
	 * @param viewUsers: String type view users.
	 * @param appDProjectId: String type application project id.	
	 * @return boolean
	 */
	public boolean updateAdminViewUsers(String adminUsers, String viewUsers, String appDProjectId) {
		logger.info("updateAdminViewUsers - START");
		try {
			AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(appDProjectId,
					Constants.REQUEST_TYPE_CREATE);
			if (tempRequest != null) {
				AppDOnboardingRequest latestRequest = new AppDOnboardingRequest();
				Iterable<AppDOnboardingRequest> requestList = requestDao
						.findAllByProjectId(tempRequest.getAppdExternalId());

				for (AppDOnboardingRequest request : requestList) {
					if(Constants.VALIDATION_RESULT_SUCCESS.equals(request.getRequestStatus()))
						latestRequest = request;
				}
				
				List<String> adminUsersOfLastRequest =new ArrayList<>( Arrays.asList(latestRequest.getRequestDetails().getAdminUsers().split(",")));
				List<String> adminUsersOfCurrentRequest =new ArrayList<>( Arrays.asList(adminUsers.split(",")));
				if (!appDRoleManager.updateRolesToUsers(adminUsersOfLastRequest, adminUsersOfCurrentRequest, tempRequest, Constants.ADMIN)) {
					logger.info("updateAdminViewUsers - updateRolesToUsers for Admin failed");
				}

				List<String> viewUsersOfLastRequest = new ArrayList<>(Arrays
						.asList(latestRequest.getRequestDetails().getViewUsers().split(",")));
				List<String> viewUsersOfCurrentRequest =new ArrayList<>( Arrays.asList(viewUsers.split(",")));
				if (!appDRoleManager.updateRolesToUsers(viewUsersOfLastRequest, viewUsersOfCurrentRequest, tempRequest, Constants.VIEW))
					logger.info("updateAdminViewUsers - updateRolesToUsers for View failed");
				logger.info("updateAdminViewUsers - update roles to user -  END");
				return true;
			} else {
				logger.info("updateAdminViewUsers - END");
				return false;
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.info("updateAdminViewUsers - Exception in adding user to Role");
			logger.error("updateAdminViewUsers - ERROR");
			return false;
		}
		
	}

}
