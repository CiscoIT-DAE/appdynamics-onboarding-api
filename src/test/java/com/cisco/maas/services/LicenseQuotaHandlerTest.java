package com.cisco.maas.services;

import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cisco.maas.util.AppDynamicsUtil;

public class LicenseQuotaHandlerTest {

	@InjectMocks
	LicenseQuotaHandler licenseQuotaHandler;

	@Mock
	AppDynamicsUtil appdUtil;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getLicenseUsageTest() throws Exception {
		String response = "{\"usages\": [ {\"id\": \"0\",   \"maxUnitsUsed\": 3930,\"avgUnitsProvisioned\": 14339} ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);
		licenseQuotaHandler.getLicenseUsage("ciscoeft");
	}

	@Test
	public void getLicenseUsageTest1() throws Exception {
		String response = "{\"usages\": [ {\"id\": \"0\",   \"maxUnitsUsed\": 3930,\"avgUnitsProvisioned\": 14339} ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);
		licenseQuotaHandler.getLicenseUsage("cisco1");
	}

	@Test
	public void getLicenseUsageTest2() throws Exception {
		String response = "{\"usages\": [ {\"id\": \"0\",   \"maxUnitsUsed\": 3930,\"avgUnitsProvisioned\": 14339} ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class), any(String.class), any(String.class),
				any(String.class))).thenReturn(response);
		licenseQuotaHandler.getLicenseUsage("cisco1nonprod");
	}
}
