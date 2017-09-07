package com.perpcast.lib.services.request;

import android.text.TextUtils;

import com.perpcast.lib.FwiFoundation.*;
import com.perpcast.lib.codec.*;
import com.perpcast.lib.foundation.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;

import java.net.*;
import java.util.*;


public class FwiRequest extends HttpEntityEnclosingRequestBase {
    
    
    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
    static public FwiRequest requestWithURL(FwiHttpMethod requestType, String url) {
        return new FwiRequest(requestType, url);
    }
    // </editor-fold>
    
    
    // Global variables
    private FwiHttpMethod _type = null;
    
    // Raw request
    FwiDataParam _raw = null;
    // Form request
    ArrayList<FwiFormParam> _form = null;
    ArrayList<FwiMultipartParam> _upload = null;

    // Class's constructors
    public FwiRequest(FwiHttpMethod type) {
        super();
        this._type = type;
    }
    public FwiRequest(FwiHttpMethod type, final String uri) {
        this(type);
        setURI(URI.create(uri));
    }

    // Class's override methods
    @Override
    public String getMethod() {
        return _type.method;
    }

    
    /** Build the request. */
    public long prepare() {
        this.setHeader("Accept-Encoding", "gzip, deflate");
        this.setHeader("Connection", "close");
        
        /* Condition validation */
        if (_raw == null && (_form == null || _form.size() == 0) && (_upload == null || _upload.size() == 0)) return 0;
        long length = 0;
        
        if (_raw != null) {
            // Define content type header
            this.setHeader("Content-Type", _raw.getContentType());
            
            HttpEntity requestEntity = new ByteArrayEntity(_raw.getData().bytes());
            this.setEntity(requestEntity);

            length = requestEntity.getContentLength();
        }
        else {
            switch (_type) {
                case kDelete: {
                    // ???
                    break;
                }
                case kPatch:
                case kPost:
                case kPut: {
                    if (_form != null && _upload == null) {
                        // Define content type header
                        this.setHeader("Content-Type", "application/x-www-form-urlencoded");

                        // Data
                        FwiData data = FwiCodec.convertStringToData(TextUtils.join("&", _form.toArray()));

                        // Define content length header
                        HttpEntity requestEntity = new ByteArrayEntity(data.bytes());
                        this.setEntity(requestEntity);

                        length = requestEntity.getContentLength();
                    }
                    else {
                        // Define boundary
                        String boundary    = String.format("----------%d", (new Date()).getTime());
                        String contentType = String.format("multipart/form-data; boundary=%s", boundary);

                        // Define content type header
                        this.addHeader("Content-Type", contentType);

                        // Define body
                        String boundaryData = String.format("\r\n--%s\r\n", boundary);
                        FwiMutableData body  = new FwiMutableData();

                        if (_upload != null && _upload.size() > 0) {
                            for (int i = 0; i < _upload.size(); i++) {
                                FwiMultipartParam part = _upload.get(i);

                                body.append(FwiCodec.convertStringToData(boundaryData));
                                body.append(FwiCodec.convertStringToData(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n", part.getName(), part.getFilename())));
                                body.append(FwiCodec.convertStringToData(String.format("Content-Type: %s\r\n\r\n", part.getContentType())));
                                body.append(part.getData());
                            }
                        }

                        if (_form != null && _form.size() > 0) {
                            for (int i = 0; i < _form.size(); i++) {
                                FwiFormParam pair = _form.get(i);

                                body.append(FwiCodec.convertStringToData(boundaryData));
                                body.append(FwiCodec.convertStringToData(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n", pair.getKey())));
                                body.append(FwiCodec.convertStringToData(pair.getValue()));
                            }
                        }
                        body.append(FwiCodec.convertStringToData(String.format("\r\n--%s--\r\n", boundary)));

                        // Define content length header
                        HttpEntity requestEntity = new ByteArrayEntity(body.bytes());
                        this.setEntity(requestEntity);

                        length = requestEntity.getContentLength();
                    }
                    break;
                }
                case kGet:
                    break;
                default: {
                    String finalURL = String.format("%s?%s", this.getURI(), TextUtils.join("&", _form.toArray()));
                    setURI(URI.create(finalURL));
                    break;
                }
            }
        }
        return length;
    }

    
    // <editor-fold defaultstate="collapsed" desc="FwiForm">
    /** Add key-value. */
    public void addFormParams(FwiFormParam... params) {
        if (_form == null) {
            this._initializeForm();
        }
        _raw = null;

        for (FwiFormParam param : params) {
            _form.add(param);
        }
    }
    /** Like add parameters but will reset the collection. */
    public void setFormParams(FwiFormParam... params) {
        if (_form == null) {
            this._initializeForm();
        } else {
            _form.clear();
        }
        _raw = null;
        this.addFormParams(params);
    }

    /** Add multipart data. */
    public void addMultipartParams(FwiMultipartParam... params) {
        if (_upload == null) {
            this._initializeUpload();
        }
        _raw = null;

        for (FwiMultipartParam param : params) {
            _upload.add(param);
        }
    }
    /** Like add multipart data but will reset the collection. */
    public void setMultipartParams(FwiMultipartParam... params) {
        if (_upload == null) {
            this._initializeUpload();
        } else {
            _upload.clear();
        }
        _raw = null;
        this.addMultipartParams(params);
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="FwiRaw">
    public void setDataParam(FwiDataParam param) {
        /* Condition validation: Validate method type */
    if (!(_type == FwiHttpMethod.kPost || _type == FwiHttpMethod.kPatch || _type == FwiHttpMethod.kPut)) return;

    /* Condition validation: Validate parameter type */
    if (param == null) return;

    // Release form
    _form = null;
    _upload = null;

    // Keep new raw
    _raw = param;
    }
    // </editor-fold>

    
    // Class's private methods
    public void _initializeForm() {
        /* Condition validation */
        if (_form != null) return;
        _form = new ArrayList<FwiFormParam>(9);
    }
    public void _initializeUpload() {
        /* Condition validation */
        if (_upload != null) return;
        _upload = new ArrayList<FwiMultipartParam>(1);
    }
}
