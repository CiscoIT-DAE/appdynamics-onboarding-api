package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class ViewAppDResponseTest {
	@InjectMocks
	ViewAppdynamicsResponse viewAppdynamicsResponse;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		ViewAppdynamicsResponse request = new ViewAppdynamicsResponse();
		request.setAdminRoleName("Test");
		request.setViewRoleName("Test1");
		request.setOperation("Test");
		request.setStatus("Test");
		request.setLicenseKey("test");
		request.getAdminRoleName();
		request.getViewRoleName();
		request.getOperation();
		request.getStatus();
		request.getLicenseKey();

	}

}
