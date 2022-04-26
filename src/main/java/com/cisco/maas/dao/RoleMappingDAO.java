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

import com.cisco.maas.dto.RoleMapping;

public interface RoleMappingDAO {
	public boolean create(RoleMapping request);	
	
    public Iterable < RoleMapping > findByApp(String appGroupName, String environment);  
    
    public RoleMapping  findApp(String appGroupName, String environment); 
    
    public boolean delete(RoleMapping roleMap);
    
    public boolean resourceMoveUpdate(String oldAppName ,String newAppName,String ctrlName);
    
}
