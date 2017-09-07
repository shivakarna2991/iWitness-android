package com.iwitness.androidapp.services;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.libraries.cache.FwiCacheFolder;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiData;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.FwiRESTService;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiFormParam;
import com.perpcast.lib.services.request.FwiMultipartParam;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;


public class UserServices extends Service {
    static public final String SERVICE_START = "Start Service";
    static public final String SERVICE_STOP = "Stop Service";
    static private final String TAG = "UserServices";
    private FwiCacheFolder _cacheFolder;
    private CountDownTimer timer;

    private boolean isUploading;
    private boolean isRefresh;
    private boolean isReload;

    // Class's constructors
    public UserServices() {
        super();
        isReload = false;
        isRefresh = false;
        isUploading = false;
    }

    @Override
    public void onCreate() {
        isReload = false;
        isRefresh = false;
        isUploading = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();
        if (action.compareTo(SERVICE_START) == 0) {
            // Backward compatible
            String profileId = UserPreferences.sharedInstance().currentProfileId();
            if (profileId == null || profileId.length() == 0) {
                TreeMap<String, Object> preferences = UserPreferences.sharedInstance().getPreferences();
                FwiJson profile = (FwiJson) preferences.get("_userProfile");
                if (profile != null) {
                    profileId = profile.jsonWithPath("id").getString();
                    Log.e(TAG, "profileId." + profileId);
                } else {
                    Log.e(TAG, "profile = null..");
                    return 0;

                }
            }

            // Load cache folder
            _cacheFolder = new FwiCacheFolder(AppDelegate.getAppContext(), profileId);

            /* Condition validation: Ignore if timer is already assigned */
            if (timer != null) return START_NOT_STICKY;

            final UserServices weakThis = this;
            timer = new CountDownTimer(Integer.MAX_VALUE, 1000) {

                @Override
                public void onTick(long l) {
                    weakThis.doRefreshAccessToken();
                    //weakThis.doReloadProfile();
                    weakThis.doReloadProfileSingleUser();
                    weakThis.doUploadVideos();
                }

                @Override
                public void onFinish() {
                }
            };
            timer.start();
        } else {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Class's private methods
    private synchronized void doRefreshAccessToken() {
        final UserPreferences preferences = UserPreferences.sharedInstance();

        Date now = new Date();
        Date expiredTime = preferences.expiredTime();

        /* Condition validation: Validate the expired time */
        if (expiredTime == null || (expiredTime.getTime() - now.getTime()) >= 180.0f) return;

        /* Condition validation */
        if (isRefresh) return;
           isRefresh = true;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                FwiJson requestInfo = FwiJson.Object(
                        "refresh_token", FwiJson.String(preferences.refreshToken()),
                        "client_id", FwiJson.String(preferences.clientId()),
                        "client_secret", FwiJson.String(preferences.clientSecret()),
                        "grant_type", FwiJson.String("refresh_token")
                );

                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost, Configuration.kService_Authorization, Configuration.kHostname);
                request.setDataParam(FwiDataParam.paramWithJson(requestInfo));

                FwiRESTService service = new FwiRESTService(request);
                FwiJson response = service.getResource();

                FwiJson refreshTokenValidation = FwiJson.Object(
                        "access_token", FwiJson.String(),
                        "token_type", FwiJson.String(),
                        "expires_in", FwiJson.Integer(),
                        "scope", FwiJson.Null()
                );

                if (response != null && response.isLike(refreshTokenValidation)) {
                    // Persist authorization code
                    preferences.setTokenType(response.jsonWithPath("token_type").getString());
                    preferences.setAccessToken(response.jsonWithPath("access_token").getString());

                    // Expire time
                    Date now = new Date();
                    Date expiredTime = new Date(now.getTime() + (response.jsonWithPath("expires_in").getInteger().longValue() * 1000));
                    preferences.setExpiredTime(expiredTime);

                    // Save
                    preferences.save();
                }

                // Release log
                isRefresh = false;
                return null;
            }
        };
        task.executeOnExecutor(AppDelegate.getThreadPool());
    }

    private synchronized void doReloadProfile() {
        /* Condition validation */
        //  if (isReload || UserPreferences.sharedInstance().userProfile() != null) return;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet, Configuration.kService_User, Configuration.kHostname, UserPreferences.sharedInstance().currentProfileId());
                FwiRESTService service = new FwiRESTService(request);
                FwiJson response = service.getResource();

                if (response != null && !response.isLike(FwiJson.Null())) {
                    FwiJson profile = response;

                    UserPreferences userPreferences = UserPreferences.sharedInstance();

                    userPreferences.setCurrentUsername(profile.jsonWithPath("phone").getString());
                    userPreferences.setCurrentProfileId(profile.jsonWithPath("id").getString());
                    userPreferences.setUserProfile(profile);
                    userPreferences.save();
                }

                // Release log
                isReload = false;
                return null;
            }
        };
        task.executeOnExecutor(AppDelegate.getThreadPool());
    }

    private synchronized void doReloadProfileSingleUser() {
        if (isReload) {
            return;
        }

        if (!AppDelegate.isLoginScreen) {
            isReload = true;
            if (UserPreferences.sharedInstance().currentProfileId() != null) {
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet, Configuration.kService_User, Configuration.kHostname, UserPreferences.sharedInstance().currentProfileId());
                        FwiRESTService service = new FwiRESTService(request);
                        FwiJson response = service.getResource();

                        if (response != null && !response.isLike(FwiJson.Null())) {
                            FwiJson profile = response;
                            UserPreferences userPreferences = UserPreferences.sharedInstance();
                            if (profile.jsonWithPath("detail").toString().equalsIgnoreCase("User not logged in")) {
                                Intent dialogIntent = new Intent(AppDelegate.getAppContext(), LoginController.class);
                                dialogIntent.putExtra("userNotloggedin", profile.jsonWithPath("detail").toString());
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dialogIntent);
                                return null;
                            }
                            try {
                                userPreferences.setCurrentUsername(profile.jsonWithPath("phone").getString());
                                userPreferences.setCurrentProfileId(profile.jsonWithPath("id").getString());
                                userPreferences.setUserProfile(profile);
                                userPreferences.save();

                            } catch (Exception e) {

                            }

                        }

                        // Release log
                        isReload = false;
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                    }
                };


                task.executeOnExecutor(AppDelegate.getThreadPool());
            } else {
                isReload = false;
            }
        }
    }

    private synchronized void doUploadVideos() {
        final UserPreferences preferences = UserPreferences.sharedInstance();
        /* Condition validation */
        if (isUploading || preferences.accessToken() == null)
            return;

        isUploading = true;


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String path = _cacheFolder.getReadyPath();
                File userDir = new File(path);
                File[] events = userDir.listFiles();
                if (events.length > 0) {
                    File event = events[0];
                    Log.d(TAG, "event :" + event.getName());
                    File[] parts = event.listFiles();
                    if (parts.length > 0) {
                        // file by sort
                        Arrays.sort(parts);                        //
                        Arrays.sort(parts, new Comparator<File>() {
                            @Override
                            public int compare(File lhs, File rhs) {
                                return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified());
                            }
                        });
                        int i = 0;
                        int n = parts.length;
                        int retrycount = 0;
//                        for(File part:parts)
                        while (i < n) {
                            retrycount++;
                            File part = parts[i];
//                        Log.d(TAG, "part index i=" + (i + 1) + "  n=" + n);
                            String[] tokens = part.getName().split("_");
                            String filename = String.format("%s.mp4", tokens[0]);
                            String lat = (tokens.length >= 3 ? tokens[1] : null);
                            String lng = (tokens.length >= 3 ? tokens[2] : null);

                            try {
                                // Load binary
                                byte[] content = new byte[(int) part.length()];
                                DataInputStream input = new DataInputStream(new FileInputStream(part));
                                input.readFully(content, 0, content.length);
                                input.close();

                                // Generate multi-parts request
                                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost, Configuration.kService_Asset, Configuration.kHostname);
                                request.addMultipartParams(
                                        FwiMultipartParam.param("media", filename, new FwiData(content), "video/mp4")
                                );

                                if (tokens.length < 3) {
                                    request.addFormParams(
                                            FwiFormParam.param("event-id", part.getParentFile().getName())
                                    );
                                } else {
                                    request.addFormParams(
                                            FwiFormParam.param("event-id", part.getParentFile().getName()),
                                            FwiFormParam.param("lat", lat),
                                            FwiFormParam.param("lng", lng)
                                    );
                                }

                                // Send request
                                FwiRESTService service = new FwiRESTService(request);
                                FwiJson response = service.getResource();

//                               Log.e("response..........", "uploaded.." + response.toString());
                                // Delete part if success
                                if (response != null && service.status() == HttpStatus.SC_CREATED) {
                                    Log.e(TAG, "Upload success file name:" + part.getAbsolutePath() + "    size = " + part.length());
                                    part.delete();
                                    i++;
                                    retrycount = 0;
                                }
                                 else if (response != null && !response.isLike(FwiJson.Null())) {
                                    FwiJson userResponse = response;
                                    if (userResponse.jsonWithPath("detail").toString().equalsIgnoreCase("User not logged in")) {
                                            Intent dialogIntent = new Intent(AppDelegate.getAppContext(), LoginController.class);
                                            dialogIntent.putExtra("userNotloggedin", userResponse.jsonWithPath("detail").toString());
                                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(dialogIntent);
                                            Process.killProcess(Process.myPid());
                                            return null;
                                    }
                                }
                                else {
                                    isUploading = false;
                                    Log.e(TAG, "Upload Fail file name:" + part.getAbsolutePath() + "    size = " + part.length());
//                                if(retrycount>=3){
//                                    retrycount=0;
//                                    i++;
//                            }
                                    part = null;
                                    if (content != null) {
                                        content = null;
                                    }
                                    System.gc();
                                    Thread.sleep(3000);
                                }
                            } catch (Exception ex) {
                                Crittercism.logHandledException(ex);
                            }
                        }
                    }

                    // Delete an event
                    String currentId = UserPreferences.sharedInstance().eventId();
                    if (currentId != null && currentId.compareTo(event.getName()) == 0) {
                        // Do nothing
                    } else {
                        event.delete();
                    }
                }

                // Release log
                isUploading = false;
                return null;
            }
        };
        task.executeOnExecutor(AppDelegate.getThreadPool());
    }

}
