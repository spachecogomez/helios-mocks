/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitaslbi.helios.mock.dto;

/**
 *
 * @author sebpache
 */
public class File implements Comparable<File>{
    
    private String fullPath;
    
    private String path;
    
    private String parent;
    
    private boolean isFile;

    private boolean isParent;

    
    @Override
    public int compareTo(File o) {
        if(this.getFullPath().equals(o.getFullPath())){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * @return the isFile
     */
    public boolean isIsFile() {
        return isFile;
    }

    /**
     * @param isFile the isFile to set
     */
    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }
    
    /**
     * @return the isParent
     */
    public boolean isIsParent() {
        return isParent;
    }

    /**
     * @param isParent the isParent to set
     */
    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

}
