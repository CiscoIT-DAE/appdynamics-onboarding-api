package com.cisco.maas.configuration;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
 
@Configuration
@Scope("singleton")
@ComponentScan(basePackages = "com.cisco.maas")
public class MongoConfig { 
	private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);
	@Bean
    public MongoDbFactory mongoDbFactory() throws IOException 
    {  
        final String dbServer;
		final String dbName;
		String dbUser;
		String dbPasswd;
		
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) 
		 {
			 Properties prop = new Properties();
			 prop.load(input);
			 dbName=prop.getProperty("mongo.dbname");
			 dbServer=prop.getProperty("mongo.hostname");
			 // Getting decoder  
			 Base64.Decoder decoder = Base64.getDecoder();  
			 // Decoding string  
			 dbUser = System.getenv("mongo_user");
	         if(dbUser != null)
	        	 dbUser = new String(decoder.decode(dbUser));
	         
	         dbPasswd = System.getenv("mongo_passwd");
	         if(dbPasswd != null)
	        	 dbPasswd = new String(decoder.decode(dbPasswd));

			 String url = "mongodb://"+dbUser+":"+dbPasswd+"@"+dbServer+"/"+dbUser;
			 MongoClientURI uri = new MongoClientURI(url);
			 MongoClient m = new MongoClient(uri);
    			 
	    	 return new SimpleMongoDbFactory(m, dbName);
		 }catch(Exception error) {
			 logger.error("Error from mongoDbFactory()");
			 throw error;
		 }
    }
 
    @Bean
    public MongoTemplate mongoTemplate() throws IOException 
    {   			 
        return new MongoTemplate(mongoDbFactory());
    } 
}
