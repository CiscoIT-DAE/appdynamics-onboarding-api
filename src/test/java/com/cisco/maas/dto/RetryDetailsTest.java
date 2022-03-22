package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class RetryDetailsTest {
	@InjectMocks
	RetryDetails rDetails;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		List<RoleMapping> mappingList = new ArrayList<>();
		List<String> mappingList1 = new ArrayList<>();
		rDetails.setFailureModule("Test");
		rDetails.getFailureModule();
		rDetails.setOperationCounter(1);
		rDetails.getOperationCounter();
		rDetails.setPendingList(mappingList1);
		rDetails.getPendingList();
		rDetails.setLastRetryTime("test");
		rDetails.getLastRetryTime();
		rDetails.setMappingList(mappingList);
		rDetails.getMappingList();
		rDetails.setAdminGroupRollback("test");
		rDetails.setDbFlagRollback("test");
		rDetails.getAdminGroupRollback();
		rDetails.getDbFlagRollback();
		rDetails.getPendingList();
		RoleMapping mapping = new RoleMapping();
		rDetails.setMapping(mapping);
		rDetails.getMapping();
		rDetails.setAppDApplicationID("1234");
		rDetails.getAppDApplicationID();
		rDetails.setTargetApplicationID("12345");
		rDetails.getTargetApplicationID();
		rDetails.setAdminUsers("test");
		rDetails.getAdminUsers();
		rDetails.setViewUsers("Test");
		rDetails.getViewUsers();
		rDetails.toString();

	}

}
