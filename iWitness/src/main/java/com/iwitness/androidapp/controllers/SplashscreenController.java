//  Project name: Workspace
//  File name   : SplashscreenController.java
//
//  Author      : Phuc
//  Created date: 6/9/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.HomeContainerController;
import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.controllers.common.OnBoardingScreensActivity;
import com.iwitness.androidapp.libraries.utils.SharedPrefUtils;
import com.iwitness.androidapp.model.Notifications;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.services.CheckAppStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;


public class SplashscreenController extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 800;
    public HashMap<String, String> hashNotifications = new HashMap<String, String>();
    ArrayList<String> arrayListNames = new ArrayList<String>();
    String[] notificationNames = {
            AppDelegate.getAppContext().getResources().getString(R.string.optimize),
            AppDelegate.getAppContext().getResources().getString(R.string.tip),
            AppDelegate.getAppContext().getResources().getString(R.string.disclimer)};

    UserPreferences userPreferences;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    List<String> permissionsNeeded = new ArrayList<String>();
    List<String> permissionsList = new ArrayList<String>();
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        userPreferences = UserPreferences.sharedInstance();

        startService(new Intent(getBaseContext(), CheckAppStatus.class));

        //check runtime permissions
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read PhoneState");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read External Storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Record Audio");

        int currentAPIVersion = Build.VERSION.SDK_INT;
            if(currentAPIVersion>= Build.VERSION_CODES.M) {
                if (!isPermissionsAllowed()) {
                    if (permissionsList.size() > 0) {
                        if (permissionsNeeded.size() > 0) {
                            // Need Rationale
                            String message = "You need to grant access to " + permissionsNeeded.get(0);
                            for (int i = 1; i < permissionsNeeded.size(); i++)
                                message = message + ", " + permissionsNeeded.get(i);
                            showAlert("iWitness", message);
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                        return;
                    }
                }

                else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //User logged in.
                            if (userPreferences.accessToken() != null) {
                                Intent intent = new Intent(SplashscreenController.this, HomeContainerController.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashscreenController.this, LoginController.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                                finish();
                            }

                        }
                    }, SPLASH_TIME_OUT);

                }

            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //User logged in.
                        if (userPreferences.accessToken() != null) {
                            Intent intent = new Intent(SplashscreenController.this, HomeContainerController.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashscreenController.this, LoginController.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                            finish();
                        }
                    }
                }, SPLASH_TIME_OUT);

            }


    }
    public void showAlert(String title,String Message)
    {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }

        return true;
    }

    // to allow runtime permissions
    private boolean isPermissionsAllowed() {
        int result,result1,result2,result3,result4,result5,result6,result7;
        result = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ;
        result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) ;
        result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) ;
        result3 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) ;
        result4 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) ;
        result5 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) ;
        result6 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ;
        result7 = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) ;

        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&
            result2 == PackageManager.PERMISSION_GRANTED && result3== PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
                && result5 == PackageManager.PERMISSION_GRANTED  && result6 == PackageManager.PERMISSION_GRANTED
                && result7 == PackageManager.PERMISSION_GRANTED)  {
            return true;
        }
        //If permission is not granted returning false
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                    // Check for ACCESS_FINE_LOCATION
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        // All Permissions Granted
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //User logged in.
                                if (userPreferences.accessToken() != null) {
                                    Intent intent = new Intent(SplashscreenController.this, HomeContainerController.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(SplashscreenController.this, LoginController.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                                    finish();
                                  }

                            }
                        }, SPLASH_TIME_OUT);

                    } else {
                        // Permission Denied
                        // All Permissions Granted
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //User logged in.
                                if (userPreferences.accessToken() != null) {
                                    Intent intent = new Intent(SplashscreenController.this, HomeContainerController.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashscreenController.this);
                                    boolean isOnBoardingScreenCheckedAlready = prefs.getBoolean("isOnBoardingScreensChecked", false);
                                    Intent intent;

                                    if(isOnBoardingScreenCheckedAlready){
                                         intent = new Intent(SplashscreenController.this, LoginController.class);
                                    } else {
                                         intent = new Intent(SplashscreenController.this, OnBoardingScreensActivity.class);
                                    }

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                                    finish();
                                }

                            }
                        }, SPLASH_TIME_OUT);
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // Called when the activity is becoming visible to the user.
    @Override
    protected void onStart() {
        super.onStart();
        if (userPreferences.accessToken() == null) {

            hashNotifications = SharedPrefUtils.getHashmapValues(SharedPrefUtils.IWITNESS_PREF);
            arrayListNames = SharedPrefUtils.getVectorVales(SharedPrefUtils.IWITNESS_PREF);

            if (arrayListNames == null || arrayListNames.size() == 0) {
                for (int i = 0; i < notificationNames.length; i++) {
                    arrayListNames.add(notificationNames[i]);
                }
                SharedPrefUtils.setVectorValues(SharedPrefUtils.IWITNESS_PREF, arrayListNames);
            }

            try {
                ArrayList<String> Names = SharedPrefUtils.getVectorVales(SharedPrefUtils.IWITNESS_PREF);
                if (hashNotifications.size() == 0 || hashNotifications == null) {
                    Notifications objNotifications = new Notifications();
                    for (int i = 0; i < Names.size(); i++) {
                        objNotifications.notificationName = Names.get(i);
                        objNotifications.Number = i;
                        if (i < 2) {
                            objNotifications.Readstatus = "false";
                        } else {
                            objNotifications.Readstatus = "true";
                        }
                        hashNotifications.put(objNotifications.notificationName, objNotifications.Readstatus);
                        SharedPrefUtils.setHashMapValues(SharedPrefUtils.IWITNESS_PREF, hashNotifications);
                        hashNotifications = SharedPrefUtils.getHashmapValues(SharedPrefUtils.IWITNESS_PREF);
                    }
                }
            } catch (Exception e) {

            }


        }



    }
    // Called when the activity will startRecording interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Called when the system is about to startRecording resuming a previous activity.
    @Override
    protected void onPause() {
        super.onPause();
    }

    // Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
    @Override
    protected void onStop() {
        super.onStop();
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Called after your activity has been stopped, prior to it being started again.
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    // <editor-fold defaultstate="collapsed" desc="Class's override public methods">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}