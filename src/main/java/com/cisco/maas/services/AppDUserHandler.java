package com.cisco.maas.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;

/**
 * This class contains methods to create users, roles mappings and update groups .
 * */
@Service 
public class AppDUserHandler extends AppDOnboardingRequestHandlerImpl{ 

	private static final Logger logger = LoggerFactory.getLogger(AppDUserHandler.class);
	private static final String SUFFIX="AppD-";
	private static final String VIEWPREFIX="v";
	private static final String ADMINPREFIX="a";
	private static String userBaseUrl="api/rbac/v1/users";
	private String controllerPrefix;
	private String password;

	@Autowired 
	AppDynamicsUtil appdUtil;				
	@Autowired
	AppDLicensesHandler licenseHandler;	
	@Autowired
	AppDApplicationCreationHandler appDApplicationCreationHandler;				
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	APPDMasterDAO aPPDMasterDAO;
	
	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public AppDUserHandler() {
		password=System.getenv("appd_localUserDefaultPassword");	
		Base64.Decoder decoder = Base64.getDecoder();	
		if (password != null) {			
			password = new String(decoder.decode(password));
		}
		
	 	try (InputStream input = new FileInputStream(getClass().getClassLoader().getResource("config.properties").getFile())) 
		{
		 	Properties properties = new Properties();
		 	properties.load(input);			
		 	controllerPrefix=properties.getProperty("appd.prefix");
		}catch(Exception error) {
		 	logger.info("constructor - Exception Loading Properties File",error); 
		}
	}
	
	/**
	 * This method handle request based on request type and sets next handler.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @throws: AppDOnboardingException.
	 */

	@Override
	public void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException
	{	
		logger.info("handleRequest - START");
		request.getRetryDetails().setFailureModule(Constants.AD_HANDLER);
		if (Constants.REQUEST_TYPE_CREATE.equals(request.getRequestType()))
		{
			logger.info("handleRequest - Started Building Groups in Create Request");
			request=this.buildAppDetails(request);
			if(request!=null)
			{
				request.getRetryDetails().setOperationCounter(1);
				logger.info("handleRequest - Create Group Success, Calling AppDApplicationCreationHandler");					
				this.setNextHandler(appDApplicationCreationHandler);	
				super.handleRequestImpl(request);				
			}
		}		
		else if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType()))
		{		
			logger.info("handleRequest - Started Building Groups in Update Request");
			logger.info("handleRequest - Pending Update Request Fetching the Request Details");				
			
			request=this.eumUpdate(request);


			if(request.getRequestDetails().getAddEumpApps()!=null && !request.getRequestDetails().getAddEumpApps().isEmpty())
				request=this.buildAppDetails(request);

			if(request!=null)
			{
				request.getRetryDetails().setOperationCounter(1);
				logger.info("handleRequest - Update  Group Success, Calling  AppDApplicationCreationHandler");					
				this.setNextHandler(appDApplicationCreationHandler);	
				super.handleRequestImpl(request);
			}
			
		}
		logger.info("handleRequest - END");
	}	

	/**
	 * This method builds application details with the given request details.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @throws: AppDOnboardingException.
	 * @returns AppDOnboardingRequest
	 */
	
	public synchronized AppDOnboardingRequest buildAppDetails(AppDOnboardingRequest request) throws AppDOnboardingException
	{
		logger.info("buildAppDetails - START");
		RetryDetails rDetails;
		
		int operationCounter=request.getRetryDetails().getOperationCounter();	
		
		if(request.getRetryDetails()!=null)
			 rDetails=request.getRetryDetails();
		else
		{
			 rDetails= new RetryDetails();
			 rDetails.setFailureModule(Constants.AD_HANDLER);
		}
	
		try 
		{	
			
			logger.info("buildAppDetails - Building Application Details");			
			if(operationCounter==1)
			{	
				RoleMapping mapping=this.prepareRoleMapping(request);
				if(mapping!=null)
					rDetails.setMapping(mapping);
				rDetails.setAdminUsers(request.getRequestDetails().getAdminUsers());
				rDetails.setViewUsers(request.getRequestDetails().getViewUsers());

				if(Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) 
					rDetails.setMappingList(request.getMapping());				

				operationCounter=2;
				rDetails.setOperationCounter(operationCounter);
				request.setRetryDetails(rDetails);								
			}	
			
			if(operationCounter==2)
			{
				if(request.getRequestType().equals(Constants.REQUEST_TYPE_UPDATE))
					request.setMapping(this.prepareOrUpdateMappingList(request, request.getMapping().get(0)));			
				else
					request.setMapping(this.prepareOrUpdateMappingList(request, request.getRetryDetails().getMapping()));			
			
			}
			logger.info("buildAppDetails - END");
			return request;
		}catch(Exception e)
		{	
			logger.error("buildAppDetails - ERROR");
			logger.info("buildAppDetails - Encountered Exception Marking for Retry");
			throw new AppDOnboardingException("AppDUserHandler - buildAppDetails - Exception in buildAppDetails", request, e);
		}	
	}  
	

	/**
	 * This method is to prepare role mappings with the given request details.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @throws: IOException.
	 * @returns RoleMapping
	 */
	public RoleMapping prepareRoleMapping(AppDOnboardingRequest request) throws IOException
	{
		logger.info("prepareRoleMapping - START");
		logger.info("prepareRoleMapping(MCMPRequest) - Started preparing RoleMapping for Application :: {}", request.getRequestDetails().getAppGroupName());	

		if(this.createAppDLocalUsersIfNotExists(request)) {
			logger.info("prepareRoleMapping() - All AppD local users exists");
		}
		else {
			logger.info("prepareRoleMapping() - Failed to create non existing appd local users");
		}
		if(request.getRequestType().equals(Constants.REQUEST_TYPE_CREATE)) {
			RoleMapping mapping=new RoleMapping();
			String appGroupName=request.getRequestDetails().getAppGroupName();
	
			String groupUUID=this.getADbySO(appGroupName);					
			String adminGroupName=SUFFIX+groupUUID+ADMINPREFIX;
			String viewGroupName=SUFFIX+groupUUID+VIEWPREFIX;
			mapping.setCtrlName(request.getRequestDetails().getCtrlName());
			mapping.setAppGroupName(appGroupName);
			mapping.setAdminGroupName(adminGroupName);
			mapping.setViewGroupName(viewGroupName);
			logger.info("prepareRoleMapping(Request) - Ended preparing RoleMapping for Application {}", request.getRequestDetails().getAppGroupName());	
			logger.info("prepareRoleMapping - END");
			return mapping;
		}
		return null;
	}
	
	/**
	 * This method is to prepare or update role mappings with the given request details.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @param mapping: RoleMapping type mapping information.	 
	 * @returns List
	 */
	
	public List<RoleMapping> prepareOrUpdateMappingList(AppDOnboardingRequest request, RoleMapping mapping)
	{	    	
		logger.info("prepareOrUpdateMappingList : START");			

		List<RoleMapping> mappingList = new ArrayList<>();  
		List<String> eumList = new ArrayList<>();

		mappingList.add(mapping);
		if("create".equalsIgnoreCase(request.getRequestType()))
		{
			eumList = request.getRequestDetails().getEumApps();
		}
		else if(Constants.REQUEST_TYPE_UPDATE.equalsIgnoreCase(request.getRequestType()))
		{
			List<String> currentEUMApps = request.getRequestDetails().getEumApps();
			if(request.getRequestDetails().getDeleteEumpApps() != null)
				currentEUMApps.removeAll(request.getRequestDetails().getDeleteEumpApps());
			eumList = currentEUMApps;
		}

		if(eumList!=null)
		{	
			for(String eumName : eumList)
			{
				RoleMapping roleMap = new RoleMapping();
				roleMap.setAppGroupName(eumName);
				roleMap.setCtrlName(mapping.getCtrlName());
				roleMap.setAdminGroupName(mapping.getAdminGroupName());
				roleMap.setViewGroupName(mapping.getViewGroupName());

				if(!mappingList.contains(roleMap))
				{
					mappingList.add(roleMap);
				}
			}
		}
		logger.info("prepareOrUpdateMappingList : END");	
		return mappingList;
	}

		/**
	 * This method is to update eum apps with the given request details.	 
	 * @param request: AppDOnboardingRequest type request details.	 
	 * @returns AppDOnboardingRequest
	 */

	public synchronized AppDOnboardingRequest eumUpdate(AppDOnboardingRequest request)
	{
		logger.info("eumUpdate - START");
		if(request!=null)
		{
			APPDMaster masterDetails=aPPDMasterDAO.findByApp(request.getRequestDetails().getAppGroupName(), request.getRequestDetails().getCtrlName());
			List<String> oldEumAppList=masterDetails.getEumApps();
			logger.info("eumUpdate - Old EUM appList Fetched from master :: {}",oldEumAppList);
			logger.info("eumUpdate - New EUM appList Fetched Incoming request :: {}",request.getRequestDetails().getEumApps());

			List<String> newEumAppList=new ArrayList<>(request.getRequestDetails().getEumApps());


			List<String> tempList=new ArrayList<>(newEumAppList);
			newEumAppList.removeAll(oldEumAppList);				
			oldEumAppList.removeAll(tempList);		
			if(!newEumAppList.isEmpty())
			{
				logger.info("eumUpdate - List of EUM apps which needs to be Added :: {}",newEumAppList);
				request.getRequestDetails().setAddEumpApps(newEumAppList);
			}
			if(!oldEumAppList.isEmpty())
			{
				logger.info("eumUpdate - List of EUM apps which needs to be Deleted :: {}",oldEumAppList);
				request.getRequestDetails().setDeleteEumpApps(oldEumAppList);
			}
			return request;

		}
		logger.info("eumUpdate - END");
		return null;
	}

	/**
	 * This method is to generate uuid with the given string.	 
	 * @param so: String type string.	 
	 * @returns String
	 */
	public String getADbySO(String so) 
	{	
		logger.info("getADbySO - START");
		String suuid = null;
		try
			{	
				so=so.trim();
				//to generate UUID based on input string
				byte[] a = so.getBytes();
				suuid = UUID.nameUUIDFromBytes(a).toString();
				logger.info("getADbySO - Generated UUID for application group name {} :: {}",so,suuid);				
			}catch(Exception e)
			{
				logger.error("getADbySO - ERROR");				
				throw e;
			}
	   logger.info("getADbySO - END");
	   return suuid;
	}
	
	/**
	 * This method is to check if the local user is exists.	 
	 * @param userName: String type user name.	 
	 * @param ctrlName: String type controller name.
	 * @throws IOException
	 * @returns boolean
	 */
	public boolean checkIfAppDLocalUserExist(String userName, String ctrlName) throws IOException{
		logger.info("checkIfAppDLocalUserExist - START");
		String rUrl=Constants.PROTO+ ctrlName + controllerPrefix +userBaseUrl+"/name/"+userName;
		String response= appdUtil.appDConnectionOnlyGet(rUrl, Constants.HTTP_VERB_GET,ctrlName,"role");
		logger.info("checkIfAppDLocalUserExist - END");
		return(response!=null);
			
		
	}
	
	/**
	 * This method is to create local user is not exists.	 
	 * @param request: AppDOnboardingRequest type user name.	 
	 * @throws IOException.
	 * @returns boolean
	 */
	public boolean createAppDLocalUsersIfNotExists(AppDOnboardingRequest request) throws IOException {
		logger.info("createAppDLocalUsersIfNotExists - START");
		List<String> adminList = Arrays.asList(request.getRequestDetails().getAdminUsers().split(","));
		List<String> viewList = Arrays.asList(request.getRequestDetails().getViewUsers().split(","));

		for (String adminUser : adminList) {
			if (!this.checkIfAppDLocalUserExist(adminUser, request.getRequestDetails().getCtrlName())) {
				logger.info("createAppDLocalUsersIfNotExists - Creating admin local user {}", adminUser);
				this.createAppDLocalUser(adminUser, request.getRequestDetails().getCtrlName());

			} else {
				logger.info("createAppDLocalUsersIfNotExists - Admin local user {} exists", adminUser);
			}
		}

		for (String viewUser : viewList) {
			if (!this.checkIfAppDLocalUserExist(viewUser, request.getRequestDetails().getCtrlName())) {
				logger.info("createAppDLocalUsersIfNotExists - Creating view local user {}", viewUser);
				this.createAppDLocalUser(viewUser, request.getRequestDetails().getCtrlName());

			} else {
				logger.info("createAppDLocalUsersIfNotExists - View local user {} exists", viewUser);
			}
		}
		logger.info("createAppDLocalUsersIfNotExists - END");
		return true;

	}
	
	/**
	 * This method is to creates appd  local user.	 
	 * @param localUserName: String type local user name.
	 * @param ctrlName : String type controller name. 
	 * @throws IOException.
	 * @returns boolean
	 */
	public boolean createAppDLocalUser(String localUserName, String ctrlName) throws IOException {	
		logger.info("createAppDLocalUser - START");
		String json= "{\n\"name\": \""+localUserName+"\",\"security_provider_type\": \"INTERNAL\",\"displayName\": \""+localUserName+"\",\"password\": \""+password+"\"}";	
		String rURL=Constants.PROTO+ ctrlName + controllerPrefix +userBaseUrl;
		
		String response=appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, "role");	

		if(response!=null)
		{
			JSONObject responseJson = new JSONObject(response);  
			try{
				if(responseJson.get("id") != null){
					logger.info("createAppDLocalUsersIfNotExists - Creation of user {} successful", localUserName);
					logger.info("createAppDLocalUser - END");
				 	return true;
				}
			}catch(Exception e){
				logger.error("createAppDLocalUser - ERROR");
				logger.info("createAppDLocalUser - Creation of AppD local user {} Failed due to Exception", localUserName);
				throw e;
			}	
		 }
		else{
			logger.info("createAppDLocalUser - Creation of AppD local user {} Failed", localUserName);
		}
		logger.info("createAppDLocalUser - END");
		return false;
	}
	
}
