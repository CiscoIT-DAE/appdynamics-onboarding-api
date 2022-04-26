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

package com.cisco.maas.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.ApplicationOnboardingError;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.ApplicationOnboardingRequest;
import com.cisco.maas.dto.ApplicationOnboardingResponse;
import com.cisco.maas.dto.ApplicationOnboardingUpdateRequest;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.ValidateResult;
import com.cisco.maas.dto.ViewAppdynamicsResponse;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.services.AppDApplicationCreationHandler;
import com.cisco.maas.services.DBOperationHandler;
import com.cisco.maas.services.ProcessRequest;
import com.cisco.maas.services.RequestHandler;
import com.cisco.maas.util.Constants;
import com.cisco.maas.util.XSSValidationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Controller class listen's to exposed Apis
 */
@RestController
@RequestMapping("/api/v1/applications")
public class AppDOnboardingHandler {
	private static final Logger logger = LoggerFactory.getLogger(AppDOnboardingHandler.class);
	private String controller;
	private int maxNumOfEum;
	private int maxNumOfAlerts;
	private static final String X_CONTENT_TYPE_OPTIONS = "x-content-type-options";
	private static final String STRICT_TRANSPORT_SECURITY = "strict-transport-security";
	private static final String X_XSS_PROTECTION = "x-xss-protection";
	private static final String X_FRAME_OPTIONS = "x-frame-options";
	private static final String CONTENT_SECURITY_POLICY = "content-security-policy";
	private static final String SET_COOKIE = "set-cookie";
	private static final String EUM_EXISTS_MSG = "Request is not Valid : EUM App name already exists";
	@Autowired
	ProcessRequest processRequest;
	@Autowired
	RequestHandler requestHandler;
	@Autowired
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	@Autowired
	RequestDAO requestDao;
	@Autowired
	DBOperationHandler operationHandler;

	/**
	 * Initializes variables from config.properties file
	 * @throws IOException
	 */
	public AppDOnboardingHandler() throws IOException {
		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			Properties properties = new Properties();
			properties.load(input);
			controller = properties.getProperty("appd.controller");
			maxNumOfEum = Integer.valueOf(properties.getProperty("eum.maxLimit"));
			maxNumOfAlerts = Integer.valueOf(properties.getProperty("alert.maxLimit"));
		} catch (Exception error) {
			logger.info("AppDOnboardingHandler() - Exception Loading Properties File", error);
		}
	}

	/**
	 * This method exposes Api to create application on controller
	 * @param onboardingRequest - Payload for application creation
	 * @return - HttpResponse
	 */
	@PostMapping(produces = "application/json")
	@Operation(summary = "Onboard Applications into AppDynamics Controller", description = "Create Application in AppDynamics Controller")
	@Tags(value = {
			@Tag(name = "Service Assurance AppDynamics Onboarding API", description = "Service Assurance AppDynamics Onboarding API") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Accepted", content = @Content(schema = @Schema(implementation = ApplicationOnboardingResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Location", description = "Location of the created resource", schema = @Schema(type = "string")),
					@Header(name = "Date", description = "Request processed date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", description = "Request processed date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", description = "Request processed date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", description = "Request processed date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "default", description = "Unexpected error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }) })
	public ResponseEntity<Object> createAppdynamics(@RequestBody ApplicationOnboardingRequest onboardingRequest) {
		ValidateResult validateResult;
		String validationErrorMessage;
		boolean createResult = false;

		String trackingId = this.createTrackingId();
		final HttpHeaders httpHeaders = this.createHeaders(trackingId);
		MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, trackingId);

		try {
			validationErrorMessage = this.validateEUMAndalertAliases(onboardingRequest.getEumApplicationGroupNames(),
					onboardingRequest.getAlertAliases());

			if (validationErrorMessage != null || !XSSValidationUtil.xSSAttackValidation(
					onboardingRequest.toString() + onboardingRequest.getAlertAliases().toString())) {
				ApplicationOnboardingError appError = new ApplicationOnboardingError();
				appError.setCode(HttpStatus.BAD_REQUEST.name());
				if (validationErrorMessage != null) {
					appError.setMessage(validationErrorMessage);
					if ((EUM_EXISTS_MSG).equals(validationErrorMessage)) {
						appError.setCode(HttpStatus.CONFLICT.name());
						return new ResponseEntity<>(appError, httpHeaders, HttpStatus.CONFLICT);
					}

				} else {
					logger.info("createAppdynamics - Request type validation failed");
					appError.setMessage("Bad request type for create");
				}
				return new ResponseEntity<>(appError, httpHeaders, HttpStatus.BAD_REQUEST);
			}

			if (appDApplicationCreationHandler.getAppID(controller,
					onboardingRequest.getApmApplicationGroupName()) != null) {
				ApplicationOnboardingError appError = new ApplicationOnboardingError();
				appError.setCode(HttpStatus.CONFLICT.name());
				appError.setMessage("Application Already Exists on Controller");
				return new ResponseEntity<>(appError, httpHeaders, HttpStatus.CONFLICT);
			}

			AppDOnboardingRequest request = this.buildRequest(Constants.REQUEST_TYPE_CREATE);
			logger.info("createAppdynamics - Started Processing Create Request");
			RequestDetails rDetails = this.buildBodyForCreate(trackingId, onboardingRequest,
					request.getAppdExternalId());
			request.setRequestDetails(rDetails);
			validateResult = requestHandler.validateRequest(request);
			logger.info("createAppdynamics - Printing Validated Result :: {}", validateResult);
			if (Constants.VALIDATION_RESULT_SUCCESS.equals(validateResult.getValidateResultStatus())) {
				logger.info("createAppdynamics :: Persisting Request to Database Started");
				createResult = requestHandler.createRequest(request);

				if (createResult) {
					logger.info("createAppdynamics - Persisting Request to Database Completed");
					logger.info("createAppdynamics - Process Request started");
					processRequest.asyncProcessRequest(request);

					ApplicationOnboardingResponse applicationOnboardingResponse = createResponse(onboardingRequest,
							request.getAppdExternalId());

					return new ResponseEntity<>(applicationOnboardingResponse, httpHeaders,
							validateResult.getResponseCode());
				} else {
					logger.info("createAppdynamics - Persisting Request to Database Failed");
					ApplicationOnboardingError appError = this.failedPersistance();
					return new ResponseEntity<>(appError, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
				}

			} else {
				logger.info("createAppdynamics - Validation of Create Request Failed");
				ApplicationOnboardingError appError = this.failedValidation(validateResult);
				return new ResponseEntity<>(appError, httpHeaders, validateResult.getResponseCode());
			}
		} catch (Exception error) {
			logger.error(error.getMessage(), error);
			logger.info("createAppdynamics - Request Failed Returning 500");
			ApplicationOnboardingError appError = new ApplicationOnboardingError();
			appError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
			appError.setMessage(Constants.ERROR_MESSAGE_INTERNAL);
			return new ResponseEntity<>(appError, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method exposes Api to update application on controller
	 * @param onboardingRequest - Payload for application update
	 * @return - HttpResponse
	 */
	@Operation(summary = "Update the Onboarded Applications in AppDynamics Controller", description = "Update Application in AppDynamics Controller")
	@Tags(value = {
			@Tag(name = "Service Assurance AppDynamics Onboarding API", description = "Service Assurance AppDynamics Onboarding API") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApplicationOnboardingResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Location", description = "Location of the created resource", schema = @Schema(type = "string")),
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "default", description = "Unexpected error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }) })
	@Parameter(name = "id", description = "Application Id")
	@PatchMapping(path = "/{id}", produces = "application/json")
	public @ResponseBody ResponseEntity<Object> updateAppdynamics(
			@RequestBody ApplicationOnboardingUpdateRequest onboardingRequest,
			@PathVariable("id") String appdProjectId) {

		ValidateResult validateResult;
		String validationErrorMessage;
		boolean updateResult = false;

		appdProjectId = HtmlUtils.htmlEscape(appdProjectId);
		appdProjectId = Encode.forHtml(appdProjectId);

		logger.info("updateAppdynamics - Started Processing Update Request");
		logger.info("updateAppdynamics - Printing ApplicationOnboardingRequest {}", onboardingRequest);

		String trackingId = this.createTrackingId();
		final HttpHeaders httpHeaders = this.createHeaders(trackingId);
		MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, trackingId);

		try {
			validationErrorMessage = this.validateEUMAndalertAliases(onboardingRequest.getEumApplicationGroupNames(),
					onboardingRequest.getAlertAliases());

			if (validationErrorMessage != null || !XSSValidationUtil.xSSAttackValidation(appdProjectId)
					|| (onboardingRequest.getAlertAliases() != null && !XSSValidationUtil.xSSAttackValidation(
							onboardingRequest.toString() + onboardingRequest.getAlertAliases().toString()))) {
				ApplicationOnboardingError appError = new ApplicationOnboardingError();
				appError.setCode(HttpStatus.BAD_REQUEST.name());
				if (validationErrorMessage != null) {
					appError.setMessage(validationErrorMessage);
					if ((EUM_EXISTS_MSG).equals(validationErrorMessage)) {
						logger.info("updateAppdynamics - Request type validation failed");
						appError.setCode(HttpStatus.CONFLICT.name());
						return new ResponseEntity<>(appError, httpHeaders, HttpStatus.CONFLICT);
					}
				} else {
					appError.setMessage("Bad request type for update");
					logger.info("updateAppdynamics - Request type validation failed");
				}
				return new ResponseEntity<>(appError, httpHeaders, HttpStatus.BAD_REQUEST);
			}

			AppDOnboardingRequest request = this.buildRequest(Constants.REQUEST_TYPE_UPDATE);
			RequestDetails rDetails = this.buildBodyForUpdate(trackingId, onboardingRequest, appdProjectId);
			request.setRequestDetails(rDetails);
			request.setAppdExternalId(appdProjectId);

			logger.info("updateAppdynamics - Validating Update Request");
			validateResult = requestHandler.validateUpdate(request);
			logger.info("updateAppdynamics - Printing Validated Result {}", validateResult);
			if (Constants.VALIDATION_RESULT_SUCCESS.equals(validateResult.getValidateResultStatus())) {
				logger.info("updateAppdynamics - Resource Move Result {}", validateResult.isResourceMoveFlag());
				request.setResourceMove(validateResult.isResourceMoveFlag());

				logger.info(" updateAppdynamics - Persisting Request to Database");
				updateResult = requestHandler.createRequest(request);

				if (updateResult) {
					logger.info("updateAppdynamics - Process Request started");
					processRequest.asyncProcessUpdateRequest(request);

					ApplicationOnboardingResponse applicationOnboardingResponse = createUpdateResponse(
							onboardingRequest, request.getAppdExternalId());

					return new ResponseEntity<>(applicationOnboardingResponse, httpHeaders,
							validateResult.getResponseCode());
				} else {

					logger.info("updateAppdynamics - Persisting Request to Database Failed");
					ApplicationOnboardingError appError = this.failedPersistance();
					return new ResponseEntity<>(appError, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
				}

			} else {
				logger.info("updatAppdynamics - Validation of Request failed");
				ApplicationOnboardingError appError = this.failedValidation(validateResult);
				return new ResponseEntity<>(appError, httpHeaders, validateResult.getResponseCode());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("updateAppdynamics - Request Failed");

			ApplicationOnboardingError appError = new ApplicationOnboardingError();
			appError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
			appError.setMessage(Constants.ERROR_MESSAGE_INTERNAL);
			return new ResponseEntity<>(appError, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method exposes Api to view application on controller
	 * @param appdExternalId - Payload for application view
	 * @return - HttpResponse
	 */
	@Operation(summary = "Get the Onboarded Applications Details in AppDynamics Controller", description = "Get Application Details from AppDynamics Controller")
	@Tags(value = {
			@Tag(name = "Service Assurance AppDynamics Onboarding API", description = "Service Assurance AppDynamics Onboarding API") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApplicationOnboardingResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Location", description = "Location of the created resource", schema = @Schema(type = "string")),
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Link", description = "Link", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }),
			@ApiResponse(responseCode = "default", description = "Unexpected error", content = @Content(schema = @Schema(implementation = ApplicationOnboardingError.class), mediaType = MediaType.APPLICATION_JSON_VALUE), headers = {
					@Header(name = "Date", schema = @Schema(type = "string", pattern = "(Sun|Mon|Tue|Wed|Thu|Fri|Sat),\\s([0-9]{1,2})\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s[0-9][0-9][0-9][0-9](:|\\s)[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\sGMT")),
					@Header(name = "TrackingId", description = "Request tracking Id", schema = @Schema(type = "string")),
					@Header(name = "Cache-Control", description = "Cache", schema = @Schema(type = "string")) }) })
	@Parameter(name = "id", description = "Application Id")
	@GetMapping(path = "/{id}", produces = "application/json")
	public @ResponseBody ResponseEntity<Object> viewAppdynamics(@PathVariable("id") String appdProjectId) {
		ViewAppdynamicsResponse viewResponse;
		String trackingId = this.createTrackingId();
		final HttpHeaders httpHeaders = this.createHeaders(trackingId);
		MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, trackingId);

		logger.info(
				"AppDOnboardingRequestHandlerImpl: viewAppdynamics :: Started View Appdynamics API For AppD external Id");
		if (!XSSValidationUtil.xSSAttackValidation(appdProjectId)) {
			ApplicationOnboardingError appError = new ApplicationOnboardingError();
			appError.setCode(HttpStatus.BAD_REQUEST.name());
			logger.info("viewAppdynamics - Request type validation failed");
			appError.setMessage("Bad request type for view");
			return new ResponseEntity<>(appError, httpHeaders, HttpStatus.BAD_REQUEST);
		}

		viewResponse = requestHandler.getRequestByProjectId(appdProjectId);

		if (viewResponse == null) {
			logger.info("viewAppdynamics - AppD external Id not found");
			ApplicationOnboardingError appError = new ApplicationOnboardingError();
			appError.setCode(HttpStatus.NOT_FOUND.name());
			appError.setMessage("Applicatin id is not found");
			return new ResponseEntity<>(appError, httpHeaders, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(viewResponse, httpHeaders, HttpStatus.OK);

	}

	/**
	 * This function used to build request from payload
	 * @param requestType - It can have CREATE or UPDATE values
	 * @return
	 */
	public AppDOnboardingRequest buildRequest(String requestType) {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		String requestDate = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		String lastUpdated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setOperationCounter(1);
		if (Constants.REQUEST_TYPE_CREATE.equals(requestType)) {
			request.setAppdExternalId(UUID.randomUUID().toString());
			request.setRequestCreatedDate(requestDate);
		}
		request.setOperationalStatus(Constants.OPERATIONAL_STATUS_INACTIVE);
		request.setRequestType(requestType);
		request.setRequestStatus(Constants.REQUEST_STATUS_PENDING);
		request.setRequestModifiedDate(lastUpdated);
		request.setRetryDetails(retryDetails);
		request.setRetryLock(false);
		return request;
	}

	/**
	 * This method builds the body of create request
	 * @param trackingId    - This is used to track request status
	 * @param rDetails      - This is the payload of the request
	 * @param appdProjectId - This is the ID of the application created on
	 *                      controller
	 * @return
	 */
	public RequestDetails buildBodyForCreate(String trackingId, ApplicationOnboardingRequest rDetails,
			String appdProjectId) {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName(rDetails.getApmApplicationGroupName());
		requestDetails.setAlertAliases(this.convertArrayToString(rDetails.getAlertAliases()));
		requestDetails.setAdminUsers(this.convertArrayToString(rDetails.getAdminUsers()));
		requestDetails.setViewUsers(this.convertArrayToString(rDetails.getViewUsers()));
		requestDetails.setApmLicenses(rDetails.getApmLicenses());
		requestDetails.setEumApps(rDetails.getEumApplicationGroupNames());
		requestDetails.setAppdProjectId(appdProjectId);
		requestDetails.setCtrlName(controller);
		requestDetails.setTrackingId(trackingId);

		return requestDetails;
	}

	/**
	 * This method builds the body of update request
	 * @param trackingId    - This is used to track request status
	 * @param rDetails      - This is the payload of the request
	 * @param appdProjectId - This is the ID of the application created on
	 *                      controller
	 * @return
	 */
	public RequestDetails buildBodyForUpdate(String trackingId, ApplicationOnboardingUpdateRequest rDetails,
			String appdProjectId) {
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases(this.convertArrayToString(rDetails.getAlertAliases()));
		requestDetails.setAdminUsers(this.convertArrayToString(rDetails.getAdminUsers()));
		requestDetails.setViewUsers(this.convertArrayToString(rDetails.getViewUsers()));
		requestDetails.setApmLicenses(rDetails.getApmLicenses());
		requestDetails.setEumApps(rDetails.getEumApplicationGroupNames());
		requestDetails.setAppdProjectId(appdProjectId);
		requestDetails.setTrackingId(trackingId);
		requestDetails.setCtrlName(controller);

		return requestDetails;
	}

	/**
	 * Creates ApplicationOnboardingError object for failed validation
	 * @param validateResult
	 * @return - Returns ApplicationOnboardingError object
	 */
	private ApplicationOnboardingError failedValidation(ValidateResult validateResult) {
		ApplicationOnboardingError appError = new ApplicationOnboardingError();
		appError.setCode(validateResult.getErrorObject().getCode() + "");
		appError.setMessage(validateResult.getErrorObject().getMsg());
		return appError;

	}

	/**
	 * Creates ApplicationOnboardingError object for failed persistance
	 * @return - Returns ApplicationOnboardingError object
	 */
	private ApplicationOnboardingError failedPersistance() {
		ApplicationOnboardingError appError = new ApplicationOnboardingError();
		appError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
		appError.setMessage(Constants.ERROR_MESSAGE_FAILED_PERSISTANCE);
		return appError;

	}

	/**
	 * Converts array to String
	 * @param dataList - data for conversion into String
	 * @return - Returns String
	 */
	public String convertArrayToString(List<String> dataList) {
		StringBuilder finalString = new StringBuilder();
		String result;
		if (dataList != null) {
			for (String element : dataList) {
				finalString.append(element).append(",");
			}
			result = finalString.deleteCharAt(finalString.length() - 1).toString();
			return result;
		} else
			return null;
	}

	/**
	 * Validates EUM and Alert Aliases in payload
	 * 
	 * @param eumApplicationGroupNames - List of Eum applications in payload
	 * @param alertAliases             - List of Alert aliases in payload
	 * @return - String
	 * @throws AppDOnboardingException
	 */
	public String validateEUMAndalertAliases(List<String> eumApplicationGroupNames, List<String> alertAliases)
			throws AppDOnboardingException {
		String finalResult = null;
		if (eumApplicationGroupNames != null && eumApplicationGroupNames.size() > maxNumOfEum) {
			logger.info("validateEUMAndalertAliases - Request is not Valid : No Of EUM Apps are more than allowed");
			return "Request is not Valid : No of  EUM Apps are more than allowed";

		}
		if (alertAliases != null && alertAliases.size() > maxNumOfAlerts) {
			logger.info(
					"validateEUMAndalertAliases - Request is not Valid : No Of Alert Aliases are more than allowed {}",
					maxNumOfAlerts);
			return "Request is not Valid : No Of Alert Aliases are more than allowed";
		}
		if (eumApplicationGroupNames != null) {
			finalResult = this.eumNameCheck(eumApplicationGroupNames);
		}
		try {
			if (!operationHandler.checkIfEUMApplicationNotExist(eumApplicationGroupNames, controller))
				return EUM_EXISTS_MSG;
		} catch (AppDOnboardingException e) {
			throw new AppDOnboardingException(
					"validateEUMAndalertAliases - checkIfEUMApplicationNotExist - Exception in checkIfEUMApplicationNotExist",
					e);

		}
		return finalResult;
	}

	/**
	 * This method is used to check whether any eum name is same as eum name is case
	 * insensitive
	 * @param eumApplicationGroupNames
	 * @return
	 */
	private String eumNameCheck(List<String> eumApplicationGroupNames) {
		for (int i = 0; i < eumApplicationGroupNames.size(); i++) {
			String checkName = eumApplicationGroupNames.get(i);
			for (int j = i + 1; j < eumApplicationGroupNames.size(); j++) {
				if (eumApplicationGroupNames.get(j).equalsIgnoreCase(checkName)) {
					logger.info(
							"validateEUMAndalertAliases - Request is not Valid :EUM App name is not case sensitive");
					return "Request is not Valid : EUM App name is case insensitive";
				}
			}
		}
		return null;
	}

	/**
	 * Creates tracking Id
	 * @return - Returns Tracking Id as String
	 */
	private String createTrackingId() {
		String dateTime = String.valueOf(System.currentTimeMillis());
		String randomUUID = UUID.randomUUID().toString();
		return Constants.SENDER_TYPE + "_" + randomUUID + "_" + dateTime;
	}

	/**
	 * Creates response for create request
	 * @param onboardingRequest - Onboarding request payload
	 * @param appdExternalId    - Application Id
	 * @return - ApplicationOnboardingResponse object
	 */
	private ApplicationOnboardingResponse createResponse(ApplicationOnboardingRequest onboardingRequest,
			String appdExternalId) {

		ApplicationOnboardingResponse appResponse = new ApplicationOnboardingResponse();
		appResponse.setApmApplicationGroupName(onboardingRequest.getApmApplicationGroupName());
		appResponse.setAlertAliases(onboardingRequest.getAlertAliases());
		appResponse.setAdminUsers(onboardingRequest.getAdminUsers());
		appResponse.setViewUsers(onboardingRequest.getViewUsers());
		appResponse.setApmLicenses(onboardingRequest.getApmLicenses());
		appResponse.setEumApplicationGroupNames(onboardingRequest.getEumApplicationGroupNames());
		appResponse.setId(appdExternalId);
		return appResponse;
	}

	/**
	 * Creates response for update request
	 * @param onboardingRequest - Onboarding request payload
	 * @param appdExternalId    - Application Id
	 * @return - ApplicationOnboardingResponse Object
	 */
	private ApplicationOnboardingResponse createUpdateResponse(ApplicationOnboardingUpdateRequest onboardingRequest,
			String appdExternalId) {
		ApplicationOnboardingResponse appResponse = new ApplicationOnboardingResponse();
		appResponse.setAlertAliases(onboardingRequest.getAlertAliases());
		appResponse.setAdminUsers(onboardingRequest.getAdminUsers());
		appResponse.setViewUsers(onboardingRequest.getViewUsers());
		appResponse.setApmLicenses(onboardingRequest.getApmLicenses());
		appResponse.setEumApplicationGroupNames(onboardingRequest.getEumApplicationGroupNames());
		appResponse.setId(appdExternalId);
		AppDOnboardingRequest tempRequest = requestDao.findByExternalIdAndRequestType(appdExternalId,
				Constants.REQUEST_TYPE_CREATE);
		if (tempRequest != null)
			appResponse.setApmApplicationGroupName(tempRequest.getRequestDetails().getAppGroupName());
		return appResponse;
	}

	/**
	 * Create header response for all request
	 * @param trackingId
	 * @return
	 */
	private HttpHeaders createHeaders(String trackingId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.REQUEST_TYPE_TRACKING_ID, trackingId);
		headers.add(X_CONTENT_TYPE_OPTIONS, "nosniff");
		headers.add(STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains; preload");
		headers.add(X_XSS_PROTECTION, "1; mode=block");
		headers.add(X_FRAME_OPTIONS, "Deny");
		headers.add(CONTENT_SECURITY_POLICY,
				"default-src 'self'; base-uri 'self'; frame-ancestors 'self'; block-all-mixed-content");
		headers.add(SET_COOKIE, "Path=/; Secure; HttpOnly");
		return headers;
	}
}
