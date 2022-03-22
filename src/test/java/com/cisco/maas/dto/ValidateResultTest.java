package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class ValidateResultTest {
	@InjectMocks
	ValidateResult validateResult;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		AppDError t1 = new AppDError();
		HttpStatus responseCode = HttpStatus.CONFLICT;
		validateResult.setValidateResultStatus("success");
		validateResult.setErrorObject(t1);
		validateResult.setResponseCode(responseCode);
		validateResult.setResourceMoveFlag(false);
		validateResult.getErrorObject();
		validateResult.getResponseCode();
		validateResult.getValidateResultStatus();
		validateResult.toString();
		validateResult.isResourceMoveFlag();
	}
}
