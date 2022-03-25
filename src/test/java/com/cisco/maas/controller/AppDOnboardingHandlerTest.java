
package com.cisco.maas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDError;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.ApplicationOnboardingRequest;
import com.cisco.maas.dto.ApplicationOnboardingUpdateRequest;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.ValidateResult;
import com.cisco.maas.dto.ViewAppdynamicsResponse;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.services.AppDApplicationCreationHandler;
import com.cisco.maas.services.DBOperationHandler;
import com.cisco.maas.services.ProcessRequest;
import com.cisco.maas.services.RequestHandler;

public class AppDOnboardingHandlerTest {
	@Mock
	ProcessRequest pr;	
	
	@Mock
	RequestHandler requestHandler;
	
	@Mock 
	APPDMasterDAO aPPDMasterDAO;
	
	@InjectMocks
	@Spy
	AppDOnboardingHandler appDOnboardingHandler;
	
	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	
	@Mock
	DBOperationHandler operationHandler;

	@Mock
	RequestDAO requestDao;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void buildRequestTest()
   {
	  AppDOnboardingRequest request=new AppDOnboardingRequest();
	  RequestDetails requestDetails=new RequestDetails();
	  RetryDetails retryDetails=new RetryDetails();
	  retryDetails.setOperationCounter(1);
	  requestDetails.setTrackingId("Test-123"); 
	  request.setRequestType("create");		
	  request.setRequestStatus("pending");   	   	
	  request.setRequestDetails(requestDetails);
	  request.setRequestModifiedDate("11-11-2020*11:11");	
	  request.setRetryDetails(retryDetails);
  	  appDOnboardingHandler.buildRequest("create");
    }
    
	@Test
	public void createAppdynamicsTest() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
 		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails(); 
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	@Test
	public void createAppdynamicsTest1() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failed");
		AppDError errorObject = new AppDError();
		errorObject.setCode(1);
		errorObject.setMsg("error");
		validateResult.setErrorObject(errorObject);
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}

	
	@Test
	public void createAppdynamicsTest4() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		eumName.add("testEUM1");
		eumName.add("testEUM2");
		eumName.add("testEUM3");
		eumName.add("testEUM4");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest5() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest6() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn("Test-Apm").when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate( any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest7() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest8() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	
	@Test
	public void createAppdynamicsTest9() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class),any(ApplicationOnboardingRequest.class), any(String.class));
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest10() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failure");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class),any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));		
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest11() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failure");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("<script>");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class),any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));		
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest12() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failure");
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn("123456").when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class),any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));		
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void createAppdynamicsTest13() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failure");
		AppDError appdError = new AppDError();
		appdError.setCode(500);
		appdError.setMsg("failure");
		validateResult.setErrorObject(appdError);
		validateResult.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("rpatta@cisco.com");
		alertList.add("test@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setApmApplicationGroupName("Test-Apm");
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDApplicationCreationHandler).getAppID(any(String.class), any(String.class));
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class),any(ApplicationOnboardingRequest.class), any(String.class));
		when(requestHandler.validateRequest(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.createAppdynamics(rDetail);
	}
	
	@Test
	public void updateAppdynamicsTest() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateUpdate(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");
	}
	
	
	
	@Test
	public void updateAppdynamicsTest1() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class));
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateUpdate(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);		
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void updateAppdynamicsTest2() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class)); 
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateUpdate(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(false);
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}

	@Test
	public void updateAppdynamicsTest3() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn("Test").when(appDOnboardingHandler).validateEUMAndalertAliases(null,null); 
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void updateAppdynamicsTest4() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn("Error").when(appDOnboardingHandler).validateEUMAndalertAliases(rDetail.getEumApplicationGroupNames(),rDetail.getAlertAliases()); 
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void updateAppdynamicsTest5() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn("Request is not Valid : EUM App name already exists"
				).when(appDOnboardingHandler).validateEUMAndalertAliases(rDetail.getEumApplicationGroupNames(),rDetail.getAlertAliases()); 
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void updateAppdynamicsTest6() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("success");
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		alertList.add("<script>");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(rDetail.getEumApplicationGroupNames(),rDetail.getAlertAliases()); 
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void updateAppdynamicsTest7() throws Exception {
		ValidateResult validateResult=new ValidateResult();
		validateResult.setValidateResultStatus("failure");
		
		AppDError errorObject = new AppDError();
		errorObject.setCode(1);
		errorObject.setMsg("error");
		validateResult.setErrorObject(errorObject);
		validateResult.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setEumApplicationGroupNames(eumName);
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setTrackingId("Test-123"); 
		Mockito.doReturn(null).when(appDOnboardingHandler).validateEUMAndalertAliases(eumName, alertList);
		Mockito.doReturn(request).when(appDOnboardingHandler).buildRequest(any(String.class));
		Mockito.doReturn(requestDetails).when(appDOnboardingHandler).buildBodyForCreate(any(String.class), any(ApplicationOnboardingRequest.class), any(String.class)) ;
		when(requestHandler.validateUpdate(any(AppDOnboardingRequest.class))).thenReturn(validateResult);
		when(requestHandler.createRequest(any(AppDOnboardingRequest.class))).thenReturn(true);		
		Mockito.doNothing().when(pr).asyncProcessUpdateRequest(any(AppDOnboardingRequest.class));
		appDOnboardingHandler.updateAppdynamics(rDetail,"test123");	
	}
	
	@Test
	public void viewAppdynamicsTest3() throws Exception {
		
		when(requestHandler.getRequestByProjectId(any(String.class))).thenReturn(null);
		appDOnboardingHandler.viewAppdynamics("test");

	}
	
	@Test
	public void viewAppdynamicsTest4() throws Exception {
		ViewAppdynamicsResponse response = new ViewAppdynamicsResponse();
		when(requestHandler.getRequestByProjectId(any(String.class))).thenReturn(response);
		appDOnboardingHandler.viewAppdynamics("test");
	}
	
	@Test
	public void viewAppdynamicsTest5() throws Exception {
		ViewAppdynamicsResponse response = new ViewAppdynamicsResponse();
		when(requestHandler.getRequestByProjectId(any(String.class))).thenReturn(response);
		appDOnboardingHandler.viewAppdynamics("test");

	}
	@Test
	public void viewAppdynamicsTest6() throws Exception {
		ViewAppdynamicsResponse response = new ViewAppdynamicsResponse();
		when(requestHandler.getRequestByProjectId(any(String.class))).thenReturn(response);
		appDOnboardingHandler.viewAppdynamics("test");
	}
	
	@Test
	public void viewAppdynamicsTest7() throws Exception {
		appDOnboardingHandler.viewAppdynamics("<script>");
	}
	
	@Test
	public void convertArrayToStringTest()
	{
		List<String> dataList = new ArrayList<>();
		dataList.add("test1");
		dataList.add("test2");
		appDOnboardingHandler.convertArrayToString(dataList);
	}
	
	@Test
	public void convertArrayToStringTest2()
	{
		List<String> dataList = null;
		appDOnboardingHandler.convertArrayToString(dataList);
	}

	@Test
	public void buildBodyTest2()
	{
		ApplicationOnboardingRequest rDetails = new ApplicationOnboardingRequest();
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("test2");
		rDetails.setAdminUsers(testData);
		rDetails.setAlertAliases(testData);
		rDetails.setApmApplicationGroupName("test");
		rDetails.setEumApplicationGroupNames(testData);
		rDetails.setApmLicenses(1);
		rDetails.setApmLicenses(1);
		rDetails.setViewUsers(testData);
		appDOnboardingHandler.buildBodyForCreate("test123", rDetails, "test123");
	}
	@Test
	public void buildBodyTest3()
	{
		ApplicationOnboardingRequest rDetails = new ApplicationOnboardingRequest();
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("test2");
		rDetails.setAdminUsers(testData);
		rDetails.setAlertAliases(testData);
		rDetails.setApmApplicationGroupName("test");
		rDetails.setEumApplicationGroupNames(testData);
		rDetails.setApmLicenses(1);
		rDetails.setApmLicenses(1);
		rDetails.setViewUsers(testData);
		appDOnboardingHandler.buildBodyForCreate("test123", rDetails, "test123");
	}
	
	
	@Test
	public void validateEUMAndalertAliasesTest() throws AppDOnboardingException
	{   Mockito.when(operationHandler.checkIfAPMApplicationNotExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("test2");
		testData.add("test3");
		testData.add("test4");
		testData.add("test5");
		testData.add("test6");
		appDOnboardingHandler.validateEUMAndalertAliases(testData,testData);
	}
	
	@Test
	public void validateEUMAndalertAliasesTest2() throws AppDOnboardingException
	{   
		Mockito.when(operationHandler.checkIfAPMApplicationNotExist(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("test2");
		testData.add("test3");
		testData.add("test4");
		appDOnboardingHandler.validateEUMAndalertAliases(testData,testData);
	}
	@Test
	public void validateEUMAndalertAliasesTest3() throws AppDOnboardingException
	{   
		Mockito.when(operationHandler.checkIfAPMApplicationNotExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("Test1");
		testData.add("test3");
		testData.add("test4");
		appDOnboardingHandler.validateEUMAndalertAliases(testData,null);
	}
	@Test
	public void validateEUMAndalertAliasesTest4() throws AppDOnboardingException
	{   
		Mockito.when(operationHandler.checkIfAPMApplicationNotExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		List<String> testData = new ArrayList<>();
		testData.add("test1");
		testData.add("test1");
		testData.add("test3");
		testData.add("test4");
		testData.add("test5");
		testData.add("test6");
		appDOnboardingHandler.validateEUMAndalertAliases(testData,null);
	}
	@Test
	public void validateEUMAndalertAliasesTestNameExists() throws AppDOnboardingException
	{   
		Mockito.when(operationHandler.checkIfAPMApplicationNotExist(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		
		appDOnboardingHandler.validateEUMAndalertAliases(null,null);
	}
	
	@Test
	public <T> void createResponseTest() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		ApplicationOnboardingRequest rDetail =new ApplicationOnboardingRequest();
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		List<String> adminList = new ArrayList<>();
		List<String> viewList = new ArrayList<>();
		adminList.add("test");
		viewList.add("test");
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);
		rDetail.setApmApplicationGroupName("Test");
		rDetail.setAdminUsers(adminList);
		rDetail.setViewUsers(viewList);
		rDetail.setApmLicenses(1);
		rDetail.setEumApplicationGroupNames(viewList); 	
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {ApplicationOnboardingRequest.class, String.class};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("createResponse",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,rDetail,"8881eadf-b19d-42c9-89da-0ff8195f18aa");
		 
	}

	@Test
	public void buildBodyForUpdateTest() 
	{
		ApplicationOnboardingUpdateRequest rDetail =new ApplicationOnboardingUpdateRequest();
		AppDOnboardingRequest appdReq = new AppDOnboardingRequest();
		appdReq.setRequestType("create");
		List<String> eumName = new ArrayList<>();
		List<String> alertList = new ArrayList<>();
		List<String> adminList = new ArrayList<>();
		List<String> viewList = new ArrayList<>();
		adminList.add("test");
		viewList.add("test");
		eumName.add("testEUM");
		alertList.add("rgundewa@cisco.com");
		rDetail.setEumApplicationGroupNames(eumName);
		rDetail.setAlertAliases(alertList);		
		rDetail.setAdminUsers(adminList);
		rDetail.setViewUsers(viewList);
		rDetail.setApmLicenses(1);
		rDetail.setEumApplicationGroupNames(viewList); 
		appDOnboardingHandler.buildBodyForUpdate("8881eadf-b19d-42c9-89da-0ff8195f18aa",rDetail,"8881eadf-b19d-42c9-89da-0ff8195f18aa");
		 
	} 
	
	@Test
	public <T> void failedPersistanceTest() throws IOException, IllegalAccessException, NullPointerException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException 
	{
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("failedPersistance",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole);
		 
	} 
	
	@Test
	public <T> void failedValidationTest() throws IOException, IllegalAccessException, NullPointerException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException 
	{
		ValidateResult vresult = new ValidateResult();
		AppDError appdError = new AppDError();
		appdError.setCode(200);
		appdError.setMsg("Message");
		vresult.setErrorObject(appdError);
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {ValidateResult.class};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("failedValidation",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,vresult);
		 
	} 
	@Test
	public <T> void eumNameCheckTest() throws IOException, IllegalAccessException, NullPointerException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException 
	{
		List<String> eumName = new ArrayList<>();
		eumName.add("test");
		eumName.add("test");
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {List.class};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("eumNameCheck",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,eumName);
		 
	} 
	@Test
	public <T> void eumNameCheckTest1() throws IOException, IllegalAccessException, NullPointerException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException 
	{
		List<String> eumName = new ArrayList<>();		
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {List.class};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("eumNameCheck",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,eumName);
		 
	} 
	
	@Test
	public <T> void eumNameCheckTest2() throws IOException, IllegalAccessException, NullPointerException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException 
	{
		List<String> eumName = new ArrayList<>();
		eumName.add("test");
		eumName.add("test1");
		eumName.add("test2");
		AppDOnboardingHandler appdrole = new AppDOnboardingHandler();
		Class<?>[] paramsTypes = new Class<?>[] {List.class};
		Method calculateMethod = AppDOnboardingHandler.class.getDeclaredMethod("eumNameCheck",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,eumName);
		 
	} 
}
