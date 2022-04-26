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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;

/**
 * This class creates apm and eum apps, updates eum apps, deletes eum apps, get app id and handles request - create and update.
 */
@Service
public class AppDApplicationCreationHandler extends AppDOnboardingRequestHandlerImpl{	
	private static final Logger logger = LoggerFactory.getLogger(AppDApplicationCreationHandler.class);	
	private String getEUMApplicationURL;
	private String deleteEUMApplicationURL;
	@Autowired
	private AppDRoleManager roleManager;
	@Autowired 
	AppDynamicsUtil appdUtil;
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	AppDLicensesHandler licenseHandler;
	
	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public AppDApplicationCreationHandler() 
	{
		try (InputStream input = new FileInputStream(getClass().getClassLoader().getResource("config.properties").getFile())) 
		 {   
			 Properties properties = new Properties();
		     properties.load(input);		 
		     getEUMApplicationURL = properties.getProperty("appd.getEUMApplication.url");
		     deleteEUMApplicationURL = properties.getProperty("appd.deleteEUMApplication.url");
		 }catch(Exception error) 
		 {
			logger.info("Exception Loading Properties File",error); 
		 }
	}
	
	/**
	 * This method handle request based on request type and sets next handler
	 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation.
	 */
	@Override
	public void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException
	{
		logger.info("handleRequest - START");
		request.getRetryDetails().setFailureModule(Constants.APP_CREATION_HANDLER); 
		
		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType()))
		{
			logger.info("handleRequest - Processing Request in Create Type");			
			request=this.createApplication(request);		
			logger.info("handleRequest - Create Request Processing Successful Calling RoleManager Handler");
			request.getRetryDetails().setOperationCounter(1);			
			this.setNextHandler(roleManager);	
			super.handleRequestImpl(request);					
		}	
		
		if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType()))
		{
			logger.info("handleRequest - Processing Request in Update Type");
			if(request.getRequestDetails().getAddEumpApps()!=null && !request.getRequestDetails().getAddEumpApps().isEmpty()) {
				request=this.updateEUMApplication(request);	
			}
			if(request.getRequestDetails().getDeleteEumpApps()!=null && !request.getRequestDetails().getDeleteEumpApps().isEmpty())
				this.deleteEUMApplication(request.getRequestDetails().getDeleteEumpApps());
			logger.info("handleRequest - Update Request Processing Successful Calling RoleManager Handler");
			
			request.getRetryDetails().setOperationCounter(1);
			this.setNextHandler(roleManager);	
			super.handleRequestImpl(request);
			logger.info("handleRequest - END");
		}
	}
	
	/**
	 * This method creates application in AppDynamics , set BT settings in AppD and fetch EUM Apps based on counter.
	 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation.
	 * @throws AppDOnboardingException
	 * @Returns AppDOnboardingRequest 
	 */
	public synchronized AppDOnboardingRequest createApplication(AppDOnboardingRequest request) throws AppDOnboardingException   
	{	
		logger.info("createApplication - START");
		int operationCounter=request.getRetryDetails().getOperationCounter();
		
		try 
		{		
			if(operationCounter==1)
			{
			
				String applicationId = appdUtil.createApplicationInAppDynamics(request.getRequestDetails().getAppGroupName(),"APM");
				logger.info("createApplicaition - APM application created - Application ID :: {}",applicationId);		
		
				operationCounter=2;
				request.setAppGroupID(applicationId);
				request.getRetryDetails().setAppDApplicationID(applicationId);
				request.getRetryDetails().setOperationCounter(operationCounter);				
			}
			
			if(operationCounter==2)
			{
				Boolean statusFlag = appdUtil.setBTSettigsInAppDynamics(request.getAppGroupID());
				if(statusFlag.equals(true)) {
					logger.info("createApplicaition - BT Lockdown enabled - for Application ID :: {}",request.getAppGroupID());
					operationCounter=3;
					request.getRetryDetails().setOperationCounter(operationCounter);
				}
				else {
					logger.info("createApplicaition - BT Lockdown couldn't be enabled - Failed Application Creation");
					request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
					throw new AppDOnboardingException("createApplicaition - BT Lockdown couldn't be enabled",request);
				}
			}
			
			if(request.getRequestDetails().getEumApps()!=null && operationCounter>=3)
			{
				List<String> eumList= request.getRequestDetails().getEumApps();
				for(String eumApp : eumList)
				{
					String eumApplicationId = appdUtil.createApplicationInAppDynamics(eumApp,"WEB");
					logger.info("createApplicaition - EUM application {} created - EUM application Id - {}",eumApp, eumApplicationId);
				}
				logger.info("createApplicaition - Create EUM Applications - END");
			}
			logger.info("createApplication - END");
			return request;				
		}catch(AppDOnboardingException ce)
		{
			logger.error("createApplication - ERROR");
			throw new AppDOnboardingException(ce.getActualMessage(), request, ce);
		}					 
	}


	/**
	 * This method Updates EUM application in AppDynamics.
	 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation.
	 * @throws AppDOnboardingException
	 * @Returns AppDOnboardingRequest 
	 */
	public synchronized AppDOnboardingRequest updateEUMApplication(AppDOnboardingRequest request) throws AppDOnboardingException 
	{	
		logger.info("updateEUMApplication - START");
		try
		{			
			int operationCounter=request.getRetryDetails().getOperationCounter();
			if(operationCounter>=1)
			{
				
				List<String> eumList= request.getRequestDetails().getAddEumpApps();
				logger.info("updateEUMApplication - Started Creating EUM Application");
				this.createEUMApplication(request, operationCounter, eumList);				
				logger.info("updateEUMApplication - Ended Creating EUM Application");			
			}
			logger.info("updateEUMApplication - END");
		   return request;
		}
		catch(AppDOnboardingException e)
		{
			logger.error("updateEUMApplication - ERROR");
			throw new AppDOnboardingException("AppDApplicationCreationHandler - updateEUMApplication - Exception in updateEUMApplication",request, e);
		}
	}
	
	/**
	 * This method creates EUM application in AppDynamics.
	 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation.
	 * @throws AppDOnboardingException
	 * @Returns AppDOnboardingRequest 
	 */
	 AppDOnboardingRequest createEUMApplication(AppDOnboardingRequest request, int operationCounter, List<String> eumList)
			throws AppDOnboardingException {
		logger.info("createEUMApplication - START");
		int ival = operationCounter - 1;
		for (int i = ival; i < eumList.size(); i++) {
			try {
				String eumApplicationId = appdUtil.createApplicationInAppDynamics(eumList.get(i), "WEB");
				logger.info("updateEUMApplication - Updated EUM Application {} - Application Id {}", eumList.get(i),
						eumApplicationId);
			} catch (AppDOnboardingException e) {
				logger.error("createEUMApplication : ERROR");
				request.setRequestStatus(Constants.REQUEST_STATUS_ERROR);
				throw new AppDOnboardingException(
						"updateEUMApplication - EUM application already exists need manual intervention",
						request, e);
			}

			operationCounter = operationCounter + 1;
			request.getRetryDetails().setOperationCounter(operationCounter);

		}
		logger.info("createEUMApplication - END");
		return request;
	}

	/**
	 * Converts List of EUM Name to List of its Id
	 * @param eumList : List of EUM Names
	 * @return List of EUM id
	 * @throws IOException
	 */
	public List<String> convertEUMNameToIDList(List<String> eumList) throws IOException {
		String response;
		HashMap<String,String> nameIDMapping = new HashMap<>();
		List<String> listOfEUMID = new ArrayList<>();
		
		response = appdUtil.getRequest(getEUMApplicationURL);
		JSONArray jsonResponse = new JSONArray(response);

		for (int i = 0; i < jsonResponse.length(); i++) {
		    JSONObject eumApp = (JSONObject)jsonResponse.get(i);
		    nameIDMapping.put(eumApp.get("name").toString(), eumApp.get("id").toString());
		}
		
		for(String eumName: eumList) {
			if(nameIDMapping.get(eumName) == null) {
				logger.info("convertEUMNameToIDList() - {} does not exist on controller",eumName);
				continue;
			}
			logger.info("convertEUMNameToIDList() - {} exist on controller",eumName);
			listOfEUMID.add(nameIDMapping.get(eumName));
		}
		
		return listOfEUMID;
		
	}
	 /**
		 * This method deletes EUM application from AppDynamics.
		 * @param eumList: List type EUM application list.
		 * @param ctrlName: String type controller name.
		 * @param requestId: String type request id.		 * 
		 * @Returns boolean 
		 */
	public boolean deleteEUMApplication(List<String> eumList)
	{
		if(!eumList.isEmpty()) {
			try {
				List<String> listOfEUMID = this.convertEUMNameToIDList(eumList);
				for(String eumID:listOfEUMID) {
					Integer responseCode = appdUtil.postRequest(deleteEUMApplicationURL, eumID).getStatusCodeValue();
					if(responseCode == 204) {
						logger.info("deleteEUMApplication() - Deletion of {} successful",eumID);
					}
					else {
						logger.info("deleteEUMApplication() - Deletion of {} failed during post request",eumID);
					}
				}
			} catch(IOException error) {
				logger.error("deleteEUMApplication() - EUM application deletion failed with error: {0}",error);
				return false;
			}
		}
		logger.info("deleteEUMApplication() - Deletion of EUM Applications successful");
		return true;
	}
	
	/**
	 * Checks whether resource move of application is pending or successful
	 * @param request : AppD Onboarding request
	 * @return
	 * @throws AppDOnboardingException
	 */
	public AppDOnboardingRequest resourceMoveApplication(AppDOnboardingRequest request) throws AppDOnboardingException
	{
		request=requestHandler.getUpdatedRequestResourceMove(request);
		String ctrlName = request.getRequestDetails().getCtrlName();
		String newAppName = request.getRequestDetails().getAppGroupName();
		if(Constants.REQUEST_STATUS_PENDING.equalsIgnoreCase(request.getRequestStatus()))
		{
		logger.info("resourceMoveApplication - Invoking mail for Renaming the applications :: {}",request.getRequestDetails().getAppGroupName());
		}
		boolean checkStatus = this.checkIfRenameIsComplete(newAppName, ctrlName, request.getRequestDetails().getEumApps());
		logger.info("resourceMoveApplication - Validation Status of rename Application :: {}",checkStatus);
		if(checkStatus)
		{
			return request;
		}
		else
		{
			throw new AppDOnboardingException("resourceMoveApplication - Rename Of Application is still pending",request);

		}
		
	}
	/**
	 * Gets application id from controller
	 * @param ctrlName : Controller Name
	 * @param appGroupName : Application Group Name
	 * @return
	 * @throws IOException
	 */
	public String getAppID(String ctrlName,String appGroupName) throws IOException
	{
		logger.info("getAppID - START");
		String rUrl=Constants.PROTO +ctrlName+".saas.appdynamics.com/controller/rest/applications/"+appGroupName+Constants.URL_PARAM_OUTPUT_JSON;

		logger.info("getAppID - Printing the URL in getApplicationID Method {}",rUrl);			
		String response= appdUtil.appDConnectionOnlyGet(rUrl, Constants.HTTP_VERB_GET, ctrlName,"app");
		
		if(response==null)
			return null;
		JSONArray responseArray=new JSONArray(response);		
		String appGroupID=null;
		for (int i=0;i<responseArray.length();i++)
		{
			if(responseArray.getJSONObject(i).get("name").equals(appGroupName))
			{
				appGroupID=responseArray.getJSONObject(i).get("id").toString();
				
			}			
		}
		logger.info("getAppID - END");
		return appGroupID;
	}

/**
	 * This method checks if application name is changed in AppDynamics.
	 * @param appGroupName: String type application group name.
	 * @param ctrlName: String type controller name.
	 * @param eumList: List type EUM applications.
	 * @throws AppDOnboardingException
	 * @Returns boolean 
	 */
	public boolean checkIfRenameIsComplete(String appGroupName,String ctrlName,List<String> eumList) throws AppDOnboardingException
	{
		logger.info("checkIfRenameIsComplete - START");
		try
		{
			boolean checkFlag=false;
			String result;
			result=this.getAppID(ctrlName, appGroupName);
			if(result!=null)
				checkFlag= true;

			if(checkFlag)
			{
			if(eumList!=null)
			{
				for(String eumName:eumList)
				{
					result= this.getAppID(ctrlName, eumName);
					if(result==null)	
						return false;
				}
				logger.info("checkIfRenameIsComplete - appId is present - END");
				return true;
			}
			else {
				logger.info("checkIfRenameIsComplete - eumList is empty -  END");
				return true;
			}
			}
			else
			{
				logger.info("checkIfRenameIsComplete -appId is not present-  END");
				return false;
			}
		}
		catch(IOException e)
		{
			logger.error("checkIfRenameIsComplete - ERROR");
			throw new AppDOnboardingException("AppDApplicationCreationHandler - checkIfRenameIsComplete - Exception in checkIfRenameIsComplete", e);
		}
	}
}
