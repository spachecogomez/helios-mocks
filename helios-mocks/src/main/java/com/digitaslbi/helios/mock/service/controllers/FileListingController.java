/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.service.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.digitaslbi.helios.mock.constants.MocksConstants;
import com.digitaslbi.helios.mock.delegates.S3Delegate;
import com.digitaslbi.helios.mock.dto.File;

/**
 *
 * @author sebpache
 */
@Controller
public class FileListingController {

    private S3Delegate delegate = new S3Delegate();

    Map files;

    @RequestMapping("/files.jsp")
    public ModelAndView helloWorld() {
        files = delegate.getFilesRootFolder();
        return new ModelAndView("welcome", "message", files);
    }

    @RequestMapping(value = "/list/{path}", method = RequestMethod.GET)
    public ModelAndView listFiles(@PathVariable("path") String path) {
    	System.out.println("Parameter->"+path);
        System.out.println("Parameter/->" + (path.contains(MocksConstants.AWS_PARENT_DELIMITER.getValue())? path: path+MocksConstants.AWS_PARENT_DELIMITER.getValue()));
        
        File selectedFile = (File) files.get(path);
        
        if (selectedFile != null) {
        	files = delegate.getFilesByPath(selectedFile.getFullPath(), selectedFile.getParent());
        }
        
        return new ModelAndView("welcome", "message", files);
    }

    @RequestMapping(value = "/file/{fileName}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable("fileName") String fileName, 
    	    HttpServletResponse response) {
        System.out.println("Parameter->"+fileName);
        try {
	        File selectedFile = (File) files.get(fileName);
	        
	        if (selectedFile != null && selectedFile.isIsFile()) {
	        	InputStream inputStream = delegate.getS3Object(selectedFile.getFullPath());
	        	
	        	if(inputStream != null) {
		        	IOUtils.copy(inputStream, response.getOutputStream());
					
		        	response.flushBuffer();
	        	}
	        }
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
