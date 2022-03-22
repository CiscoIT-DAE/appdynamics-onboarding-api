package com.cisco.maas.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.cisco.maas.dto.RoleMapping;
import com.mongodb.MongoException;

public class RoleMappingDAOImplTest {
	@Mock
	MongoTemplate mongoTemplate;

	@InjectMocks
	RoleMappingDAOImpl RoleMappingDAOImpl;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);		
	}

	@Test
	public void testMockCreation(){	        
		assertNotNull(mongoTemplate);
	}

	@Test
	public void testCreate() throws Exception
	{	

		RoleMapping request=new RoleMapping();
		request.setId("567089003");
		request.setAppGroupName("Aditya APM Test");
		request.setCtrlName("prod");
		request.setAdminGroupName("AppD-Aditya-APM-Test-pa");
		request.setViewGroupName("AppD-Aditya-APM-Test-pv");			
		when(mongoTemplate.save(request)).thenReturn(request);	
		boolean result= RoleMappingDAOImpl.create(request);       
		assertTrue(result);
	} 

	@Test(expected=Exception.class)
	public void testCreate2() throws Exception
	{		
		RoleMapping request=new RoleMapping();
		request.setId("567089003");
		request.setAppGroupName("Aditya APM Test");
		request.setCtrlName("prod");
		request.setAdminGroupName("AppD-Aditya-APM-Test-pa");
		request.setViewGroupName("AppD-Aditya-APM-Test-pv");			
		when(mongoTemplate.save(request)).thenThrow(new MongoException("Exception Connecting to Mongo DB"));	
		assertNotNull(RoleMappingDAOImpl.create(request));  
	} 

	@Test
	public void testCreateFalsePath() throws Exception
	{	
		RoleMapping request=new RoleMapping();
		request.setId(null);
		request.setAppGroupName("Aditya APM Test");
		request.setCtrlName("prod");
		request.setAdminGroupName("AppD-Aditya-APM-Test-pa");
		request.setViewGroupName("AppD-Aditya-APM-Test-pv");		

		when(mongoTemplate.save(request)).thenReturn(request);	
		boolean result= RoleMappingDAOImpl.create(request);       
		assertFalse(result);
	} 

	@Test
	public void findByAppTest() throws Exception
	{	

		String appGroupName="Application Performance Management";			
		String ctrlName="prod";	
		List<RoleMapping> list=new LinkedList<RoleMapping>();
		Query query = new Query(Criteria.where("appGroupName").is(appGroupName).andOperator(Criteria.where("environment").is(ctrlName)));			
		when(mongoTemplate.find(query, RoleMapping.class)).thenReturn(list);
		Iterable<RoleMapping> requestList = RoleMappingDAOImpl.findByApp(appGroupName, ctrlName);       
		assertEquals(list, requestList);				
	}

	@Test
	public void findAppTest() throws Exception
	{	

		String appGroupName="Application Performance Management";			
		String ctrlName="prod";	
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		Query query = new Query(Criteria.where("appGroupName").is(appGroupName).andOperator(Criteria.where("ctrlName").is(ctrlName)));			
		when(mongoTemplate.findOne(query, RoleMapping.class)).thenReturn(roleMap);
		RoleMappingDAOImpl.findApp(appGroupName, ctrlName);       
					
	}
	
	@Test
	public void findByAppTest2() throws Exception
	{	

		String appGroupName="Application Performance Management";			
		String ctrlName="prod";				
		Query query = new Query(Criteria.where("appGroupName").is(appGroupName).andOperator(Criteria.where("environment").is(ctrlName)));			
		when(mongoTemplate.find(query, RoleMapping.class)).thenReturn(null);
		RoleMappingDAOImpl.findByApp(appGroupName, ctrlName); 						
	}	

	@Test(expected=Exception.class)
	public void findByAppTest3() throws Exception
	{ 	 				
		String appGroupName="Application Performance Management";			
		String ctrlName="cisco1nonprod";				
		Query query = new Query(Criteria.where("appGroupName").is(appGroupName).andOperator(Criteria.where("ctrlName").is(ctrlName)));			
		when(mongoTemplate.find(query, RoleMapping.class)).thenThrow(new MongoException("Unable to Connect to MongDB"));
		RoleMappingDAOImpl.findByApp(appGroupName, ctrlName);        	
	}
	
	@Test(expected=Exception.class)
	public void findAppTest2() throws Exception
	{	
		String appGroupName="Application Performance Management";			
		String ctrlName="prod";	
		Query query = new Query(Criteria.where("appGroupName").is(appGroupName).andOperator(Criteria.where("ctrlName").is(ctrlName)));			
		when(mongoTemplate.findOne(query, RoleMapping.class)).thenThrow(new MongoException("Unable to Connect to MongDB"));
		RoleMappingDAOImpl.findApp(appGroupName, ctrlName);       
					
	}
	
	@Test
	public void deleteTest() throws Exception
	{
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		RoleMappingDAOImpl.delete(roleMap);
	}
	
	@Test(expected=MongoException.class)
	public void deleteTest1() throws Exception
	{
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		when(mongoTemplate.remove(any(RoleMapping.class))).thenThrow(new MongoException("unable to connect"));
		RoleMappingDAOImpl.delete(roleMap);
	}
	
	@Test
	public void resourceMoveUpdateTest() throws Exception
	{
		String oldAppName ="Application Performance Management";
		String newAppName ="Application Performance Management - New";	
		String ctrlName="ciscoeft";	
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		Query query = new Query(Criteria.where("appGroupName").is(oldAppName).andOperator(Criteria.where("ctrlName").is(ctrlName)));
		when(mongoTemplate.findOne(query, RoleMapping.class)).thenReturn(roleMap);
		RoleMappingDAOImpl.resourceMoveUpdate(oldAppName, newAppName, ctrlName);
	}
	@Test
	public void resourceMoveUpdateTest1() throws Exception
	{
		String oldAppName ="Application Performance Management";
		String newAppName ="Application Performance Management - New";	
		String ctrlName="ciscoeft";	
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setId("12346");
		Query query = new Query(Criteria.where("appGroupName").is(oldAppName).andOperator(Criteria.where("ctrlName").is(ctrlName)));
		when(mongoTemplate.findOne(query, RoleMapping.class)).thenReturn(roleMap);
		RoleMappingDAOImpl.resourceMoveUpdate(oldAppName, newAppName, ctrlName);
	}
	
	@Test(expected=MongoException.class)
	public void resourceMoveUpdateTest2() throws Exception
	{
		String oldAppName ="Application Performance Management";
		String newAppName ="Application Performance Management - New";	
		String ctrlName="ciscoeft";	
		RoleMapping roleMap=new RoleMapping();
		roleMap.setAppGroupName("test");
		roleMap.setId("12346");
		Query query = new Query(Criteria.where("appGroupName").is(oldAppName).andOperator(Criteria.where("ctrlName").is(ctrlName)));
		when(mongoTemplate.findOne(query, RoleMapping.class)).thenThrow(new MongoException("unable to connect"));
		RoleMappingDAOImpl.resourceMoveUpdate(oldAppName, newAppName, ctrlName);
	}
}
