# Service Assurance AppDynamics Application Onboarding Automation
This repository contains the reusable code to onboard Applications, Users, Roles and License Rules into AppDynamics Controller. This code can be reused by developers to automate the AppDynamics APM and EUM application onboarding for thier projects.

## Build

This code build requires Java 1.8, Maven 3.8.4, MongoDB 5.0.6, Docker

```
git clone https://github.com/CiscoIT-DAE/appdynamics-onboarding-api.git
cd appdynamics-onboarding-api

### Configuration (src/main/resources/config.properties) - Below is the config.properties changes required before building this project
appd.controller={AppD SaaS Controller Name}  

The controller name should be picked from the SaaS controller URL.  For example, If the controller URL is https://abc.saas.appdynamics.com/controller/, then the 'appd.controller' value should be 'abc'.

The below mentioned json files are instrumented with default permissions and can be used to extend further set of permissions, if required 

src/main/resources/AdminRole.json
- This file contains the default permission settings for the Admin role to be configured on the AppD controller
- Admin Role used to view and modify components that change state, such as applications, business transactions, dashboards, and so on

src/main/resources/ViewRole.json
- This file contains the default permission settings for the View role to be configured on the AppD controller
- View Role used to view components such as applications, business transactions, dashboards, and so on

src/main/resources/HealthRule.json
- This file contains the default Health Rules to be configured on the AppD controller
- Health rules let you specify the parameters that represent what you consider normal or expected operations for your environment

src/main/resources/Actions.json
- This file contains the default Actions to be configured on the AppD controller
- An action is a predefined, reusable, automated response to an event

src/main/resources/Policies.json
- This file contains the default Policies to be configured on the AppD controller
- A policy consists of a trigger based on one or more events and an action in response to the trigger. You can use policies to automate monitoring, alerting, and problem remediation

src/main/resources/createLicenseRule.json
- This file contains the License information to be configured on the AppD controller


mvn clean install
docker build -t appd-onboarding-api:v1.0.0 .
```

## To Run ( Local )
##### NOTE: 
	1) Make sure ports 8080 and 27017 are not used by another services 
	2) Replace {db_user} and {db_password} with the db credentials that you want to set for Mongodb
	3) Replace {AppDUser}, {AppDPass} and {WelcomePass} with the Base64 encoded format AppDynamics Controller credentials. 
	   AppDUser and AppDPass are the AppDynamics controller local user credentials, with administration privileges for for this API automation to work. 
	   {WelcomePass} is the default password for the users created through this automation. User should reset this password
	4) Replace {mongo_user} and {mongo_password} with the base64 encoded version of {db_user} and {db_password}
```
docker network create --driver bridge dev-net
docker run --name local-mongodb -e MONGO_INITDB_ROOT_USERNAME={db_user} -e MONGO_INITDB_ROOT_PASSWORD={db_password} --network dev-net -d -p 27017:27017 mongo:5.0.6
docker run --name appd-onboarding-api --network dev-net  -e appd_user='{base64 encoded AppDUser}' -e appd_pass='{base64 encoded AppDPass}' -e appd_localUserDefaultPassword='{base64 encoded WelcomePass}' -e mongo_user='{base64 encoded db_user}' -e mongo_passwd='{base64 encoded db_password}' -d -p 8080:8080 appd-onboarding-api:v1.0.0
```

## APIs Provided
Once the appd-onboarding-api and mongo docker containers started, Rest APIs documentation is available at 
[OpenAPI Specs] (http://localhost:8080/appd-onboarding-api/swagger-ui/index.html?configUrl=/appd-onboarding-api/v3/api-docs/swagger-config)

Note: Replace the localhost with IP address or domain name of the VM, if this is deployed in a VM

Below are the APIs provided by this code base
```
POST /api​/v1​/applications - Onboard Applications into AppDynamics Controller
GET /api​/v1​/applications​/{id} - Get the Onboarded Applications Details in AppDynamics Controller
PATCH /api​/v1​/applications​/{id} - Update the Onboarded Applications in AppDynamics Controller
```

## Create AppDynamics Application

###Example API Request:

```
curl -X POST "http://localhost:8080/appd-onboarding-api/api/v1/applications" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"apmApplicationGroupName\":\"DemoApplication1\",\"alertAliases\":[\"alertalias@domain.com\"],\"adminUsers\":[\"adminUser1\",\"adminUser2\"],\"viewUsers\":[\"viewUser1\",\"viewUser2\"],\"apmLicenses\":6,\"eumApplicationGroupNames\":[\"Demo_EUM_APP_1\",\"Demo_EUM_APP_2\"]}"
```
###Example API Response:
```
HTTP Response Headers: 
	TrackingID: S1_c33f6c30-eb73-43fe-b0d9-6ffc786c13fa_1645113524688
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Thu, 17 Feb 2022 16:15:20 GMT
	Keep-Alive: timeout=20
	Connection: keep-alive

HTTP Response Code: 202

HTTP Response Body: {
    "eumApplicationGroupNames": [
        "Demo_EUM_APP_1",
        "Demo_EUM_APP_2"
    ],
    "viewUsers": [
        "viewUser1",
        "viewUser2"
    ],
    "apmLicenses": 6,
    "apmApplicationGroupName": "DemoApplication1",
    "id": "8d10b1de-d0ca-4c21-ae23-ddb3979e07f6",
    "alertAliases": ["alertalias@domain.com"],
    "adminUsers": [
        "adminUser1",
        "adminUser2"
    ]
}
```

###This API does

1. Create APM and EUM application in AppDynamics Controller with the given name
2. Create Local users in AppDynamics Controller
3. Create Admin and View Roles for these users in AppDynamics Controller and provides access to the application that is being created
4. Create a License rule for this application, based on the number of licenses requested
5. Create health rules, actions



## Update AppDynamics Application

###Example API Request:
```
curl -X PATCH "http://localhost:8080/appd-onboarding-api/api/v1/applications/8d10b1de-d0ca-4c21-ae23-ddb3979e07f6" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"alertAliases\":[\"alertalias@domain.com\"],\"adminUsers\":[\"adminUser1\",\"adminUser3\"],\"viewUsers\":[\"viewUser2\",\"viewUser3\"],\"apmLicenses\":10,\"eumApplicationGroupNames\":[\"Demo_EUM_APP_1\",\"Demo_EUM_APP_2\",\"Demo_EUM_APP_3\"]}"
```

###Example API Response:
```
HTTP Response Headers: 
	TrackingID: S1_2c0f0a07-5185-4116-b46d-39457ca84a58_1645114520064
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Thu, 17 Feb 2022 16:15:20 GMT
	Keep-Alive: timeout=20
	Connection: keep-alive

HTTP Response Code: 202

HTTP Response Body: {
    "eumApplicationGroupNames": [
        "Demo_EUM_APP_1",
   	    "Demo_EUM_APP_2",
        "Demo_EUM_APP_3"
    ],
    "viewUsers": [
        "viewUser2",
        "viewUser3"
    ],
    "apmLicenses": 10,
    "apmApplicationGroupName": "DemoApplication1",
    "id": "8d10b1de-d0ca-4c21-ae23-ddb3979e07f6",
    "alertAliases": ["alertalias@domain.com"],
    "adminUsers": [
        "adminUser1",
        "adminUser3"
    ]
}
```

This API does
1. Creates additional users or remove existing users from the Admin or View roles of this application
2. Updates the required licenses for the application
3. Updates the email alias for this application actions
4. Creates additional EUM application required


## View Application Application

This API gets the full details of the application including license key

###Example API Request:
```
curl -X GET "http://localhost:8080/appd-onboarding-api/api/v1/applications/8d10b1de-d0ca-4c21-ae23-ddb3979e07f6" -H  "accept: application/json"
```
###Example API Response:
```
HTTP Response Headers: 
	TrackingID: S1_4a6b896c-1081-46fd-a888-e2762a547b44_1645113849612
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Thu, 17 Feb 2022 16:04:09 GMT
	Keep-Alive: timeout=20
	Connection: keep-alive

HTTP Response Code: 200

HTTP Response Body: {
    "eumApplicationGroupNames": [
        "Demo_EUM_APP_1",
   	    "Demo_EUM_APP_2",
        "Demo_EUM_APP_3"
    ],
    "apmLicenses": 10,
    "viewRoleName": "AppD-914fb361-5ff6-33ae-999d-3c97a96ef490-devv",
    "adminUsers": [
        "adminUser1",
        "adminUser3"
    ]
    "licenseKey": "1e95f674-d0e8-4a72-833c-22c8a806werf",
    "viewUsers": [
        "viewUser2",
        "viewUser3"
    ],
    "adminRoleName": "AppD-914fb361-5ff6-33ae-999d-3c97a96ef490-deva",
    "apmApplicationGroupName": "DemoApplication1",
    "id": "8d10b1de-d0ca-4c21-ae23-ddb3979e07f6",
    "operation": "create",
    "alertAliases": ["alertalias@domain.com"],
    "status": "ACTIVE"
}
```

## Database Overview
The MongoDB instance captures the metadata information about the onboarded application(s) for the onboarding API.

The following collections will be created to keep track of the metadata;

- appDOnboardingRequest:

This collection stores the metadata for the create or update request generated via onboarding API call. 
It stores flags which indicate the status of request, the request tracking Id and all the details associated with the request. It also contains retry details incase the request failed initially.

- roleMapping:

This collection stores metadata for each role created in AppDynamics via onboarding API call.
It stores the controller name, application name and name of the admin and view roles associated with the application.

- eUMMetaData:

This collection stores EUM metadata which includes the appD project Id asscociated with that EUM, it's name and the date (and time) of EUM creation.

- aPPDMaster:

This collection stores the license metadata for APM and EUM. 
It stores the License key for the lincenses created in the controller, the application and the application Id associated with the licenses. It also stores the alert aliases for the application.

## Authors & Maintainers
Fabeha Fatima <ffatima@cisco.com >
Nanda Krishna K S <nkanakap@cisco.com>
Kaleswara Rao Nallapuneni <knallapu@cisco.com>
Sankaranarayanan Thirupudarjunan <sthirupu@cisco.com>
Seshagiri Rao Kotichintala <skotichi@cisco.com>
Murali Kante <mukante@cisco.com>
Clement Joseph <cljoseph@cisco.com>
Seshagirirao Surapaneni <sesurapa@cisco.com>

## Credits
Murali Kante <mukante@cisco.com>
Clement Joseph <cljoseph@cisco.com>
Seshagirirao Surapaneni <sesurapa@cisco.com>

## License

This project is licensed to you under the terms of the [Apache License 2.0](./LICENSE).
