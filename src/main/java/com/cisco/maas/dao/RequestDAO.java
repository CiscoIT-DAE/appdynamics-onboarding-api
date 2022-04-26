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

import com.cisco.maas.dto.AppDOnboardingRequest;

public interface RequestDAO {
	public boolean create(AppDOnboardingRequest request);	 
	public boolean updateRequest(AppDOnboardingRequest request);
	public Iterable<AppDOnboardingRequest> findFailedRequests(); 
	public AppDOnboardingRequest findByProjectIdAndOpStatus(String appdProjectId,String operationalStatus);
	public Iterable < AppDOnboardingRequest > findAllByProjectId(String appdProjectId);     
    public Iterable < AppDOnboardingRequest > findByOpertionalStatus(String operationalStatus); 
    public AppDOnboardingRequest findByAppNameAndRequestType(String appGroupName,String ctrlName,String requestType);
    public boolean findAppByExternalId(String appdExternalId);
    public AppDOnboardingRequest findByExternalIdAndRequestType(String appdExternalId, String requestType);
    public boolean updateRetryLock(AppDOnboardingRequest request);
    public AppDOnboardingRequest findByRequestId(String requestId);
    public boolean updateCreateRequest(AppDOnboardingRequest request);
}
