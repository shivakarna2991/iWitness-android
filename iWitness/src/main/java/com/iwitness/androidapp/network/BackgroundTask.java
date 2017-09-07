//  Project name: Workspace
//  File name   : BackgroundTask.java
//
//  Author      : Phuc
//  Created date: 6/6/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.network;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.iwitness.androidapp.AppDelegate;
import com.perpcast.lib.services.FwiRESTService;
import com.perpcast.lib.services.FwiService;
import com.perpcast.lib.services.request.FwiRequest;

import java.util.UUID;


public class BackgroundTask extends AsyncTask<Void, Integer, Object> {


    // Global variables
    protected UUID taskId;
    protected FwiService service;
    protected TaskDelegate delegate;


    // Class's constructors
    public BackgroundTask(FwiRequest request) {
        this(new FwiRESTService(request));
    }
    public BackgroundTask(FwiService service) {
        this(service, UUID.randomUUID());
    }
    public BackgroundTask(FwiRequest request, UUID taskId) {
        this(new FwiRESTService(request), taskId);
    }
    public BackgroundTask(FwiService service, UUID taskId) {
        this.taskId  = taskId;
        this.service = service;
    }


    // Class's public methods
    public void run(TaskDelegate delegate) {
        this.delegate = delegate;
        this.executeOnExecutor(AppDelegate.getThreadPool());
    }


    // Class's override methods
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Object doInBackground(Void... params) {
        return service.getResource();
    }
    @Override
    protected void onPostExecute(Object response) {
        final BackgroundTask weakThis = this;
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {
                if (!weakThis.isCancelled() && delegate != null) {
                    delegate.taskDidFinish(taskId, service.status(), message.obj);
                }
                return true;
            }
        });
        Message message = handler.obtainMessage();
        message.obj = response;
        handler.sendMessage(message);
        super.onPostExecute(response);
    }


}
