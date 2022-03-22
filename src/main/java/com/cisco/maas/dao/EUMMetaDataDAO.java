package com.cisco.maas.dao;

import com.cisco.maas.dto.EUMMetaData;


public interface EUMMetaDataDAO {

	public boolean createEUMApplication(EUMMetaData eumDetails) ;	 
	
	public boolean deleteEUMApplication(EUMMetaData eumDetails) ;
	
	public EUMMetaData findByApp(String appdProjectId , String eumName) ;
	
	public Iterable <EUMMetaData> findByProjectID(String appdProjectId) ;
	
	public boolean resourceMoveUpdateEUM(String appdProjectId,String oldEUMName ,String newEUMName) ;
}
