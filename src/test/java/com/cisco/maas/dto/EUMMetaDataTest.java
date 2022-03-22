package com.cisco.maas.dto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class EUMMetaDataTest {

	@InjectMocks
	EUMMetaData eUMMetaData;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void allPojoTest() {
		eUMMetaData.setAppdProjectId("test");
		eUMMetaData.setEumCreatedDate("test");
		eUMMetaData.setEumName("testApp");
		eUMMetaData.setId("1");
		eUMMetaData.getAppdProjectId();
		eUMMetaData.getEumCreatedDate();
		eUMMetaData.getEumName();
		eUMMetaData.getId();
		eUMMetaData.toString();
	}
}
