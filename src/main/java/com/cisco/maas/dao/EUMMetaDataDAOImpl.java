package com.cisco.maas.dao;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.cisco.maas.dto.EUMMetaData;
import com.cisco.maas.util.Constants;
import com.mongodb.MongoException;

@Repository
@Qualifier("EUMMetaDataDAO")
public class EUMMetaDataDAOImpl implements EUMMetaDataDAO{

	private static final Logger logger = LoggerFactory.getLogger(EUMMetaDataDAOImpl.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public boolean createEUMApplication(EUMMetaData eumDetails) {
		
		try 
		{
			logger.info("createEUMApplication - Start");			
			mongoTemplate.save(eumDetails);
			if(eumDetails.getId()!=null)
			{
				logger.info("createEUMApplication - get Id Success");
				logger.info("createEUMApplication - End");
				return true;
			}
			else
			{	
				logger.info("createEUMApplication - get Id failed");
				logger.info("createEUMApplication - End");
				return false;
			}
		}catch(Exception error) 
		{
			logger.info("createEUMApplication - Exception in Insert Request");
			throw new MongoException(error.getMessage(),error);
		}	
	}
	
	@Override
	public boolean deleteEUMApplication(EUMMetaData eumDetails)  {
	
		try 
		{
			logger.info("createEUMApplication - Start Delete()");			
			mongoTemplate.remove(eumDetails);
			logger.info("createEUMApplication - Delete request Success");
			logger.info("createEUMApplication - End Delete()");
			return true;
			
			
		}catch(Exception error) 
		{
			logger.info("createEUMApplication - Exception in Delete Request");
			throw new MongoException(error.getMessage(),error);
	}

}

	@Override
	public EUMMetaData findByApp(String appdProjectId, String eumName)  {
		try 
		{
			logger.info("findByApp - Start");
			Query query = new Query(Criteria.where(Constants.APPD_PROJECT_ID ).is(appdProjectId).andOperator(Criteria.where(Constants.EUM_NAME).is(eumName)));
			EUMMetaData eumDetails=mongoTemplate.findOne(query, EUMMetaData.class);
			logger.info("findByApp - End");
			return eumDetails;			
		}catch(Exception error) {
			logger.info("findByApp - Exception");
			throw new MongoException(error.getMessage(),error);
		}
	}

	@Override
	public Iterable<EUMMetaData> findByProjectID(String appdProjectId) {
		try 
		{
			Iterable<EUMMetaData> eumListDetails;
			logger.info("findByProjectID - Start");
			Query query = new Query(Criteria.where(Constants.APPD_PROJECT_ID ).is(appdProjectId));
			eumListDetails=mongoTemplate.find(query, EUMMetaData.class);
			if(IterableUtils.size(eumListDetails)>0)	
			{	
				logger.info("findByProjectID - End");
				return eumListDetails;
			}						
			else 
				return null;	
		}catch(Exception error) {
			logger.info("findByProjectID - Exception");
			throw new MongoException(error.getMessage(),error);
		}
	}

	@Override
	public boolean resourceMoveUpdateEUM(String appdProjectId,String oldEUMName, String newEUMName) {
		try
		{
		Query query = new Query(Criteria.where(Constants.APPD_PROJECT_ID ).is(appdProjectId).andOperator(Criteria.where(Constants.EUM_NAME).is(oldEUMName)));
		EUMMetaData eumDetails=mongoTemplate.findOne(query, EUMMetaData.class);
		if(eumDetails!=null)
		{
		eumDetails.setEumName(newEUMName);
		mongoTemplate.save(eumDetails);
		if(eumDetails.getId()!=null)
		{
			logger.info("resourceMoveUpdateEUM - Update request Success");
			logger.info("resourceMoveUpdateEUM - End Update()");
			return true;
		}
		else
		{	
			logger.info("EUMMetaDataDAOImpl: Update request Fail");
			logger.info("End Update()");
			return false;
		}
		}
		else
		{
			logger.info("resourceMoveUpdateEUM - End Update()");
			return true;
		}
		}
		
		catch(Exception error)
		{
			logger.info("resourceMoveUpdateEUM - Exception");
			throw new MongoException(error.getMessage(),error);
		}
	}
}