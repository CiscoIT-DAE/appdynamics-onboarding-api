package com.cisco.maas.services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;



/**
 * This class contains methods to process callback request and process license quota check.
 * */
@Service
@Qualifier("RollBackHandler")
public class RollBackHandler {

	private static final Logger logger = LoggerFactory.getLogger(RollBackHandler.class);
	private static final String EUM_DELETE_MSG = "Delete EUM Application[Rollback] {}";

	@Autowired
	DBHandler dbHandler;
	@Autowired
	RequestDAO requestDao;
	@Autowired
	DBOperationHandler operationHandler;
	
	/**
	 * This method handle request based on request type and sets next handler.
	 * @param request: AppDOnboardingRequest type which contains payload.	 
	 */
	public void handleRequest(AppDOnboardingRequest request) {
		logger.info("handleRequest - START");
		boolean result = false;
		try {			
			if (Constants.ROLLBACK_ERROR.equals(request.getRequestStatus())) {
				logger.info("handleRequest - Processing Request in Rollback Error Type ");
			} else if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType())) {
				logger.info("handleRequest - Processing Request in Create Type ");
				result = this.rollbackForCreate(request);
				if (result) {
					request.setRequestStatus(Constants.ROLLBACK_SUCCESS);
					request.setRetryLock(false);
					requestDao.updateRequest(request);
					logger.info("handleRequest - result - END");
				}
			} else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
				logger.info("handleRequest - Processing Request in Update Type ");
				result = this.rollbackForUpdate(request);
				if (result) {
					request.setRequestStatus(Constants.ROLLBACK_SUCCESS);
					request.setRetryLock(false);
					requestDao.updateRequest(request);
					logger.info("handleRequest - END");
					requestDao.updateRequest(request);
				}
			}
		} catch (AppDOnboardingException error) {
			
			logger.info("handleRequest - Processing Request in Catch Block ",error);
			logger.error("handleRequest - ERROR");
			request.setRequestStatus(Constants.ROLLBACK_FAILED);
			request.setRetryLock(false);
			requestDao.updateRequest(request);
			
		}

	}
	
	/**
	 * This method is used to rollback for application creation. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */	 
	public boolean rollbackForCreate(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("rollbackForCreate - START");
		String failureModule = request.getRetryDetails().getFailureModule();
		try {
			if (Constants.AD_HANDLER.equals(failureModule)) {
				logger.info("rollbackForCreate - Processing request in AD_HANDLER Type ");
				logger.info("rollbackForCreate - AD_HANDLER - END");
				return true;
			} else if (Constants.APP_CREATION_HANDLER.equals(failureModule)) {
				logger.info("rollbackForCreate - Processing request in AC_HANDLER Type ");
				if (request.getRollbackCounter() == 1)
					this.processApplicationDeletion(request, true);

			} else if (Constants.RM_HANDLER.equals(failureModule) || Constants.LIC_HANDLER.equals(failureModule)
					|| Constants.ALERT_HANDLER.equals(failureModule)) {
				logger.info("rollbackForCreate - Processing request in RM_HANDLER Type ");
				if (request.getRollbackCounter() == 1) {
					this.processApplicationDeletion(request, false);
				}
				logger.info("rollbackForCreate -RM_HANDLER | LIC_HANDLER | ALERT_HANDLER is failure - END");
				return this.checkIfApplicationDeleted(request, false);
			} else if (Constants.DB_HANDLER.equals(failureModule)) {
				logger.info("rollbackForCreate - DB_HANDLER - END");
				return this.dbFailureModule(request);

			}
			logger.info("rollbackForCreate - other handler - END");
			throw new AppDOnboardingException("RollBackHandler - rollbackForCreate - Exception in rollbackForCreate");
		} catch (AppDOnboardingException ce) {			
			logger.error("rollbackForCreate - ERROR");
			logger.info("rollbackForCreate - Exception in rollbackForCreate ");
			throw new AppDOnboardingException(ce.getActualMessage(), ce.getRequest(), ce);
		}

	}
	
	/**
	 * This method is used to invoke an email for db module failure. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */
	private boolean dbFailureModule(AppDOnboardingRequest request) throws AppDOnboardingException {
		if (request.getRollbackCounter() == 1) {
			logger.info("RollBack DB Handler is failing Please Take immediate Action {} ",
					request.getAppdExternalId());
		}
		if (Constants.VALIDATION_RESULT_SUCCESS.equals(request.getRetryDetails().getDbFlagRollback()))
			return true;
		else
			throw new AppDOnboardingException(
					"RollBackHandler - dbFailureModule - Exception in while processing DB_HANDLER");

	}
	/**
	 * This method is used to rollback for application updation. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */

	public boolean rollbackForUpdate(AppDOnboardingRequest request) throws AppDOnboardingException {
		String failureModule = request.getRetryDetails().getFailureModule();
		logger.info("rollbackForUpdate - START");
		try {
			if (Constants.AD_HANDLER.equals(failureModule)) {
				logger.info("rollbackForUpdate - Processing request in AD_HANDLER Type ");
				return true;
			} else if (Constants.APP_CREATION_HANDLER.equals(failureModule)) {
				logger.info("rollbackForUpdate - Processing request in APP_CREATION_HANDLER Type ");
				if (request.getRollbackCounter() == 1) {
					this.processAppDeletionUpdate(request, true);
				}
			} else if (Constants.RM_HANDLER.equals(failureModule)) {
				logger.info("rollbackForUpdate - Processing request in RM_HANDLER Type ");
				if (request.getRollbackCounter() == 1) {
					this.processAppDeletionUpdate(request, false);
				}
				logger.info("rollbackForUpdate - RM_HANDLER - END");
				return this.checkIfAppDeletedUpdate(request);
			} else if (Constants.DB_HANDLER.equals(failureModule)) {

				if (request.getRollbackCounter() == 1) {
					logger.info("RollBack DB Handler is failing Please Take immediate Action {} ",
							request.getAppdExternalId());
				}
				if (Constants.VALIDATION_RESULT_SUCCESS.equals(request.getRetryDetails().getDbFlagRollback()))
				{
					logger.info("rollbackForUpdate - VALIDATION_RESULT_SUCCESS - END");
					return true;
				}
				else
				{
					logger.info("rollbackForUpdate - else - END");
					throw new AppDOnboardingException(
							"RollBackHandler - rollbackForUpdate - Exception in while processing DB_HANDLER");
				}

			}
			throw new AppDOnboardingException("RollBackHandler - rollbackForUpdate - Exception in rollbackForUpdate");

		} catch (AppDOnboardingException error) {
			logger.info("rollbackForUpdate - Exception in rollbackForUpdate ");
			throw new AppDOnboardingException(error.getActualMessage(), request, error);
		}

	}
	
	/**
	 * This method is used to invoke email for application deletion. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @param checkOperationCounter : boolean type true or false.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */

	public boolean processApplicationDeletion(AppDOnboardingRequest request, boolean checkOperationCounter)
			throws AppDOnboardingException {
		logger.info("processApplicationDeletion - START");
		boolean result = false;
		List<String> eumList = new ArrayList<>();

		if (checkOperationCounter) {
			if (request.getRetryDetails().getOperationCounter() <= 3)
				result = true;
			else if (request.getRetryDetails().getOperationCounter() >= 4
					&& request.getRetryDetails().getOperationCounter() <= 6)
			{ logger.info("Delete APM Application [Rollback] {}  {}", request.getRequestDetails().getAppGroupName(), request.getRequestDetails().getCtrlName());
			result = true;
			}
			else if (request.getRetryDetails().getOperationCounter() >= 7) {
				result = this.getResultForOpCounter(request, eumList);
					logger.info(EUM_DELETE_MSG, eumList);
				
			}
		} else {
			 logger.info("Delete APM Application [Rollback - Delete All AppD Role, License Rule Related To APP] {} {}",
					request.getRequestDetails().getAppGroupName(), request.getRequestDetails().getCtrlName());
			 result = true;
			if (request.getRequestDetails().getEumApps() != null
					&& !request.getRequestDetails().getEumApps().isEmpty()) {
				logger.info(EUM_DELETE_MSG, request.getRequestDetails().getEumApps());
			}
		}
		if (result)
		{
			logger.info("processApplicationDeletion - END");
			return true;
		}
		else
		{
			logger.info("processApplicationDeletion - END");
			throw new AppDOnboardingException(
					"RollBackHandler - processAplicationDeletion - Exception while deleting Applications");
		}

	}
	
	/**
	 * This method is used to invoke email for application deletion. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @param eumList : List type eum apps list.	 
	 * @return boolean
	 */
	private boolean getResultForOpCounter(AppDOnboardingRequest request, List<String> eumList) {
		logger.info("getResultForOpCounter - START");
		logger.info("Delete APM Application [Rollback] {} {}",
				request.getRequestDetails().getAppGroupName(), request.getRequestDetails().getCtrlName());
		 boolean result = true;
		int count = request.getRetryDetails().getOperationCounter() - 6;
		for (int i = 0; i < count; i++) {
			eumList.add(request.getRequestDetails().getEumApps().get(i));
		}
		logger.info("getResultForOpCounter - END");
		return result;
	}
	/**
	 * This method is used to process application deletion update. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @param checkOperationCounter: boolean type true or false.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */
	public boolean processAppDeletionUpdate(AppDOnboardingRequest request, boolean checkOperationCounter) {
		logger.info("processAppDeletionUpdate - START");
		List<String> eumList = new ArrayList<>();
		boolean result = false;
		if (checkOperationCounter) {
			int count = request.getRetryDetails().getOperationCounter() - 1;
			for (int i = 0; i < count; i++) {
				eumList.add(request.getRequestDetails().getAddEumpApps().get(i));
			}
			 logger.info(EUM_DELETE_MSG, eumList);
			 logger.info("processAppDeletionUpdate - check opearation counter - END");
			 result = true;
		} else {
			 logger.info("processAppDeletionUpdate - not check opearation counter - END");
			logger.info("Delete EUM Application[Rollback - Delete All AppD Role related to APP] {} {}",
					request.getRequestDetails().getAddEumpApps(), request.getRequestDetails().getCtrlName());
			result = true;
		}
		logger.info("processAppDeletionUpdate - END");
		return result;
	}
	/**
	 * This method is used to check if application is deleted. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @param checkOperationCounter: boolean type true or false.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */

	public boolean checkIfApplicationDeleted(AppDOnboardingRequest request, boolean checkOperationCounter)
			throws AppDOnboardingException {
		logger.info("checkIfApplicationDeleted - START");
		boolean result = false;

		if (checkOperationCounter) {
			if (request.getRetryDetails().getOperationCounter() <= 3)
				result = true;
			else if (request.getRetryDetails().getOperationCounter() >= 4
					&& request.getRetryDetails().getOperationCounter() <= 6)
				result = operationHandler.checkIfAPMApplicationNotExist(request.getRequestDetails().getAppGroupName(),
						request.getRequestDetails().getCtrlName());
			else if (request.getRetryDetails().getOperationCounter() >= 7) {
				result = operationHandler.checkIfAPMApplicationNotExist(request.getRequestDetails().getAppGroupName(),
						request.getRequestDetails().getCtrlName());
				if (result) {
					result = operationHandler.checkIfEUMApplicationNotExist(request.getRequestDetails().getEumApps(),
							request.getRequestDetails().getCtrlName());

				}
			}
		} else {
			result = operationHandler.checkIfAPMApplicationNotExist(request.getRequestDetails().getAppGroupName(),
					request.getRequestDetails().getCtrlName());
			if (result && request.getRequestDetails().getEumApps() != null
					&& !request.getRequestDetails().getEumApps().isEmpty()) {
				result = operationHandler.checkIfEUMApplicationNotExist(request.getRequestDetails().getEumApps(),
						request.getRequestDetails().getCtrlName());
			}

		}
		if (result) {
			logger.info("checkIfApplicationDeleted - result - END");
			return true;
		}
		else
		{
			logger.info("checkIfApplicationDeleted - END");
			throw new AppDOnboardingException(
					"RollBackHandler - checkIfApplicationDeleted - Applications are still present");
		}

	}

	/**
	 * This method is used to check if eum application is deleted. 
	 * @param request: AppDOnboardingRequest type payload.
	 * @throws AppDOnboardingException.
	 * @return boolean
	 */
	public boolean checkIfAppDeletedUpdate(AppDOnboardingRequest request) throws AppDOnboardingException {
		logger.info("checkIfAppDeletedUpdate - START");
		boolean result = false;
		result = operationHandler.checkIfEUMApplicationNotExist(request.getRequestDetails().getAddEumpApps(),
				request.getRequestDetails().getCtrlName());
		if (result)
		{
			logger.info("checkIfAppDeletedUpdate - result - END");
			return true;
		}
		else
		{
			logger.info("checkIfAppDeletedUpdate - END");		
			throw new AppDOnboardingException(
					"RollBackHandler - checkIfAppDeletedUpdate - EUM Applications are still present");
		}

	}

}