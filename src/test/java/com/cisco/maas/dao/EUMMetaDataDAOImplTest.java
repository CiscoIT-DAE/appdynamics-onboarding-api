/*
 *  AppDynamics Onboarding APIs.
 *
 *  Copyright 2022 Cisco
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.maas.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.cisco.maas.dto.EUMMetaData;
import com.mongodb.MongoException;

public class EUMMetaDataDAOImplTest {

	@Mock
	MongoTemplate mongoTemplate;

	@InjectMocks
	EUMMetaDataDAOImpl eUMMetaDataDAOImpl;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);		
	}

	@Test
	public void testCreateEUMApplication() throws Exception
	{	
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test123");
		when(mongoTemplate.save(eumData)).thenReturn(eumData);	
		eUMMetaDataDAOImpl.createEUMApplication(eumData); 
	}

	@Test
	public void testCreateEUMApplication3() throws Exception
	{	
		EUMMetaData eumData = new EUMMetaData();
		eumData.setAppdProjectId("test123");
		when(mongoTemplate.save(eumData)).thenReturn(eumData);	
		eUMMetaDataDAOImpl.createEUMApplication(eumData); 
	}
	
	@Test(expected=MongoException.class)
	public void testCreateEUMApplication2() throws Exception
	{	
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test123");
		when(mongoTemplate.save(eumData)).thenThrow(new MongoException(""));
		eUMMetaDataDAOImpl.createEUMApplication(eumData); 
	}

	@Test
	public void deleteEUMApplication() throws Exception
	{
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test123");

		eUMMetaDataDAOImpl.deleteEUMApplication(eumData);
	}

	@Test(expected=MongoException.class)
	public void deleteEUMApplication2() throws Exception
	{
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test123");
		when(mongoTemplate.remove(any(EUMMetaData.class))).thenThrow(new MongoException("unable to connect"));
		eUMMetaDataDAOImpl.deleteEUMApplication(eumData);
	}

	@Test
	public void findByAppTest() throws Exception
	{
		String appdProjectId="test";
		String eumName ="test";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test");
		eumData.setEumName("test");
		Query query = new Query(Criteria.where("appdProjectId").is("test").andOperator(Criteria.where("eumName").is("test")));		
		Mockito.when(mongoTemplate.findOne(query,EUMMetaData.class)).thenReturn(eumData);
		eUMMetaDataDAOImpl.findByApp(appdProjectId, eumName);
	}
	@Test(expected=MongoException.class)
	public void findByAppTest2() throws Exception
	{
		String appdProjectId="test";
		String eumName ="test";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test");
		eumData.setEumName("test");
		Query query = new Query(Criteria.where("appdProjectId").is("test").andOperator(Criteria.where("eumName").is("test")));		
		Mockito.when(mongoTemplate.findOne(query,EUMMetaData.class)).thenThrow(new MongoException("unable to connect"));
		eUMMetaDataDAOImpl.findByApp(appdProjectId, eumName);
	}

	@Test(expected=MongoException.class)
	public void findByProjectId2() throws Exception
	{
		String appdProjectId="test";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test");
		eumData.setEumName("test");
		ArrayList<EUMMetaData> eumList=new ArrayList<EUMMetaData>();
		eumList.add(eumData);
		Query query = new Query(Criteria.where("appdProjectId").is("test"));		
		Mockito.when(mongoTemplate.find(query,EUMMetaData.class)).thenThrow(new MongoException("unable to connect"));
		eUMMetaDataDAOImpl.findByProjectID(appdProjectId);
	}
	@Test
	public void findByProjectId() throws Exception
	{
		String appdProjectId="test";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test");
		eumData.setEumName("test");
		ArrayList<EUMMetaData> eumList=new ArrayList<EUMMetaData>();
		eumList.add(eumData);
		Query query = new Query(Criteria.where("appdProjectId").is("test"));		
		Mockito.when(mongoTemplate.find(query,EUMMetaData.class)).thenReturn(eumList);
		eUMMetaDataDAOImpl.findByProjectID(appdProjectId);
	}
	
	@Test
	public void resourceMoveUpdateEUM() throws Exception
	{
		String appdProjectId="test";
		String oldEUMName="Test-EUM";
		String newEUMName="Test-New-EUM";
		EUMMetaData eumData = new EUMMetaData();
		eumData.setId("test");
		eumData.setAppdProjectId("test");
		eumData.setEumName("test");
		Query query = new Query(Criteria.where("appdProjectId").is(appdProjectId).andOperator(Criteria.where("eumName").is(oldEUMName)));
		Mockito.when(mongoTemplate.findOne(query,EUMMetaData.class)).thenReturn(eumData);
		when(mongoTemplate.save(eumData)).thenReturn(eumData);	
		eUMMetaDataDAOImpl.resourceMoveUpdateEUM(appdProjectId, oldEUMName, newEUMName);
		
	}
}
