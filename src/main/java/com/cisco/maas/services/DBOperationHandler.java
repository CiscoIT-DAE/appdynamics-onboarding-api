package com.cisco.maas.services;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.EUMMetaDataDAO;
import com.cisco.maas.dao.RoleMappingDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.EUMMetaData;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.exception.AppDOnboardingException;
/**
 * This class contains DB operational related methods.
 * */
@Service
@Qualifier("DBOperationHandler")
public class DBOperationHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(DBOperationHandler.class);
	@Autowired
	APPDMasterDAO appDMasterDao;	
	@Autowired
	EUMMetaDataDAO eUMMetaDataDAO;
	@Autowired
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	@Autowired
	RoleMappingDAO roleMappingDAO;	
	
	/**
	 * This method is used to persist Appd metadata.	 
	 * @param request: AppDOnboardingRequest type request details.	 	
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public boolean persistAppDMetadata(AppDOnboardingRequest request) throws AppDOnboardingException
	{
		try
		{
			logger.info("persistAppDMetadata - Started Processing Request in persistAppDMetadata");
			APPDMaster appdMaster=new APPDMaster();
			List<String> eumList = new ArrayList<>();
			appdMaster.setCtrlName(request.getRequestDetails().getCtrlName());
			appdMaster.setAppGroupId(Integer.parseInt(request.getAppGroupID()));
			appdMaster.setAppGroupName(request.getRequestDetails().getAppGroupName());
			appdMaster.setLicenseKey(request.getLicenseKey());
			appdMaster.setApmLicenses(request.getRequestDetails().getApmLicenses());
			
			if(request.getRequestDetails().getEumApps()!=null)
				appdMaster.setEumApps(request.getRequestDetails().getEumApps());
			else if (request.getRequestDetails().getEumApps()==null)
				appdMaster.setEumApps(eumList);
			
			
			appdMaster.setAlertAliases(request.getRequestDetails().getAlertAliases());
            
			if(request.getRequestDetails().getEumApps()!=null && !request.getRequestDetails().getEumApps().isEmpty())
            	appdMaster.setNoOfEUMLicenses(1);
			
			boolean res=appDMasterDao.createApplication(appdMaster);
			
			if(!res)
			{
				logger.info("persistAppDMetadata - Error occurred while inserting Master object to db");
				throw new AppDOnboardingException("DBHandler - persistAppDMetadata - Exception in persistAppDMetadata",request);
			}
			
			logger.info("persistAppDMetadata - Ended Processing Request in persistAppDMetadata");
			return res;
		}
		catch(AppDOnboardingException e)
		{	
			logger.error("persistAppDMetadata - ERROR");
			throw new AppDOnboardingException("DBOperationHandler - persistAppDMetadata - Exception in persistAppDMetadata"+e.getMessage(),request, e);
		}
	}
	
	/**
	 * This method is used to Update Appd metadata.	 
	* @param request: AppDOnboardingRequest type request details.	 	
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public boolean updateAppDMetadata(AppDOnboardingRequest request) throws AppDOnboardingException
	{	
		logger.info("updateAppDMetadata - Started Processing Request in updateAppDMetadata");
		try
		{
			boolean res=appDMasterDao.updateApplication(request);
			if(!res)
			{
				logger.info("updateAppDMetadata - Error occurred while Updating Master object to db");
				throw new AppDOnboardingException("DBHandler - updateAppDMetadata - Exception in updateAppDMetadata",request);
			}
			logger.info("updateAppDMetadata - Ended Processing Request in updateAppDMetadata");
			return res;
		}
		catch(AppDOnboardingException e)
		{
			logger.error("updateAppDMetadata - ERROR");
			throw new AppDOnboardingException("DBHandler - updateAppDMetadata - Exception in updateAppDMetadata",request, e);

		}
	}	
	
	
	/**
	 * This method is used to persist role mappings.	 
	 * @param mappingList: List type role mappings.	 
	 * @returns boolean
	 */
	public boolean persistMappings(List<RoleMapping> mappingList){
			logger.info("persistMappings - START");
			logger.info("persistMappings - Started Processing Request in persistMappings mapping list = {}",
					mappingList);
			for (RoleMapping mapping : mappingList) {
				roleMappingDAO.create(mapping);
			}
			logger.info("persistMappings - END");
			return true;
	}
	/**
	 * This method is used to get role mappings based on application name.	 
	 * @param appName: String type application name.
	 * @param ctrlName: String type controller name. 
	 * @returns List
	 */
	public Iterable<RoleMapping> readMappings(String appName,String ctrlName)
	{			
		return roleMappingDAO.findByApp(appName, ctrlName);		
	}

	/**
	 * This method is used to check if given application is present or not.	 
	 * @param appGroupName: String type application group name.
	 * @param ctrlName: String type controller name. 
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public boolean checkIfAPMApplicationNotExist(String appGroupName,String ctrlName) throws AppDOnboardingException 
	{
		try
		{
			logger.info("checkIfAPMApplicationNotExist - START");
			String result;
			result=appDApplicationCreationHandler.getAppID(ctrlName, appGroupName);
			logger.info("DBOperationHandler - checkIfAPMApplicationNotExist - END");
			return(result==null);
		}
		catch(IOException e)
		{
			logger.error("checkIfAPMApplicationNotExist - ERROR");
			throw new AppDOnboardingException("DBHandler - checkIfAPMApplicationNotExist - Exception in checkIfAPMApplicationNotExist", e);

		}
	}
	
	/**
	 * This method is used to check if given eum application is present or not.	 
	 * @param eumList: List type eum application list.
	 * @param ctrlName: String type controller name. 
	 * @throws AppDOnboardingException.
	 * @returns boolean
	 */
	public boolean checkIfEUMApplicationNotExist(List<String> eumList, String ctrlName) throws AppDOnboardingException {
		try {
			logger.info("checkIfEUMApplicationNotExist - START");
			String result;
			if (eumList != null) {
				for (String eumName : eumList) {
					result = appDApplicationCreationHandler.getAppID(ctrlName, eumName);
					if (result != null) {
						logger.info("checkIfEUMApplicationNotExist - false - END");
						return false;
					}
				}
				logger.info("checkIfEUMApplicationNotExist - true - END");
				return true;
			} else {
				logger.info("checkIfEUMApplicationNotExist - eumList is null- END");
				return true;
			}
		} catch (IOException e) {
			logger.error("checkIfEUMApplicationNotExist - ERROR");
			throw new AppDOnboardingException(
					"DBHandler - checkIfEUMApplicationNotExist - Exception in checkIfEUMApplicationNotExist", e);
		}
	}
	/**
	 * This method is used to delete given mapping.	 
	 * @param eumList: List type eum application list.
	 * @returns boolean
	 */
	public boolean deleteMapping(List<RoleMapping> mappingList)
	{
		    logger.info("deleteMapping - START");
			logger.info("deleteMapping - Started Processing Request in deleteMapping");
			if(mappingList!=null)
			{
				for(RoleMapping roleMap : mappingList)
				{
					RoleMapping roleMapDetails = roleMappingDAO.findApp(roleMap.getAppGroupName(),roleMap.getCtrlName());
					if(roleMapDetails!=null)
						roleMappingDAO.delete(roleMapDetails);
				}
			}
			logger.info("deleteMapping - Ended Processing Request in deleteMapping");
			logger.info("deleteMapping - END");
			return true;
		}
	
	/**
	 * This method is used to persist eum metadata.	
	 * @param appdProjectId: String type application project id. 
	 * @param eumList: List type eum application list.
	 * @param eumCreationDate: String type eum application creation date.
	 * @returns boolean
	 */
	public boolean persistEUMMetaData(String appdProjectId , List<String> eumList, String eumCreationDate)
	{
			logger.info("persistEUMMetaData - START");
			logger.info("persistEUMMetaData - Started Processing Request in persistEUMMetaData eum list = {}",eumList);

			if(eumList!=null)
			{
				for(String eumName : eumList)
				{
					EUMMetaData eumDetails = new EUMMetaData();
					eumDetails.setAppdProjectId(appdProjectId);
					eumDetails.setEumName(eumName);
					eumDetails.setEumCreatedDate(eumCreationDate);
					eUMMetaDataDAO.createEUMApplication(eumDetails);
				}
			}
			logger.info("persistEUMMetaData - Ended Processing Request in persistEUMMetaData");
			logger.info("persistEUMMetaData - END");
			return true;
		}
	
	
	/**
	 * This method is used to delete eum metadata.	
	 * @param appdProjectId: String type application project id. 
	 * @param deleteEumList: List type eum application list.	 
	 * @returns boolean
	 */
	public boolean deleteEUMMetaData(String appdProjectId , List<String> deleteEumList) 
	{
			logger.info("deleteEUMMetaData - START");
			logger.info("deleteEUMMetaData - Started Processing Request in deleteEUMMetaData");
			if(deleteEumList!=null)
			{
				for(String eumName : deleteEumList)
				{
					EUMMetaData eumDetails = eUMMetaDataDAO.findByApp(appdProjectId, eumName);
					if(eumDetails!=null)
						eUMMetaDataDAO.deleteEUMApplication(eumDetails);
				}
			}
			logger.info("deleteEUMMetaData - Ended Processing Request in deleteEUMMetaData");
			logger.info("deleteEUMMetaData - END");
			return true;
		
	}
	
}
