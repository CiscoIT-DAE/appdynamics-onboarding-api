package com.cisco.maas.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.EUMMetaDataDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dao.RoleMappingDAO;
import com.cisco.maas.dto.EUMMetaData;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;

/**
 * This class contains methods to create, update , delete applications.
 */
@Service
@Qualifier("DBHandler")
public class DBHandler extends AppDOnboardingRequestHandlerImpl {
	private static final Logger logger = LoggerFactory.getLogger(DBHandler.class);

	@Autowired
	RequestDAO requestDao;
	@Autowired
	APPDMasterDAO appDMasterDao;
	@Autowired
	RoleMappingDAO roleMappingDAO;
	@Autowired
	EUMMetaDataDAO eUMMetaDataDAO;
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	DBOperationHandler operationHandler;

	/**
	 * This method is used to handle request based on given input.
	 * @param request: AppDOnboardingRequest type request data.
	 * @throws AppDOnboardingException.
	 * @returns
	 */
	public void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("handleRequest - START");
		try {
			boolean res = false;
			if (Constants.REQUEST_STATUS_FAILED.equals(request.getRequestStatus())) {
				logger.info("handleRequest - Processing Request in Failed Type");
				res = this.checkRequestType(request);

			} else if (Constants.REQUEST_STATUS_ERROR.equals(request.getRequestStatus())) {
				logger.info("handleRequest - Processing Request in ERROR Type");

				res = requestDao.updateRequest(request);

			} else if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
				logger.info("handleRequest - Processing Request in Create Type");

				res = this.createApplication(request);

			} else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
				logger.info("handleRequest - Processing Request in Update Type");
				if (request.isResourceMove()) {
					res = this.resourceMoveUpdateApplication(request);
				} else {
					res = this.updateApplication(request);
				}

			}
			if (res) {
				logger.info("handleRequest - END");
			}
		} catch (AppDOnboardingException e) {
			logger.info("handleRequest - Processing catch block");
			request.setRequestStatus(Constants.REQUEST_STATUS_FAILED);
			request.setRetryLock(false);
			requestDao.updateRequest(request);
			logger.error("handleRequest - ERROR");
			throw new AppDOnboardingException(" handleRequest - Exception in handleRequest", request, e);

		}
	}

	/**
	 * This method is used to create application with the given request data.
	 * 
	 * @param request: AppDOnboardingRequest type request data.
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */

	public boolean createApplication(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("createApplication - START");
			logger.info("createApplication - Started Processing Request createApplication");
			request.getRetryDetails().setFailureModule(Constants.DB_HANDLER);
			int operationCounter = request.getRetryDetails().getOperationCounter();

			if (operationCounter == 1) {
				boolean res = operationHandler.persistAppDMetadata(request);

				if (res) {
					operationCounter = 2;
					request.getRetryDetails().setOperationCounter(operationCounter);
				} else {
					logger.info("createApplication - END");
					throw new AppDOnboardingException(" persistAppDMetadata - Exception in createApplication", request);
				}
			}

			if (operationCounter == 2 && operationHandler.persistMappings(request.getMapping())) {
				operationCounter = 3;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}

			if (operationCounter == 3 && operationHandler.persistEUMMetaData(request.getAppdExternalId(),
					request.getRequestDetails().getEumApps(), request.getRequestCreatedDate())) {
				operationCounter = 4;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}

			if (operationCounter == 4) {
				request.setOperationalStatus(Constants.OPERATIONAL_STATUS_ACTIVE);
				request.setRequestStatus(Constants.VALIDATION_RESULT_SUCCESS);
				request.setRetryLock(false);
				request.getRetryDetails().setFailureModule(null);
				request.getRetryDetails().setOperationCounter(0);
				logger.info("createApplication - Ended Processing Request createApplication");
				if (requestDao.updateRequest(request))
					return true;

			}
			logger.info("createApplication - END");
			throw new AppDOnboardingException("persistAppDMetadata - Exception while processing createApplication",
					request);
		} catch (Exception e) {
			logger.error("createApplication - ERROR");
			throw new AppDOnboardingException(" createApplication - Exception " + e.getMessage(), request, e);
		}
	}

	/**
	 * This method is used to update application with the given request data.
	 * @param request: AppDOnboardingRequest type request data.
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public boolean updateApplication(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("updateApplication - Started Processing Request updateApplication");
			request.getRetryDetails().setFailureModule(Constants.DB_HANDLER);
			request = requestHandler.getUpdatedRequest(request);
			String ctrlName = request.getRequestDetails().getCtrlName();
			int operationCounter = request.getRetryDetails().getOperationCounter();

			boolean checkStatus = operationHandler
					.checkIfEUMApplicationNotExist(request.getRequestDetails().getDeleteEumpApps(), ctrlName);
			logger.info("updateApplication - eum validation status {}", checkStatus);
			if (checkStatus) {
				logger.info("updateApplication - Inside update success path");
				request = this.firstUpdateOperations(request, operationCounter);
				operationCounter = request.getRetryDetails().getOperationCounter();
				if (operationCounter == 6) {
					request.setRequestStatus(Constants.VALIDATION_RESULT_SUCCESS);
					request.setOperationalStatus(Constants.OPERATIONAL_STATUS_ACTIVE);
					request.getRetryDetails().setOperationCounter(0);
					request.getRetryDetails().setFailureModule(null);
					request.setRetryLock(false);
					if (requestDao.updateRequest(request))
						return true;

				}
				logger.info("updateApplication - checkStatus is not empty - END");

				throw new AppDOnboardingException(" Exception in Processing updateApplication", request);
			} else {
				logger.info("updateApplication - Inside update failed path");
				request.setRequestStatus(Constants.REQUEST_STATUS_FAILED);
				request.setRetryLock(false);
				request.getRetryDetails().setFailureModule(Constants.DB_HANDLER);
				if (requestDao.updateRequest(request)) {
					logger.info("updateApplication - update request is true -  END");
					return false;
				} else {
					logger.info("updateApplication - update request is false -  END");
					throw new AppDOnboardingException(
							" Exception in updateApplication while updating failed Status to Request", request);
				}
			}
		} catch (AppDOnboardingException e) {
			logger.error("updateApplication - AppDOnboardingException - ERROR");
			throw new AppDOnboardingException(" Exception in updateApplication", e);
		}
	}

	/**
	 * This method is used to update operations with the given request data.
	 * @param request: AppDOnboardingRequest type request data.
	 * @param operationCounter: int type operation counter.
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public AppDOnboardingRequest firstUpdateOperations(AppDOnboardingRequest request, int operationCounter)
			throws AppDOnboardingException {
		logger.info("firstUpdateOperations - START");
		if (operationCounter == 1) {
			boolean res = operationHandler.updateAppDMetadata(request);
			if (res) {
				operationCounter = 2;
				request.getRetryDetails().setOperationCounter(operationCounter);
			} else {
				logger.info("firstUpdateOperations - END");
				throw new AppDOnboardingException("updateApplication - updateAppDMetadata - Exception", request);
			}
		}

		if (operationCounter == 2 && operationHandler.persistMappings(request.getMapping())) {
			operationCounter = 3;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 3 && operationHandler.persistEUMMetaData(request.getAppdExternalId(),
				request.getRequestDetails().getAddEumpApps(), request.getRequestModifiedDate())) {
			operationCounter = 4;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 4 && operationHandler.deleteEUMMetaData(request.getAppdExternalId(),
				request.getRequestDetails().getDeleteEumpApps())) {
			operationCounter = 5;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 5) {

			AppDOnboardingRequest tempRequest = requestDao.findByProjectIdAndOpStatus(request.getAppdExternalId(),
					Constants.OPERATIONAL_STATUS_ACTIVE);
			tempRequest.setOperationalStatus(Constants.OPERATIONAL_STATUS_INACTIVE);
			logger.info("updateApplication - Ended Processing Request updateApplication");
			if (requestDao.updateRequest(tempRequest)) {
				operationCounter = 6;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}
		}
		logger.info("firstUpdateOperations - END");
		return request;
	}

	/**
	 * This method is used to update resource move application.
	 * @param request: AppDOnboardingRequest type request data.
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */

	public boolean resourceMoveUpdateApplication(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info(" resourceMoveUpdateApplication - START");
		try {
			logger.info("resourceMoveUpdateApplication - Started Processing Request resourceMoveUpdateApplication");
			request.getRetryDetails().setFailureModule(Constants.DB_HANDLER);
			String newAppName = request.getRequestDetails().getAppGroupName();
			String oldAppName = request.getRequestDetails().getOldAppGroupName();
			int operationCounter = request.getRetryDetails().getOperationCounter();

			logger.info("updateApplication - Inside update success path");
			request = this.resourceMoveFirstSetOfOperations(request, operationCounter);
			if (operationCounter == 5) {
				AppDOnboardingRequest tempRequest = requestDao.findByProjectIdAndOpStatus(request.getAppdExternalId(),
						Constants.OPERATIONAL_STATUS_ACTIVE);
				tempRequest.setOperationalStatus(Constants.OPERATIONAL_STATUS_INACTIVE);
				if (requestDao.updateRequest(tempRequest)) {
					operationCounter = 6;
					request.getRetryDetails().setOperationCounter(operationCounter);
				}

			}
			if (operationCounter == 6) {
				logger.info("resourceMoveUpdateApplication - Ended Processing Request resourceMoveUpdateApplication");
				request.setMapping(this.modifyAppName(request.getMapping(), oldAppName, newAppName));
				request.setRequestStatus(Constants.VALIDATION_RESULT_SUCCESS);
				request.setOperationalStatus(Constants.OPERATIONAL_STATUS_ACTIVE);
				request.setRetryLock(false);
				if (requestDao.updateRequest(request)) {
					logger.info("resourceMoveUpdateApplication - END");
					return true;
				}
			}
			logger.info(" resourceMoveUpdateApplication - END");
			throw new AppDOnboardingException(" Exception in Processing resourceMoveUpdateApplication", request);

		} catch (AppDOnboardingException e) {
			logger.error("resourceMoveUpdateApplication - ERROR");
			throw new AppDOnboardingException(" Exception in updateApplication", e);
		}
	}

	/**
	 * This method is used to set of operations for move application.
	 * @param request:AppDOnboardingRequest type request data.
	 * @param operationCounter: int type counter.
	 * @throws AppDOnboardingException.
	 * @returns AppDOnboardingRequest
	 */
	public AppDOnboardingRequest resourceMoveFirstSetOfOperations(AppDOnboardingRequest request, int operationCounter)
			throws AppDOnboardingException {
		logger.info("resourceMoveFirstSetOfOperations - START");
		if (operationCounter == 1
				&& appDMasterDao.updateNewAppName(request, request.getRequestDetails().getOldAppGroupName())) {
			operationCounter = 2;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 2 && this.resourceMoveUpdateRoleMapping(
				request.getRequestDetails().getOldAppGroupName(), request.getRequestDetails().getAppGroupName(),
				request.getRequestDetails().getCtrlName(), request.getRequestDetails().getOldEumApps())) {
			operationCounter = 3;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 3 && this.resourceMoveUpdateEUMMetaData(request.getAppdExternalId(),
				request.getRequestDetails().getOldAppGroupName(), request.getRequestDetails().getAppGroupName())) {
			operationCounter = 4;
			request.getRetryDetails().setOperationCounter(operationCounter);
		}

		if (operationCounter == 4) {
			AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(request.getAppdExternalId(),
					Constants.REQUEST_TYPE_CREATE);
			tempRequest.getRequestDetails().setOldEumApps(request.getRequestDetails().getOldEumApps());
			tempRequest.getRequestDetails().setOldAppGroupName(request.getRequestDetails().getOldAppGroupName());
			tempRequest.getRequestDetails().setAppGroupName(request.getRequestDetails().getAppGroupName());
			tempRequest.getRequestDetails().setEumApps(request.getRequestDetails().getEumApps());

			if (requestDao.updateCreateRequest(tempRequest)) {
				operationCounter = 5;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}
		}
		logger.info("resourceMoveFirstSetOfOperations - END");
		return request;
	}

	/**
	 * This method is used to update set of operations on application.	 
	 * @param appdProjectId: String type appd project id.	 
	 * @param oldAppName: String type old application name.	 
	 * @param newAppName: String type new application name.
	 * @returns boolean
	 */

	public boolean resourceMoveUpdateEUMMetaData(String appdProjectId, String oldAppName, String newAppName)
			throws AppDOnboardingException {
		logger.info(" resourceMoveUpdateEUMMetaData - START");
		logger.info("resourceMoveUpdateEUMMetaData - Started Processing Request in resourceMoveUpdateEUMMetaData");
		Iterable<EUMMetaData> eumMetaDataList;
		eumMetaDataList = eUMMetaDataDAO.findByProjectID(appdProjectId);
		if (eumMetaDataList != null) {
			for (EUMMetaData eumDetails : eumMetaDataList) {
				if (eumDetails != null) {
					String newEUMName = eumDetails.getEumName().replace(oldAppName, newAppName);
					eUMMetaDataDAO.resourceMoveUpdateEUM(appdProjectId, eumDetails.getEumName(), newEUMName);
				}
			}
		} else
			throw new AppDOnboardingException(
					" resourceMoveUpdateEUMMetaData - Exception in resourceMoveUpdateEUMMetaData");

		logger.info("resourceMoveUpdateEUMMetaData - Ended Processing Request in resourceMoveUpdateEUMMetaData");
		logger.info("resourceMoveUpdateEUMMetaData - END");
		return true;
	}

	/**
	 * This method is used to update role mappings.
	 * 
	 * @param oldAppName: String type old application name.
	 * @param newAppName: String type new application name.
	 * @param appdProjectId: String type appd project id.
	 * @param ctrlName: String type controller name.
	 * @param eumApps: list type eum apps.
	 * @returns boolean
	 */
	public boolean resourceMoveUpdateRoleMapping(String oldAppName, String newAppName, String ctrlName,
			List<String> eumApps) {
		logger.info(" resourceMoveUpdateRoleMapping - START");
		roleMappingDAO.resourceMoveUpdate(oldAppName, newAppName, ctrlName);
		if (eumApps != null) {
			for (String eumName : eumApps) {
				String newEUMName = eumName.replace(oldAppName, newAppName);
				roleMappingDAO.resourceMoveUpdate(eumName, newEUMName, ctrlName);
			}
		}
		logger.info("resourceMoveUpdateRoleMapping - END");
		return true;

	}

	/**
	 * This method is used to modify app name.
	 * 
	 * @param mappingList: List type of mapping list.
	 * @param oldAppName: String type old application name.
	 * @param newAppName: String type new application name.
	 * @returns List
	 */
	public List<RoleMapping> modifyAppName(List<RoleMapping> mappingList, String oldAppName, String newAppName) {
		logger.info(" modifyAppName - START");
		List<RoleMapping> updatedMappingList = new ArrayList<>();
		for (RoleMapping roleMap : mappingList) {
			String appName = roleMap.getAppGroupName();
			roleMap.setAppGroupName(appName.replace(oldAppName, newAppName));
			updatedMappingList.add(roleMap);
		}
		logger.info(" modifyAppName - END");
		return updatedMappingList;
	}
	
	/**
	 * This method is used to check request type.	 
	 * @param request : AppDOnboardingRequest type of request details.	
	 * @throws AppDOnboardingException 	 	
	 * @returns boolean
	 */
	public boolean checkRequestType(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("checkRequestType - START");
			if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())
					&& Constants.DB_HANDLER.equals(request.getRetryDetails().getFailureModule())) {
				logger.info("checkRequestType - Inside update path");
				if (request.isResourceMove()) {
					logger.info("checkRequestType - resource move is true - END");
					return this.resourceMoveUpdateApplication(request);
				} else {
					logger.info("checkRequestType - resource move is false - END");
					return this.updateApplication(request);
				}

			} else if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())
					&& Constants.DB_HANDLER.equals(request.getRetryDetails().getFailureModule())) {
				logger.info("checkRequestType - request type create - END");
				logger.info("checkRequestType - Inside create path");
				return this.createApplication(request);
			} else {
				requestDao.updateRequest(request);
				logger.info("checkRequestType - request type is not create or update - END");
				return false;
			}
		} catch (AppDOnboardingException e) {
			logger.error(" checkRequestType - AppDOnboardingException - ERROR");
			throw new AppDOnboardingException(" checkRequestType - Exception in checkRequestType", request, e);

		}
	}

}
