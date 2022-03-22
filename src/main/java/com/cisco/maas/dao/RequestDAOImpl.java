package com.cisco.maas.dao;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.util.Constants;
import com.mongodb.MongoException;

/**
 * This class is used to store request data in database and query data from db using different fields
 */
@Repository
@Qualifier("RequestDAO")
public class RequestDAOImpl implements RequestDAO {
	private static final Logger logger = LoggerFactory.getLogger(RequestDAOImpl.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
  /**
   * This method is used to save create request in data base
   * @param request: AppDOnboardingRequest type
   * @return : boolean, true if successful in saving request in db
   * @throws : MongoException: if failed to save create request in mongo data base
   */
	@Override
	public boolean create(AppDOnboardingRequest request) {
		try {
			logger.info("create :: Start create()");
			Iterable<AppDOnboardingRequest> requestList = this.findAllByProjectId(request.getAppdExternalId());
			if (requestList == null) {
				logger.info("create :: Setting the Request Counter to 1");
				request.setRequestCounter(1);
			} else {
				request.setRequestCounter(IterableUtils.size(requestList) + 1);
				logger.info("create :: Setting the Request Counter to {}",
						IterableUtils.size(requestList) + 1);
			}

			mongoTemplate.save(request);
			logger.info("create :: End create()");
			return true;
		} catch (Exception error) {
			logger.info("Exception in Insert Request");
			throw new MongoException(error.getMessage(), error);
		}
	}
	
/**
 * This method is used to save update request in data base
 *  @param request: AppDOnboardingRequest type which contains payload for create operation
 *  @return : boolean, true if successful in saving request in db
 *  @throws : MongoException: if failed to save update request in mongo data base
 */
	@Override
	public boolean updateRequest(AppDOnboardingRequest request)  {
		try {
			logger.info("updateRequest - Start");
			Query query = new Query(
					Criteria.where(Constants.REQUEST_ID).is(request.getRequestDetails().getTrackingId()));
			AppDOnboardingRequest tempRequest = mongoTemplate.findOne(query, AppDOnboardingRequest.class);

			if (tempRequest != null) {
				tempRequest = this.prepareUpdateRequest(request, tempRequest);
				mongoTemplate.save(tempRequest);
				logger.info("updateRequest - Update Status Success");
				logger.info("updateRequest - End");
				return true;
			} else {
				logger.info("updateRequest - AppdProjectId not found");
				logger.info("updateRequest - End");
				return false;
			}
		} catch (Exception error) {
			logger.info("updateRequest - Exception in UpdateStatus()");
			throw new MongoException(error.getMessage(), error);
		}
	}
	/**
	 * This method is used to save updated retry lock for update request in data base
	 *  @param request: AppDOnboardingRequest type which contains payload for create operation
	 *  @return : boolean, true if successful in saving request in db
	 *  @throws : MongoException: if failed to save update request in mongo data base
	 */
	@Override
	public boolean updateRetryLock(AppDOnboardingRequest request)  {
		try {
			logger.info("updateRetryLock - Start");
			Query query = new Query(
					Criteria.where(Constants.REQUEST_ID).is(request.getRequestDetails().getTrackingId()));
			AppDOnboardingRequest tempRequest = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
			logger.info("updateRetryLock - Query output of updateRetryLock {}", tempRequest);

			if (tempRequest != null) {
				tempRequest.setRetryLock(request.isRetryLock());
				tempRequest.setRetryCount(request.getRetryCount());

				mongoTemplate.save(tempRequest);

				logger.info("updateRetryLock - updateRetryLock Success");
				logger.info("updateRetryLock - End updateRetryLock()");
				return true;
			} else {
				logger.info("updateRetryLock - AppdProjectId not found");
				logger.info("updateRetryLock - End updateRetryLock()");
				return false;
			}
		} catch (Exception error) {
			logger.info("updateRetryLock - Exception in updateRetryLock()");
			throw new MongoException(error.getMessage(), error);
		}
 }
 /**
  * This method is used to prepare update request and update values if not null 
  * @param request: AppDOnboardingRequest type which contains payload for update operation
  * @param tempRequest: AppDOnboardingRequest type which contains last request saved in data base for a given application
  * @return: AppDOnboardingRequest after updating
  */
	public AppDOnboardingRequest prepareUpdateRequest(AppDOnboardingRequest request, AppDOnboardingRequest tempRequest) {
		logger.info("prepareUpdateRequest - New Application Name {}", request.getRequestDetails().getAppGroupName());
		if (request.getRequestDetails().getAppGroupName() != null)
			tempRequest.getRequestDetails().setAppGroupName(request.getRequestDetails().getAppGroupName());
		if (request.getRequestDetails().getCtrlName() != null)
			tempRequest.getRequestDetails().setCtrlName(request.getRequestDetails().getCtrlName());
		if (request.getMapping() != null)
			tempRequest.setMapping(request.getMapping());
		if (request.getAppGroupID() != null)
			tempRequest.setAppGroupID(request.getAppGroupID());
		if (request.getLicenseKey() != null)
			tempRequest.setLicenseKey(request.getLicenseKey());
		if (request.getRetryDetails() != null)
			tempRequest.setRetryDetails(request.getRetryDetails());
		if (request.getRequestDetails().getAddEumpApps() != null)
			tempRequest.getRequestDetails().setAddEumpApps(request.getRequestDetails().getAddEumpApps());
		if (request.getRequestDetails().getDeleteEumpApps() != null)
			tempRequest.getRequestDetails().setDeleteEumpApps(request.getRequestDetails().getDeleteEumpApps());
		if (request.isResourceMove()) {
			tempRequest.getRequestDetails().setOldEumApps(request.getRequestDetails().getOldEumApps());
			tempRequest.getRequestDetails().setOldAppGroupName(request.getRequestDetails().getOldAppGroupName());
			tempRequest.getRequestDetails().setEumApps(request.getRequestDetails().getEumApps());
		}
		tempRequest.setRequestStatus(request.getRequestStatus());
		tempRequest.setRequestType(request.getRequestType());
		tempRequest.setRetryCount(request.getRetryCount());
		tempRequest.setRetryLock(request.isRetryLock());
		tempRequest.setResourceMove(request.isResourceMove());
		tempRequest.setRollbackCounter(request.getRollbackCounter());
		if (!Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType()))
			tempRequest.setOperationalStatus(request.getOperationalStatus());

		return tempRequest;
	}
	
 /**
  * This method is used to save updated request after resource move
  * @param request: AppDOnboardingRequest type which contains payload for update operation
  * @return: boolean, true if successful in saving updated request in db and false if id not found
  */
	@Override
	public boolean updateCreateRequest(AppDOnboardingRequest request)  {
		try {
			logger.info("updateCreateRequest - Start updateCreateRequest()");
			Query query = new Query(
					Criteria.where(Constants.REQUEST_ID).is(request.getRequestDetails().getTrackingId()));
			AppDOnboardingRequest tempRequest = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
		

			if (tempRequest != null) {
				tempRequest.getRequestDetails().setAppGroupName(request.getRequestDetails().getAppGroupName());
				tempRequest.getRequestDetails().setEumApps(request.getRequestDetails().getEumApps());
				tempRequest.getRequestDetails().setOldEumApps(request.getRequestDetails().getOldEumApps());
				tempRequest.getRequestDetails().setOldAppGroupName(request.getRequestDetails().getOldAppGroupName());

				mongoTemplate.save(tempRequest);
				logger.info("updateCreateRequest - updateCreateRequest Success");
				logger.info("updateCreateRequest - End updateCreateRequest()");
				return true;
			} else {
				logger.info("updateCreateRequest - ID not found");
				logger.info("updateCreateRequest - End updateCreateRequest()");
				return false;
			}
		} catch (Exception error) {
			logger.info("updateCreateRequest - Exception in updateCreateRequest()");
			throw new MongoException(error.getMessage(), error);
		}
	}
	
	/**
     * This method is used to query data from db by using operational status
	 * @param String, the value of status to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given operational status
     */
	@Override
	public Iterable<AppDOnboardingRequest> findByOpertionalStatus(String operationalStatus) {
		Iterable<AppDOnboardingRequest> requestList = null;
		try {
			logger.info("findByOpertionalStatus - Start");
			Query query = new Query(Criteria.where(Constants.OPERATIONAL_STATUS).is(operationalStatus));
			requestList = mongoTemplate.find(query, AppDOnboardingRequest.class);
			logger.info("findByOpertionalStatus -End");
			return requestList;
			
		} catch (Exception error) {
			logger.info("findByOpertionalStatus - Exception in findByOpertionalStatus()");
			throw new MongoException(error.getMessage(), error);
		}
	}

	/**
     * This method is used to query data from db by using failure module
	 * @return: Iterable of AppDOnboardingRequest containing data with given failure module
     */
	@Override
	public Iterable<AppDOnboardingRequest> findFailedRequests()  {
		Iterable<AppDOnboardingRequest> requestList = null;
		try {
			logger.info("findFailedRequests - START");
			Query query = new Query(Criteria.where(Constants.REQUEST_DETAILS_FAILURE_MODULE).exists(true)
					.andOperator(Criteria.where(Constants.REQUEST_STATUS)
							.in(Constants.REQUEST_STATUS_FAILED, Constants.ROLLBACK_FAILED)
							.andOperator(Criteria.where(Constants.RETRY_LOCK).is(false))));
			requestList = mongoTemplate.find(query, AppDOnboardingRequest.class);
			if (IterableUtils.size(requestList) > 0) {
				logger.info("findFailedRequests - RequestDAOImpl: End findByApp()");
				return requestList;
			} else
				return null;
		} catch (MongoException error) {
			logger.info("findFailedRequests - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}

	/**
     * This method is used to check if application exists in db or not by using appdExternalId
	 * @param String, the value of appdExternalId to be queried
	 * @return: boolean, true if application with given appdExternalId exists in db and false if it does not exists
     */
	@Override
	public boolean findAppByExternalId(String appdExternalId)  {
		AppDOnboardingRequest request = null;
		try {
			logger.info("findAppByExternalId- START");
			Query query = new Query(Criteria.where(Constants.APPD_EXTERNAL_ID).is(appdExternalId)
					.andOperator(Criteria.where(Constants.REQUEST_STATUS).in(Constants.REQUEST_STATUS_FAILED,
							Constants.REQUEST_STATUS_PENDING)));

			request = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
			if (request != null) {
				logger.info("findAppByExternalId - End for AppD External Id {}", request.getRequestDetails().getAppdProjectId());
				return true;
			} else
				return false;
		} catch (Exception error) {
			logger.info("findAppByExternalId - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}
	/**
     * This method is used to query data from db by using appdExternalId
	 * @param String, the value of appdExternalId to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given appdExternalId or null if appdExternalId does not exists
     */
	@Override
	public Iterable<AppDOnboardingRequest> findAllByProjectId(String appdExternalId)  {
		Iterable<AppDOnboardingRequest> requestList = null;
		try {
			logger.info("findAllByProjectId - Start");
			Query query = new Query(Criteria.where(Constants.APPD_EXTERNAL_ID).is(appdExternalId));
			requestList = mongoTemplate.find(query, AppDOnboardingRequest.class);
			if (IterableUtils.size(requestList) > 0) {
				logger.info("findAllByProjectId - End");
				return requestList;
			} else
				return null;
		} catch (Exception error) {
			logger.info("findAllByProjectId - Exception in findAllByProjectId()");
			throw new MongoException(error.getMessage(), error);
		}
	}
     
	/**
     * This method is used to query data from db by using appdExternalId and operational Status
	 * @param appdExternalId: String, the value of appdExternalId to be queried
	 * @param operationalStatus: String, the value of operational status to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given appdExternalId and operational Status or null if appdExternalId and operational Status does not exists
     */
	@Override
	public AppDOnboardingRequest findByProjectIdAndOpStatus(String appdExternalId, String operationalStatus)  {

		AppDOnboardingRequest request = null;
		try {
			logger.info("findByProjectIdAndOpStatus - Start");
			Query query = new Query(Criteria.where(Constants.APPD_EXTERNAL_ID).is(appdExternalId)
					.andOperator(Criteria.where(Constants.OPERATIONAL_STATUS).is(operationalStatus)));

			request = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
			if (request != null) {
				logger.info("findByProjectIdAndOpStatus - End findByProjectIdAndOpStatus() ");
				return request;
			} else
				return null;
		} catch (Exception error) {
			logger.info("findByProjectIdAndOpStatus - Exception in findByProjectIdAndOpStatus()");
			throw new MongoException(error.getMessage(), error);
		}
	}
	/**
     * This method is used to query data from db by using application name and request type
	 * @param appGroupName: String, the value of application name to be queried
	 * @param ctrlName: String, the name of AppD controller
	 * @param requestType: String, the value of request type to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given application name and request type or null if application name and request type does not exists
     */
	@Override
	public AppDOnboardingRequest findByAppNameAndRequestType(String appGroupName, String ctrlName, String requestType)
			 {

		AppDOnboardingRequest request = null;
		try {
			logger.info("findByAppNameAndRequestType - START");
			Sort sort = new Sort(Sort.Direction.DESC, "requestModifiedDate");

			Query query = new Query(Criteria.where(Constants.REQUEST_DETAILS_APP_GROUP_NAME).is(appGroupName)
					.andOperator(Criteria.where(Constants.REQUEST_DETAILS_CONTROLLER_NAME).is(ctrlName)
							.andOperator(Criteria.where(Constants.REQUEST_TYPE).is(requestType)))).with(sort);

			request = mongoTemplate.findOne(query, AppDOnboardingRequest.class);

			logger.info("findByAppNameAndRequestType - Added Sort to findOne create request");
			if (request != null) {
				logger.info("findByAppNameAndRequestType - End for AppD External Id {}", request.getRequestDetails().getAppdProjectId());
				return request;
			} else
				return request;
		} catch (Exception error) {
			logger.info("findByAppNameAndRequestType- Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}
	
	/**
     * This method is used to query data from db by using appdExternalId and request type
	 * @param appdExternalId: String, the value of appdExternalId to be queried
	 * @param requestType: String, the value of request type to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given appdExternalId and request type or null if appdExternalId and request type does not exists
     */
	@Override
	public AppDOnboardingRequest findByExternalIdAndRequestType(String appdExternalId, String requestType)  {

		AppDOnboardingRequest request = null;
		try {
			logger.info("findByExternalIdAndRequestType - START");
			Query query = new Query(Criteria.where(Constants.APPD_EXTERNAL_ID).is(appdExternalId)
					.andOperator(Criteria.where(Constants.REQUEST_TYPE).is(requestType)));

			request = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
			if (request != null) {
				logger.info("findByExternalIdAndRequestType  - End for AppD External Id {}", request.getAppdExternalId());
				return request;
			} else
				return request;
		} catch (MongoException error) {
			logger.info("findByExternalIdAndRequestType - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}

	/**
     * This method is used to query data from db by using tracking Id
	 * @param requestId: String, the value of tracking id to be queried
	 * @return: Iterable of AppDOnboardingRequest containing data with given tracking id or null if it does not exists
     */
	@Override
	public AppDOnboardingRequest findByRequestId(String requestId)  {
		try {
			logger.info("findByRequestId - Start");
			Query query = new Query(Criteria.where(Constants.REQUEST_ID).is(requestId));
			AppDOnboardingRequest tempRequest = mongoTemplate.findOne(query, AppDOnboardingRequest.class);
			if (tempRequest != null) {
				logger.info("findByRequestId - End");
				return tempRequest;
			} else {
				logger.info("findByRequestId - RequestId not found");
				logger.info("findByRequestId - End");
				return null;
			}
		} catch (Exception error) {
			logger.info("findByRequestId - Exception in findByRequestId()");
			throw new MongoException(error.getMessage(), error);
		}
	}
}