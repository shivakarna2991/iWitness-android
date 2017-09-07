package com.perpcast.lib.services.request;


import com.perpcast.lib.codec.*;
import com.perpcast.lib.foundation.*;

import java.io.*;


public class FwiDataParam implements Serializable {

    
    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
    static public FwiDataParam paramWithJson(FwiJson json) {
        /* Condition validation */
        if (json == null) {
            return null;
        }
        else {
            return FwiDataParam.paramWithData(json.encode(), "application/json; charset=UTF-8");
        }
    }
    static public FwiDataParam paramWithString(String string) {
        /* Condition validation */
        if (string == null || string.length() == 0) {
            return null;
        }
        else {
            return FwiDataParam.paramWithData(FwiCodec.convertStringToData(string), "application/json; charset=UTF-8");
        }
    }
    static public FwiDataParam paramWithData(FwiData data, String contentType) {
        /* Condition validation */
        if (data == null || data.length() == 0 || contentType == null || contentType.length() == 0) {
            return null;
        }
        return new FwiDataParam(data, contentType);
    }
    // </editor-fold>
    
    
    // Global variables
    private String _contentType = null;
    private FwiData _data = null;
    
    
    // Class's constructors
    public FwiDataParam(FwiData data, String contentType) {
        this._contentType = contentType;
        this._data = data;
    }
    
    
    // Class's properties
    public FwiData getData() {
        return _data;
    }
    public String getContentType() {
        return _contentType;
    }
}
