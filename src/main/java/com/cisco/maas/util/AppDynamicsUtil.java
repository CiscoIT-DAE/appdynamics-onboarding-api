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

package com.cisco.maas.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.cisco.maas.exception.AppDOnboardingException;
/**
 * This class contains the utility methods which helps to login to AppD, create application on appD, delete application from AppD, set BT settings on appD. 
  */

@Service
public class AppDynamicsUtil {

	String userName;
	String userPassword;
	String controllerPrefix;
	private String controller;
	private String createAPMURL;
	private String createEUMURL;
	private String enableBTLockdownURL;
	private HttpURLConnection con;
	private static final Logger logger = LoggerFactory.getLogger(AppDynamicsUtil.class);
	private static final String PROTO= Constants.PROTO;
	private static final String AUTHORIZATION = "Authorization";
	private static final String BASIC = "Basic ";
	private static final String ROLE = "role";
	private static final String USER = "user";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String ACCEPT = "Accept";
	private static final String JSESSIONID = "jSessionId";
	private static final String J_SESSION_ID = "JSESSIONID";
	private static final String XCSRFTOKEN = "xCsrfToken";
	private static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";
	private static final String COOKIE = "Cookie";
	
	@Autowired 
	private RestTemplate restTemplate;
	
	/**
	 * Initializing constants from config.properties file in constructor
	 */
	public AppDynamicsUtil() throws IOException {
		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			logger.info("AppDynamicsUtil - START");
			Properties properties = new Properties();
			properties.load(input);
			controllerPrefix = properties.getProperty("appd.prefix");
			controller = properties.getProperty("appd.controller");
			createAPMURL = properties.getProperty("appd.createApplicationAPM.url");
			createEUMURL = properties.getProperty("appd.createApplicationEUM.url");
			enableBTLockdownURL = properties.getProperty("appd.enableBTLockdown.url");
			// Getting decoder  
	        Base64.Decoder decoder = Base64.getDecoder();  
	        // Decoding string  
	        userName = new String(decoder.decode(System.getenv("appd_user")));
	        userPassword = new String(decoder.decode(System.getenv("appd_pass")));
		} catch (Exception e) {
			logger.error("AppDynamicsUtil - ERROR");
			logger.error("AppDynamicsUtil - Exception Loading Properties File", e);
		}
	}
	/**
	 * This method creates HttpConnection with the given Url object
	 * @param URL type which contains url
	 * @throws IOException
	 */
	protected HttpURLConnection createHttpURLConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}
	/**
	 * This method creates Url object with the given String
	 * @param String type which contains url.
	 * @throws IOException
	 * @returns HttpURLConnection
	 */
	protected URL createURL(String str) throws IOException {
		return new URL(str);
	}
	
	/**
	 * This method creates connection to appD controller.
	 * @param String type which contains url, String type method type, String type json and String type context.
	 * @throws IOException
	 *@returns String
	 */
	public synchronized String appDConnection(String rURL, String methodType, String json, String context) throws IOException {
		logger.info("appDConnection - START");
		logger.info("appDConnection - Connecting to AppD :: {}", rURL);
		String basicAuth = null;
		rURL = rURL.replace(" ", "%20");
		con = this.createHttpURLConnection(this.createURL(rURL));
		con.setReadTimeout(3000);
		con.setRequestMethod(methodType);
		
		basicAuth = Base64.getEncoder().encodeToString((userName + ":" + userPassword).getBytes(StandardCharsets.UTF_8));
		con.setRequestProperty(AUTHORIZATION, BASIC + basicAuth);
		con.setDoOutput(true);
		con.setUseCaches(false);

		if (Constants.HTTP_VERB_POST.equals(methodType) || Constants.HTTP_VERB_PUT.equals(methodType)) {
			if (ROLE.equals(context)) {
				con.setRequestProperty(CONTENT_TYPE, "application/vnd.appd.cntrl+json;v=1");
			} else {
				con.setRequestProperty(CONTENT_TYPE, Constants.CONTENT_TYPE_APPLICATION_JSON);
				con.setRequestProperty(ACCEPT, Constants.CONTENT_TYPE_APPLICATION_JSON);
			}

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = json.getBytes();
				os.write(input, 0, input.length);
			}
		}

		logger.info("appDConnection: Http Response Code for the Current POST Request is :: {}", con.getResponseCode());
		logger.info("appDConnection - END");
		return this.retrieveResponse(con,methodType);  
	}
	
	private void initializeConnection(String rURL, String methodType) throws IOException {
		String basicAuth = null;
		rURL = rURL.replace(" ", "%20");
		con = this.createHttpURLConnection(this.createURL(rURL));
		con.setReadTimeout(3000);
		con.setRequestMethod(methodType);
		con.setRequestProperty(CONTENT_TYPE, Constants.CONTENT_TYPE_APPLICATION_JSON);
		basicAuth = Base64.getEncoder().encodeToString((userName + ":" + userPassword).getBytes(StandardCharsets.UTF_8));
		con.setRequestProperty(AUTHORIZATION, BASIC + basicAuth);
		con.setDoOutput(true);
		con.setUseCaches(false);
	}
	/**
	 * This method creates connection to appD controller
	 * @param String type which contains url, String type method type, String type json and String type context.
	 * @throws IOException
	 * @returns String
	 */
	public synchronized String appDConnectionOnlyGet(String rURL, String methodType, String ctrlName, String context)
			throws IOException {
		logger.info("appDConnectionOnlyGet - START");
		logger.info("appDConnectionOnlyGet - Connecting to AppD {}", rURL);
		logger.info("appDConnectionOnlyGet - Controller Name {}" , ctrlName);
		this.initializeConnection(rURL, methodType);
		if (!ROLE.equalsIgnoreCase(context) && !USER.equalsIgnoreCase(context)
				&& !"licenseQuota".equalsIgnoreCase(context))
			con.setRequestProperty(ACCEPT, Constants.CONTENT_TYPE_APPLICATION_JSON);

		logger.info("appDConnectionOnlyGet - Http Response Code for the Current GET Request is :: {}",
				con.getResponseCode());
		logger.info("appDConnectionOnlyGet - END");		
		return this.retrieveResponse(con,methodType);

	}
	
	/**
	 * This method deletes the connection to appD controller
	 * @param String type which contains url, String type method type, String type json and String type context.
	 * @throws IOException
	 * @retruns String
	 */
	public synchronized String appDConnectionDelete(String rURL, String methodType, String ctrlName, String context)
			throws IOException {
		logger.info("appDConnectionDelete - START");
		logger.info("appDConnectionDelete - Connecting to AppD {}", rURL);
		logger.info("Controller Name {}" , ctrlName);
		this.initializeConnection(rURL, methodType);
		if (!ROLE.equalsIgnoreCase(context) && USER.equalsIgnoreCase(context))
			con.setRequestProperty(ACCEPT, Constants.CONTENT_TYPE_APPLICATION_JSON);

		logger.info("AppDynamicsUtil - appDConnectionDelete : Http Response Code for the Current DELETE Request is :: {}",
				con.getResponseCode());
		
		logger.info("appDConnectionDelete - END");
		return this.retrieveResponse(con,methodType); 

	}
	
	/**
	 * This method retrieves response from connection 
	 * @param HttpConnection type con and String type context.
	 * @throws IOException
	 * @retruns String
	 */
	public String retrieveResponse(HttpURLConnection con,String context) throws IOException {
		logger.info("retrieveResponse - START");
		logger.info("retrieveResponse - inside retrieveResponse");

		if (con.getResponseCode() >= 200 && con.getResponseCode() < 400) {
			InputStream inputstream = con.getInputStream();
			String response = this.getResponseContent(inputstream);
			logger.info("retrieveResponse - Ended retrieveResponse success response and code is {} for {} API ", con.getResponseCode(), context);
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				logger.info("retrieveResponse -  HTTP_OK - END");
				return response;
			} else if ((Constants.HTTP_VERB_POST.equals(context) || Constants.HTTP_VERB_PUT.equals(context))
					&& con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
				logger.info("retrieveResponse - HTTP_VERB_POST | HTTP_VERB_PUT |HTTP_CREATED - END");
				return response;
			} else if (Constants.HTTP_VERB_DELETE.equals(context) && con.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
				logger.info("retrieveResponse - HTTP_VERB_DELETE - END");
				return response;
			} else
			{
				logger.info("retrieveResponse - else - END");
				return null;
			}
		}

		else {
			InputStream errorstream = con.getErrorStream();

			String response = this.getResponseContent(errorstream);
			logger.info("retrieveResponse - Ended retrieveResponse error response {} and code is {} for {} API", response,
					con.getResponseCode(), context);

		}
		logger.info("retrieveResponse - status code not > = 200 and not < 400 -  END");
		return null;
	}
	
	/**
	 * This method creates an application in appDynamics
	 * @param String type applicationGroupName, String type applicationGroupName.
	 * @throws AppDOnboardingException
	 * @Returns String
	 */
	public String createApplicationInAppDynamics(String applicationGroupName, String applicationType) throws AppDOnboardingException {
		logger.info("createApplicationInAppDynamics - START");
		logger.info("createApplicationInAppDynamics - Creation of application {} started",applicationGroupName);
		
		String rURL;
		if("APM".equals(applicationType)) {
			rURL = createAPMURL;
		}
		else {
			rURL = createEUMURL;
		}
		
		try {
			String payload = "{\"name\": \""+ applicationGroupName +"\",\"description\": \"\"}";
			ResponseEntity<JSONObject> response = this.postRequest(rURL,payload);
			logger.info("createApplicationInAppDynamics - END");
			if(response.hasBody()) {
				JSONObject responseBody = response.getBody();
				if(responseBody!=null)
					return responseBody.get("id").toString();
				return null;
			} 
			else
				return null;
		}
		catch(IOException error) {			
			logger.error("createApplicationInAppDynamics - ERROR");
			throw new AppDOnboardingException("Unexpected error while creating application",error);
		}
		
	}

	/**
	 * This method sets BT settings in AppDynamics
	 * @param String type applicationId.	 
	 * @returns Boolean
	 */
	public Boolean setBTSettigsInAppDynamics(String applicationId)  {
		logger.info("setBTSettigsInAppDynamics - START");
		logger.info("setBTSettigsInAppDynamics - BT lockdown enablement started");
		try{
			String payload = "{\"isBtLockDownEnabled\":true,\"isBtAutoCleanupEnabled\": true, \"btAutoCleanupTimeFrame\": 15, \"btAutoCleanupCallCountThreshold\": 1}";
			ResponseEntity<JSONObject> response = this.postRequest(enableBTLockdownURL + applicationId, payload);
			if(response.getStatusCodeValue()==204) {
				logger.info("setBTSettigsInAppDynamics - END");
				return true;
			}
			logger.info("setBTSettigsInAppDynamics - END");
			logger.info("setBTSettigsInAppDynamics - BT lockdown enablement failed - {} ",response);
			return false;
		}
		catch(IOException error){
			logger.error("setBTSettigsInAppDynamics - ERROR");
			logger.info("setBTSettigsInAppDynamics - BT lockdown enablement failed - Exception - {}", error.getMessage());
			return false;
		}
	}
	
	private HttpHeaders initializeHeaders(){
		HttpHeaders headers = new HttpHeaders();
		Map<String,String> secrets = login(controller);
		headers.add(COOKIE, secrets.get(JSESSIONID));
		headers.add(COOKIE, secrets.get(XCSRFTOKEN));
		headers.add(J_SESSION_ID,secrets.get(JSESSIONID).split("=")[1]);
		headers.add(X_CSRF_TOKEN,secrets.get(XCSRFTOKEN).split("=")[1]);
		headers.add(ACCEPT, "application/json");
		headers.add(CONTENT_TYPE, "application/json;charset=UTF-8");
		return headers;
	}
	/**
	 * This method creates an application in appDynamics
	 * @param rURL : String type which contains url.
	 * @param json : String type json
	 * @throws IOException
	 * @returns ResponseEntity
	 */
	public ResponseEntity<JSONObject> postRequest(String rURL,String json) throws IOException
	{
		logger.info("postRequest - START");
		logger.info("postRequest - Posting Request");
		String url = PROTO + controller + controllerPrefix + rURL;
		HttpHeaders headers = this.initializeHeaders();
		HttpEntity<String> request = new HttpEntity<>(json,headers);
		try {
			ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.POST, request, JSONObject.class);
			logger.info("postRequest - Printing Status code :: {}",response.getStatusCodeValue());
			logger.info("postRequest - END");
			return response;				
		} 
		catch(RestClientException error) {
			logger.error("postRequest - ERROR");			
			throw new IOException("Unexpected Error while posting the request",error);
		} catch (Exception error) {
			throw new IOException("Unexpected Error while posting the request",error);
		}
	}
	
	/**
	 * This method performs GET method on appDynamics internal APIs
	 * @param rURL - contains relative url
	 * @throws IOException
	 */
	public String getRequest(String rURL) throws IOException
	{
		logger.info("getRequest - START");
		logger.info("getRequest - Getting Request");
		String url = PROTO + controller + controllerPrefix + rURL;
		HttpHeaders headers = this.initializeHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			logger.info("getRequest - Printing Status code :: {}",response.getStatusCodeValue());
			logger.info("getRequest - END");
			return response.getBody();				
		} 
		catch(RestClientException error) {
			logger.error("getRequest - ERROR");
			throw new IOException("RestClientException: Unexpected Error while performing get request",error);
		} catch (Exception error) {
			throw new IOException("Exception: Unexpected Error while performing get request",error);
		}
	}
	
	/**
	 * This method login to appD controller
	 * @param String type controller.	
	 * @returns Map
	 */
	public Map<String,String> login(String controller) {	
		logger.info("login - logging into AppDynamics - START");
		String jSessionId ;
		String xCsrfToken ; 
		Map<String,String> secrets = new HashMap<>(); 
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(userName,userPassword);
		HttpEntity<String> request = new HttpEntity<>(headers);
		String url = PROTO + controller + controllerPrefix +"auth?action=login";
		try 
	    {  
		    ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, request, String.class);   
		    if(resp.getStatusCodeValue()==200)
		    {
		    	logger.info("login - Login to AppDynamics Controller Successful");
			    headers=resp.getHeaders();
			    List<String> headersList=headers.get("Set-Cookie");
				for(String headerValue:headersList)
				{
					if(headerValue.contains("JSESSIONID="))
					{				
						jSessionId = headerValue.split(";")[0];	
						secrets.put(JSESSIONID,jSessionId);
						logger.info("login - Jession ID {}",jSessionId);
					}
					if(headerValue.contains("X-CSRF-TOKEN="))
					{				
						xCsrfToken = headerValue.split(";")[0];	
						secrets.put(XCSRFTOKEN, xCsrfToken);
						logger.info("login - CSRF Token {}",xCsrfToken);
					}		
				}
				logger.info("login - END");
			  return secrets;
		    }else {
		    	logger.info("login - END");
		    	logger.info("login - Login to AppDynamics Controller Failed");
			    return Collections.emptyMap();
		    }
	    }catch(RestClientException e){
	    	logger.error("login - ERROR");
	    	logger.info(e.getMessage(),e);	    	
	    	return Collections.emptyMap();
	    }
	}
	
	/**
	 * This method returns Response content
	 * @param InputStream type inputstream.
	 * @throws IOException
	 * @returns String
	 */
	public String getResponseContent(InputStream inputstream) throws IOException
	{
		logger.info("getResponseContent - START");
		StringBuilder responseData = new StringBuilder();
		
		if(inputstream!=null)
		{
		BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
		String inputLine;


		while ((inputLine = in.readLine()) != null) 
		{
			responseData.append(inputLine);
		}
		in.close();
		}
		logger.info("getResponseContent - END");
		return responseData.toString();
		
	}
}