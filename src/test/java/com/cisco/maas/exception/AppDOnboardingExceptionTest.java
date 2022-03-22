package com.cisco.maas.exception;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class AppDOnboardingExceptionTest {

	@InjectMocks
	AppDOnboardingException appDOnboardingException;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testException() {
		appDOnboardingException.getActualMessage();
		appDOnboardingException.getReqAction();
		appDOnboardingException.getRequest();
		appDOnboardingException.getError();
	}

}
