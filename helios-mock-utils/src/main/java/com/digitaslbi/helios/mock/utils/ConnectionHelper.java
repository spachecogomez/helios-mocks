/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.digitaslbi.helios.mock.constants.MocksConstants;
import com.digitaslbi.helios.mock.dto.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        listObjectsRequest = new ListObjectsRequest().withBucketName(prop.getProperty(MocksConstants.AWS_BUCKET_NAME.getValue())).withDelimiter(MocksConstants.AWS_PARENT_DELIMITER.getValue());
        return listObjectsRequest;
    }

    private static ListObjectsRequest getContentByPreffix(String prefix) {
        connect();
        listObjectsRequest = new ListObjectsRequest().withBucketName(prop.getProperty(MocksConstants.AWS_BUCKET_NAME.getValue())).withDelimiter(MocksConstants.AWS_PARENT_DELIMITER.getValue()).withPrefix(prefix);
        return listObjectsRequest;
    }
    
    private static Map convertResultToFile(ListObjectsRequest listObjectsRequest , String path){
        Map<String, File> filesMap = new HashMap<String, File>();
        File aux;
        if (s3Client.listObjects(listObjectsRequest).getObjectSummaries().size() > 1) {
            for (int i = 1; i < s3Client.listObjects(listObjectsRequest).getObjectSummaries().size(); i++) {
                aux = new File();
                aux.setFullPath(s3Client.listObjects(listObjectsRequest).getObjectSummaries().get(i).getKey());
                aux.setIsFile(true);
                aux.setParent(MocksConstants.AWS_PARENT_DELIMITER.getValue());
                if(aux.getFullPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue()) > 0 ){
                    aux.setPath(aux.getFullPath().substring(aux.getFullPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue())));
                }else{
                    aux.setPath(aux.getFullPath());
                }
                if(aux.getFullPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue())>0){
                    filesMap.put(aux.getFullPath().substring(aux.getFullPath().lastIndexOf(MocksConstants.AWS_PARENT_DELIMITER.getValue())),aux);
                }else{
                    filesMap.put(aux.getPath(),aux);
                }
            }
        }
        for (String folderNames : s3Client.listObjects(listObjectsRequest).getCommonPrefixes()) {
            aux = new File();
            aux.setFullPath(folderNames);
            aux.setIsFile(false);
            aux.setParent(MocksConstants.AWS_PARENT_DELIMITER.getValue());
            if(folderNames.contains(path) ){
                aux.setPath(aux.getFullPath().replace(path, ""));
            }else{
                aux.setPath(folderNames);
            }
            filesMap.put((aux.getPath().contains(MocksConstants.AWS_PARENT_DELIMITER.getValue())? aux.getPath(): aux.getPath()+MocksConstants.AWS_PARENT_DELIMITER.getValue()),aux);
        }
        return filesMap;
    }

    public static Map getRootFiles() {
        
        
        listObjectsRequest = getRootFolders();
        
        Map<String , File> files = convertResultToFile(listObjectsRequest , MocksConstants.AWS_PARENT_DELIMITER.getValue());
        
        return files;
    }
    
    public static Map getFilesByPath(String path){
        listObjectsRequest = getContentByPreffix(path);
        
        Map<String , File> files = convertResultToFile(listObjectsRequest, path);
        
        return files;
        
    }
    
    public static void main(String[] args){
        getFilesByPath("/mocks");
    }

}
