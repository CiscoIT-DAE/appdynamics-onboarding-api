package com.cisco.maas.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;

/**
 * This class contains methods to process create, update and delete requests.
 * */
@Configuration
@EnableAsync
public class ProcessRequest {
	private static final Logger logger = LoggerFactory.getLogger(ProcessRequest.class);
	
	@Autowired
	AppDUserHandler appDUserHandler;
	@Autowired
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	@Autowired
	AppDRoleManager roleManager;
	@Autowired
	AppDLicensesHandler licenseHandler;
	@Autowired
	AppDAlertsHandler alertHandler;
	@Autowired
	DBHandler dbHandler;
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	RequestDAO requestDao;


	/**
	 * This async method is used to process request .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @throws AppDOnboardingException
	 */
	@Async
	public void asyncProcessRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("asyncProcessRequest - START");
		MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, request.getRequestDetails().getTrackingId());
		try {
			if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())
					&& Constants.REQUEST_STATUS_PENDING.equals(request.getRequestStatus())) {
				logger.info("asyncProcessRequest - Started Request Type {}", request.getRequestType());
				appDUserHandler.handleRequest(request);
			} else if (Constants.REQUEST_STATUS_FAILED.equals(request.getRequestStatus())) {
				logger.info("asyncProcessRequest - Started Request Type {}  and Module {}", request.getRequestType(),
						request.getRetryDetails().getFailureModule());
				retryMap(request.getRetryDetails().getFailureModule(), request);
				logger.info("asyncProcessRequest - END");
			}

		} catch (Exception error) {
			logger.error("asyncProcessRequest - ERROR");
			this.errorHandler(error, request);
		}
	}
	
	/**
	 * This async method is used to process update request .
	 * @param request: AppDOnboardingRequest type request details.	
	 * @throws AppDOnboardingException
	 */
	@Async
	public void asyncProcessUpdateRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, request.getRequestDetails().getTrackingId());
		try {
			logger.info("asyncProcessUpdateRequest - Started Request Type {}",
					request.getRequestType()); 
			
			
			if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())
					&& Constants.REQUEST_STATUS_PENDING.equals(request.getRequestStatus())) {
				int updateCounter = this.handleUpdate(request);
				if (updateCounter == 2) {
					logger.info("asyncProcessUpdateRequest - updateCounter is 2 - END");
					alertHandler.handleRequest(request);
				}
				else if (updateCounter == 3) {
					logger.info("asyncProcessUpdateRequest - updateCounter is 3 - END");
					licenseHandler.handleRequest(request);
				}
				else if (updateCounter == 4) {
					logger.info("asyncProcessUpdateRequest - updateCounter is 4 - END");
					roleManager.handleRequest(request);
				}
				request = this.initializeUpdateRequest(request);
				
				if (updateCounter == 5) {
					logger.info("asyncProcessUpdateRequest - updateCounter is 5 - END");
					appDUserHandler.handleRequest(request);
				}
				else if (updateCounter == 6) {
					logger.info("asyncProcessUpdateRequest - updateCounter is 6T - END");
					appDApplicationCreationHandler.handleRequest(request);
				}
			} else if (Constants.REQUEST_STATUS_FAILED.equals(request.getRequestStatus())) {
				logger.info("asyncProcessUpdateRequest - Started Request Type {}  and Module {}",
						request.getRequestType(), request.getRetryDetails().getFailureModule());
				retryMap(request.getRetryDetails().getFailureModule(), request);
			}

		} catch (Exception error) {
			this.errorHandler(error, request);
		}
	}
	/**
	 * This async method is used to call handlers for retry.
	 * @param failureModule: String type failure module.
	 * @param request: AppDOnboardingRequest type request details.	
	 * @throws AppDOnboardingException
	 */
	public void retryMap(String failureModule, AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("retryMap - START");
		if (Constants.AD_HANDLER.equals(failureModule)) {
			appDUserHandler.handleRequest(request);
			logger.info("retryMap - ad handler -  END");
		} else if (Constants.APP_CREATION_HANDLER.equals(failureModule)) {
			appDApplicationCreationHandler.handleRequest(request);
			logger.info("retryMap - creation handler - END");
		} else if (Constants.RM_HANDLER.equals(failureModule)) {
			roleManager.handleRequest(request);
			logger.info("retryMap -  rm handler - END");
		} else if (Constants.LIC_HANDLER.equals(failureModule)) {
			licenseHandler.handleRequest(request);
			logger.info("retryMap - lic handler -  END");
		} else if (Constants.ALERT_HANDLER.equals(failureModule)) {
			alertHandler.handleRequest(request);
			logger.info("retryMap -  alert handler - END");
		} else if (Constants.DB_HANDLER.equals(failureModule)) {
			dbHandler.handleRequest(request);
			logger.info("retryMap - db handler -  END");
		}

	}

	/**
	 * This method is used to update counter.	 
	 * @param request: AppDOnboardingRequest type request details.	
	 * @return int
	 */
	
	public int handleUpdate(AppDOnboardingRequest request) {
		int updateCounter = 0;
		if (request.isResourceMove()) {
			updateCounter = 6;
			return updateCounter;
		}
		if (request.getRequestDetails().getAlertAliases() != null)
			updateCounter = 2;
		if (request.getRequestDetails().getApmLicenses() != 0)
			updateCounter = 3;
		if (request.getRequestDetails().getAdminUsers() != null || request.getRequestDetails().getViewUsers() != null)
			updateCounter = 4;
		if (request.getRequestDetails().getEumApps() != null) 
			updateCounter = 5;

		return updateCounter;
	}

	/**
	 * Initializes fields required for Update Request
	 * @param request
	 * @return
	 * @throws AppDOnboardingException
	 */
	public AppDOnboardingRequest initializeUpdateRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		RequestDetails requestDetails = request.getRequestDetails();
		AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(request.getRequestDetails().getAppdProjectId(),
				Constants.REQUEST_TYPE_CREATE);
		if (tempRequest != null) {
			Map<String,Object> latestFieldData = this.getLatestFields(request);
			AppDOnboardingRequest latestRequest = (AppDOnboardingRequest) latestFieldData.get("latestRequest");
			String adminUsers = (String) latestFieldData.get("adminUsers");
			String viewUsers = (String) latestFieldData.get("viewUsers");
			
			requestDetails.setAppGroupName(tempRequest.getRequestDetails().getAppGroupName());
			request.setAppGroupID(tempRequest.getAppGroupID());
			request.setMapping(latestRequest.getMapping());
			if(adminUsers != null)
				request.getRequestDetails().setAdminUsers(adminUsers);
			if(viewUsers!=null)
				request.getRequestDetails().setViewUsers(viewUsers);
			return request;
		}
		else {
			logger.info("initializeUpdateRequest - Create request of the current application cannot be found");
			throw new AppDOnboardingException("ProcessRequest - initializeUpdateRequest() - Create request of the current application cannot be found");
		}
		
	}
	
	/**
	 * Returns recent values of fields
	 * @param request
	 * @return
	 */
	public Map<String,Object> getLatestFields(AppDOnboardingRequest request){
		HashMap<String,Object> latestFieldsData = new HashMap<>();
		
		AppDOnboardingRequest latestRequest = new AppDOnboardingRequest();
		Iterable<AppDOnboardingRequest> requestList = requestDao
				.findAllByProjectId(request.getRequestDetails().getAppdProjectId());
		
		String viewUsers = null;
		String adminUsers = null;
		for (AppDOnboardingRequest requestDocument : requestList) {
			if(Constants.VALIDATION_RESULT_SUCCESS.equals(requestDocument.getRequestStatus()) 
					&& requestDocument.getMapping()!=null )
				latestRequest = requestDocument;
			if(Constants.VALIDATION_RESULT_SUCCESS.equals(requestDocument.getRequestStatus()) && 
					request.getRequestDetails().getAdminUsers()==null && requestDocument.getRequestDetails().getAdminUsers()!=null ) {
				adminUsers= requestDocument.getRequestDetails().getAdminUsers();

				}
			if(Constants.VALIDATION_RESULT_SUCCESS.equals(requestDocument.getRequestStatus()) && 
					request.getRequestDetails().getViewUsers()==null && requestDocument.getRequestDetails().getViewUsers()!=null )
				{
				viewUsers= requestDocument.getRequestDetails().getViewUsers();
				}
		}
		latestFieldsData.put("latestRequest", latestRequest);
		latestFieldsData.put("adminUsers", adminUsers);
		latestFieldsData.put("viewUsers", viewUsers);
		
		return latestFieldsData;
	}
	/**
	 * This method is used to update error request and send email.	 
	 * @param processException: Exception type exception.
	 * @param request: AppDOnboardingRequest type request details.	
	 * @throws AppDOnboardingException
	 */
	public void errorHandler(Exception processException, AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("errorHandler - START");
		try {
		
			if (processException instanceof AppDOnboardingException) {
				if (Constants.REQUEST_STATUS_ERROR.equals(request.getRequestStatus())) {
					request.setRetryLock(false);
					dbHandler.handleRequest(request);
					logger.info("errorHandler - request status error equals to request status code - END");
				} else if (!Constants.DB_HANDLER.equals(request.getRetryDetails().getFailureModule())) {
					request.setRequestStatus(Constants.REQUEST_STATUS_FAILED);
					request.setRetryLock(false);
					dbHandler.handleRequest(request);
					logger.info("errorHandler - db handler equals to retry module - END");
				}
			} else {
				request.setRequestStatus(Constants.REQUEST_STATUS_FAILED);
				request.setRetryLock(false);
				dbHandler.handleRequest(request);
				logger.info("errorHandler -  else  - END");
			}
		} catch (AppDOnboardingException e) {
			logger.error("errorHandler - AppDOnboardingException - ERROR");
			if (e.getStackTrace() != null)
			{
				logger.info("errorHandler - Encountered Exception while handling error \n {}",
				Arrays.toString(e.getStackTrace()));
			}
			else
			{
				logger.info("errorHandler - Encountered Exception while handling error - exception details not available {}", e.getMessage() );
			}
			request.setRequestStatus(Constants.REQUEST_STATUS_FAILED);
			request.setRetryLock(false);
			dbHandler.handleRequest(request);
		}
	}

}