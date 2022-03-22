package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class UserDetailTest {

	@InjectMocks
	UserDetail userDetail;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		userDetail.setId(1);
		userDetail.setName("rgundewa");
		userDetail.getId();
		userDetail.getName();
		userDetail.toString();
	}
}
