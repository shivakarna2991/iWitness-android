//  Project name: Workspace
//  File name   : FwiCacheHandler.java
//
//  Author      : Phuc
//  Created date: 6/16/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.libraries.cache;


import android.text.TextUtils;

import com.iwitness.androidapp.network.*;
import com.iwitness.androidapp.libraries.utils.*;
import com.perpcast.lib.*;
import com.perpcast.lib.services.*;
import com.perpcast.lib.services.request.*;

import org.apache.http.*;

import java.io.*;
import java.util.*;

public final class FwiCacheHandler implements TaskDelegate {

    // Global variables
    private FwiCacheFolder _cacheFolder = null;
    private Vector<Map<String, Object>> _placeHolder = null;


    // Class's constructors
    public FwiCacheHandler(FwiCacheFolder cacheFolder) {
        this._cacheFolder = cacheFolder;
        this._placeHolder = new Vector<Map<String, Object>>(20);
    }


    // Class's properties
    public FwiCacheFolder getCacheFolder() {
        return _cacheFolder;
    }


    // Class's public methods
    public void handleDelegate(FwiCacheHandlerDelegate delegate) {
        /* Condition validation */
        if (delegate == null) return;

        String imageUrl = delegate.urlForHandler(this);
        String readyFile = _cacheFolder.readyPathForFilename(imageUrl);
        String loadingFile = _cacheFolder.loadingPathForFilename(imageUrl);

        File ready = new File(readyFile);
        if (ready.exists()) {
            _cacheFolder.updateFile(readyFile);
            delegate.cacheHandlerDidFinishDownloadingImage(this, readyFile);
        } else {
            final String url = imageUrl;
            final FwiCacheHandlerDelegate del = delegate;
            _placeHolder.addElement(new HashMap<String, Object>() {{
                put("url", url);
                put("delegate", del);
            }});

            /* Condition validation: Validate if loading file is exited or not */
            delegate.cacheHandlerWillStartDownloading(this);


            // Perform download file from server
            FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet, imageUrl);
            FwiDownloadService downloadService = new FwiDownloadService(request, loadingFile);

            BackgroundTask task = new BackgroundTask(downloadService);
            task.run(this);
        }
    }


    // TaskDelegate's members
    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if (statusCode == HttpStatus.SC_OK) {
            String url = (String) response;
            String readyFile = _cacheFolder.loadingFinishedForFilename(url);

            for (int i = (_placeHolder.size() - 1); i >= 0; i--) {
                Map<String, Object> record = _placeHolder.elementAt(i);

                String u = (String) record.get("url");
                FwiCacheHandlerDelegate delegate = (FwiCacheHandlerDelegate) record.get("delegate");

                if (u.compareTo(url) == 0) {
                    delegate.cacheHandlerDidFinishDownloadingImage(this, readyFile);
                    _placeHolder.removeElementAt(i);
                }
            }
        } else if (statusCode == -1) {
        } else {
            String url = (String) response;

            for (int i = (_placeHolder.size() - 1); i >= 0; i--) {
                Map<String, Object> record = _placeHolder.elementAt(i);

                String u = (String) record.get("url");
                if (TextUtils.isEmpty(u) || u.compareTo(url) == 0) {
                    _placeHolder.removeElementAt(i);
                }
            }
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Define FwiCacheHandlerDelegate">
    public interface FwiCacheHandlerDelegate {

        public String urlForHandler(FwiCacheHandler cacheHandler);

        /**
         * Notify delegate that handler will begin downloading image.
         */
        public void cacheHandlerWillStartDownloading(FwiCacheHandler cacheHandler);

        /**
         * Notify delegate that handler did finish download image.
         */
        public void cacheHandlerDidFinishDownloadingImage(FwiCacheHandler cacheHandler, String path);
    }
    // </editor-fold>
}
