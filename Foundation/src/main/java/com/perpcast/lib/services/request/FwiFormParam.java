package com.perpcast.lib.services.request;


import java.io.*;
import java.net.*;


public class FwiFormParam implements Serializable {
    
    
    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
    static public FwiFormParam param(String key, String value) {
        return new FwiFormParam(key, value);
    }
    // </editor-fold>
    
    
    // Global variables
    private String _key   = null;
    private String _value = null;
    
    
    // Class's constructors
    public FwiFormParam(String key, String value) {
        this._key   = key;
        this._value = value;
    }
    
    
    // Class's properties
    public String getKey() {
        return _key;
    }
    public String getValue() {
        return _value;
    }
    
    
    // Class's override methods
    @Override
    public boolean equals(Object o) {
        /* Condition validation */
        if (o == null || !(o instanceof FwiFormParam)) {
            return false;
        }
        else {
            return this.hashCode() == o.hashCode();
        }
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this._key != null ? this._key.hashCode() : 0);
        hash = 97 * hash + (this._value != null ? this._value.hashCode() : 0);
        
        return hash;
    }
    
    @Override
    public String toString() {
        try {
            return String.format("%s=%s", _key, URLEncoder.encode(_value, "UTF-8"));
        }
        catch(Exception ex) {
            return String.format("%s=", _key);
        }
    }
}
