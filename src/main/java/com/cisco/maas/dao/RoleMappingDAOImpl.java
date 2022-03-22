package com.cisco.maas.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.util.Constants;
import com.mongodb.MongoException;

/**
 * This class is used to store role mapping data in database and query data from db using different fields
 */
@Repository
@Qualifier("RoleMappingDAO")
public class RoleMappingDAOImpl implements RoleMappingDAO {
	private static final Logger logger = LoggerFactory.getLogger(RoleMappingDAOImpl.class);
	@Autowired
	MongoTemplate mongoTemplate;
	 /**
	   * This method is used to save create role mapping request in data base
	   * @param request: RoleMapping type
	   * @return : boolean, true if successful in saving request in db and false if failed to sav in db
	   * @throws : MongoException: if failed to save request in mongo data base
	   */
	public boolean create(RoleMapping request)  {
		try {
			logger.info("create - Start create() in DAO Layer");
			if (mongoTemplate != null)
				mongoTemplate.save(request);

			if (request.getId() != null) {
				logger.info("create - Insert request Success");
				logger.info("create - End create()");
				return true;
			} else {
				logger.info("create - Insert request Fail");
				logger.info("create - End create()");
				return false;
			}
		} catch (Exception error) {
			logger.info("create - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}
  

	/**
     * This method is used to query data from db by using application name and controller name
	 * @param appdExternalId: String, the name of application to be queried
	 * @param  environment: String, the name of controller to be queried
	 * @return: Iterable of RoleMapping containing data with given application name and controller name or null if application name and controller name does not exists
     */
	public Iterable<RoleMapping> findByApp(String appGroupName, String environment)  {
		Iterable<RoleMapping> requestList = null;
		try {
			logger.info("findByApp - Start");
			Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(appGroupName)
					.andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(environment)));
			if (mongoTemplate != null)
				requestList = mongoTemplate.find(query, RoleMapping.class);
			if (requestList != null) {
				logger.info("findByApp - End");
				return requestList;
			}
			return null;
		} catch (Exception error) {
			logger.info("findByApp - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}
	/**
     * This method is used to query data from db by using application name and controller name
	 * @param appdExternalId: String, the name of application to be queried
	 * @param  environment: String, the name of controller to be queried
	 * @return: RoleMapping type containing data with given application name and controller name or null if application name and controller name does not exists
     */
	public RoleMapping findApp(String appGroupName, String environment)  {
		RoleMapping roleMap = null;
		try {
			logger.info("findApp - Start");
			Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(appGroupName)
					.andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(environment)));
			if (mongoTemplate != null)
				roleMap = mongoTemplate.findOne(query, RoleMapping.class);
			if (roleMap != null) {
				logger.info("findApp - End");
				return roleMap;
			}
			return null;
		} catch (Exception error) {
			logger.info("findApp - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}
	
    /**
     * This method deletes given role mapping object from data base
     * @param RoleMapping: the entry to be deleted
     * @return : true, if deletion is successful
     * @throws MongoException : if remove throws exception
     */
	@Override
	public boolean delete(RoleMapping roleMap)  {
		try {
			logger.info("delete - Start Delete()");
			mongoTemplate.remove(roleMap);
			logger.info("delete - Delete request Success - END");
			return true;

		} catch (Exception error) {
			logger.info("delete - Exception in Delete Request");
			throw new MongoException(error.getMessage(), error);
		}
	}
	  /**
     * This method is used to update resource from old to new application or eum
     * @param oldAppName: String type, the old name of application
     * @param newAppName: String type, the new application name to be updated
     * @param ctrlName: String type, the name of controller 
     * @return boolean : true, if update is successful and false if update fails
     */
	@Override
	public boolean resourceMoveUpdate(String oldAppName, String newAppName, String ctrlName) {
			Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(oldAppName)
					.andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(ctrlName)));
			RoleMapping roleMap = mongoTemplate.findOne(query, RoleMapping.class);
			if (roleMap != null) {
				logger.info("resourceMoveUpdate - Updating Record with new AppName");
				roleMap.setAppGroupName(newAppName);
				mongoTemplate.save(roleMap);
				if (roleMap.getId() != null) {
					logger.info("resourceMoveUpdate - Update request Success - END");
					return true;
				} else {
					logger.info("resourceMoveUpdate - Update request Fai - END");
					return false;
				}
			} else {

				logger.info("resourceMoveUpdate - END");
				return true;
			}
	}
}
