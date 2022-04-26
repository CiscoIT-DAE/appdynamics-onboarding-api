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

package com.cisco.maas.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.util.Constants;
import com.mongodb.MongoException;
import java.io.IOException;

@Repository
@Qualifier("APPDMasterDAO")
public class APPDMasterDAOImpl implements APPDMasterDAO
{
	private static final Logger logger = LoggerFactory.getLogger(APPDMasterDAOImpl.class);
	 @Autowired
	 MongoTemplate mongoTemplate;
	 
	
	@Override
	public boolean createApplication(APPDMaster masterDetails) {
		try {
			logger.info("createApplication- Start create()");
			mongoTemplate.save(masterDetails);
			return(this.checkGetId(masterDetails));
			
			
		} catch (Exception error) {
			logger.info("createApplication - Exception in Insert Request");
			throw new MongoException(error.getMessage(), error);
		}
	}

	

	@Override
	public boolean updateApplication(AppDOnboardingRequest request)  {
		try {
			logger.info("updateApplication - Start Update()");

			APPDMaster appDetails = this.findByApp(request.getRequestDetails().getAppGroupName(),
					request.getRequestDetails().getCtrlName());

			appDetails.setApmLicenses(request.getRequestDetails().getApmLicenses());

			if (request.getRequestDetails().getEumApps() != null)
				appDetails.setEumApps(request.getRequestDetails().getEumApps());
			if (request.getRequestDetails().getAlertAliases() != null)
				appDetails.setAlertAliases(request.getRequestDetails().getAlertAliases());

			mongoTemplate.save(appDetails);
			return(this.checkGetId(appDetails));
			
		} catch (Exception error) {
			logger.info("updateApplication - Exception in Update Request");
			throw new MongoException(error.getMessage(), error);
		}
	}

	@Override
	public boolean updateNewAppName(AppDOnboardingRequest request, String appName)  {
		try {
			logger.info("updateNewAppName - Start updateNewAppName()");

			APPDMaster appDetails = this.findByApp(appName, request.getRequestDetails().getCtrlName());

			if (request.isResourceMove())
				appDetails.setAppGroupName(request.getRequestDetails().getAppGroupName());
			if (request.getRequestDetails().getEumApps() != null)
				appDetails.setEumApps(request.getRequestDetails().getEumApps());
			if (request.getRequestDetails().getApmLicenses() != 0)
				appDetails.setApmLicenses(request.getRequestDetails().getApmLicenses());
			if (request.getRequestDetails().getAlertAliases() != null)
				appDetails.setAlertAliases(request.getRequestDetails().getAlertAliases());
		
			mongoTemplate.save(appDetails);

			if (appDetails.getId() != null) {
				logger.info("updateNewAppName - Update request Success");
				logger.info("updateNewAppName - End updateNewAppName()");
				return true;
			} else {
				logger.info("updateNewAppName - Update request Fail");
				logger.info("updateNewAppName - End updateNewAppName()");
				return false;
			}
		} catch (Exception error) {
			logger.info("APPDMasterDAOImpl: Exception in Update Request");
			throw new MongoException(error.getMessage(), error);
		}
	}

	@Override
	public boolean updateEUMLicense(String appGroupName, String ctrlName, int eUMLicense)  {
		APPDMaster appDetails = this.findByApp(appGroupName, ctrlName);
		appDetails.setNoOfEUMLicenses(eUMLicense);
		mongoTemplate.save(appDetails);
		return true;
	}

	@Override
	public boolean deleteApplication(APPDMaster appdMaster)  {
		try {
			logger.info("deleteApplication - Start");
			mongoTemplate.remove(appdMaster);

			logger.info("deleteApplication - Delete request Success");
			logger.info("deleteApplication - End Delete()");
			return true;

		} catch (Exception error) {
			logger.info("deleteApplication - Exception in deleteApplication Request");
			throw new MongoException(error.getMessage(), error);
		}
	}

	@Override
	public Iterable<APPDMaster> findAll() throws IOException {
		Iterable<APPDMaster> requestList = null;
		try {
			logger.info("findAll - Start findAll()");
			if (mongoTemplate != null)
				requestList = mongoTemplate.findAll(APPDMaster.class);

			if (requestList != null) {
				logger.info("findAll - End findAll()");
				return requestList;
			} else {
				logger.info("findAll - Request List is null and End findAll()");
				return null;
			}
		} catch (Exception error) {
			logger.info("findAll - Exception in findAll()");
			throw new MongoException(error.getMessage(), error);
		}
	}

	@Override
	public APPDMaster findByApp(String appGroupName, String environment)  {
		try {
			logger.info("findByApp - Start");

			Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(appGroupName)
					.andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(environment)));
			APPDMaster appdDetails = mongoTemplate.findOne(query, APPDMaster.class);

			logger.info("findByApp - End");
			return appdDetails;
		} catch (Exception error) {
			logger.info("findByApp - Exception");
			throw new MongoException(error.getMessage(), error);
		}
	}

	@Override
	public String getLicenseKey(String appGroupName, String environment) {
		logger.info("getLicenseKey - Start");
		Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(appGroupName).andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(environment)));
		APPDMaster appdDetails = mongoTemplate.findOne(query, APPDMaster.class);
		logger.info("getLicenseKey - End findByApp()");
		if(appdDetails!=null)
			{
			return appdDetails.getLicenseKey();
			}
	
		return null;
	}

	@Override
	public int[] getApmLicenses(String appGroupName, String ctrlName)  {
		logger.info("getApmLicenses - Start getApmLicenses()");
		Query query = new Query(Criteria.where(Constants.APP_GROUP_NAME).is(appGroupName)
				.andOperator(Criteria.where(Constants.CONTROLLER_NAME).is(ctrlName)));
		APPDMaster appdDetails = mongoTemplate.findOne(query, APPDMaster.class);
		logger.info("getApmLicenses - End getApmLicenses()");
	   if(appdDetails!=null) {
		int apmLicensesLicenses = appdDetails.getApmLicenses();
		return new int[] {apmLicensesLicenses};
	   }
	return new int[0];
	   
	}
	public boolean checkGetId(APPDMaster masterDetails) {
		logger.info("checkGetId - START");
		if (masterDetails.getId() != null) {
			logger.info("checkGetId - success");
			return true;
		} else {
			logger.info("checkGetId - not found");
			logger.info("checkGetId - End");
			return false;
		}
		
	}
}