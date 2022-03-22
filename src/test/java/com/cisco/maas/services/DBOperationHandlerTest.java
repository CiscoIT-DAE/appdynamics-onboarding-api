package com.cisco.maas.services;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.cisco.maas.dao.APPDMasterDAO;
import com.cisco.maas.dao.EUMMetaDataDAO;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dao.RoleMappingDAO;
import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.EUMMetaData;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.exception.AppDOnboardingException;
import com.mongodb.MongoException;

public class DBOperationHandlerTest {

	@InjectMocks
	@Spy
	DBOperationHandler operationHandler;

	@Mock
	RequestDAO requestDao;

	@Mock
	APPDMasterDAO appDMasterDao;

	@Mock
	RoleMappingDAO roleMappingDAO;
	@Mock
	AppDApplicationCreationHandler appDApplicationCreationHandler;
	@Mock
	EUMMetaDataDAO eUMMetaDataDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void persistAppDMetadata() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setApmLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setLicenseKey("652hgjdsad");
		Mockito.when(appDMasterDao.createApplication(any(APPDMaster.class))).thenReturn(true);
		operationHandler.persistAppDMetadata(request);
	}

	@Test
	public void persistAppDMetadata_eumListIsEmpty() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setApmLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setLicenseKey("652hgjdsad");
		Mockito.when(appDMasterDao.createApplication(any(APPDMaster.class))).thenReturn(true);
		operationHandler.persistAppDMetadata(request);
	}

	@Test
	public void persistAppDMetadata_eumListIsNull() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setApmLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setLicenseKey("652hgjdsad");

		Mockito.when(appDMasterDao.createApplication(any(APPDMaster.class))).thenReturn(true);
		operationHandler.persistAppDMetadata(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void persistAppDMetadata_responseIsFalse() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setApmLicenses(1);
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		request.setAppGroupID("123");
		request.setLicenseKey("652hgjdsad");
		Mockito.when(appDMasterDao.createApplication(any(APPDMaster.class))).thenReturn(false);
		operationHandler.persistAppDMetadata(request);
	}

	@Test
	public void updateAppDMetadataTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.when(appDMasterDao.updateApplication(request)).thenReturn(true);
		operationHandler.updateAppDMetadata(request);
	}

	@Test(expected = AppDOnboardingException.class)
	public void updateAppDMetadataTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		Mockito.when(appDMasterDao.updateApplication(request)).thenReturn(false);
		operationHandler.updateAppDMetadata(request);
	}

	@Test
	public void readMappingsTest() throws Exception {
		operationHandler.readMappings("Test", "Test");
	}

	@Test
	public void deleteMappingTest() throws Exception {
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		Mockito.when(roleMappingDAO.findApp(any(String.class), any(String.class))).thenReturn(roleMap);
		Mockito.when(roleMappingDAO.delete(any(RoleMapping.class))).thenReturn(true);
		operationHandler.deleteMapping(mappingList);
	}

	@Test
	public void deleteMapping_mappingListIsNull() throws Exception {
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = null;
		Mockito.when(roleMappingDAO.findApp(any(String.class), any(String.class))).thenReturn(roleMap);
		Mockito.when(roleMappingDAO.delete(any(RoleMapping.class))).thenReturn(true);
		operationHandler.deleteMapping(mappingList);
	}

	@Test
	public void deleteMapping_roleMapDetailsIsNull() throws Exception {
		RoleMapping roleMap = new RoleMapping();
		RoleMapping roleMap2 = null;
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		Mockito.when(roleMappingDAO.findApp(any(String.class), any(String.class))).thenReturn(roleMap2);
		Mockito.when(roleMappingDAO.delete(any(RoleMapping.class))).thenReturn(true);
		operationHandler.deleteMapping(mappingList);
	}

	@Test(expected = MongoException.class)
	public void deleteMapping_exception() throws Exception {
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setCtrlName("test");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		Mockito.when(roleMappingDAO.findApp(any(String.class), any(String.class))).thenThrow(new MongoException(""));
		Mockito.when(roleMappingDAO.delete(any(RoleMapping.class))).thenReturn(true);
		operationHandler.deleteMapping(mappingList);
	}

	@Test
	public void checkIfAPMApplicationNotExist() throws Exception {
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenReturn(null);
		operationHandler.checkIfAPMApplicationNotExist("test", "test");
	}

	@Test
	public void checkIfAPMApplicationNotExist_resultIsNotNull() throws Exception {
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenReturn("");
		operationHandler.checkIfAPMApplicationNotExist("test", "test");
	}

	@Test(expected = AppDOnboardingException.class)
	public void checkIfAPMApplicationNotExist_exception() throws Exception {
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenThrow(new IOException());
		operationHandler.checkIfAPMApplicationNotExist("test", "test");
	}

	@Test(expected = MongoException.class)
	public void persistEUMMetaData_exception() throws Exception {
		List<String> EumApps = new ArrayList<String>();
		EumApps.add("emu App");
		Mockito.when(eUMMetaDataDAO.createEUMApplication(any(EUMMetaData.class))).thenThrow(new MongoException(""));
		operationHandler.persistEUMMetaData("test123", EumApps, "1-12-2011");
	}

	@Test
	public void persistEUMMetaData() throws Exception {
		List<String> EumApps = new ArrayList<String>();
		EumApps.add("emu App");
		Mockito.when(eUMMetaDataDAO.createEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.persistEUMMetaData("test123", EumApps, "1-12-2011");
	}

	@Test
	public void persistEUMMetaData_eumListIsNull() throws Exception {
		List<String> EumApps = null;
		Mockito.when(eUMMetaDataDAO.createEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.persistEUMMetaData("test123", EumApps, "1-12-2011");
	}

	@Test
	public void checkIfEUMApplicationNotExistTest() throws Exception {
		List<String> eumList = new ArrayList<>();
		eumList.add("test1");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenReturn(null);
		operationHandler.checkIfEUMApplicationNotExist(eumList, "test");
	}

	@Test
	public void checkIfEUMApplicationNotExist_resultIsNotNull() throws Exception {
		List<String> eumList = new ArrayList<>();
		eumList.add("test1");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenReturn("");
		operationHandler.checkIfEUMApplicationNotExist(eumList, "test");
	}

	@Test
	public void checkIfEUMApplicationNotExist_eumListIsNull() throws Exception {
		List<String> eumList = null;
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenReturn(null);
		operationHandler.checkIfEUMApplicationNotExist(eumList, "test");
	}

	@Test(expected = AppDOnboardingException.class)
	public void checkIfEUMApplicationNotExist_exception() throws Exception {
		List<String> eumList = new ArrayList<>();
		eumList.add("test1");
		Mockito.when(appDApplicationCreationHandler.getAppID(any(String.class), any(String.class))).thenThrow(new IOException());
		operationHandler.checkIfEUMApplicationNotExist(eumList, "test");
	}

	@Test
	public void deleteEUMMetaData_eumListIsNull() throws Exception {
		EUMMetaData eumData = new EUMMetaData();
		List<String> deleteEumApps = null;
		Mockito.when(eUMMetaDataDAO.findByApp(any(String.class), any(String.class))).thenReturn(eumData);
		Mockito.when(eUMMetaDataDAO.deleteEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.deleteEUMMetaData("test123", deleteEumApps);
	}

	@Test
	public void deleteEUMMetaData_eumDetails() throws Exception {
		EUMMetaData eumData = null;
		List<String> deleteEumApps = new ArrayList<String>();
		deleteEumApps.add("emu App");
		Mockito.when(eUMMetaDataDAO.findByApp(any(String.class), any(String.class))).thenReturn(eumData);
		Mockito.when(eUMMetaDataDAO.deleteEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.deleteEUMMetaData("test123", deleteEumApps);
	}

	@Test
	public void deleteEUMMetaData() throws Exception {
		EUMMetaData eumData = new EUMMetaData();
		List<String> deleteEumApps = new ArrayList<String>();
		deleteEumApps.add("emu App");
		Mockito.when(eUMMetaDataDAO.findByApp(any(String.class), any(String.class))).thenReturn(eumData);
		Mockito.when(eUMMetaDataDAO.deleteEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.deleteEUMMetaData("test123", deleteEumApps);
	}

	@Test(expected = MongoException.class)
	public void deleteEUMMetaData_exception() throws Exception {
		List<String> deleteEumApps = new ArrayList<String>();
		deleteEumApps.add("emu App");
		Mockito.when(eUMMetaDataDAO.findByApp(any(String.class), any(String.class))).thenThrow(new MongoException(""));
		Mockito.when(eUMMetaDataDAO.deleteEUMApplication(any(EUMMetaData.class))).thenReturn(true);
		operationHandler.deleteEUMMetaData("test123", deleteEumApps);
	}
}