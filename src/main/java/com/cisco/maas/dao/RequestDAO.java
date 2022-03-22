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
