package com.cisco.maas.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class APPDMasterTest {
	@InjectMocks
	APPDMaster appdmaster;
	
	@Before
	 public void setup()
	 {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void allPojoTest()
	{
		appdmaster.getAlertAliases();
		appdmaster.getAppGroupId();
		appdmaster.getAppGroupName();
		appdmaster.getCtrlName();
		appdmaster.getEumApps();
		appdmaster.getId();
		appdmaster.getLicenseKey();
		appdmaster.getNoOfEUMLicenses();
		appdmaster.getApmLicenses();
	
		List<String> eumApps = new ArrayList<String>();
		eumApps.add("EUM1");
		appdmaster.setAlertAliases("achavali@cisco.com");
		appdmaster.setAppGroupId(12345);
		appdmaster.setAppGroupName("APMApp");
		appdmaster.setCtrlName("cisco1nonprod");
		appdmaster.setEumApps(eumApps);
		appdmaster.setId("123");
		appdmaster.setLicenseKey("qwunj458i8jkvnv");
		
		appdmaster.setNoOfEUMLicenses(5);
		appdmaster.setApmLicenses(5);
		appdmaster.toString();
	}
}
