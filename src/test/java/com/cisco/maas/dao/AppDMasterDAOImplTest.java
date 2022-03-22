package com.cisco.maas.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.cisco.maas.dto.APPDMaster;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.mongodb.MongoException;

public class AppDMasterDAOImplTest {
	@Mock
	MongoTemplate mongoTemplate;

	@InjectMocks
	@Spy
	APPDMasterDAOImpl appDMasterDAOImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMockCreation() {
		assertNotNull(mongoTemplate);
	}

	@Test
	public void createApplicationTest() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");
		appdDetails.setId("12345");
		when(mongoTemplate.save(appdDetails)).thenReturn(appdDetails);
		boolean result = appDMasterDAOImpl.createApplication(appdDetails);
		assertTrue(result);
	}

	@Test
	public void createTestApplication2() throws Exception {

		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		when(mongoTemplate.save(appdDetails)).thenReturn(appdDetails);
		boolean result = appDMasterDAOImpl.createApplication(appdDetails);
		assertFalse(result);
	}

	@Test(expected = Exception.class)
	public void createApplicationTestException() throws Exception {

		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		when(mongoTemplate.save(appdDetails)).thenThrow(new MongoException("Unable to Connect to MongoDB"));
		assertNotNull(appDMasterDAOImpl.createApplication(appdDetails));
	}

	@Test
	public void findAllTest() throws Exception {

		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		ArrayList<APPDMaster> requestList = new ArrayList<APPDMaster>();
		requestList.add(appdDetails);
		when(mongoTemplate.findAll(APPDMaster.class)).thenReturn(requestList);
		assertNotNull(appDMasterDAOImpl.findAll());
	}

	@Test
	public void findAllTest2() throws Exception {
		when(mongoTemplate.findAll(APPDMaster.class)).thenReturn(null);
		assertNull(appDMasterDAOImpl.findAll());
	}

	@Test(expected = Exception.class)
	public void findAllTestException() throws Exception {
		when(mongoTemplate.findAll(APPDMaster.class)).thenThrow(new MongoException("Unable to Connect MongoDB"));
		assertNull(appDMasterDAOImpl.findAll());
	}

	@Test
	public void findByAppTest() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		Query query = new Query(Criteria.where("appGroupName").is("Test-AppGroupName")
				.andOperator(Criteria.where("ctrlName").is("cisco1")));

		Mockito.when(mongoTemplate.findOne(query, APPDMaster.class)).thenReturn(appdDetails);

		assertNotNull(appDMasterDAOImpl.findByApp("Test-AppGroupName", "cisco1"));
	}

	@Test(expected = MongoException.class)
	public void findByAppTest2() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		Query query = new Query(Criteria.where("appGroupName").is("Test-AppGroupName")
				.andOperator(Criteria.where("ctrlName").is("cisco1")));

		Mockito.when(mongoTemplate.findOne(query, APPDMaster.class)).thenThrow(new MongoException("unable to connect"));

		assertNotNull(appDMasterDAOImpl.findByApp("Test-AppGroupName", "cisco1"));
	}

	@Test
	public void getLicenseKeyTest() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");

		String appGroupName = "Test-AppGroupName";
		String ctrlName = "cisco1";

		Query query = new Query(Criteria.where("appGroupName").is("Test-AppGroupName")
				.andOperator(Criteria.where("ctrlName").is("cisco1")));
		Mockito.when(mongoTemplate.findOne(query, APPDMaster.class)).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.getLicenseKey(appGroupName, ctrlName));
	}

	@Test
	public void getApmLicensesTest() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setAppGroupName("Test-AppGroupName");
		appdDetails.setCtrlName("cisco1");
		appdDetails.setLicenseKey("Test License Key");
		appdDetails.setApmLicenses(5);
		String appGroupName = "Test-AppGroupName";
		String ctrlName = "cisco1";
		Query query = new Query(Criteria.where("appGroupName").is("Test-AppGroupName")
				.andOperator(Criteria.where("ctrlName").is("cisco1")));
		Mockito.when(mongoTemplate.findOne(query, APPDMaster.class)).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.getApmLicenses(appGroupName, ctrlName));
	}

	@Test
	public void updateApplication() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test
	public void updateApplication2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		requestDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test
	public void updateApplication3() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test
	public void updateApplication4() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test
	public void updateApplication5() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test(expected = MongoException.class)
	public void updateApplication6() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		Mockito.doThrow(new com.mongodb.MongoException("MongoException")).when(appDMasterDAOImpl)
				.findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		assertNotNull(appDMasterDAOImpl.updateApplication(request));
	}

	@Test
	public void deleteApplicationTest() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		assertNotNull(appDMasterDAOImpl.deleteApplication(appdDetails));
	}

	@Test(expected = MongoException.class)
	public void deleteApplicationTest2() throws Exception {
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		when(mongoTemplate.remove(any(APPDMaster.class))).thenThrow(new MongoException("unable to connect"));
		appDMasterDAOImpl.deleteApplication(appdDetails);
	}

	@Test
	public void updateEUMLicenseTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("Application Performance Management");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setNoOfEUMLicenses(2);
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		appDMasterDAOImpl.updateEUMLicense("Application Performance Management", "cisco1nonprod", 2);
	}

	@Test
	public void updateNewAppNameTest() throws Exception {
		String appName = "Application Performance Management";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);

		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		appDMasterDAOImpl.updateNewAppName(request, appName);
	}

	@Test
	public void updateNewAppNameTest1() throws Exception {
		String appName = "Application Performance Management";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenReturn(appdDetails);
		appDMasterDAOImpl.updateNewAppName(request, appName);
	}

	@Test(expected = MongoException.class)
	public void updateNewAppNameTest2() throws Exception {
		String appName = "Application Performance Management";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setApmLicenses(1);
		requestDetails.setAlertAliases("test@cisco.com");
		requestDetails.setAppGroupName("test");
		requestDetails.setCtrlName("test");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("emu App");
		requestDetails.setEumApps(eumApps);
		request.setRequestDetails(requestDetails);
		APPDMaster appdDetails = new APPDMaster();
		appdDetails.setId("12345");
		Mockito.doReturn(appdDetails).when(appDMasterDAOImpl).findByApp(any(String.class), any(String.class));
		when(mongoTemplate.save(any(APPDMaster.class))).thenThrow(new MongoException("unable to connect"));
		appDMasterDAOImpl.updateNewAppName(request, appName);
	}
}