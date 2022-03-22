package com.cisco.maas.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.cisco.maas.dao.RequestDAO;
import com.cisco.maas.dto.AppDOnboardingRequest;
import com.cisco.maas.dto.RequestDetails;
import com.cisco.maas.dto.RetryDetails;
import com.cisco.maas.dto.RoleMapping;
import com.cisco.maas.dto.UserDetail;
import com.cisco.maas.util.AppDynamicsUtil;
@SuppressWarnings("unchecked")
public class AppDRoleManagerTest {

	@InjectMocks
	@Spy
	AppDRoleManager roleManager;

	@Mock 
	AppDynamicsUtil appdUtil;
	@Mock
	RequestDAO mcmpRequestDao;
	@Before
	public void setup()
	{		
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void prepareAdminJSONTest2() throws Exception
	{	List<String> eumIDList =new ArrayList<>();
	eumIDList.add("1");
	Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("");
	assertNotNull(roleManager.prepareAdminJSON("AppD-GATS-3DIT-Orchestration-na","207",eumIDList));
	}

	@Test
	public void prepareViewJSONTest2() throws Exception
	{		
		List<String> eumIDList =new ArrayList<>();
		eumIDList.add("1");
		Mockito.when(appdUtil.getRequest(any(String.class))).thenReturn("");
		assertNotNull(roleManager.prepareViewJSON("AppD-GATS-3DIT-Orchestration-nv","207",eumIDList));
	}
	@Test
	public <T> void createRoleTest() throws Exception
	{   
		List<RoleMapping> mappingList =new ArrayList<>();
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RoleMapping mapping=new RoleMapping();
		mapping.setAdminGroupName("AppD-GATS-3DIT-Orchestration-na");
		mapping.setViewGroupName("AppD-GATS-3DIT-Orchestration-nv");
		mappingList.add(mapping);
		request.setMapping(mappingList);
		RetryDetails rDetails=new RetryDetails();
		rDetails.setOperationCounter(1);
		rDetails.setFailureModule("RM_HANDLER");
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setCtrlName("cisco1nonprod");
		requestDetails.setAdminUsers("test,test1");
		requestDetails.setViewUsers("test,test1");
		request.setAppGroupID("207");		
		request.setRequestDetails(requestDetails);
		request.setRetryDetails(rDetails);
		request.setRequestType("create");
		Mockito.doReturn(mappingList).when(roleManager).getAllUser(any(String.class));
		String response="{\"id\":211,\"name\":\"AppD-GATS-3DIT-Orchestration-na\",\"description\":\"This is Admin Role for App APM-Java-Template\"}";
		Mockito.when(appdUtil.appDConnection(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(response);
		Mockito.doReturn(null).when(roleManager).checkIfRoleIsPresent(any(String.class),any(String.class));
		Mockito.doReturn("{test}").when(roleManager).prepareAdminJSON(any(String.class),any(String.class),any(List.class));
		Mockito.doReturn("{test}").when(roleManager).prepareViewJSON(any(String.class),any(String.class),any(List.class));
		Mockito.doReturn(true).when(roleManager).addRoleToUser(any(String.class),any(String.class),any(List.class),any(List.class));	
		roleManager.createRole(request);
	}
	
	@Test
	public void handleRequestTest() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		request.setRequestType("create");
		RetryDetails rDetails =new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doReturn(true).when(roleManager).createRole(any(AppDOnboardingRequest.class)); 
		Mockito.doNothing().when(roleManager).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(roleManager).handleRequestImpl(any(AppDOnboardingRequest.class));
		roleManager.handleRequest(request);
	}

	@Test
	public void handleRequestTest1() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		request.setRequestType("update");
		RetryDetails rDetails =new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		List<String> eumList = new ArrayList<>();
		eumList.add("Test1");
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setEumApps(eumList);
		requestDetails.setAddEumpApps(eumList);
		requestDetails.setAdminUsers("admin");
		requestDetails.setViewUsers("view");
		List<RoleMapping> roleData = new ArrayList<RoleMapping>();
		RoleMapping rmapping = new RoleMapping();
		rmapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		rmapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		roleData.add(rmapping);
		request.setMapping(roleData);
		request.setRequestDetails(requestDetails);
		Mockito.doReturn(true).when(roleManager).createRole(any(AppDOnboardingRequest.class)); 
		Mockito.doNothing().when(roleManager).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(roleManager).handleRequestImpl(any(AppDOnboardingRequest.class));
		roleManager.handleRequest(request);
	}

	@Test
	public void handleRequestTest2() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		request.setRequestType("delete");
		RetryDetails rDetails =new RetryDetails();
		rDetails.setOperationCounter(1);
		request.setRetryDetails(rDetails);
		Mockito.doNothing().when(roleManager).setNextHandler(any(AppDOnboardingRequestHandlerImpl.class));
		Mockito.doNothing().when(roleManager).handleRequestImpl(any(AppDOnboardingRequest.class));
		roleManager.handleRequest(request);
	}

	@Test
	public void getEUMApplicationIDsTest() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		request.setRequestType("update");
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setAppGroupName("Test11");
		requestDetails.setCtrlName("cisco1nonprod");
		List<String> EUMapps = new ArrayList<>();
		EUMapps.add("Test2");
		requestDetails.setAddEumpApps(EUMapps);
		request.setRequestDetails(requestDetails);
		String content ="[{\"name\": \"Test2\",\"id\": 2812351,}]";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
		roleManager.getEUMApplicationIDs(request);

	}

	@Test
	public void getEUMApplicationIDsTest1() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		request.setRequestType("create");
		RequestDetails requestDetails=new RequestDetails();
		requestDetails.setAppGroupName("Test11");
		requestDetails.setCtrlName("cisco1nonprod"); 
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		requestDetails.setEumApps(addEUMapps);
		request.setRequestDetails(requestDetails);
		String content ="[{\"name\": \"Test1\",\"id\": 2812350,}]";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(content);
		roleManager.getEUMApplicationIDs(request);
	}

	@Test
	public void getRolesTest()
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RequestDetails requestDetails=new RequestDetails();
		request.setRequestType("update");
		List<String> addEUMapps = new ArrayList<>();
		addEUMapps.add("Test1");
		requestDetails.setAddEumpApps(addEUMapps);
		request.setRequestDetails(requestDetails);
		RoleMapping roleMap = new RoleMapping();
		roleMap.setAppGroupName("Test1");
		roleMap.setAdminGroupName("tesrole");
		roleMap.setViewGroupName("testrole");
		List<RoleMapping> mappingList = new ArrayList<>();
		mappingList.add(roleMap);
		request.setMapping(mappingList);
		roleManager.getRoles(request);
	}

	@Test
	public void getRolesTest1() throws Exception
	{
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		List<RoleMapping> roleData = new ArrayList<RoleMapping>();
		RoleMapping rmapping = new RoleMapping();
		rmapping.setAppGroupName("APMAPP");
		rmapping.setAdminGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na");
		rmapping.setViewGroupName("AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-nv");
		roleData.add(rmapping);
		request.setMapping(roleData);
		request.setRequestType("create");
		assertNotNull(roleManager.getRoles(request));
	}

	@Test
	public void checkIfRoleIsPresentTest() throws Exception
	{
		String json="{\"id\":606,\"name\":\"AppD-0e387be4-c4fd-31c9-9ed2-787d5a264846-na\",\"description\":\"This is Admin Role for App APM-Java-Template\"}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(json);
		roleManager.checkIfRoleIsPresent("test", "ciscoeft");
	}

	@Test
	public void getAllUser() throws Exception
	{
		String json = "{ \"users\": [ {\"id\": 6412, \"name\": \"rgundewa\"}, { \"id\": 2061, \"name\": \"rpatta\" } ]}";
		Mockito.when(appdUtil.appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(json);
		roleManager.getAllUser("ciscoeft"); 
	}

	@Test
	public void removeRoleFromUser() throws Exception
	{
		UserDetail userDetail= new UserDetail();
		userDetail.setId(1);
		userDetail.setName("rgundewa");
		List<UserDetail> userList= new ArrayList<>();
		userList.add(userDetail);
		List<String> usernameList= new ArrayList<>();
		usernameList.add("rgundewa");
		List<Integer> idList = new ArrayList<>();
		idList.add(126);
		Mockito.doReturn(idList).when(roleManager).getUserId(any(List.class),any(List.class));
		String response= "Done";
		when(appdUtil.appDConnectionDelete(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(response);
		roleManager.removeRoleFromUser("AppD-testRole-na", "124", "ciscoeft", userList, usernameList);
	}

	@Test
	public void getUserId() throws Exception
	{
		UserDetail userDetail= new UserDetail();
		userDetail.setId(1);
		userDetail.setName("rgundewa");
		List<UserDetail> userList= new ArrayList<>();
		userList.add(userDetail);
		List<String> usernameList= new ArrayList<>();
		usernameList.add("rgundewa");
		roleManager.getUserId(userList, usernameList);
	}
	
	@Test
	public void getRoleIdTest() throws Exception
	{
		Mockito.doReturn("{id:test}").when(appdUtil).appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class));
		roleManager.getRoleId("test","test");
	}
	
	@Test
	public void getRoleIdTest1() throws Exception
	{
		Mockito.doReturn(null).when(appdUtil).appDConnectionOnlyGet(any(String.class),any(String.class),any(String.class),any(String.class));
		roleManager.getRoleId("test","test");
	}
	
	@Test
	public void addRoleToUserTest() throws Exception
	{   
		UserDetail userDetail= new UserDetail();
		userDetail.setId(1);
		userDetail.setName("knallapu");
		List<UserDetail> userList= new ArrayList<>();
		userList.add(userDetail);

		List<String> membersList= new ArrayList<>();
		membersList.add("knallapu");
		membersList.add("skotichi");
		
		Mockito.doReturn("d1d8a64b-ca2b-3301-8389-77acd631fedf").when(roleManager).getRoleId(any(String.class),any(String.class));
		roleManager.addRoleToUser("test","test",userList,membersList);
	}
	@Test
	public void addRoleToUserTest1() throws Exception
	{   
		UserDetail userDetail= new UserDetail();
		userDetail.setId(1);
		userDetail.setName("knallapu");
		List<UserDetail> userList= new ArrayList<>();
		userList.add(userDetail);
		List<String> membersList= new ArrayList<>();
		membersList.add("knallapu");
		membersList.add("skotichi");
		Mockito.doReturn(null).when(roleManager).getRoleId(any(String.class),any(String.class));
		roleManager.addRoleToUser("test","test",userList,membersList);
	}
	
	@Test
	public void prepareEUMViewJsonTest() throws Exception
	{   
		String appJSON = "[test]";
		List<String> membersList= new ArrayList<>();
		membersList.add("knallapu");
		membersList.add("skotichi");
		Mockito.doReturn(null).when(roleManager).getRoleId(any(String.class),any(String.class));
		roleManager.prepareEUMViewJson(appJSON,membersList);
	}
	
	@Test
	public void prepareEUMAdminJsonTest() throws Exception
	{   
		String appJSON = "[test]";
		List<String> membersList= new ArrayList<>();
		membersList.add("knallapu");
		membersList.add("skotichi");
		Mockito.doReturn(null).when(roleManager).getRoleId(any(String.class),any(String.class));
		roleManager.prepareEUMAdminJson(appJSON,membersList);
	}
	
	@Test
	public <T> void validateUserResultTest2() throws Exception
	{  
		AppDOnboardingRequest request=new AppDOnboardingRequest();
		RetryDetails rtryDetails = new RetryDetails();		
		RoleMapping mapping=new RoleMapping();
		mapping.setAdminGroupName("AppD-GATS-3DIT-Orchestration-na");
		mapping.setViewGroupName("AppD-GATS-3DIT-Orchestration-nv");
		rtryDetails.setMapping(mapping);
		request.setRetryDetails(rtryDetails);
		AppDRoleManager appdrole = new AppDRoleManager();
		Class<?>[] paramsTypes = new Class<?>[] {boolean.class, AppDOnboardingRequest.class, String.class};
		Method calculateMethod = AppDRoleManager.class.getDeclaredMethod("validateUserResult",paramsTypes);
		calculateMethod.setAccessible(true);
		calculateMethod.invoke(appdrole,true,request,"admin");
	}
	
    @Test
    public void updateRolesToUsersTest() throws Exception
    {
    	List<String> usersOfLastRequest =new ArrayList<>();
    	List<String> currentRequest =new ArrayList<>();
    	AppDOnboardingRequest tempRequest=new AppDOnboardingRequest();
    	String userType = "admin";
    	roleManager.updateRolesToUsers(usersOfLastRequest,currentRequest,tempRequest,userType); 	
    	
    }
    
    @Test
    public void updateRolesToUsersTest1() throws Exception
    {
    	List<String> usersOfLastRequest =new ArrayList<>();
    	List<String> currentRequest =new ArrayList<>();
    	AppDOnboardingRequest tempRequest=new AppDOnboardingRequest();
    	String userType = "view";
    	roleManager.updateRolesToUsers(usersOfLastRequest,currentRequest,tempRequest,userType);   	
    	
    }
    
    @Test
    public void updateRolesToUsersTest2() throws Exception
    {
    	List<String> usersOfLastRequest =new ArrayList<>();
    	List<String> currentRequest =new ArrayList<>();
    	AppDOnboardingRequest tempRequest=new AppDOnboardingRequest();
    	String userType = "test";
    	roleManager.updateRolesToUsers(usersOfLastRequest,currentRequest,tempRequest,userType);   	
   
    }
	
}