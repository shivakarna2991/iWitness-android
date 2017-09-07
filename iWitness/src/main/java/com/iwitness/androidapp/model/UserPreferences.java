package com.iwitness.androidapp.model;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.services.UserServices;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.codec.FwiBase64;
import com.perpcast.lib.foundation.FwiData;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.key.FwiAESFactory;
import com.perpcast.lib.key.FwiAesKey;
import com.perpcast.lib.key.FwiKeystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;


public class UserPreferences implements Serializable {


    // <editor-fold defaultstate="collapsed" desc="Singleton">
    private static UserPreferences sharedInstance = null;

    static public synchronized UserPreferences sharedInstance() {
        /* Condition validation */
        if (sharedInstance != null) return sharedInstance;

        if (sharedInstance == null) {
            sharedInstance = new UserPreferences();
        }
        return sharedInstance;
    }
    // </editor-fold>


    // Global variables
    private FwiJson profile;
    private Context appContext;

    private FwiAesKey aesKey;
    private TreeMap<String, Object> preferences;


    // Class's constructors
    private UserPreferences() {
        appContext = AppDelegate.getAppContext();

        // Initialize AES Key
        aesKey = FwiKeystore.retrieveAesKey("__iwitness.aes.key");
        if (aesKey == null) {
            aesKey = FwiAESFactory.generateAesKey(FwiFoundation.FwiAesSize.k256, "__iwitness.aes.key");
            FwiKeystore.insertAesKey(aesKey);
        }

        // Initialize preferences
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppDelegate.kTag, Context.MODE_PRIVATE);
        String base64Data = sharedPreferences.getString(AppDelegate.kTag, null);

        if (base64Data == null || base64Data.length() == 0) {
            preferences = new TreeMap<String, Object>();
        } else {
            // Decrypted data
            FwiData encryptedData = FwiBase64.decodeBase64Data(base64Data);
            FwiData decryptedData = aesKey.decryptData(encryptedData);

            // Parse byte array to Map
            try {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(decryptedData.bytes());
                ObjectInputStream input = new ObjectInputStream(byteStream);
                preferences = (TreeMap<String, Object>) input.readObject();

                // Start services as soon as possible
                if (!TextUtils.isEmpty(this.accessToken())) {
                    Intent intent = new Intent(appContext, UserServices.class);
                    intent.setAction(UserServices.SERVICE_START);
                    appContext.startService(intent);
                }
            } catch (Exception ex) {
                preferences = new TreeMap<String, Object>();
            }
        }
    }


    // Class's properties
    public TreeMap<String, Object> getPreferences() {
        return preferences;
    }


    // <editor-fold defaultstate="collapsed" desc="Class's public methods">

    /**
     * Load/Save object value.
     */
    public Object objectForKey(String key) {
        return preferences.get(key);
    }

    public synchronized void setValue(String key, Object value) {
        /* Condition validation */
        if (key == null || key.length() == 0) return;

        if (value != null) preferences.put(key, value);
        else preferences.remove(key);
    }

    /**
     * Reset user's preferences.
     */
    public void reset() {
//        Intent intent = new Intent(appContext, UserServices.class);
//        intent.setAction(UserServices.SERVICE_STOP);
//        appContext.stopService(intent);

        appContext.stopService(new Intent(appContext, UserServices.class));

        this.setCurrentUsername(null);
        this.setCurrentUserPassword(null);
        this.setTokenType(null);
        this.setExpiredTime(null);
        this.setAccessToken(null);
        this.setRefreshToken(null);
        this.setUserProfile(null);
        this.setEventId(null);
        this.setCurrentProfileId(null);

        this.save();
    }

    /**
     * Save user's preferences.
     */
    public void save() {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(byteStream);
            output.writeObject(preferences);

            // Encrypt data
            FwiData encryptedData = aesKey.encryptData(new FwiData(byteStream.toByteArray()));

            // Save data
            SharedPreferences sharedPreferences = appContext.getSharedPreferences(AppDelegate.kTag, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(AppDelegate.kTag, FwiBase64.encodeBase64String(encryptedData));
            editor.commit();
        } catch (Exception ex) {
            // Do nothing here
        }
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="User's credentials">
    public String clientId() {
        return "ba5659b4-f5a1-11e3-bc94-000c29c9a052";
    }

    public String clientSecret() {
        return "a81af5fc-23f0-11e4-b8aa-000c29c9a052";
    }

    public String currentUsername() {
        return (String) this.objectForKey("_currentUsername");
    }

    //    public String currentPassword() {
//        return (String) this.objectForKey("_currentPassword");
//    }
    public synchronized void setCurrentUsername(String currentUsername) {
        if (currentUsername == null || currentUsername.length() == 0) {
            preferences.remove("_currentUsername");
        } else {
            this.setValue("_currentUsername", currentUsername);
        }
    }

    public synchronized void setCurrentUserPassword(String password) {
        if (password == null || password.length() == 0) {
            preferences.remove("_currentPassword");
        } else {
            this.setValue("_currentPassword", password);
        }
    }

    public String getCurrentPasword() {
        return (String) this.objectForKey("_currentPassword");
    }

//    public synchronized void setLogoutFlag(int logout) {
//        this.setValue("_logout", logout);
//
//    }

//    public int getLogoutFlag() {
//        try{
//            return (int) this.objectForKey("_logout");
//        }
//        catch (Exception e){
//            return 0;
//        }
//
//    }


    public String currentProfileId() {
        return (String) this.objectForKey("_currentProfileId");
    }

    public synchronized void setCurrentProfileId(String currentProfileId) {
        this.setValue("_currentProfileId", currentProfileId);

        if (!isServiceRunning(appContext, UserServices.class)) {
            if (!TextUtils.isEmpty(this.accessToken())) {
                Intent intent = new Intent(appContext, UserServices.class);
                intent.setAction(UserServices.SERVICE_START);
                appContext.startService(intent);
            }
        } else {

        }

    }

    public static boolean isServiceRunning(final Context ctx, final Class<?> cls) {
        final ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (final ActivityManager.RunningServiceInfo serviceInfo : services) {
            final ComponentName componentName = serviceInfo.service;
            final String serviceName = componentName.getClassName();
            if (serviceName.equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    public String tokenType() {
        return (String) this.objectForKey("_tokenType");
    }

    public synchronized void setTokenType(String tokenType) {
        if (tokenType == null || tokenType.length() == 0) {
            preferences.remove("_tokenType");
        } else {
            this.setValue("_tokenType", tokenType);
        }
    }

    public Date expiredTime() {
        return (Date) this.objectForKey("_expiredTime");
    }

    public synchronized void setExpiredTime(Date expiredTime) {
        if (expiredTime == null) {
            preferences.remove("_expiredTime");
        } else {
            this.setValue("_expiredTime", expiredTime);
        }
    }

    public String accessToken() {
        return (String) this.objectForKey("_accessToken");
    }

    public synchronized void setAccessToken(String accessToken) {
        this.setValue("_accessToken", accessToken);
    }

    public String refreshToken() {
        return (String) this.objectForKey("_refreshToken");
    }

    public synchronized void setRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.length() == 0) {
            preferences.remove("_refreshToken");
        } else {
            this.setValue("_refreshToken", refreshToken);
        }
    }
    // </editor-fold>


    public boolean isAccountExpired() {
        if (profile == null) {
            return true;
        }

        boolean isAdmin = profile.jsonWithPath("type").getString().equalsIgnoreCase("Admin") ? true : false;
        if (isAdmin) {
            return false;
        } else {
            long expiredTime = profile.jsonWithPath("subscriptionExpireAt").getInteger().longValue();
            long currentTime = (new Date()).getTime();
            long currentTimeinSec = currentTime / 1000;
            // Never expired.
            if (expiredTime == 0.0) return false;

            if (currentTimeinSec < expiredTime) {
                return false;
            } else {
                return true;
            }
        }
    }

    public FwiJson userProfile() {
        return profile;
    }

    public synchronized void setUserProfile(FwiJson profile) {
        this.profile = profile;
    }

    public boolean isFirstLogin() {
        Boolean result = (Boolean) this.objectForKey(String.format("%s/_isFirstLogin", this.currentProfileId()));
        return (result != null ? result.booleanValue() : true);
    }

    public synchronized void setFirstLogin(boolean isFirstLogin) {
        this.setValue(String.format("%s/_isFirstLogin", this.currentProfileId()), isFirstLogin);
    }

    public boolean isFirstRegistered() {
        Boolean result = (Boolean) this.objectForKey(String.format("%s/_isFirstRegistered", this.currentProfileId()));
        return (result != null ? result.booleanValue() : false);
    }

    public synchronized void setFirstRegistered(boolean isFirstRegistered) {
        this.setValue(String.format("%s/_isFirstRegistered", this.currentProfileId()), isFirstRegistered);
    }

    public String eventId() {
        return (String) this.objectForKey(String.format("%s/_eventId", this.currentProfileId()));
    }

    public synchronized void setEventId(String eventId) {
        this.setValue(String.format("%s/_eventId", this.currentProfileId()), eventId);
    }


    // ======== Should remove this
    public String receiptId() {
        return (String) this.objectForKey(String.format("%s/_receiptId", this.currentProfileId()));
    }

    public void setReceiptId(String receiptId) {
        this.setValue(String.format("%s/_receiptId", this.currentProfileId()), receiptId);
    }

    public String profileId() {
        FwiJson profile = this.userProfile();
        return (profile != null ? profile.jsonWithPath("id").getString() : null);
    }

    public String firstName() {
        FwiJson profile = this.userProfile();
        return (profile != null ? profile.jsonWithPath("firstName").getString() : "");
    }

    public String lastName() {
        FwiJson profile = this.userProfile();
        return (profile != null ? profile.jsonWithPath("lastName").getString() : "");
    }

    public String phone() {
        FwiJson profile = this.userProfile();
        return (profile != null ? profile.jsonWithPath("phone").getString() : null);
    }

    public String email() {
        FwiJson profile = this.userProfile();
        return (profile != null ? profile.jsonWithPath("email").getString() : null);
    }

    public boolean enableShake() {
        Boolean enableShake = (Boolean) this.objectForKey(String.format("%s/_enableShake", this.currentProfileId()));
        return (enableShake != null ? enableShake.booleanValue() : true);
    }

    public void setEnableShake(boolean enableShake) {
        this.setValue(String.format("%s/_enableShake", this.currentProfileId()), Boolean.valueOf(enableShake));
    }

    public boolean enableTap() {
        Boolean enableTap = (Boolean) this.objectForKey(String.format("%s/_enableTap", this.currentProfileId()));
        return (enableTap != null ? enableTap.booleanValue() : true);
    }

    public void setEnableTap(boolean enableTap) {
        this.setValue(String.format("%s/_enableTap", this.currentProfileId()), Boolean.valueOf(enableTap));
    }

    public boolean enableEmergencyContact() {
        Boolean enableEmergencyContact = (Boolean) this.objectForKey(String.format("%s/_enableEmergencyContact", this.currentProfileId()));
        return (enableEmergencyContact != null ? enableEmergencyContact.booleanValue() : true);
    }

    public void setEnableEmergencyContact(boolean enableEmergencyContact) {
        this.setValue(String.format("%s/_enableEmergencyContact", this.currentProfileId()), Boolean.valueOf(enableEmergencyContact));
    }

    public boolean enableSoundAndAlarm() {
        Boolean enableSoundAndAlarm = (Boolean) this.objectForKey(String.format("%s/_enableSoundAndAlarm", this.currentProfileId()));
        return (enableSoundAndAlarm != null ? enableSoundAndAlarm.booleanValue() : true);
    }

    public void setEnableSoundAndAlarm(boolean enableSoundAndAlarm) {
        this.setValue(String.format("%s/_enableSoundAndAlarm", this.currentProfileId()), Boolean.valueOf(enableSoundAndAlarm));
    }

    public boolean enableTorch() {
        Boolean enableTorch = (Boolean) this.objectForKey(String.format("%s/_enableTorch", this.currentProfileId()));
        return (enableTorch != null ? enableTorch.booleanValue() : false);
    }

    public void setEnableTorch(boolean enableTorch) {
        this.setValue(String.format("%s/_enableTorch", this.currentProfileId()), Boolean.valueOf(enableTorch));
    }

    public boolean enablebackFacingCamera() {
        Boolean enableBackfacingCamera = (Boolean) this.objectForKey(String.format("%s/_enableTorch", this.currentProfileId()));
        return (enableBackfacingCamera != null ? enableBackfacingCamera.booleanValue() : false);
    }

    public void setEnablebackFacingCamera(boolean enablebackFacingCamera) {
        this.setValue(String.format("%s/enablebackFacingCamera", this.currentProfileId()), Boolean.valueOf(enablebackFacingCamera));
    }



    public int getRecordIndex() {
        Integer result = (Integer) this.objectForKey(String.format("%s/_recordIndex", this.currentProfileId()));
        return (result != null ? result.intValue() : -1);
    }

    public void setRecordIndex(int recindex) {
        this.setValue(String.format("%s/_recordIndex", this.currentProfileId()), new Integer(recindex));
    }

    public int getEmergencyIndex() {
        Integer result = (Integer) this.objectForKey(String.format("%s/_emergencyIndex", this.currentProfileId()));
        return (result != null ? result.intValue() : -1);
    }

    public void setEmergencyIndex(int emergencyIndex) {
        this.setValue(String.format("%s/_emergencyIndex", this.currentProfileId()), new Integer(emergencyIndex));
    }

    public String getEmergencyLocation() {
        String currentLocation = (String) this.objectForKey(String.format("%s/_emergencyLocation", this.currentProfileId()));
        return (!TextUtils.isEmpty(currentLocation) ? currentLocation : "United States");
    }

    public void setEmergencyLocation(String emergencyLocation) {
        this.setValue(String.format("%s/_emergencyLocation", this.currentProfileId()), emergencyLocation);
    }

    public int recordTime() {
        Integer recordTime = (Integer) this.objectForKey(String.format("%s/_recordTime", this.currentProfileId()));
        return (recordTime != null ? recordTime.intValue() : (Configuration.VIDEO_RECORD_DURATION * 60)); //Min * secs per min * Millisec per sec
//        return (recordTime != null ? recordTime.intValue() : (Configuration.VIDEO_RECORD_DURATION *60)); //Min
    }

    public void setRecordTime(int recordTime) {
        /* Condition validation */
        if (this.currentProfileId() == null || this.currentProfileId().length() == 0) return;
        this.setValue(String.format("%s/_recordTime", this.currentProfileId()), Integer.valueOf(recordTime));
    }

    public int getrecordTime_New() {
        Integer recordTime = (Integer) this.objectForKey(String.format("%s/_recordTime", this.currentProfileId()));
        return (recordTime != null ? recordTime.intValue() : (Configuration.VIDEO_RECORD_DURATION)); //Min * secs per min * Millisec per sec
//        return (recordTime != null ? recordTime.intValue() : (Configuration.VIDEO_RECORD_DURATION *60)); //M
    }

    public void setRecordTime_New(int recordTime) {
        /* Condition validation */
        if (this.currentProfileId() == null || this.currentProfileId().length() == 0) return;
        this.setValue(String.format("%s/_recordTime", this.currentProfileId()), Integer.valueOf(recordTime));
    }


    public String getProfileUsername() {
        String result = (String) this.objectForKey(String.format("%s/_emergencyNumber", this.currentProfileId()));
        return (!TextUtils.isEmpty(result) ? result : "911");
    }

    public String getEmergencyNumber() {
        String result = (String) this.objectForKey(String.format("%s/_emergencyNumber", this.currentProfileId()));
        return (!TextUtils.isEmpty(result) ? result : "911");
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.setValue(String.format("%s/_emergencyNumber", this.currentProfileId()), emergencyNumber);
    }

    public String getEmergencyNumberLocation() {
        String result = (String) this.objectForKey(String.format("%s/_emergencyNumberLocation", this.currentProfileId()));
        return (!TextUtils.isEmpty(result) ? result : "911");
    }

    public void setEmergencyNumberLocation(String emergencyNumber) {
        this.setValue(String.format("%s/_emergencyNumberLocation", this.currentProfileId()), emergencyNumber);
    }

    public String getTimeZone() {
        FwiJson profile = this.userProfile();
        if (profile != null) {
            String timezone = profile.jsonWithPath("timezone").getString();
            if (!TextUtils.isEmpty(timezone)) {
                return timezone;
            }
        }
        return "America/Los_Angeles";
    }
}
