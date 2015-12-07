/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.delegates;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.digitaslbi.helios.mock.dto.File;
import com.digitaslbi.helios.mock.utils.ConnectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sebpache
 */
public class S3Delegate {
    
    
    public Map getFilesRootFolder(){
        return ConnectionHelper.getRootFiles() ;
    }
    
    
    public Map getFilesByPath(String path){
        return ConnectionHelper.getFilesByPath(path) ;
    }
    
}
