package com.cisco.maas.dao;

import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import java.io.IOException;

public interface APPDMasterDAO {
	public boolean createApplication(APPDMaster masterDetails);	 
	public boolean updateApplication(AppDOnboardingRequest request);	
	public boolean deleteApplication(APPDMaster masterDetails);
    public Iterable <APPDMaster> findAll() throws IOException;
    public APPDMaster findByApp(String appGroupName,String ctrlName);	
	public String getLicenseKey(String appGroupName,String environment);
	public int[] getApmLicenses(String appGroupName,String ctrlName);
	public boolean updateEUMLicense(String appGroupName,String ctrlName,int eUMLicense);
	public boolean updateNewAppName(AppDOnboardingRequest request,String appName);
	public boolean checkGetId(APPDMaster appDetails);
}