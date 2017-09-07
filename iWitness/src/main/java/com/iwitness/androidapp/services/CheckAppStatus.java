package com.iwitness.androidapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.iwitness.androidapp.model.UserPreferences;

/**
 * Created by samadhanmalpure on 2017-07-28.
 */

public class CheckAppStatus extends Service {

    private UserPreferences userPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       userPreferences = UserPreferences.sharedInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        userPreferences.setEnableTap(true);
        userPreferences.setEnableSoundAndAlarm(true);
        userPreferences.setEnableEmergencyContact(true);
        userPreferences.save();
        stopSelf();
    }
}
