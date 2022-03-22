package com.cisco.maas.dao;

import com.cisco.maas.dto.RoleMapping;

public interface RoleMappingDAO {
	public boolean create(RoleMapping request);	
	
    public Iterable < RoleMapping > findByApp(String appGroupName, String environment);  
    
    public RoleMapping  findApp(String appGroupName, String environment); 
    
    public boolean delete(RoleMapping roleMap);
    
    public boolean resourceMoveUpdate(String oldAppName ,String newAppName,String ctrlName);
    
}
