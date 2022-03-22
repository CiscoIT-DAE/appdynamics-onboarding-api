package com.cisco.maas.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;
/**
 * This class creates, updates and handles alerts: policies, actions, and health rules
 */
@Service
public class AppDAlertsHandler extends AppDOnboardingRequestHandlerImpl {
	private static final Logger logger = LoggerFactory.getLogger(AppDAlertsHandler.class);
	private static final String ACTION = "action";
	private static final String ACTIONS = "actions";
	private static final String POLICY = "policy";
	private static final String ALERT_BASE_URL = "alerting/rest/v1/applications/";
	private static final String POLICIES = "/policies";

	private String controllerPrefix;
	JSONParser parser = new JSONParser();
	@Autowired
	AppDynamicsUtil appdUtil;
	@Autowired
	APPDMasterDAO aPPDMasterDAO;
	@Autowired
	DBHandler dbHandler;
	@Autowired
	RequestHandler requestHandler;

	/**
	 * Initializing constants from config.properties file in constructor
	 */
	public AppDAlertsHandler() throws IOException {
		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			Properties properties = new Properties();
			properties.load(input);
			controllerPrefix = properties.getProperty("appd.prefix");
		} catch (Exception error) {
			logger.info("Exception Loading Properties File", error);
		}
	}
/**
 * This method handle request based on request type and sets next handler
 * @param request: AppDOnboardingRequest type which contains payload for create operation or update operation
 */
	public void handleRequest(AppDOnboardingRequest request) throws AppDOnboardingException {
		boolean result = false;
		if (Constants.REQUEST_TYPE_CREATE.equalsIgnoreCase(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Create Type");
			result = this.createAlerts(request);
			logger.info("handleRequest - Final Create Result {}", result);
		} else if (Constants.REQUEST_TYPE_UPDATE.equalsIgnoreCase(request.getRequestType())) {
			logger.info("handleRequest - Processing Request in Update Type");
			if (request.getRequestDetails().getAlertAliases() != null)
				result = this.updateAction(request);
			else {
				logger.info("handleRequest - Update Request Processing Successful Calling DB Handler");
				RetryDetails rDetails = new RetryDetails();
				rDetails.setOperationCounter(1);
				request.setRetryDetails(rDetails);
				this.setNextHandler(dbHandler);
				super.handleRequestImpl(request);
			}

		}

		if (result) {
			logger.info("handleRequest - Request Processing Successful Calling DB Handler");
			RetryDetails rDetails = new RetryDetails();
			rDetails.setOperationCounter(1);
			request.setRetryDetails(rDetails);
			this.setNextHandler(dbHandler);
			super.handleRequestImpl(request);
		}
	}
/**
 * This method deletes (appD default) existing health rules and creates new health rules, actions and policies
 * @param request: AppDOnboardingRequest type which contains payload for create operation
 * @return boolean: true if health rules, actions and policies are created successfully
 * @throws AppDOnboardingException: if creation of new health rules, actions and policies or deletion of existing health rules fails at any stag
 */
	public synchronized boolean createAlerts(AppDOnboardingRequest request) throws AppDOnboardingException {
		RetryDetails rDetails = request.getRetryDetails();
		rDetails.setFailureModule(Constants.ALERT_HANDLER);
		request.setRetryDetails(rDetails);
		try {
			if (request.getRetryDetails().getOperationCounter() == 1) {
				logger.info(" createAlerts - Started Deleting Existing Health Rules");
				if (this.getExistingHealthRuleId(request)) {
					logger.info(" createAlerts - Ended Deleting Existing Health Rules");
					request.getRetryDetails().setOperationCounter(2);
				}
			}

			if (request.getRetryDetails().getOperationCounter() == 2) {
				logger.info("createAlerts - Started Creating Health rule");
				if (this.createHealthRules(request)) {
					logger.info("createAlerts - Ended Creating Health rule");
					request.getRetryDetails().setOperationCounter(3);
				}
			}

			if (request.getRetryDetails().getOperationCounter() >= 3
					&& request.getRetryDetails().getOperationCounter() < 7) {
				logger.info(" createAlerts - Started Creating Actions");
				request = this.createActions(request);
				if (request.getRetryDetails().getOperationCounter() == 5
						|| request.getRetryDetails().getOperationCounter() == 6)
					request.getRetryDetails().setOperationCounter(7);
				logger.info(" createAlerts - Ended Creating Actions");
			}

			if (request.getRetryDetails().getOperationCounter() >= 7) {
				logger.info(" createAlerts - Started Creating Policies");
				this.createPolicies(request);
				logger.info(" createAlerts - Ended Creating Policies");
			}
			return true;
		} catch (AppDOnboardingException ce) {
			throw new AppDOnboardingException(ce.getActualMessage(), request, ce);
		}
	}

	/**
	 * This methods makes AppD API call to get health rule Id of existing default Health Rules in appD controller and deletes them from the controller
	 * @param request: AppDOnboardingRequest type which contains payload for create operation
	 * @return: true, if successful in deleting default appD health rules
	 * @throws AppDOnboardingException: if API call to controller fails or the health rules list in response could not be parsed
	 */
	public boolean getExistingHealthRuleId(AppDOnboardingRequest request) throws AppDOnboardingException {
		int applicationId = Integer.parseInt(request.getAppGroupID());
		String ctrlName = request.getRequestDetails().getCtrlName();
		String appGroupName = request.getRequestDetails().getAppGroupName();
		String rURL = ALERT_BASE_URL + applicationId + "/health-rules";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + Constants.URL_PARAM_OUTPUT_JSON;
		logger.info("getExistingHealthRuleId - Get Health Rules ID in AlertHandler {}", rURL);
		try {
			String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, "healthRule");
			JSONArray hrList = (JSONArray) parser.parse(response);
			logger.info("getExistingHealthRuleId - No of HR {} for application {}", hrList.size(), appGroupName);

			for (Object obj : hrList) {
				JSONObject dhealthrule = (JSONObject) obj;
				Long hrID = (Long) dhealthrule.get("id");
				String rURL1 = ALERT_BASE_URL + applicationId + "/health-rules/" + hrID;
				rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL1;
				response = appdUtil.appDConnectionDelete(rURL, Constants.HTTP_VERB_DELETE, ctrlName, "healthRule");
				if (response == null) {
					logger.info("getExistingHealthRuleId - Error occurred while removing existing health rule");
					throw new AppDOnboardingException(
							"AppDAlertsHandler - getExistingHealthRuleId - Exception in getExistingHealthRuleId",
							request);
				}
			}
		} catch (IOException ie) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createHealthRules - IOException in getExistingHealthRuleId", request, ie);
		} catch (ParseException pe) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createHealthRules - ParseException in getExistingHealthRuleId", request, pe);
		}
		return true;
	}
	
   /**
    * This method creates Health rules specified in HealthRule.json on the appD controller
    * @param request: AppDOnboardingRequest type which contains payload for create operation
    * @return: true, if successfully created health rules on controller
    * @throws AppDOnboardingException: if API call to controller fails or the healthRuleData list from HealthRule.json could not be parsed
    */
	public boolean createHealthRules(AppDOnboardingRequest request) throws AppDOnboardingException {
		int applicationId = Integer.parseInt(request.getAppGroupID());
		String ctrlName = request.getRequestDetails().getCtrlName();
		String rURL = ALERT_BASE_URL + applicationId + "/health-rules";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL;
		String json;
		try {
			logger.info("createHealthRules - start");
			File file = new File(getClass().getClassLoader().getResource("HealthRule.json").getFile());
			JSONObject resp = (JSONObject) parser.parse(new FileReader(file));
			JSONArray hrList = (JSONArray) resp.get("healthRuleData");
			for (Object obj : hrList) {
				JSONObject dhealthrule = (JSONObject) obj;
				json = dhealthrule.toString();
				logger.info("createHealthRules - HealthRules JSON is {}", json);
				String response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, "healthrule");
				logger.info("createHealthRules - Response in createHealthRules {}", response);
				if (response == null) {
					logger.info("createHealthRules - Error occurred while creating health rule");
					throw new AppDOnboardingException(
							"AppDAlertsHandler - createHealthRules - Exception in createHealthRules", request);
				}
			}
			logger.info("createHealthRules - created successfully - end");
		} catch (IOException ie) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createHealthRules - IOException in getExistingHealthRuleId", request, ie);
		} catch (ParseException pe) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createHealthRules - ParseException in getExistingHealthRuleId", request, pe);
		}
		return true;
	}
	/**
	 * This method calls another method to create actions on the appD controller
	 * @param request: AppDOnboardingRequest type which contains payload for create operation
	 * @return AppDOnboardingRequest: request with updated operationCounter
	 * @throws AppDOnboardingException: if createDynamicAction throws exception
	 */
	public AppDOnboardingRequest createActions(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			int applicationId = Integer.parseInt(request.getAppGroupID());
			int operationCounter = request.getRetryDetails().getOperationCounter();
			String alertAliases = request.getRequestDetails().getAlertAliases();

			if (operationCounter == 3) {
				operationCounter = 4;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}
			List<String> actionList = Arrays.asList(alertAliases.split(","));
			int index = 0;
			for (int i = index; i < actionList.size(); i++) {
				this.createDynamicAction(request, applicationId, actionList.get(i));
				operationCounter = operationCounter + 1;
				request.getRetryDetails().setOperationCounter(operationCounter);
				logger.info("createActions - Dynamic Action Block Complete");
			}
			return request;
		} catch (AppDOnboardingException e) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createActions - Exception in createAction" + e.getMessage(), request, e);
		}
	}
	/**
	 * This method creates actions of type email with given alert alias in request
	 * @param request: AppDOnboardingRequest type which contains payload for create operation
	 * @param appId: Application Id of the created Application on controller
	 * @param alertAliases: alert aliases given by user in payload
	 * @return: String API response after creation of email action on controller
	 * @throws AppDOnboardingException: if controller connection fails or response is null
	 */
	public String createDynamicAction(AppDOnboardingRequest request, int applicationId, String alertAliases)
			throws AppDOnboardingException {
		try {
			String ctrlName = request.getRequestDetails().getCtrlName();
			String rURL = ALERT_BASE_URL + applicationId + "/" + ACTIONS;
			rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL;
			String payloadJson = "{\r\n\t\"actionType\": \"EMAIL\",\r\n\t\"emails\": [\r\n\"" + alertAliases
					+ "\"\r\n]\r\n}";
			logger.info("createDynamicAction - Getting response in createDynamicAction");
			String response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, payloadJson, "createAction");
			if (response == null) {
				logger.info("createActions - Error occurred while creating Dynamic action");
				throw new AppDOnboardingException(
						"AppDAlertsHandler - createDynamicAction - Exception in createDynamicAction", request);
			}

			return response;
		} catch (IOException e) {
			throw new AppDOnboardingException(
					"AppDAlertsHandler - createDynamicAction - Exception in createDynamicAction", request, e);

		}
	}
   /**
    * This method is used to create Policies on appD controller
    * @param request: AppDOnboardingRequest type which contains payload for create operation
    * @return: true, if creation of policies on controller is successful
    * @throws AppDOnboardingException: if controller connection fails or response is null or parser throws exception
    */
	@SuppressWarnings("unchecked")
	public boolean createPolicies(AppDOnboardingRequest request) throws AppDOnboardingException {
		try {
			logger.info("createPolicies - Start createPolicies");
			int applicationId = Integer.parseInt(request.getAppGroupID());
			String ctrlName = request.getRequestDetails().getCtrlName();
			int operationCounter = request.getRetryDetails().getOperationCounter();
			String alertAliases = request.getRequestDetails().getAlertAliases();
			String rURL = ALERT_BASE_URL + applicationId + POLICIES;
			rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL;
			String json;
			File file = new File(getClass().getClassLoader().getResource("Policies.json").getFile());
			JSONObject resp1 = (JSONObject) parser.parse(new FileReader(file));
			JSONArray policyList = (JSONArray) resp1.get("policiesData");
			int ival = operationCounter - 7;
			for (int i = ival; i < policyList.size(); i++) {
				JSONObject policy = (JSONObject) policyList.get(i);
				for (String alert_alias : alertAliases.split(",")) {
					JSONArray actionList = (JSONArray) policy.get(ACTIONS);
					String newAction = "{ \"actionName\": \"" + alert_alias + "\", \"actionType\": \"EMAIL\"}";
					JSONObject newObj = (JSONObject) parser.parse(newAction);
					actionList.add(newObj);
					policy.put(ACTIONS, actionList);
				}
				json = policy.toString();
				String response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_POST, json, POLICY);
				
				if (response == null) {
					logger.info("createPolicies - Error occurred while creating Policies");
					throw new AppDOnboardingException(
							"AppDAlertsHandler - createPolicies - Exception in createPolicies", request);
				}
				operationCounter = operationCounter + 1;
				request.getRetryDetails().setOperationCounter(operationCounter);
			}
			logger.info("createPolicies - created successfully - end");
			return true;
		} catch (IOException e) {
			throw new AppDOnboardingException("AppDAlertsHandler - createPolicies - IOException", request, e);
		} catch (ParseException pe) {
			throw new AppDOnboardingException("AppDAlertsHandler - createPolicies - ParseException", request, pe);
		}
	}

	/**
	 * This method is used to delete old actions during update process
	 * @param request: AppDOnboardingRequest type which contains payload for update operation
	 * @param appId: application Id of the application which needs action update
	 * @param removeAlias: alert alias to be removed from actions
	 * @return : response of API call to AppD controller to delete Actions
	 * @throws AppDOnboardingException: if AppD connection to controller fails or response of API call is null
	 */
	public String deleteActionById(AppDOnboardingRequest request, int appId, String removeAlias)
			throws AppDOnboardingException {
		try {
			Long actionID = (long) 0;
			int applicationId = appId;
			String ctrlName = request.getRequestDetails().getCtrlName();
			String appGroupName = request.getRequestDetails().getAppGroupName();
			String rURL = ALERT_BASE_URL + applicationId + "/" + ACTIONS;
			rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + Constants.URL_PARAM_OUTPUT_JSON;
			logger.info(" Get Actions ID in AlertHandler {}", rURL);

			String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, ACTION);

			JSONArray actionList = (JSONArray) parser.parse(response);

			logger.info("deleteActionById - No of Actions {} for application {}", actionList.size(), appGroupName);
			for (Object obj : actionList) {
				JSONObject deleteAction = (JSONObject) obj;

				if (deleteAction.get("name").equals(removeAlias))
					actionID = (Long) deleteAction.get("id");

			}
			String rURL1 = ALERT_BASE_URL + applicationId + "/actions/" + actionID;
			rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL1;

			response = appdUtil.appDConnectionDelete(rURL, Constants.HTTP_VERB_DELETE, ctrlName, ACTION);
			if (response == null) {
				logger.info("deleteActionById - Error occurred while Deleting Action");

				throw new AppDOnboardingException(
						"AppDAlertsHandler - deleteActionById - Exception in deleteActionById", request);
			}
			return response;
		} catch(IOException e)
		{
			throw new AppDOnboardingException("AppDAlertsHandler - deleteActionById - IOException", request,e);
		}
		catch(ParseException pe)
		{
			throw new AppDOnboardingException("AppDAlertsHandler - deleteActionById - ParseException", request,pe);
		}
	}
   /**
    * This method makes call to update actions and policies
    * @param request: AppDOnboardingRequest type which contains payload for update operation
    * @return: true, if update of actions and policies is successful
    * @throws AppDOnboardingException: if function calls for creating and deleting actions or update policies throws exception
    */
	public synchronized boolean updateAction(AppDOnboardingRequest request) throws AppDOnboardingException {
			logger.info("updateAction - Started Updating Alert Alias");
			RetryDetails rDetails = request.getRetryDetails();
			rDetails.setFailureModule(Constants.ALERT_HANDLER);
			request.setRetryDetails(rDetails);

			if (Constants.REQUEST_TYPE_UPDATE.equals(request.getRequestType())) {
				request.getRetryDetails().setFailureModule(Constants.ALERT_HANDLER);
				request = requestHandler.getUpdatedRequest(request);
			}
			String appGroupName = request.getRequestDetails().getAppGroupName();
			String ctrlName = request.getRequestDetails().getCtrlName();
			String updatedalertAliases = request.getRequestDetails().getAlertAliases();

			if (updatedalertAliases == null)
				return true;

			int operationCounter = request.getRetryDetails().getOperationCounter();
			int ival;
			APPDMaster masterObj = aPPDMasterDAO.findByApp(appGroupName, ctrlName);
			int applicationId = masterObj.getAppGroupId();
			List<String> dbActionList = Arrays.asList(masterObj.getAlertAliases().split(","));
			List<String> newActionList = Arrays.asList(updatedalertAliases.split(","));
			List<String> removeList = this.updateRemoveList(dbActionList, newActionList);
			List<String> addList = this.updateAddList(dbActionList, newActionList);

			if (operationCounter == 1 || operationCounter < 4) {
				logger.info("updateAction - Started Creating Dynamics Actions");
				ival = operationCounter - 1;
				for (int i = ival; i < addList.size(); i++) {
					this.createDynamicAction(request, applicationId, addList.get(i));
					operationCounter = operationCounter + 1;
					request.getRetryDetails().setOperationCounter(operationCounter);
				}
				operationCounter = 4;
				request.getRetryDetails().setOperationCounter(operationCounter);
				logger.info("updateAction - Ended Creating Dynamics Actions");
			}
			logger.info("updateAction - Started Updating Policies");
			if (operationCounter == 4 && this.updatePolicies(request, applicationId, addList, removeList)) {
				operationCounter = operationCounter + 1;
				request.getRetryDetails().setOperationCounter(operationCounter);
				logger.info("updateAction - Ended Updating Policies");
			}
			if (operationCounter >= 5) {
				logger.info("updateAction - Started Deleting Actions");
				ival = operationCounter - 5;
				for (int i = ival; i < removeList.size(); i++) {
					this.deleteActionById(request, applicationId, removeList.get(i));

					operationCounter = operationCounter + 1;
					request.getRetryDetails().setOperationCounter(operationCounter);
				}
				logger.info("updateAction - Ended Deleting Actions");
			}
			logger.info("updateAction - Ended Updating Alert Alias");
			return true;
	}
	/**
	 * This method is used to update policies and actions
	 * @param request: AppDOnboardingRequest type which contains payload for update operation
	 * @param appId: application Id of the application which requires policy update
	 * @param addList: alert alias to be added in actions
	 * @param removeList: alert alias to be removed from actions
	 * @return : true, if update policies and actions is successful
	 * @throws AppDOnboardingException: if API call connection fails or parser fails to parse response
	 */
	@SuppressWarnings("unchecked")
	public boolean updatePolicies(AppDOnboardingRequest request, int applicationId, List<String> addList,
			List<String> removeList) throws AppDOnboardingException {
		try {
			String appGroupName = request.getRequestDetails().getAppGroupName();
			String ctrlName = request.getRequestDetails().getCtrlName();
			String rURL = ALERT_BASE_URL + applicationId + POLICIES;
			rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + Constants.URL_PARAM_OUTPUT_JSON;
			logger.info("updatePolicies -  Get Policies ID in AlertHandler {}", rURL);
			String json;
			String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, POLICY);
			JSONArray policyList = (JSONArray) parser.parse(response);
			logger.info("updatePolicies -  No of Policies {} for application {}", policyList.size(), appGroupName);
			for (Object obj : policyList) {
				JSONObject updatePolicy = (JSONObject) obj;
				Long policyID = (Long) updatePolicy.get("id");
				String rURL1 = ALERT_BASE_URL + applicationId + "/policies/" + policyID;
				rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL1;
				response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, POLICY);
				JSONObject policyData = (JSONObject) parser.parse(response);
				JSONArray actionList = (JSONArray) policyData.get(ACTIONS);
				for (String alert_alias : addList) {
					String newAction = "{ \"actionName\": \"" + alert_alias + "\", \"actionType\": \"EMAIL\"}";
					JSONObject newObj = (JSONObject) parser.parse(newAction);
					actionList.add(newObj);
				}
				for (String remove_alias : removeList) {

					JSONArray updatedActionList = this.removeActionFromPolicy(actionList, remove_alias);
					actionList = updatedActionList;
				}
				policyData.put(ACTIONS, actionList);
				JSONObject newEventsObj = (JSONObject) policyData.get("events");
				newEventsObj.put("anomalyEvents", null);
				policyData.put("events", newEventsObj);

				json = policyData.toString();
				response = appdUtil.appDConnection(rURL, Constants.HTTP_VERB_PUT, json, POLICY);
				logger.info("updatePolicies - Response in updatePolicies {}", response);
				if (response == null) {
					logger.info("updatePolicies - Error ocurred while updating policy");
					throw new AppDOnboardingException(
							"AppDAlertsHandler - updatePolicies -  Exception in updatePolicies", request);
				}
			}
			return true;
		}catch(IOException ie)
		{
			throw new AppDOnboardingException("AppDAlertsHandler - updatePolicies - IOException in updatePolicies",request,ie);
		}catch(ParseException pe)
		{
			throw new AppDOnboardingException("AppDAlertsHandler - updatePolicies - ParseException in updatePolicies",request,pe);
		}
	}
/**
 * This method removes action for given alert alias from Policy
 * @param actionList: list of actions to be deleted
 * @param removeAlias: alert alias to be removed
 * @return : list after removing required alert alias
 */
	public JSONArray removeActionFromPolicy(JSONArray actionList, String removeAlias) {
		for (Object obj : actionList) {
			JSONObject deleteAction = (JSONObject) obj;
			if ("EMAIL".equals(deleteAction.get("actionType")) && deleteAction.get("actionName").equals(removeAlias)) {
				actionList.remove(deleteAction);
				break;
			}
		}
		return actionList;
	}

/**
 * This method is used to compare alert alias list in db and alert alias list in update request and give the alert aliases to be removed
 * @param dbActionList: alert alias list in db 
 * @param newActionList: alert alias list in update request
 * @return : list of alert aliases to be removed
 */
	public List<String> updateRemoveList(List<String> dbActionList, List<String> newActionList) {
		List<String> removeList = new ArrayList<>();

		for (String action : dbActionList) {
			boolean result = newActionList.contains(action);
			if (!result)
				removeList.add(action);
		}
		return removeList;
	}
/**
 * This method is used to compare alert alias list in db and alert alias list in update request and give the alert aliases to be added
 * @param dbActionList: alert alias list in db 
 * @param newActionList: alert alias list in update request
 * @return : list of alert aliases to be added
 */
	public List<String> updateAddList(List<String> dbActionList, List<String> newActionList) {
		List<String> addList = new ArrayList<>();

		for (String action : newActionList) {
			boolean result = dbActionList.contains(action);
			if (!result)
				addList.add(action);
		}
		return addList;
	}
}
