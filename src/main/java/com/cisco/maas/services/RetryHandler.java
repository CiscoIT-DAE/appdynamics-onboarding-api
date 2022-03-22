package com.cisco.maas.services;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;

@Configuration
@PropertySource(value="classpath:config.properties")
@EnableScheduling
public class RetryHandler {

	private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RequestDAO requestDAO;
	@Autowired
	ProcessRequest processRequest;
	@Autowired
	RollBackHandler rollBackHandler;

	private int limitMax;
	private int limitTimeOut;

	public RetryHandler() {

		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			Properties properties = new Properties();
			properties.load(input);
			limitMax = Integer.valueOf(properties.getProperty("retry.limitMax"));
			limitTimeOut = Integer.valueOf(properties.getProperty("retry.limitTimeOut"));
		} catch (Exception error) {
			logger.info("RetryHandler() - Exception Loading Properties File", error);

		}
	}

	@Scheduled(cron = "${cronjob.expression}")
	public void retryFailedRequests(){
		logger.info(
				"retryFailedRequests - Task executed at every 10 minutes. Current time is :: {}",
				new Date());

		Iterable<AppDOnboardingRequest> requests = requestDAO.findFailedRequests();

		if (requests != null) {
			logger.info("retryFailedRequests - Total {} failed Orders found", IterableUtils.size(requests));

			for (AppDOnboardingRequest request : requests) {
				try {
					MDC.put(Constants.REQUEST_TYPE_TRACKING_ID, request.getRequestDetails().getTrackingId());
					request.setRetryCount(request.getRetryCount() + 1);
					request.setRetryLock(true);
					if (!requestDAO.updateRetryLock(request))
						logger.info("retryFailedRequests - Persisting Retry Count Failed");

					if (request.getRequestType().equals(Constants.REQUEST_TYPE_CREATE)) {
						this.createHandler(request);
					} else if (request.getRequestType().equals(Constants.REQUEST_TYPE_UPDATE)) {

						this.updateHandler(request);

					} 
				} catch (Exception error) {
					logger.info("retryFailedRequests - Error Ocurred while processing for Project ID {} in retry ", request.getRequestDetails().getAppdProjectId());
					logger.error(error.getMessage(), error);
				}
			}
		} else {
			logger.info("retryFailedRequests - No failed orders found");
		}
	}

	public void createHandler(AppDOnboardingRequest request) throws AppDOnboardingException {
		if (request.getRetryCount() <= limitMax) {
			logger.info("createHandler - Retrying Create Request");
			processRequest.asyncProcessRequest(request);
		} else if (request.getRollbackCounter() < limitTimeOut) {
			request.setRollbackCounter(request.getRollbackCounter() + 1);
			logger.info("createHandler - Not Retrying Create Request due to retry timeout Performing Roll Back");
			rollBackHandler.handleRequest(request);
		} else {
			request.setRequestStatus(Constants.ROLLBACK_ERROR);
			rollBackHandler.handleRequest(request);
		}
	}

	public void updateHandler(AppDOnboardingRequest request) throws AppDOnboardingException {
		if (request.getRetryCount() <= limitMax) {
			logger.info("updateHandler - Retrying Update Request");
			processRequest.asyncProcessUpdateRequest(request);
		} else if (request.getRollbackCounter() < limitTimeOut
				&& request.getRequestDetails().getAddEumpApps() != null) {
			request.setRollbackCounter(request.getRollbackCounter() + 1);
			logger.info("updateHandler - Not Retrying Update Request due to retry timeout Performing Roll Back");
			rollBackHandler.handleRequest(request);

		} else if (request.getRollbackCounter() >= limitTimeOut) {
			request.setRequestStatus(Constants.ROLLBACK_ERROR);
			rollBackHandler.handleRequest(request);
		}
	}
}
