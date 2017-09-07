package com.iwitness.androidapp.controllers.common;

import android.support.annotation.NonNull;

/**
 * Created by prameela on 8/26/2016.
 */
public interface PermissionRequestor{
    void request(String permission
            , int requestcode
            , String message
            , PermissionListener callback);
    void _onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                     @NonNull int[] grantResults);

}