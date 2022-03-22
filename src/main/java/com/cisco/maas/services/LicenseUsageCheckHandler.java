package com.cisco.maas.services;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.exception.AppDOnboardingException;
import com.cisco.maas.util.Constants;

/**
 * This class contains methods to process callback request and process license quota check.
 * */
@Service
@Qualifier("LicenseUsageCheckHandler")
public class LicenseUsageCheckHandler extends AppDOnboardingRequestHandlerImpl{

	private static final Logger logger = LoggerFactory.getLogger(LicenseUsageCheckHandler.class);
	private int maxLicenseUsageLimit;


	@Autowired
	LicenseQuotaHandler quotaHandler;

	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public LicenseUsageCheckHandler() {
		try (InputStream input = new FileInputStream(getClass().getClassLoader().getResource("config.properties").getFile())) 
		{
		 	Properties properties = new Properties();
		 	properties.load(input);			
		 	maxLicenseUsageLimit=Integer.valueOf(properties.getProperty("appd.maxLicenseUsageLimitForAlert"));
		 	
		}catch(Exception error) {
		 	logger.info("Exception Loading Properties File",error); 
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
		if(Constants.REQUEST_STATUS_ERROR.equals(request.getRequestStatus()))
		{
			logger.info("handleRequest - Processing Request in ERROR type ");	
		}
		else
		{
			logger.info("handleRequest - Processing Request in LicenseUsageCheckHandler");	
			this.processCallBack(request);			
			logger.info("handleRequest - END");
		}

	}
	
	/**
	 * This method is to process callback.
	 * @param request: AppDOnboardingRequest type which contains payload.
	 * @return : boolean.
	 */
	public boolean processCallBack(AppDOnboardingRequest request)
	{
		try {
			logger.info("processCallBack - START" );
			this.processLicenseQuotaCheck(request);
	  	} catch (Exception error) {
			logger.info("processCallBack - Error occured during sending success payload to callback Url", error);
		}
		logger.info("processCallBack - END" );
		return true;
	}

	/**
	 * This method is to process license quotes check.
	 * @param request: AppDOnboardingRequest type which contains payload.	 
	 */
	public void processLicenseQuotaCheck(AppDOnboardingRequest request)
	{
		try
		{
			logger.info("processESPandLicenseQuota - START");			
			float licenseUsage=quotaHandler.getLicenseUsage(request.getRequestDetails().getCtrlName());
			logger.info("processESPandLicenseQuota - License Usage is {}%",licenseUsage);	
			if(licenseUsage>=maxLicenseUsageLimit)
			{
			logger.info("processESPandLicenseQuota - Controller License Usage is greater than max usage limit which is {}%", maxLicenseUsageLimit);
			logger.info("processESPandLicenseQuota - END");	
			}
		}
		catch(Exception error)
		{
			logger.info("processESPandLicenseQuota - Error occurred while checking license Quota",error);
		}
	}
}
