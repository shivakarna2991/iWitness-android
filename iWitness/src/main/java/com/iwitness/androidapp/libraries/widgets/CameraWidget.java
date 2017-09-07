//  Project name: Workspace
//  File name   : CameraWidget.java
//
//  Author      : Phuc
//  Created date: 6/9/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------
//
//  Instruction:
//  <uses-feature android:name="android.hardware._camera" />
//  <uses-feature android:name="android.hardware._camera.autofocus" />
//
//  <uses-permission android:name="android.permission.CAMERA" />
//  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

package com.iwitness.androidapp.libraries.widgets;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.crittercism.app.Crittercism;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.libraries.FwiLocation;
import com.iwitness.androidapp.libraries.cache.FwiCacheFolder;
import com.iwitness.androidapp.libraries.utils.CameraUtils;

import java.io.IOException;
import java.util.List;


public class CameraWidget extends SurfaceView implements SurfaceHolder.Callback {
    static private final String[] SAMSUNG_GALAXY_S2 = new String[]{"SGH-I777", "SGH-I727", "SGH-I927", "SPH-D710", "SGH-T989", "SCH-R760", "GT-I9105/P"};
    static private final String[] CAMERA_BLUR_DEVICES = new String[]{"GT-I8262"};
    static private final String TAG = "CameraWidget";


    // Global variables
    private int _direction = 0;
    private int width = 0;
    private int height = 0;
    private boolean _isRecording = false;
    Size mSize;

    private FwiLocation _location = null;

    private static Camera _camera = null;
    private MediaRecorder _recorder = null;

    private FwiCacheFolder _cacheFolder = null;
    private CameraWidgetDelegate _delegate = null;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraWidget(Context context) {
        this(context, null);
        _initialControl();
    }

    public CameraWidget(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
        _initialControl();
    }

    public CameraWidget(Context context, AttributeSet attributes, int defineStyle) {
        super(context, attributes, defineStyle);
        _initialControl();
    }


    public void setCacheFolder(FwiCacheFolder cacheFolder) {
        this._cacheFolder = cacheFolder;
    }

    public void setDelegate(CameraWidgetDelegate delegate) {
        this._delegate = delegate;
    }

    public void setLocation(FwiLocation location) {
        this._location = location;
    }
    /**
     * Turn on/off torch
     */
    public synchronized void torchOn() {
        /* Condition validation */
        //before code
//        if (_camera == null) return;
        //Added newly
        try {
            // Open camera
            if (_camera == null) {
                Log.e("CameraWidget", "before camera 3");
                _camera = CameraUtils.openCamera();
                _camera.startPreview();
                Log.e("CameraWidget", "after camera 3");
                if (_camera == null) {
                    Log.e(TAG, "CameraUtils.openCamera fail.. Not able to proceeed");
                    return;
                }
            }
        } catch (Exception e) {

        }

        Parameters params = null;
        try {
            params = _camera.getParameters();
        } catch (Exception e) {
            Log.e(TAG, "ex:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        List<String> list = null;

        if (params != null) {
            list = params.getSupportedFlashModes();
        }
        if (list != null && list.contains(Parameters.FLASH_MODE_TORCH)) {
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            _camera.setParameters(params);
        } else if (list != null && list.contains(Parameters.FLASH_MODE_ON)) {
            params.setFlashMode(Parameters.FLASH_MODE_ON);
            _camera.setParameters(params);
        }


    }

    public synchronized void torchOff() {
        /* Condition validation */
        if (_camera == null) return;

        Parameters params = _camera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        _camera.setParameters(params);
    }

    /**
     * Stop recording video.
     */
    public synchronized void stopRecording() {
        /* Condition validation */
        if (!_isRecording) return;
        _recorder.reset();
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM, true);
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_MUSIC, true);
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_RING, true);
        AudioManager audioMgr = ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE));

        _recorder.release();
        _recorder = null;

        if (_camera != null) {
            _camera.lock();
        }
        _isRecording = false;

        // Notify delegate
        if (_delegate != null) {
            String path = _cacheFolder.loadingPathForFilename("video.mp4");
            _delegate.cameraManagerDidFinishRecording(this, path);
        }
    }

    /*
       switch camera method
     */
    public void switchCamera(){
        _camera.stopPreview();
        _camera.release();

        //swap the id of the camera to be used
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        _camera = Camera.open(currentCameraId);


        //set the orientation as per device orientation.
        setCameraDisplayOrientation((Activity) getContext(), currentCameraId, _camera);

        try {
            //this step is critical or preview on new camera will no know where to render to
            _camera.setPreviewDisplay(getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        _camera.startPreview();

    }



    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    //TODO- sam code end

    /**
     * Start recording video.
     */
    public synchronized void startRecording() {
        /* Condition validation */
        if (_isRecording) return;

        // Prepare recorder
        _recorder = new MediaRecorder();
        if (_camera != null) {
            _camera.unlock();
        } else {
            Log.e(TAG, "_camera = null.. Get camera again");
            _camera = CameraUtils.openCamera();
            ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_MUSIC, true);
            ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_RING, true);
            AudioManager audioMgr = ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE));
            _camera.unlock();
        }
        _recorder.reset();
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM, true);
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_MUSIC, true);
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_RING, true);
        AudioManager audioMgr = ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE));

        // Define input source
        _recorder.setCamera(_camera);
        _recorder.setPreviewDisplay(this.getHolder().getSurface());
        _recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        _recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile camcorderProfile = null;

        if (Build.VERSION.SDK_INT > 8) {
            camcorderProfile = CamcorderProfile.get(CameraUtils.CAMERA_INDEX, CamcorderProfile.QUALITY_QVGA);
        } else {
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
        }

        camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
        camcorderProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        _recorder.setProfile(camcorderProfile);
//        _recorder.setVideoSize(640, 480);
        _recorder.setVideoSize(mSize.width, mSize.height);
        _recorder.setMaxDuration(10000);    // 10 seconds
        _recorder.setMaxFileSize(20000000); // 20M

        if (_location != null) {
            _recorder.setLocation((float) _location.getLatitude(), (float) _location.getLongitude());
        }
        switch (_direction) {
            case Surface.ROTATION_90:
                // Do nothing
                break;

            case Surface.ROTATION_180:
                // Unknown
                break;

            case Surface.ROTATION_270:
                _recorder.setOrientationHint(180);
                break;

            case Surface.ROTATION_0:
                _recorder.setOrientationHint(90);
                break;
        }

        // Define temp video
        String path = _cacheFolder.loadingPathForFilename("video.mp4");
        _recorder.setOutputFile(path);

        try {
            _recorder.prepare();
            _recorder.start();

            _isRecording = true;

            // Notify delegate
            if (_delegate != null) {
                _delegate.cameraManagerDidBeginRecording(this);
            }
        } catch (Exception ex) {
            Log.e(TAG, "ex:" + ex.getMessage());
            Crittercism.logHandledException(ex);
            _recorder.reset();   // clear recorder configuration
            _recorder.release(); // release the recorder object
            _recorder = null;
            _camera.lock();
            this.stopRecording();
        }
    }

    private void _initialControl() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    private Size _optimalSize(List<Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) width / height;
        if (sizes == null) return null;

        Size optimal = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimal = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimal == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimal = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimal;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /* Condition validation: Validate camera hardware available */
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // There is no camera
            return;
        }

        /* Condition validation: Validate number of camera */
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 1) {
            // There is camera feature, but camera might broken
            return;
        }

        // Open camera
        if (_camera == null) {
            Log.e("CameraWidget", "before camera 3");
            _camera = CameraUtils.openCamera();
            try {
                _camera.setPreviewDisplay(holder);
                _camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("CameraWidget", "after camera 3");
            if (_camera == null) {
                Log.e(TAG, "CameraUtils.openCamera fail.. Not able to proceeed");
                return;
            }
        }

        try {
            // Enable continuous video focus mode
            Parameters params = _camera.getParameters();
            List<String> list = params.getSupportedFocusModes();

            if (list != null && list.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                _camera.setParameters(params);
            }

            // start
            List<Size> sizes = params.getSupportedPictureSizes();
            // Iterate through all available resolutions and choose one.
            // The chosen resolution will be stored in mSize.

            for (Size size : sizes) {
                Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
                if (size.width <= 500 && size.width >= 300) {
                    mSize = size;
                }
            }

            if (mSize == null) {
                if (sizes != null && sizes.size() > 0) {
                    mSize = sizes.get(sizes.size() - 1);
                    Log.i(TAG, "surfaceCreated:PictureSize resolution: " + mSize.width + " " + mSize.height);
                    params.setPictureSize(mSize.width, mSize.height);
                    _camera.setParameters(params);
                }
            }
            // end

            String build = Build.MODEL;

            // Prepare camera
            _camera.setPreviewDisplay(holder);

            boolean isSG2 = false;
            for (String model : SAMSUNG_GALAXY_S2) {
                if (build.equalsIgnoreCase(model)) {
                    isSG2 = true;
                    break;
                }
            }

            // Only manual start preview for GS2
            if (isSG2) {
                _camera.startPreview();
            }

            if (_delegate != null) {
                _delegate.cameraManagerDidFinishInitialize(this);
            }
        } catch (Exception ex) {
            Log.e(TAG, "ex:" + ex.getMessage());
            if (_camera != null) {
                _camera.release();
                _camera = null;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /* Condition validation */
        if (_camera == null) return;

        float factor = (float) width / (float) 480;
//        float factor = (float)width / (float)mSize.height;
        this.width = Math.round(width / factor);
        this.height = Math.round(height / factor);

        // Retrieve camera's parameters and _camera's ideal size
        Parameters parameters = _camera.getParameters();

        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = _optimalSize(sizes, this.width, this.height);


        Log.i(TAG, "surfaceChanged: PreviewSize Before: " + optimalSize.width + " " + optimalSize.height);

        String build = Build.MODEL;
        Log.i(TAG, "build: " + build);
        for (String model : CAMERA_BLUR_DEVICES) {
            if (build.equalsIgnoreCase(model)) {
                optimalSize.width = 640;
                optimalSize.height = 480;
                break;
            }
        }
        // Change camera's preview size
        try {
            Log.i(TAG, "surfaceChanged: PreviewSize: " + optimalSize.width + " " + optimalSize.height);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            _camera.setParameters(parameters);
        } catch (Exception ex) {  //Preview already set. Ignoe
            Log.e(TAG, "ex:" + ex.getMessage());
        }

        try {
            // Enable continuous focus
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            // Determine camera _direction
            WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            _direction = manager.getDefaultDisplay().getDisplayId();

            Log.d(TAG, "surfaceChanged");
            switch (_direction) {
                case Surface.ROTATION_90:
                    // Do nothing
                    Log.d(TAG, "surfaceChanged ROTATION_90");
                    break;

                case Surface.ROTATION_180:
                    // Unknown
                    Log.d(TAG, "surfaceChanged ROTATION_180");
                    break;

                case Surface.ROTATION_270:
                    _camera.setDisplayOrientation(180);
                    Log.d(TAG, "surfaceChanged ROTATION_270");
                    break;

                case Surface.ROTATION_0:
                    Log.d(TAG, "surfaceChanged ROTATION_0");
                    _camera.setDisplayOrientation(90);
                    break;
            }
        } catch (Exception ex) {
            Log.e(TAG, "ex" + ex.getMessage());
            if (_camera != null) _camera.release();
            _camera = null;

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (_recorder != null) {
            this.stopRecording();
            if (_delegate != null) {
                _delegate.cameraMinimizedOrStopped(this);
            }

        }

        if (_camera != null) {
            _camera.stopPreview();
            _camera.release();
            _camera = null;
        }
    }
    public interface CameraWidgetDelegate {

         void cameraManagerDidFinishInitialize(CameraWidget cameraWidget);

         void cameraManagerDidBeginRecording(CameraWidget cameraWidget);

         void cameraManagerDidFinishRecording(CameraWidget cameraWidget, String outputFile);

         void cameraMinimizedOrStopped(CameraWidget cameraWidget);
    }
}
