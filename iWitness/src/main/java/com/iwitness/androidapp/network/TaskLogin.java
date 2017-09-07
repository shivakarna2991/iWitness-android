package com.iwitness.androidapp.network;


import android.content.Context;

import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.FwiRESTService;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.util.Date;


public class TaskLogin extends ForegroundTask {


    // Global variables
    private String phone;


    public TaskLogin(Context context, FwiRequest request, String phone) {
        super(context, request);
        this.phone = phone;
    }


    // Class's override methods
    @Override
    protected Object doInBackground(Void... params) {
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        FwiJson response = (FwiJson) service.getResource();

        /* Condition validation: Validate response status */
        if (service.status() != HttpStatus.SC_OK) return response;

        // 1. Update authorization code
        userPreferences.setTokenType(response.jsonWithPath("token_type").getString());
        userPreferences.setAccessToken(response.jsonWithPath("access_token").getString());
        userPreferences.setRefreshToken(response.jsonWithPath("refresh_token").getString());

        // 2. Calculate expire time
        Date now = new Date();
        Date expiredTime = new Date(now.getTime() + (response.jsonWithPath("expires_in").getInteger().longValue() * 1000));
        userPreferences.setExpiredTime(expiredTime);

        // 3. Save
        userPreferences.setFirstRegistered(false);
        userPreferences.save();

        // 4. Download user's profile
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet, Configuration.kService_UserInfo, Configuration.kHostname, phone);
        service = new FwiRESTService(request);

        /* Condition validation: Validate response status */
        response = (FwiJson) service.getResource();
        if (service.status() != HttpStatus.SC_OK) {
           // userPreferences.reset();
            return response;
        }
        FwiJson profile = response.jsonWithPath("_embedded/user/0");

        userPreferences.setCurrentUsername(profile.jsonWithPath("phone").getString());
        userPreferences.setCurrentProfileId(profile.jsonWithPath("id").getString());
        userPreferences.save();
        userPreferences.setUserProfile(profile);
        userPreferences.save();
        return profile;
    }
}
