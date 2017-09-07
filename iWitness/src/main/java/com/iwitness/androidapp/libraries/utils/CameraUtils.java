package com.iwitness.androidapp.libraries.utils;


import android.hardware.Camera;
import android.os.Build;
import android.util.Log;


public class CameraUtils {
    static public final int CAMERA_INDEX = 0;


    static public Camera openCamera() {
        Camera camera=null;
        try {
            if (Build.VERSION.SDK_INT > 8) {
                camera = Camera.open(CAMERA_INDEX);
            } else {
                camera = Camera.open();
            }
            if (camera == null) {
                camera = Camera.open();
            }
        }catch(Exception ex){
            Log.e("CameraUtils", "Camera.open() fail");
        }
        return camera;
    }
}
