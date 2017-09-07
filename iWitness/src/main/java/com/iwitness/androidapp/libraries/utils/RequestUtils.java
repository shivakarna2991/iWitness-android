//  Project name: Workspace
//  File name   : RequestUtils.java
//
//  Author      : Phuc
//  Created date: 6/9/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.libraries.utils;


import android.os.Build;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.model.UserPreferences;
import com.perpcast.lib.FwiFoundation.FwiHttpMethod;
import com.perpcast.lib.services.request.FwiRequest;


public final class RequestUtils {

    /** Centralize generate http request. */
    static public FwiRequest generateHttpRequest(FwiHttpMethod method, String uri, String ... params) {
        FwiRequest request = FwiRequest.requestWithURL(method, String.format(uri, (Object[])params));
        request.setHeader("User-Agent", String.format("iWitness/2.0.8 (%s %s; Android %d; Scale/%.2f)",
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.SDK_INT,
                AppDelegate.getAppContext().getResources().getDisplayMetrics().density));

        UserPreferences userPreferences = UserPreferences.sharedInstance();
        if (userPreferences.accessToken() != null) {
//            Toast.makeText(AppDelegate.getAppContext(),"Authorization..."+userPreferences.tokenType()+userPreferences.accessToken(),Toast.LENGTH_LONG).show();
            request.addHeader("Authorization", String.format("%s %s", userPreferences.tokenType(), userPreferences.accessToken()));
        }
        return request;
    }
}
