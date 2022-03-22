package com.cisco.maas.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cisco.maas.util.AppDynamicsUtil;
import com.cisco.maas.util.Constants;

/**
 * This class contains methods to license usage.
 * */
@Service
public class LicenseQuotaHandler {
	private static final Logger logger = LoggerFactory.getLogger(LicenseQuotaHandler.class);

	private String controllerPrefix;
	private String licenseAccountIdURL;

	@Autowired
	AppDynamicsUtil appdUtil;
	
	/**
	 * Initializing constants from config.properties file in constructor.
	 */
	public LicenseQuotaHandler() throws IOException {

		try (InputStream input = new FileInputStream(
				getClass().getClassLoader().getResource("config.properties").getFile())) {
			Properties properties = new Properties();
			properties.load(input);
			controllerPrefix = properties.getProperty("appd.prefix");
			licenseAccountIdURL = properties.getProperty("appd.getLicenseAccountID.url");
		}
	}
	
	/**
	 * This method is used to get license usage.	 
	 * @param ctrlName: String type controller name.	 	
	 * @throws IOException.
	 * @returns Float
	 */
	public Float getLicenseUsage(String ctrlName) throws IOException {
		try {
		logger.info("getLicenseUsage - START");
		String accountId;
		String accountIdURL = Constants.PROTO + ctrlName + controllerPrefix + licenseAccountIdURL;
		String accountIdResponse = appdUtil.appDConnectionOnlyGet(accountIdURL, Constants.HTTP_VERB_GET, ctrlName, "licenseQuota");
		
		JSONObject accountIdJSON = new JSONObject(accountIdResponse);
		
		if(accountIdJSON.length()!=0 && accountIdJSON.getString("id")!=null) {
			accountId = accountIdJSON.getString("id");
		}
		else {
			logger.error("getLicenseUsage - Unable to fetch account id. "
					+ "Response returned from controller is {0}",accountIdResponse);
			return 0f;
		}
		
		String rURL = "api/accounts/";
		rURL = Constants.PROTO + ctrlName + controllerPrefix + rURL + accountId + "/licensemodules/apm/usages";
		String response = appdUtil.appDConnectionOnlyGet(rURL, Constants.HTTP_VERB_GET, ctrlName, "licenseQuota");
		
		JSONObject res = new JSONObject(response);
		
		if(res.length()!=0) {
			JSONArray usages = res.getJSONArray("usages");
	
			int totalLicenses = usages.getJSONObject(0).getInt("avgUnitsProvisioned");
			int utilizedLicenses = usages.getJSONObject(0).getInt("maxUnitsUsed");
	
			float usageValue = (utilizedLicenses / totalLicenses);
			logger.info("getLicenseUsage - response - END");
			return usageValue * 100;
		}
		else {
			logger.info("getLicenseUsage - END");			
			return 0f;
		}
		} catch(Exception error) {
			logger.error("getLicenseUsage - Error encountered {0}",error);
			return 0f;
		}
	}
}
