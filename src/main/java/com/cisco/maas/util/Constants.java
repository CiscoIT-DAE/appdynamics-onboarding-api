package com.cisco.maas.util;
/**
 * This class contains all constants which are being used through out the application. 
  */
public class Constants {
	public static final String REQUEST_TYPE_CREATE = "create";
	public static final String REQUEST_TYPE_UPDATE = "update";
	public static final String REQUEST_TYPE_DELETE = "delete";
	public static final String REQUEST_TYPE_VIEW = "view";
	public static final String REQUEST_TYPE_TRACKING_ID = "TrackingID";
	public static final String REQUEST_STATUS_FAILED = "failed";
	public static final String REQUEST_STATUS_PENDING = "pending";
	public static final String REQUEST_STATUS_ERROR = "ERROR";
	public static final String NO_EXCEPTION_STR = "No Exception Trace Available";
	public static final String ROLLBACK_ERROR = "rollbackError";
	public static final String VALIDATION_RESULT_SUCCESS = "success";
	public static final String ROLLBACK_FAILED="rollbackFailed";
	public static final String ROLLBACK_SUCCESS="rollbackSuccess";
	public static final String RETRY_LOCK="retryLock";
	
	// Handler names for recording the failure mode
	public static final String AD_HANDLER = "AD_HANDLER";
	public static final String APP_CREATION_HANDLER = "APP_CREATION_HANDLER";
	public static final String RM_HANDLER = "RM_HANDLER";
	public static final String LIC_HANDLER = "LIC_HANDLER";
	public static final String ALERT_HANDLER = "ALERT_HANDLER";
	public static final String DB_HANDLER = "DB_HANDLER";

	public static final String OPERATIONAL_STATUS_INACTIVE = "INACTIVE";
	public static final String OPERATIONAL_STATUS_ACTIVE ="ACTIVE";
	public static final String ERROR_MESSAGE_INTERNAL = "Error while processing request";
	public static final String ERROR_MESSAGE_FAILED_PERSISTANCE = "Request persistance failed";
    
	//DB Fields
	public static final String APP_GROUP_NAME = "appGroupName";
	public static final String CONTROLLER_NAME = "ctrlName";
	public static final String APPD_PROJECT_ID = "appdProjectId";
	public static final String EUM_NAME = "eumName";
	public static final String REQUEST_ID="requestDetails.trackingId";
	public static final String REQUEST_DETAILS_APP_GROUP_NAME= "requestDetails.appGroupName";
	public static final String REQUEST_DETAILS_CONTROLLER_NAME = "requestDetails.ctrlName";
	public static final String API_RESULT_ERROR="error";
	public static final String CONTENT_TYPE_APPLICATION_JSON="application/json";
	public static final String REQUEST_STATUS="requestStatus";
	public static final String OPERATIONAL_STATUS="operationalStatus";
	public static final String APPD_EXTERNAL_ID="appdExternalId";
	public static final String REQUEST_DETAILS_FAILURE_MODULE= "retryDetails.failureModule";
	public static final String REQUEST_TYPE="requestType";
	//HTTP Verbs
	public static final String HTTP_VERB_DELETE="DELETE";
	public static final String URL_PARAM_OUTPUT_JSON = "?output=JSON";
	public static final String HTTP_VERB_GET="GET";
	public static final String HTTP_VERB_POST="POST";
	public static final String HTTP_VERB_PUT="PUT";
	public static final String PROTO="https://";
	public static final String SENDER_TYPE = "S1";
	//Users
	public static final String ADMIN = "admin";
	public static final String VIEW = "view";
	
	private Constants() {
		//does nothing
	}
}