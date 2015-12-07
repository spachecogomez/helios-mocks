/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.constants;

/**
 *
 * @author sebpache
 */
public enum MocksConstants {
    
    AWS_ACCESS_KEY_ID ("aws_access_key_id"),
    AWS_SECRET_ACCESS_KEY ("aws_secret_access_key"),
    AWS_BUCKET_NAME ("bucket_name"),
    AWS_PARENT_DELIMITER ("/"),
    JSON_FILE_EXTENSION(".json");
    
    private final String value;
    
    private MocksConstants(String value){
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }
    
}
