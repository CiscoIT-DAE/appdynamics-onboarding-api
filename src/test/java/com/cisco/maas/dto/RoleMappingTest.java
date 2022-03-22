package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class RoleMappingTest {
	@InjectMocks
	RoleMapping mapping;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		mapping.setAdminGroupName("Test");
		mapping.setAppGroupName("Test");
		mapping.setCtrlName("Test");
		mapping.setId("1");
		mapping.setViewGroupName("Test");
		mapping.toString();
		mapping.getAdminGroupName();
		mapping.getAppGroupName();
		mapping.getCtrlName();
		mapping.getId();
		mapping.getViewGroupName();
	}

}
