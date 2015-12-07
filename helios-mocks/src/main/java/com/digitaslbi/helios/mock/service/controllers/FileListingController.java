/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.service.controllers;

import com.digitaslbi.helios.mock.constants.MocksConstants;
import com.digitaslbi.helios.mock.delegates.S3Delegate;
import com.digitaslbi.helios.mock.dto.File;
import com.digitaslbi.helios.mock.utils.ConnectionHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

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
        File selectedFile = null;
        if(!path.contains(MocksConstants.JSON_FILE_EXTENSION.getValue())){
            selectedFile = (File) files.get((path.contains(MocksConstants.AWS_PARENT_DELIMITER.getValue())? path: path+MocksConstants.AWS_PARENT_DELIMITER.getValue()));
            files = delegate.getFilesByPath(selectedFile.getFullPath());
        }else{
            selectedFile = (File) files.get(path);
        }
        return new ModelAndView("welcome", "message", files);
    }
}
