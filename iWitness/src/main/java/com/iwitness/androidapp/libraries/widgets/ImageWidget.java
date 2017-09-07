//  Project name: Workspace
//  File name   : ImageWidget.java
//
//  Author      : Phuc
//  Created date: 6/16/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.libraries.widgets;


import android.content.*;
import android.graphics.*;
import android.util.*;
import android.widget.*;
import com.iwitness.androidapp.*;
import com.iwitness.androidapp.libraries.cache.*;

import java.io.*;


public class ImageWidget extends ImageView implements FwiCacheHandler.FwiCacheHandlerDelegate {


    // Global static variables
    static private FwiCacheHandler _cacheHandler = null;


    // Global variables
    private String _imageURL = null;


    // <editor-fold defaultstate="collapsed" desc="Class's constructors">
    public ImageWidget(Context context) {
        super(context);
        if (!super.isInEditMode()) _initialControl();
    }
    public ImageWidget(Context context, AttributeSet attributes) {
        super(context, attributes);
        if (!super.isInEditMode()) _initialControl();
    }
    public ImageWidget(Context context, AttributeSet attributes, int defineStyle) {
        super(context, attributes, defineStyle);
        if (!super.isInEditMode()) _initialControl();
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's public methods">
    public void downloadImage(String imageURL) {
        this.downloadImage(imageURL, false);
    }
    public void downloadImage(String imageURL, boolean isReload) {
        /* Condition validation */
        if (imageURL == null || imageURL.length() == 0) return;

        this._imageURL = imageURL;

        if (isReload) {
            String path = _cacheHandler.getCacheFolder().readyPathForFilename(imageURL);

            File file = new File(path);
            if (file.exists()) file.delete();
        }

        _cacheHandler.handleDelegate(this);
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="SurfaceHolder.Class's private methods">
    private void _initialControl() {
        if (_cacheHandler == null) {
            FwiCacheFolder cacheFolder = new FwiCacheFolder(AppDelegate.getAppContext(), "Image");
            _cacheHandler = new FwiCacheHandler(cacheFolder);
        }
    }


    @Override
    public String urlForHandler(FwiCacheHandler cacheHandler) {
        return _imageURL;
    }

    @Override
    public void cacheHandlerWillStartDownloading(FwiCacheHandler cacheHandler) {
    }

    @Override
    public void cacheHandlerDidFinishDownloadingImage(FwiCacheHandler cacheHandler, String path) {
        Bitmap image = BitmapFactory.decodeFile(path);
        this.setImageBitmap(image);
    }
    // </editor-fold>
}
