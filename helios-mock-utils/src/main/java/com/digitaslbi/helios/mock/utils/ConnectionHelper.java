/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.digitaslbi.helios.mock.constants.MocksConstants;
import com.digitaslbi.helios.mock.dto.File;

/**
 *
 * @author sebpache
 */
public class ConnectionHelper {

    private static Logger log = LogManager.getLogger(ConnectionHelper.class);

    private static AmazonS3 s3Client;

    private static Properties prop;

    private static ListObjectsRequest listObjectsRequest;

    {
        connect();
    }

    private static void loadProperties() {
        try {
            prop = new Properties();
            prop.load(ConnectionHelper.class.getResourceAsStream("/helios-mock-utils.properties"));
        } catch (IOException ex) {
            log.error("The configuration file was not found");
            System.exit(0);
        }
    }

    public static AmazonS3 connect() {
        loadProperties();
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(prop.getProperty(MocksConstants.AWS_ACCESS_KEY_ID.getValue()), prop.getProperty(MocksConstants.AWS_SECRET_ACCESS_KEY.getValue()));
        s3Client = new AmazonS3Client(awsCreds);
        return s3Client;
    }

    private static ListObjectsRequest getRootFolders() {
    	connect();
        
        listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.withBucketName(prop.getProperty(MocksConstants.AWS_BUCKET_NAME.getValue()));
        listObjectsRequest.withDelimiter(MocksConstants.AWS_PARENT_DELIMITER.getValue());
        
        return listObjectsRequest;
    }

    private static ListObjectsRequest getContentByPreffix(String prefix) {
    	connect();
        
        listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.withBucketName(prop.getProperty(MocksConstants.AWS_BUCKET_NAME.getValue()));
        listObjectsRequest.withDelimiter(MocksConstants.AWS_PARENT_DELIMITER.getValue());
        listObjectsRequest.withPrefix(prefix);
        
        return listObjectsRequest;
    }
    
    private static Map convertResultToFile(ListObjectsRequest listObjectsRequest , String path, String parentPath) {
    	Map<String, File> filesMap = new HashMap<String, File>();
        File aux;
        
        ObjectListing objListing = s3Client.listObjects(listObjectsRequest);
        
        // creates the parent elemment
        aux = new File();
        aux.setIsParent(true);
        aux.setFullPath(parentPath);
        aux.setPath(parentPath);

        // adapts the path in order to use as map key
        if(aux.getPath() != null) {
        	aux.setPath(clearPathDelimiter(aux.getPath()));
        	
        	if(aux.getPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue()) > 0) {
            	aux.setPath(aux.getPath().substring(aux.getPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue())+1));        		
        	}
        }
        filesMap.put(aux.getPath(), aux);

		for (S3ObjectSummary objSummary : objListing.getObjectSummaries()) {
			aux = new File();
			aux.setFullPath(objSummary.getKey());
			aux.setParent(objListing.getPrefix());
			
			// if size is 0 is considered a folder
			aux.setIsFile((objSummary.getSize() == 0) ? false : true);
			
			if (aux.getParent() != null) {
				if (!aux.getFullPath().equals(aux.getParent())) {
					aux.setPath(aux.getFullPath().replace(aux.getParent(),""));
				}
			} else {
				aux.setPath(aux.getFullPath());
			}
			
			if (aux.getPath() != null) {
				filesMap.put(clearPathDelimiter(aux.getPath()), aux);
			}
		}

		for (String folderNames : objListing.getCommonPrefixes()) {
            aux = new File();
            aux.setFullPath(folderNames);
            aux.setParent(objListing.getPrefix());
            aux.setIsFile(false);
            
            if (aux.getParent() != null) {
            	aux.setPath(aux.getFullPath().replace(aux.getParent(),""));
			} else {
				aux.setPath(aux.getFullPath());					
			}
            
            if (aux.getPath() != null) {
				filesMap.put(clearPathDelimiter(aux.getPath()), aux);
			}
        }
		
        return filesMap;
    }

    public static Map getRootFiles() {
    	listObjectsRequest = getRootFolders();
        
        Map<String, File> files = convertResultToFile(listObjectsRequest, MocksConstants.AWS_PARENT_DELIMITER.getValue(), null);
        
        return files;
    }
    
    public static Map getFilesByPath(String path, String parentPath) {
    	listObjectsRequest = getContentByPreffix(path);
        
        Map<String, File> files = null;
		
    	files = convertResultToFile(listObjectsRequest, path, parentPath);
		
        return files;
    }
    
    private static String clearPathDelimiter(String path) {
    	if(path.substring(path.length() - 1).equals(MocksConstants.AWS_PARENT_DELIMITER.getValue())) {
    		return path.substring(0, path.length() - 1);
		}
    	
    	return path;
    }
    
    public static void createFolder(String bucketName, String folderName) {
    	// create meta-data for your folder and set content-length to 0
    	ObjectMetadata metadata = new ObjectMetadata();
    	metadata.setContentLength(0);
    	
    	// create empty content
    	InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
    	
    	// create a PutObjectRequest passing the folder name suffixed by /
    	PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
    				folderName + MocksConstants.AWS_PARENT_DELIMITER.getValue(), emptyContent, metadata);
    	
    	// send request to S3 to create folder
    	try{
    		s3Client.putObject(putObjectRequest);
	    } 
    	catch (AmazonServiceException ase) {
    		log.error("Caught an AmazonServiceException, which " +
	        		"means your request made it " +
	                "to Amazon S3, but was rejected with an error response" +
	                " for some reason.");
    		log.error("Error Message:    " + ase.getMessage());
    		log.error("HTTP Status Code: " + ase.getStatusCode());
    		log.error("AWS Error Code:   " + ase.getErrorCode());
    		log.error("Error Type:       " + ase.getErrorType());
    		log.error("Request ID:       " + ase.getRequestId());
	    } catch (AmazonClientException ace) {
	    	log.error("Caught an AmazonClientException, which " +
	        		"means the client encountered " +
	                "an internal error while trying to " +
	                "communicate with S3, " +
	                "such as not being able to access the network.");
	    	log.error("Error Message: " + ace.getMessage());
	    }
    }
    
    public static void uploadFile(String bucketName, String folderName, String filePath) {
    	try {
    		log.debug("Uploading a new object to S3 from a file\n");
    		java.io.File file = new java.io.File(filePath);
    		s3Client.putObject(new PutObjectRequest(bucketName, folderName,file).withCannedAcl(CannedAccessControlList.PublicRead));
         } catch (AmazonServiceException ase) {
        	 log.error("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
        	 log.error("Error Message:    " + ase.getMessage());
        	 log.error("HTTP Status Code: " + ase.getStatusCode());
        	 log.error("AWS Error Code:   " + ase.getErrorCode());
        	 log.error("Error Type:       " + ase.getErrorType());
        	 log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.error("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
        	log.error("Error Message: " + ace.getMessage());
        }
    }
    
	public static void deleteFolder(String bucketName, String folderName) {
		List<S3ObjectSummary> fileList = s3Client.listObjects(bucketName, folderName).getObjectSummaries();
		
		try{
			for (S3ObjectSummary file : fileList) {
				s3Client.deleteObject(bucketName, file.getKey());
			}
			
			s3Client.deleteObject(bucketName, folderName);
			
		} catch (AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException.");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
	    } catch (AmazonClientException ace) {
	    	log.error("Caught an AmazonClientException.");
	    	log.error("Error Message: " + ace.getMessage());
	    }
	}
    
    public static InputStream getObject(String key) {
		try {
			log.debug("Downloading an object");
            
			S3Object s3object = s3Client.getObject(new GetObjectRequest(prop.getProperty(MocksConstants.AWS_BUCKET_NAME.getValue()), key));
			
            log.debug("Content-Type: "  + s3object.getObjectMetadata().getContentType());
            //displayTextInputStream(s3object.getObjectContent());
            
            return s3object.getObjectContent();            
        } catch (AmazonServiceException ase) {
        	log.error("Caught an AmazonServiceException, which" +
            		" means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
        	log.error("Error Message:    " + ase.getMessage());
        	log.error("HTTP Status Code: " + ase.getStatusCode());
        	log.error("AWS Error Code:   " + ase.getErrorCode());
        	log.error("Error Type:       " + ase.getErrorType());
        	log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.error("Caught an AmazonClientException, which means"+
            		" the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
        	log.error("Error Message: " + ace.getMessage());
        }
		
		return null;
	}

    public static void main(String[] args) {
        getFilesByPath("/mocks", null);
    }

}
