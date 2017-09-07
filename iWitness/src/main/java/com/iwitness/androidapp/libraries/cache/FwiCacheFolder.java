//  Project name: Workspace
//  File name   : FwiCacheFolder.java
//
//  Author      : Phuc
//  Created date: 6/16/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.libraries.cache;


import android.annotation.*;
import android.content.*;
import android.os.*;

import java.io.*;
import java.util.*;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class FwiCacheFolder {


    static private int kSeconds_Per1day  = 86400;                   // 1 day  = 24 * 60 * 60
    static private int kSeconds_Per7days = 604800;                  // 7 days =  7 * 24 * 60 * 60


    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
    static public FwiCacheFolder cacheFolderWithPath(Context context, String path) {
        return null;
    }

    private long    _ageLimit    = kSeconds_Per7days;
    private String  _pathReady   = null;
    private String  _pathLoading = null;


    // Class's constructors
    public FwiCacheFolder(Context context, String path) {
        Boolean isMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if(isMounted) {
            File readyDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), path);
            if (!readyDir.exists()) {
                readyDir.mkdirs();
            }

            _pathReady   = readyDir.getAbsolutePath();
            _pathLoading = context.getExternalCacheDir().getAbsolutePath();
        }
        else {
            File readyDir = new File(context.getDir(Environment.DIRECTORY_DOWNLOADS, Context.MODE_PRIVATE), path);
            if (!readyDir.exists()) {
                readyDir.mkdirs();
            }

            _pathReady   = readyDir.getAbsolutePath();
            _pathLoading = context.getCacheDir().getAbsolutePath();
        }

        // Clear cache folder
        this._clearAllFilesAtPath(_pathLoading);
    }


    // Class's properties
    public String getReadyPath() {
        return _pathReady;
    }


    // Class's public methods
    /** Get path to downloaded file. */
    public String readyPathForFilename(String filename) {
        /* Condition validation */
        if (filename == null || filename.length() == 0) return null;

        filename = this._validateFilename(filename);
        return String.format("%s/%s", _pathReady, filename);
    }
    /** Get path to downloading file. */
    public String loadingPathForFilename(String filename) {
        /* Condition validation */
        if (filename == null || filename.length() == 0) return null;

        filename = this._validateFilename(filename);
        return String.format("%s/%s", _pathLoading, filename);
    }
    /** Get path to downloaded file. */
    public String loadingFinishedForFilename(String filename) {
        /* Condition validation */
        if (filename == null || filename.length() == 0) return null;
        String loadingFile = this.loadingPathForFilename(filename);
        String readyFile   = this.readyPathForFilename(filename);

        // Move file to ready folder
        File src = new File(loadingFile);
        File dst = new File(readyFile);
        if (src.exists()) {
            return (src.renameTo(dst) ? readyFile : null);
        }
        else {
            return null;
        }
    }

    /** Update modification files. */
    public void updateFile(String filename) {
        /* Condition validation */
        File file = new File(filename);
        if (!file.exists()) return;

        /* Condition validation: */
        long now = (new Date()).getTime();
        long lastModified = file.lastModified();

        long seconds = now - lastModified;
        if (seconds < (_ageLimit / 2)) return;

        file.setLastModified(now);
    }
    /** Delete all files. */
    public void clearCache() {
        this._clearAllFilesAtPath(_pathReady);
        this._clearAllFilesAtPath(_pathLoading);
    }


    // Class's private methods
    private String _validateFilename(String filename) {
        char[] chars = filename.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == ':' || c == '/') {
                chars[i] = '_';
            }
        }
        return String.valueOf(chars);
    }

    /** Delete all expired files. */
    private void _clearFolder() {

    }
    /** Delete all files at specific path. */
    private void _clearAllFilesAtPath(String filename) {
        File path = new File(filename);

        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
                else {
                    this._clearAllFilesAtPath(file);
                }
            }
        }
        else {
            path.delete();
        }
    }
    private void _clearAllFilesAtPath(File path) {

        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
                else {
                    this._clearAllFilesAtPath(file);
                }
            }
        }
        else {
            path.delete();
        }
    }
}
