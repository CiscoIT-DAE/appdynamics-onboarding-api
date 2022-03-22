package com.cisco.maas.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cisco.maas.exception.AppDOnboardingException;

@SuppressWarnings("unchecked")
public class AppDynamicsUtilTest {

	@InjectMocks
	@Spy
	AppDynamicsUtil appdUtil;

	@Mock
	HttpURLConnection con = mock(HttpURLConnection.class);

	@Mock
	URL url = mock(URL.class);

	@Mock
	RestTemplate restTemplate;

	@Mock
	AppDynamicsUtil appDUtil;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void appdConnectionOnlyGetRoleContextTest() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(appdUtil.appDConnectionOnlyGet("https://cisco1.saas.appdynamics.com/", "GET", "cisco1", "role"));
	}

	@Test
	public void appdConnectionOnlyGetUserContext() throws Exception {
		assertNotNull(appdUtil.appDConnectionOnlyGet("https://cisco1nonprod.saas.appdynamics.com/", "GET",
				"cisco1nonprod", "user"));
	}

	@Test
	public void appdConnectionOnlyGetLicenseQuotaContext() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(appdUtil.appDConnectionOnlyGet("https://cisco1nonprod.saas.appdynamics.com/", "GET", "ciscoef",
				"licenseQuota"));
	}

	@Test
	public void appdConnectionOnlyGetOtherContext() throws Exception {

		assertNull(appdUtil.appDConnectionOnlyGet("https://ciscoeft.saas.appdynamics.com/controllerapi/", "GET",
				"ciscoeft", "test"));
	}

	@Test
	public void appdConnectionPostMethodTest() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));

		assertNotNull(
				appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "POST", "", "licenseQuota"));
	}

	@Test
	public void appdConnectionPutMethodTest() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(
				appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "PUT", "", "licenseQuota"));
	}

	@Test
	public void appdConnectionTest() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "PUT", "", "role"));
	}

	@Test
	public void appdConnectionAcceptedTest() throws Exception {
		Mockito.when(url.openConnection()).thenReturn(con);
		Mockito.when(con.getResponseCode()).thenReturn(201);
		assertNull(appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "PUT", "", "role"));
	}

	@Test
	public void appdConnectionResponseFailedTest() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(100);
		assertNull(appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "PUT", "", "role"));
	}

	@Test
	public void appdConnection_invalidMethodName() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(appdUtil.appDConnection("https://cisco1nonprod.saas.appdynamics.com/", "GET", "", "license"));
	}

	@Test
	public void appDConnectionDelete_cisco1() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(
				appdUtil.appDConnectionDelete("https://cisco1.saas.appdynamics.com/", "DELETE", "cisco1", "user"));
	}

	@Test
	public void appDConnectionDelete_cisco1nonprod() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(appdUtil.appDConnectionDelete("https://cisco1nonprod.saas.appdynamics.com/", "DELETE",
				"cisco1nonprod", "role"));
	}

	@Test
	public void appDConnectionDelete_ciscoeft() throws Exception {
		String response = new String();
		Mockito.doReturn(response).when(appdUtil).retrieveResponse(any(HttpURLConnection.class), any(String.class));
		assertNotNull(
				appdUtil.appDConnectionDelete("https://cisco1.saas.appdynamics.com/", "DELETE", "ciscoeft", "test"));
	}

	@Test
	public void retrieveResponse_invalidContext() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(204);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNull(appdUtil.retrieveResponse(con, "delet"));
	}

	@Test
	public void retrieveResponse_invaliderrorCode() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(100);
		Mockito.when(con.getErrorStream()).thenReturn(null);
		assertNull(appdUtil.retrieveResponse(con, "delet"));
	}

	@Test
	public void retrieveResponse_postMethod() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(201);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNotNull(appdUtil.retrieveResponse(con, "POST"));
	}

	@Test
	public void retrieveResponse_putMethod() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(201);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNotNull(appdUtil.retrieveResponse(con, "PUT"));
	}

	@Test
	public void retrieveResponse_putMethod2() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(203);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNull(appdUtil.retrieveResponse(con, "PUT"));
	}

	@Test
	public void retrieveResponse_deleteMethod() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(204);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNotNull(appdUtil.retrieveResponse(con, "DELETE"));
	}

	@Test
	public void retrieveResponse_deleteMethod2() throws Exception {
		Mockito.when(con.getResponseCode()).thenReturn(205);
		Mockito.when(con.getInputStream()).thenReturn(null);
		assertNull(appdUtil.retrieveResponse(con, "DELETE"));
	}

	@Test
	public void testLogin_whenHttpCode200() {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.controllerPrefix = ".saas.petstore.com";
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.login("xyzController");
	}

	@Test
	public void testLogin_whenHttpCode500() {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response);
		appdUtil.controllerPrefix = ".saas.petstore.com";
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.login("xyzController");
	}

	@Test
	public void testLogin_whenException() {
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenThrow(RestClientException.class);
		appdUtil.controllerPrefix = ".saas.petstore.com";
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.login("xyzController");
	}

	@Test(expected = IOException.class)
	public void testPostRequestThrowError() throws IOException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response)
				.thenThrow(RestClientException.class);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.postRequest("?applicationType=APM", "");
	}

	@Test
	public void testPostRequest() throws IOException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		ResponseEntity<Resource> resp = Mockito.mock(ResponseEntity.class);
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response).thenReturn(resp);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.postRequest("", "");
	}

	@Test
	public void testCreateApplicationInAppDynamics() throws AppDOnboardingException, IOException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		JSONObject json = new JSONObject();
		json.put("id", 1233);
		ResponseEntity<JSONObject> responseSecond = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response)
				.thenReturn(responseSecond);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.createApplicationInAppDynamics("Maas Portal", "WEB");
	}

	@Test
	public void testCreateApplicationInAppDynamicsForNull() throws AppDOnboardingException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		ResponseEntity<Resource> resp = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response).thenReturn(resp);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.createApplicationInAppDynamics("Maas Portal", "APM");
	}

	@Test(expected = AppDOnboardingException.class)
	public void testCreateApplicationInAppDynamicsForException() throws AppDOnboardingException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response)
				.thenThrow(RestClientException.class);
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		appdUtil.createApplicationInAppDynamics("Maas Portal", "APM");
	}

	@Test
	public void testSetBTSettigsInAppDynamicsFalse() {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		ResponseEntity<Resource> resp = Mockito.mock(ResponseEntity.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response).thenReturn(resp);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		assertEquals(false, appdUtil.setBTSettigsInAppDynamics("123"));
	}

	@Test
	public void testSetBTSettigsInAppDynamicsTrue() {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		JSONObject json = new JSONObject();
		json.put("id", 123);
		ResponseEntity<JSONObject> responseSecond = new ResponseEntity<JSONObject>(json, HttpStatus.NO_CONTENT);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response)
				.thenReturn(responseSecond);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		assertEquals(true, appdUtil.setBTSettigsInAppDynamics("123"));
	}

	@Test
	public void testSetBTSettigsInAppDynamicsIOException() throws IOException {
		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		JSONObject json = new JSONObject();
		json.put("id", 123);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		List<String> headersList = new ArrayList<>();
		headersList.add("JSESSIONID=ed3b5cbde2aa18306ea917692233; Path=/; HttpOnly; Secure ");
		headersList.add("X-CSRF-TOKEN=15fcc7e6b59b9a47670ab2f8db3eefbee94f4428; Path=\"\"; Secure ");
		Mockito.when(response.getStatusCodeValue()).thenReturn(200);
		Mockito.when(response.getHeaders()).thenReturn(headers);
		Mockito.when(headers.get("Set-Cookie")).thenReturn(headersList);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
				Mockito.any(HttpEntity.class), Mockito.any(Class.class))).thenReturn(response)
				.thenThrow(RestClientException.class);
		appdUtil.userPassword = "abc";
		appdUtil.userName = "def";
		assertEquals(false, appdUtil.setBTSettigsInAppDynamics("123"));
	}
}
