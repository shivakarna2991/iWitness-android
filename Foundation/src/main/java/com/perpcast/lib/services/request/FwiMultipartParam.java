package com.perpcast.lib.services.request;


import com.perpcast.lib.foundation.*;


public class FwiMultipartParam {
    
    
    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
    static public FwiMultipartParam param(String name, String filename, FwiData data, String contentType) {
        return new FwiMultipartParam(name, filename, data, contentType);
    }
    // </editor-fold>
    
    
    // Global variables
    private String  _name        = null;
    private String  _filename    = null;
    private FwiData _data        = null;
    private String  _contentType = null;
    
    
    // Class's constructors
    public FwiMultipartParam(String name, String filename, FwiData data, String contentType) {
        this._name        = name;
        this._filename    = filename;
        this._data        = data;
        this._contentType = contentType;
    }
    
    
    // Class's properties
    public String getName() {
        return _name;
    }
    public String getFilename() {
        return _filename;
    }
    public FwiData getData() {
        return _data;
    }
    public String getContentType() {
        return _contentType;
    }
    
    
    // Class's override methods
    @Override
    public boolean equals(Object o) {
        /* Condition validation */
        if (o == null || !(o instanceof FwiMultipartParam)) {
            return false;
        }
        else {
            return this.hashCode() == o.hashCode();
        }
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this._name != null ? this._name.hashCode() : 0);
        hash = 47 * hash + (this._filename != null ? this._filename.hashCode() : 0);
        hash = 47 * hash + (this._data != null ? this._data.hashCode() : 0);
        hash = 47 * hash + (this._contentType != null ? this._contentType.hashCode() : 0);
        return hash;
    }
}
