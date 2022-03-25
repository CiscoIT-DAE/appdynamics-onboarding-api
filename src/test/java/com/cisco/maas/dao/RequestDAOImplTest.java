package com.cisco.maas.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.mongodb.MongoException;

public class RequestDAOImplTest {
	@Mock
	MongoTemplate mongoTemplate;
	@Mock
	Document doc;

	@InjectMocks
	RequestDAOImpl requestDAOImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMockCreation() {
		assertNotNull(mongoTemplate);
	}

	@Test
	public void testCreateSuccess() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("123");
		request.setAppGroupID("456");
		request.setId("123");
		request.setLicenseKey("adfghguyijkj123");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("Test");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setOperationalStatus("1");
		request.setRequestCreatedDate("11-11-2020");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers("rpatta");
		requestDetails.setAlertAliases("rgundewa@cisco.com");
		requestDetails.setAppdProjectId("123");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setApmLicenses(10);
		requestDetails.setViewUsers("jjadav");
		requestDetails.setTrackingId("test-1234");
		request.setRequestDetails(requestDetails);
		request.setRequestModifiedDate("21-11-2020");
		request.setRequestStatus("success");
		request.setRequestType("create");
		RetryDetails retryDetails = new RetryDetails();
		request.setRetryDetails(retryDetails);
		Query query = new Query();
		Update update = new Update();
		when(mongoTemplate.upsert(query, update, AppDOnboardingRequest.class)).thenReturn(null);
		when(mongoTemplate.executeCommand(Mockito.anyString())).thenReturn(doc);
		when(doc.getString(Mockito.anyString())).thenReturn(null);
		boolean result = requestDAOImpl.create(request);
		assertTrue(result);
	}

	@Test
	public void testCreateNull() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("123");
		request.setAppGroupID("456");
		request.setId("123");
		request.setLicenseKey("adfghguyijkj123");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("Test");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setOperationalStatus("1");
		request.setRequestCreatedDate("11-11-2020");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers("rpatta");
		requestDetails.setAlertAliases("rgundewa@cisco.com");
		requestDetails.setAppdProjectId("123");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setApmLicenses(10);
		requestDetails.setViewUsers("jjadav");
		requestDetails.setTrackingId("test-1234");
		request.setRequestDetails(requestDetails);
		request.setRequestModifiedDate("21-11-2020");
		request.setRequestStatus("success");
		request.setRequestType("create");
		RetryDetails retryDetails = new RetryDetails();
		request.setRetryDetails(retryDetails);
		String appdExternalId = "123";
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		requestList.add(request);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(requestList);
		boolean result = requestDAOImpl.create(request);
		assertTrue(result);
	}

	@Test(expected = Exception.class)
	public void testCreateException() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("123");
		request.setAppGroupID("456");
		request.setId("123");
		request.setLicenseKey("adfghguyijkj123");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("Test");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setOperationalStatus("1");
		request.setRequestCreatedDate("11-11-2020");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAdminUsers("rpatta");
		requestDetails.setAlertAliases("rgundewa@cisco.com");
		requestDetails.setAppdProjectId("123");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setApmLicenses(10);
		requestDetails.setViewUsers("jjadav");
		requestDetails.setTrackingId("test-1234");
		request.setRequestDetails(requestDetails);
		request.setRequestModifiedDate("21-11-2020");
		request.setRequestStatus("success");
		request.setRequestType("create");
		RetryDetails retryDetails = new RetryDetails();
		request.setRetryDetails(retryDetails);
		when(mongoTemplate.save(request)).thenThrow(new MongoException("Unable to Connect Mongo DB"));
		assertNotNull(requestDAOImpl.create(request));

	}

	@Test
	public void updateRequestTest() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		request.setResourceMove(true);
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("admingroupname");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setAppGroupID("app-123");
		request.setLicenseKey("test-licensekey");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM-n");
		requestDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setRollbackCounter(1);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));

		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateRequest(request);
	}
	@Test
	public void updateRequestTestPending() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("update");
		request.setResourceMove(true);
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mapping.setAdminGroupName("admingroupname");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		request.setAppGroupID("app-123");
		request.setLicenseKey("test-licensekey");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		request.setRequestStatus("pending");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setAddEumpApps(eumApps);
		requestDetails.setDeleteEumpApps(eumApps);
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM-n");
		requestDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setRollbackCounter(1);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));

		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateRequest(request);
	}

	@Test
	public void updateRequestTestNull() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		request.setRequestType("create");
		List<RoleMapping> mappingList = new ArrayList<>();
		RoleMapping mapping = new RoleMapping();
		mappingList.add(mapping);
		RetryDetails rdetails = null;
		request.setRetryDetails(rdetails);
		requestDetails.setAddEumpApps(null);
		requestDetails.setDeleteEumpApps(null);
		requestDetails.setEumApps(null);
		requestDetails.setOldAppGroupName(null);
		requestDetails.setAlertAliases("test@cisco.com");
		request.setRequestDetails(requestDetails);
		request.setRollbackCounter(1);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateRequest(request);
	}

	@Test(expected = Exception.class)
	public void testUpdateRequestException() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class))
				.thenThrow(new MongoException("Unable to connect to MongoDB"));
		assertNull(requestDAOImpl.updateRequest(request));
	}

	@Test
	public void testUpdateRequestFalsePath() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		boolean result = requestDAOImpl.updateRequest(request);
		assertFalse(result);
	}

	@Test
	public void findByOperationalStatusTest() {
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		String status = "ACTIVE";
		Query query = new Query(Criteria.where("operationalStatus").is(status));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(requestList);
		requestDAOImpl.findByOpertionalStatus(status);

	}

	@Test
	public void findByOperationalStatusTest2() {

		String status = "ACTIVE";
		Query query = new Query(Criteria.where("operationalStatus").is(status));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.findByOpertionalStatus(status);

	}

	@Test(expected = MongoException.class)
	public void findByOperationalStatusTestException() throws Exception {
		String status = "ACTIVE";
		Query query = new Query(Criteria.where("operationalStatus").is(status));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenThrow(new MongoException("Unable to Connect"));
		requestDAOImpl.findByOpertionalStatus(status);
	}

	@Test
	public void findFailedRequestsTest() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");

		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setFailureModule("RM_Handler");
		request.setRetryDetails(retryDetails);

		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		requestList.add(request);

		Query query = new Query(
				Criteria.where("retryDetails.failureModule").exists(true).andOperator(Criteria.where("requestStatus")
						.in("failed", "rollbackFailed").andOperator(Criteria.where("retryLock").is(false))));

		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(requestList);

		requestDAOImpl.findFailedRequests();
	}

	@Test(expected = MongoException.class)
	public void findFailedRequestsExceptionTest() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setFailureModule("RM_Handler");
		request.setRetryDetails(retryDetails);
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		requestList.add(request);
		Query query = new Query(
				Criteria.where("retryDetails.failureModule").exists(true).andOperator(Criteria.where("requestStatus")
						.in("failed", "rollbackFailed").andOperator(Criteria.where("retryLock").is(false))));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.findFailedRequests();
	}

	@Test
	public void findFailedRequestsTest2() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setRequestStatus("failed");
		RetryDetails retryDetails = new RetryDetails();
		retryDetails.setFailureModule("RM_Handler");
		request.setRetryDetails(retryDetails);
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		Query query = new Query(
				Criteria.where("retryDetails.failureModule").exists(true).andOperator(Criteria.where("requestStatus")
						.in("failed", "rollbackFailed").andOperator(Criteria.where("retryLock").is(false))));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(requestList);
		requestDAOImpl.findFailedRequests();
	}


	@Test
	public void findByProjectIdAndOpStatusTestNull() throws Exception {
		String appdExternalId = "test123";
		String operationalStatus = "ACTIVE";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("operationalStatus").is(operationalStatus)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.findByProjectIdAndOpStatus(appdExternalId, operationalStatus);
	}
	@Test
	public void findByProjectIdAndOpStatusTest() {
		String appdExternalId = "test123";
		String operationalStatus = "ACTIVE";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("operationalStatus").is(operationalStatus)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.findByProjectIdAndOpStatus(appdExternalId, operationalStatus);
	}

	@Test(expected=MongoException.class)
	public void findByProjectIdAndOpStatusExceptionTest() {
		String appdExternalId = "test123";
		String operationalStatus = "ACTIVE";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("operationalStatus").is(operationalStatus)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.findByProjectIdAndOpStatus(appdExternalId, operationalStatus);
	}
	@Test
	public void findAppByExternalIdTest() {
		String appdExternalId = "test";
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestStatus").in("failed", "pending")));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		boolean res = requestDAOImpl.findAppByExternalId(appdExternalId);
		assertFalse(res);
	}

	@Test
	public void findAppByExternalIdTest1() {
		String appdExternalId = "test";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestStatus").in("failed", "pending")));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.findAppByExternalId(appdExternalId);
	}
	@Test(expected = MongoException.class)
	public void findAppByExternalIdExeptionTest() {
		String appdExternalId = "test";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestStatus").in("failed", "pending")));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.findAppByExternalId(appdExternalId);
	}

	@Test
	public void findByExternalIdAndRequestTypeTest() throws Exception {
		String appdExternalId = "test123";
		String requestType = "create";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("devnet");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestType").is(requestType)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.findByExternalIdAndRequestType(appdExternalId, requestType);
	}

	@Test
	public void findByExternalIdAndRequestTypeNullTest() {
		String appdExternalId = "test123";
		String requestType = "create";
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestType").is(requestType)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.findByExternalIdAndRequestType(appdExternalId, requestType);
	}

	@Test(expected = MongoException.class)
	public void findByExternalIdAndRequestTypeExceptionTest() throws Exception {
		String appdExternalId = "test123";
		String requestType = "create";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("devnet");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId)
				.andOperator(Criteria.where("requestType").is(requestType)));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.findByExternalIdAndRequestType(appdExternalId, requestType);
	}

	@Test
	public void findByAppNameAndRequestTypeTest() {
		String appGroupName = "test123";
		String requestType = "create";
		String ctrlName = "ciscoeft";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Sort sort = new Sort(Sort.Direction.DESC, "requestModifiedDate");
		Query query = new Query(Criteria.where("requestDetails.appGroupName").is(appGroupName)
				.andOperator(Criteria.where("requestDetails.ctrlName").is(ctrlName)
						.andOperator(Criteria.where("requestType").is(requestType)))).with(sort);
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.findByAppNameAndRequestType(appGroupName, ctrlName, requestType);
	}

	@Test(expected = MongoException.class)
	public void findByAppNameAndRequestTypeTest2() {
		String appGroupName = "test123";
		String requestType = "create";
		String ctrlName = "ciscoeft";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Sort sort = new Sort(Sort.Direction.DESC, "requestModifiedDate");
		Query query = new Query(Criteria.where("requestDetails.appGroupName").is(appGroupName)
				.andOperator(Criteria.where("requestDetails.ctrlName").is(ctrlName)
						.andOperator(Criteria.where("requestType").is(requestType)))).with(sort);
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class))
				.thenThrow(new MongoException("Unable to Connect"));
		requestDAOImpl.findByAppNameAndRequestType(appGroupName, ctrlName, requestType);
	}

	@Test
	public void findByAppNameAndRequestTypeNullTest() {
		String appGroupName = "test123";
		String requestType = "create";
		String ctrlName = "ciscoeft";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Sort sort = new Sort(Sort.Direction.DESC, "requestModifiedDate");
		Query query = new Query(Criteria.where("requestDetails.appGroupName").is(appGroupName)
				.andOperator(Criteria.where("requestDetails.ctrlName").is(ctrlName)
						.andOperator(Criteria.where("requestType").is(requestType)))).with(sort);
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.findByAppNameAndRequestType(appGroupName, ctrlName, requestType);
	}

	@Test
	public void updateRetryLockTest() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("4215-126528-saj");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		request.setRetryLock(false);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.updateRetryLock(request);
	}

	@Test
	public void updateRetryLockTestNull() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("4215-126528-saj");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		request.setRetryLock(false);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.updateRetryLock(request);
	}

	@Test(expected = MongoException.class)
	public void updateRetryLockTestException() {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setTrackingId("4215-126528-saj");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		request.setRetryLock(false);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.updateRetryLock(request);
	}

	@Test
	public void findAllByProjectId() {
		String appdExternalId = "test123";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test123");
		request.setRequestStatus("failed");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setTrackingId("4215-126528-saj");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		requestList.add(request);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenReturn(requestList);
		requestDAOImpl.findAllByProjectId(appdExternalId);

	}
	@Test(expected=MongoException.class)
	public void findAllByProjectIdExceptionTest() {
		String appdExternalId = "test123";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		request.setAppdExternalId("test123");
		request.setRequestStatus("failed");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppdProjectId("test123");
		requestDetails.setTrackingId("4215-126528-saj");
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		ArrayList<AppDOnboardingRequest> requestList = new ArrayList<AppDOnboardingRequest>();
		requestList.add(request);
		Query query = new Query(Criteria.where("appdExternalId").is(appdExternalId));
		when(mongoTemplate.find(query, AppDOnboardingRequest.class)).thenThrow(MongoException.class);
		requestDAOImpl.findAllByProjectId(appdExternalId);

	}

	@Test
	public void findByRequestIdTest() {
		String requestId = "test";
		Query query = new Query(Criteria.where("requestDetails.trackingId").is(requestId));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		requestDAOImpl.findByRequestId(requestId);

	}

	@Test
	public void findByRequestId1() {
		String requestId = "test";
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		request.setRequestDetails(requestDetails);
		Query query = new Query(Criteria.where("requestDetails.trackingId").is(requestId));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		requestDAOImpl.findByRequestId(requestId);
	}

	@Test(expected = MongoException.class)
	public void findByRequestId2() {
		String requestId = "test";
		Query query = new Query(Criteria.where("requestDetails.trackingId").is(requestId));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class))
				.thenThrow(new MongoException("Unable to Connect"));
		requestDAOImpl.findByRequestId(requestId);

	}

	@Test
	public void updateCreateRequestTest() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(request);
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateCreateRequest(request);
	}

	@Test
	public void updateCreateRequestTest1() throws Exception {

		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class)).thenReturn(null);
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateCreateRequest(request);
	}

	@Test(expected = MongoException.class)
	public void updateCreateRequestTest2() throws Exception {
		AppDOnboardingRequest request = new AppDOnboardingRequest();
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppGroupName("APMApp");
		requestDetails.setCtrlName("cisco1nonprod");
		RetryDetails rdetails = new RetryDetails();
		request.setRetryDetails(rdetails);
		request.setRequestStatus("success");
		request.setRequestType("update");
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		requestDetails.setEumApps(eumApps);
		requestDetails.setOldAppGroupName("APM");
		requestDetails.setOldEumApps(eumApps);
		requestDetails.setTrackingId("88a3c38c-937a-4ebe");
		request.setRequestDetails(requestDetails);
		Query query = new Query(
				Criteria.where("requestDetails.trackingId").is(request.getRequestDetails().getTrackingId()));
		when(mongoTemplate.findOne(query, AppDOnboardingRequest.class))
				.thenThrow(new MongoException("Unable to Connect"));
		when(mongoTemplate.save(any(AppDOnboardingRequest.class))).thenReturn(request);
		requestDAOImpl.updateCreateRequest(request);
	}

}