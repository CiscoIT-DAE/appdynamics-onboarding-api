package com.cisco.maas.dto;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class LicenseRuleTest {
	@InjectMocks
	LicenseRule rule;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		rule.setAccessKey("Test");
		rule.setJson(new JSONObject("{\"Test\":\"test\"}"));
		rule.setLicenseRuleName("Test Lic");
		rule.setNoOfApmLicenses("1");
		rule.setNoOfMALicenses("2");
		rule.getAccessKey();
		rule.getJson();
		rule.getLicenseRuleName();
		rule.getNoOfApmLicenses();
		rule.getNoOfMALicenses();
		rule.toString();
	}

}
