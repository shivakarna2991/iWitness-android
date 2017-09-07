package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.crittercism.app.Crittercism;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authentication.SubscriptionController;
import com.iwitness.androidapp.controllers.common.BottomSheetDialogOnRecordVideoScreen;
import com.iwitness.androidapp.controllers.common.CommonAlertDialog;
import com.iwitness.androidapp.controllers.common.FragmentPermissionRequestor;
import com.iwitness.androidapp.controllers.common.PermissionListener;
import com.iwitness.androidapp.controllers.common.PermissionRequestor;
import com.iwitness.androidapp.libraries.FwiLocation;
import com.iwitness.androidapp.libraries.cache.FwiCacheFolder;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.libraries.widgets.CameraWidget;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.BackgroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;
import org.apache.http.HttpStatus;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecordVideoFragment extends Fragment implements View.OnClickListener, LocationListener, SensorEventListener, View.OnTouchListener, CameraWidget.CameraWidgetDelegate, OnMapReadyCallback, BottomSheetDialogOnRecordVideoScreen.BottomsheetItemClickListener {

    static private final int SHAKE_THRESHOLD = 800;
    private final String TAG = "RecordVideoFragment";
    static private final int REMAIN_TIME = 5500;

    @BindView(R.id.myLocation)
    ImageView mMyLocation;

    @BindView(R.id.iv_videobutton)
    ImageView mDisplayVideoScreen;

    @BindView(R.id.openBottomsheetLayout)
    LinearLayout mLayoutOPenBottomSheet;

    @BindView(R.id.cameraView)
    CameraWidget cameraView;

    @BindView(R.id.tv_text_below_currently_enabled_action)
    TextView mTextViewTapOrShakeText;

    @BindView(R.id.img_shake)
    ImageView mImageViewShakeFunctionality;

    @BindView(R.id.tv_currently_enabled_action)
    TextView mTextViewCurrentlyEnabledActionText;

    @BindView(R.id.authorities_alerted_text)
    TextView mTextViewAuthoritiesAlertedText;

    @BindView(R.id.video_sent_text)
    TextView mTextViewVideoSent;

    @BindView(R.id.rl_background_camera_view)
    FrameLayout rl_background_camera_view;

    @BindView(R.id.textViewInforAboutVideo)
    TextView mTextViewInfoAboutVideoStreaming;

    @BindView(R.id.img_call_911)
    ImageView mCall911BotttomImage;

    @BindView(R.id.img_emergency_contacts)
    ImageView mCallEmergencyContactsBotttomImage;

    @BindView(R.id.img_sound_and_alarm)
    ImageView mCallAlaramBottomImage;

    @BindView(R.id.redBorder)
    View view;

    @BindView(R.id.fiveSecondsScreen)
    View mFiveSecondsScreen;

    @BindView(R.id.top_left_image_shape)
    ImageView mTopLeftShape;

    @BindView(R.id.top_right_image_shape)
    ImageView mTopRightShape;

    @BindView(R.id.bottom_left_image_shape)
    ImageView mBottomLeftShape;

    @BindView(R.id.bottom_right_image_shape)
    ImageView mBottomRightShape;

    @BindView(R.id.center_circle)
    ImageView mCenterCircle;

    @BindView(R.id.bottom_buttons)
    LinearLayout mBottomButtonLayout;

    @BindView(R.id.button_Cancel)
    Button mButtonCancel;

    @BindView(R.id.lblTimer)
    TextView mTextViewTimerOnFiveSecondsScreen;


    private ToggleButton mStartRecordingButton;
    private ImageView mSwitchTorch;
    private ImageView mSwitchCamera;
    public static boolean isCallInitiated = false;
    private MediaPlayer videoPlayer;
    private CountDownTimer timerCounter;
    private FwiCacheFolder cacheFolder;
    private SimpleDateFormat dateFormat;
    private Sensor sensor;
    private SensorManager sensorManager;
    private boolean isAlreadyFlashed;
    private boolean isCameraRecording = false;
    private boolean isVibrated;
    private int videoDurationExtended = 0;
    private int timeCounter;
    private int videoPartCounter;
    private long lastUpdate;
    private float lastX;
    private float lastY;
    private float lastZ;
    private GoogleMap googleMap;
    private MapFragment mapView;
    private FwiLocation locationManager;
    private BroadcastReceiver broadcastReceiver;
    private UserPreferences userPreferences;
    private boolean isenableTourch = true;
    private PermissionRequestor _requestor;
    private boolean isFromPreviousRun;
    private boolean isBackCameraOn = true;
    private TextView toolBarTitle;
    private BottomSheetDialogOnRecordVideoScreen.BottomsheetItemClickListener mItemClickListener;
    private CommonAlertDialog.CommonAlertDialogButtonClickListener mAlertDialogButtonClickListerer;
    private Camera camera = null;
    private CountDownTimer timer = null;
    private MediaPlayer mediaPlayer = null;

    AlertDialog.Builder alertDialogBuilder = null;
    AlertDialog alert = null;
    Handler handler = new Handler();
    Runnable counterMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _requestor = new FragmentPermissionRequestor(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AppDelegate.isLoginScreen = false;
        View view = inflater.inflate(R.layout.view_record_video_constraintlayout_trial, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        initializeToolBarButtons();

        setDataAndIcons();

        mItemClickListener = new BottomSheetDialogOnRecordVideoScreen.BottomsheetItemClickListener() {
            @Override
            public void setSelectedItemToTextView(String selectedItem) {
                if (null != selectedItem) {
                    mTextViewCurrentlyEnabledActionText.setText(selectedItem.toUpperCase());
                    enableCenterText();

                } else {
                    disableCenterText();
                }
                isShakeEnable(userPreferences.enableShake());
            }
        };

        mAlertDialogButtonClickListerer = new CommonAlertDialog.CommonAlertDialogButtonClickListener() {

            @Override
            public void setCounterToInitialAmount() {
                // Remove eventId after stop recording
                UserPreferences.sharedInstance().setEventId(null);
                //reset timer to normal recording time
                timeCounter = UserPreferences.sharedInstance().recordTime() * 1000;
            }
        };

    }


    // Called when the activity is becoming visible to the user.
    @Override
    public void onStart() {
        super.onStart();

        /*
         * If call is initiated then we have to reset the timer to initial time.
         * Also inform the user that Authorities have been alerted and video has benn sent.
         */
        if (isCallInitiated) {
            //reset timer to normal recording time
            timeCounter = UserPreferences.sharedInstance().recordTime() * 1000;

            mFiveSecondsScreen.setVisibility(View.GONE);
            enableAuthorityAlertedText();
            enableRecordVideoImages();
        } else {
            disableAuthorityAlertedText();
        }

        enableCenterText();

        if (!isCameraRecording) {
            disableAuthorityAlertedText();
        }

        // Keep screen on when on this view
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Called when the activity will startRecording interacting with the user.
    @Override
    public void onResume() {
        super.onResume();
        if (isCameraRecording && isFromPreviousRun) {
            startRecording();
        }
        if (locationManager == null) {
            locationManager = new FwiLocation(getActivity(), this, false);
        }

        try {
            this.initializeGoogleMap();
        } catch (Exception ex) {
            Crittercism.logHandledException(ex);
        }
        if (doesUserHaveCameraPermission()) {
            cameraView.setVisibility(View.VISIBLE);
        }

        if (Camera.getNumberOfCameras() == 1) {
            mSwitchCamera.setVisibility(View.GONE);
        } else {
            mSwitchCamera.setVisibility(View.VISIBLE);
        }

        // Register shake event
        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            //By default camera is ON
            if (cameraView != null) {
                if (userPreferences.enableTorch()) {
                    cameraView.torchOn();
                    mSwitchTorch.setImageResource(R.drawable.icon_flashlight_on);
                    isenableTourch = true;
                } else {
                    cameraView.torchOff();
                    mSwitchTorch.setImageResource(R.drawable.icon_flashlight_off);
                    isenableTourch = false;
                }

                userPreferences.setEnableTorch(isenableTourch);

                if (userPreferences.enablebackFacingCamera()) {
                    mSwitchCamera.setImageResource(R.drawable.icon_camera_back);
                    isBackCameraOn = true;
                    userPreferences.save();
                } else {
                    mSwitchCamera.setImageResource(R.drawable.icon_camera_front);
                    isBackCameraOn = false;
                }

                userPreferences.setEnablebackFacingCamera(isBackCameraOn);

                userPreferences.save();
            }
        } catch (Exception e) {
            Log.e("Exception ", "" + e.fillInStackTrace());
        }

        if (userPreferences.enableTorch()) {
            cameraView.torchOn();
        }
    }


    // Called when the system is about to startRecording resuming a previous activity.
    @Override
    public void onPause() {
        isFromPreviousRun = true;
        // Handle case: Pause view when flashing the torch
        if (isAlreadyFlashed && isCameraRecording) {

            // Double if here because sometimes the condition is right, but the timer had been cancel, prevent crash
            checkTimer();

            isAlreadyFlashed = false;
        }

        checkTimer();

        // Handle case: Pause view when camera is recording video //old code
        if (isCameraRecording) {

            // Temporary disable
            isCameraRecording = false;

            // Stop camera
            cameraView.stopRecording();

            isCameraRecording = true;           // Restore
        }

        sensorManager.unregisterListener(this);
        super.onPause();
    }

    // Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
    @Override
    public void onStop() {
        Intent intent = new Intent(RecordVideoFragment.class.getName());
        LocalBroadcastManager.getInstance(AppDelegate.getAppContext()).sendBroadcast(intent);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        MapFragment fragment = getMapFragment();
        if (fragment.isResumed()) {
            getFragmentManager().beginTransaction().remove(fragment)
                    .commit();
        }
        // Remove eventId
        UserPreferences.sharedInstance().setEventId(null);
        isCallInitiated = false;
        super.onDestroyView();

    }

    // The final call you receive before your activity is destroyed.
    @Override
    public void onDestroy() {
        // Stop location manager
        if (locationManager != null) {
            locationManager.stopUsingGPS();
        }
        // Release video
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
            videoPlayer = null;
        }

        // Remove eventId
        UserPreferences.sharedInstance().setEventId(null);
        super.onDestroy();
    }

    /**
     * When user will click on any button from call 911, add emergency contact and alarm,
     * bottomsheet will open and user can set the preferences for those buttons.
     */

    @OnClick(R.id.openBottomsheetLayout)
    void openBottomSheet() {

        BottomSheetDialogOnRecordVideoScreen dialog = new BottomSheetDialogOnRecordVideoScreen(getActivity(), mItemClickListener);        // Setting dialogview
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    @OnClick(R.id.myLocation)
    void getCurrentLocation() {

        mMyLocation.setVisibility(View.GONE);
        mDisplayVideoScreen.setVisibility(View.VISIBLE);
        mapView.getView().setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_videobutton)
    void getRecordVideoScreen() {

        mMyLocation.setVisibility(View.VISIBLE);
        mDisplayVideoScreen.setVisibility(View.GONE);
        mapView.getView().setVisibility(View.GONE);

    }

    @OnClick(R.id.img_shake)
    void changeShakeFunctionalityStatus() {

        if (userPreferences.enableShake()) {
            userPreferences.setEnableShake(false);
            mImageViewShakeFunctionality.setImageResource(R.drawable.icon_shake_white);
            isShakeEnable(false);
        } else {
            userPreferences.setEnableShake(true);
            mImageViewShakeFunctionality.setImageResource(R.drawable.icon_shake_red);
            isShakeEnable(true);
        }

        userPreferences.save();
    }

    @OnClick(R.id.tv_currently_enabled_action)
    void performActionBasedOnPreferences() {
        if (userPreferences.enableTap()) {
            call911Method();
        } else if (userPreferences.enableEmergencyContact()) {

        } else {

        }
    }

    @OnClick(R.id.button_Cancel)
    void cancelFiveSecondsScreen() {

        mFiveSecondsScreen.setVisibility(View.GONE);
        enableCenterText();
        enableRecordVideoImages();
        isCallInitiated = false;
        timer.cancel();

        if (timerCounter != null) {
            timerCounter.start();
        }

        // Stop alarm
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void enableRecordVideoImages() {
        mTopLeftShape.setVisibility(View.VISIBLE);
        mTopRightShape.setVisibility(View.VISIBLE);
        mBottomLeftShape.setVisibility(View.VISIBLE);
        mBottomRightShape.setVisibility(View.VISIBLE);
        mCenterCircle.setVisibility(View.VISIBLE);
        mBottomButtonLayout.setVisibility(View.VISIBLE);

        if (isCameraRecording) {
            mTextViewInfoAboutVideoStreaming.setVisibility(View.VISIBLE);
        } else {
            mTextViewInfoAboutVideoStreaming.setVisibility(View.GONE);
        }
    }

    private void init() {
        userPreferences = UserPreferences.sharedInstance();
        if (locationManager == null) {
            locationManager = new FwiLocation(getActivity(), this);
        }
    }

    private void initializeToolBarButtons() {

        Toolbar mToolbar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
        toolBarTitle = ((TextView) mToolbar.findViewById(R.id.toolbar_title));
        mStartRecordingButton = (ToggleButton) mToolbar.findViewById(R.id.btnRec);
        mSwitchTorch = (ImageView) mToolbar.findViewById(R.id.swEnableTorch);
        mSwitchCamera = (ImageView) mToolbar.findViewById(R.id.switch_camera);

        mStartRecordingButton.setOnClickListener(this);
        mSwitchTorch.setOnClickListener(this);
        mSwitchCamera.setOnClickListener(this);

    }

    private void setDataAndIcons() {

        /**
         * If "shake to call" functionality is on then the text will be "Tap or shake to send for help"
         * else it will "Tap to send for help"
         */
        isShakeEnable(userPreferences.enableShake());

        if (userPreferences.enableTap()) {
            mTextViewCurrentlyEnabledActionText.setText("" + userPreferences.getEmergencyNumber().toUpperCase());
        } else if (userPreferences.enableEmergencyContact()) {
            mTextViewCurrentlyEnabledActionText.setText(getResources().getString(R.string.str_contact).toUpperCase());
        } else {
            mTextViewCurrentlyEnabledActionText.setText(getResources().getString(R.string.str_alert).toUpperCase());
        }

        // Record timer
        dateFormat = new SimpleDateFormat(getResources().getString(R.string.str_date_format));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerCounter = null;
        timeCounter = UserPreferences.sharedInstance().recordTime() * 1000;

        // Assign location to video camera
        cameraView.setLocation(locationManager);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception ex) {
            Crittercism.logHandledException(ex);
        }

        mapView = getMapFragment();
        mapView.getMapAsync(this);
        mapView.getView().setVisibility(View.GONE);

        mTextViewInfoAboutVideoStreaming.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewAuthoritiesAlertedText.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewVideoSent.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewCurrentlyEnabledActionText.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewTapOrShakeText.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonCancel.setTypeface(FontUtils.getFontFabricGloberBold());

        if (googleMap != null) {
            googleMapSetLocationEnabled(googleMap);
        }

        if (locationManager == null) {
            locationManager = new FwiLocation(getActivity(), this);
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(UserPreferences.class.getName()));

        if (userPreferences.enableTorch()) {
            cameraView.torchOn();
        }
    }

    private void googleMapSetLocationEnabled(GoogleMap googleMap){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }


    public void showAlert(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        dialogInterface.dismiss();
                    }
                });

        builder.create().show();
    }

    private MapFragment getMapFragment() {
        FragmentManager fm = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.mapView);
    }

    public void handleRecButtonOnPressed() {
        //audio set stream mute - record sound mute
        ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM, true);
        ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_MUSIC, true);
        ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_RING, true);
        AudioManager audioMgr = ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE));

        cameraView.setVisibility(View.VISIBLE);
        mapView.getView().setVisibility(View.GONE);
        //updateMessage();
        if (timerCounter != null) {
            timerCounter.cancel();
            timerCounter = null;
        }

        if (isCameraRecording) {

            isCameraRecording = false;
            mStartRecordingButton.setChecked(false);
            mTextViewInfoAboutVideoStreaming.setVisibility(View.GONE);
            disableAuthorityAlertedText();

            cameraView.stopRecording();

            // Control view group
            cameraView.setVisibility(View.VISIBLE);
            mapView.getView().setVisibility(View.GONE);

            toolBarTitle.setText(dateFormat.format(new Date(userPreferences.recordTime() * 1000)));
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(counterMessage);
            counterMessage = null;

            //VIDEO FINISHING ALERT
            CommonAlertDialog dialog = new CommonAlertDialog(getActivity(), mAlertDialogButtonClickListerer);        // Setting dialogview
            dialog.show();

        } else {

            userPreferences.setEventId(UUID.randomUUID().toString());
            isCameraRecording = true;

            mStartRecordingButton.setChecked(true);
            mTextViewInfoAboutVideoStreaming.setVisibility(View.VISIBLE);
            cameraView.setVisibility(View.VISIBLE);

            if (cacheFolder == null) {
                cacheFolder = new FwiCacheFolder(AppDelegate.getAppContext(), String.format("%s/%s", userPreferences.currentProfileId(), userPreferences.eventId()));
                cameraView.setDelegate(this);
                cameraView.setCacheFolder(cacheFolder);
            }

            this.startRecording();

        }
        if (userPreferences.enableTorch()) {
            isenableTourch = true;
            cameraView.torchOn();
        }

    }

    // Class's private methods
    private synchronized void startRecording() {
        cameraView.startRecording();

        /* Condition validation: Do not start the timer if it is already available */
        if (this.timerCounter != null) return;

        if (isCameraRecording) {
            new MyCountDownTimer(Integer.MAX_VALUE, 1000).Start();
        }

        //alert showing no of times
        if (timeCounter == (10 * 60 * 1000)) {
            videoDurationExtended = 3;
        } else if (timeCounter == (20 * 60 * 1000)) {
            videoDurationExtended = 2;
        }

        final RecordVideoFragment weakThis = this;
        timerCounter = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l) {

                toolBarTitle.setText(dateFormat.format(new Date(timeCounter)));

                // Update timer
                timeCounter -= 1000;

                if (timeCounter <= 10000 && !isVibrated) {
                    isVibrated = true;

                    // Vibrate for 500 milliseconds
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
                if (timeCounter == 30000 && videoDurationExtended > 0) {
                    videoDurationExtended--;
                    if (timeCounter == 30000 && videoDurationExtended >= 1) {
                        if (alert == null) {
                            alertDialogBuilder = new AlertDialog.Builder(getActivity())
                                    .setTitle(weakThis.getString(R.string.app_name))
                                    .setMessage(weakThis.getString(R.string.recordcontinue))
                                    .setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {//
                                            dialog.dismiss();
                                            Log.d(TAG, "alertDialog +ve button");
                                            timeCounter += (Configuration.VIDEO_RECORD_DURATION * 60 * 1000);
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            Log.d(TAG, "alertDialog -ve button");
                                            videoDurationExtended = 0;
                                        }
                                    });

                            alert = alertDialogBuilder.create();
                        }
                        alert.show();
                    }

                }

                if ((timeCounter / 1000) % 5 == 0) { //Instead of 10 sec make id 3 sec
                    cameraView.stopRecording();
                    //audio set stream mute - record sound mute
                    ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_MUSIC, true);
                    ((AudioManager) AppDelegate.getAppContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_RING, true);
                    AudioManager audioMgr = ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE));

                }


                if (timeCounter == 0) {
                    if (alert != null && alert.isShowing() && videoDurationExtended > 0) {
                        alert.dismiss();
                        timeCounter += (Configuration.VIDEO_RECORD_DURATION * 60 * 1000);
                    } else {
                        isCameraRecording = true;
                        handleRecButtonOnPressed();
                        timeCounter = UserPreferences.sharedInstance().recordTime() * 1000; //Min * secs per min * Millisec per sec
                    }
                }
                // Stop video if neccessary/**/
                if (timeCounter <= 0) {
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                    }
                }

            }

            @Override
            public void onFinish() {
                cameraView.stopRecording();
            }
        };
        timerCounter.start();
    }


    // LocationListener's members
    @Override
    public void onLocationChanged(Location location) {
        //Home Fragment
        if (googleMap != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
            googleMap.moveCamera(update);
            googleMap.animateCamera(zoom);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMapSetLocationEnabled(googleMap);
    }


    class MyCountDownTimer {
        private long millisInFuture;
        private long countDownInterval;

        public MyCountDownTimer(long pMillisInFuture, long pCountDownInterval) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
        }

        public void Start() {
            handler = new Handler();
            counterMessage = new Runnable() {
                public void run() {
                    if (millisInFuture <= 0) {
                    } else {
                        long sec = millisInFuture / 1000;
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                        if (sec % 4 == 0) {
                            // tv_Message.setText("Streaming to Secure Server...");
                        } else {
                            // tv_Message.setText("Recording will automatically stop when timer reads 0:00");
                        }

                    }
                }
            };
            handler.postDelayed(counterMessage, countDownInterval);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    // SensorEventListener's members
    @Override
    public void onSensorChanged(SensorEvent event) {

        /* Condition validation */
        if (!UserPreferences.sharedInstance().enableShake()) return;

        if (isCameraRecording && event.sensor.getType() == this.sensor.getType() && !isCallInitiated) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                if (userPreferences.enableShake()) {
                    if (speed > SHAKE_THRESHOLD) {
                        if (userPreferences.enableTap()) {
                            call911Method();
                        } else if (userPreferences.enableEmergencyContact()) {

                        } else if (userPreferences.enableSoundAndAlarm()) {

                        } else {

                        }

                    }
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                }

            }
        }
    }

    private void call911Method() {

        if (timerCounter != null) {
            timerCounter.cancel();
        }

        mTextViewInfoAboutVideoStreaming.setVisibility(View.GONE);
        mFiveSecondsScreen.setVisibility(View.VISIBLE);
        mTextViewTimerOnFiveSecondsScreen.setVisibility(View.VISIBLE);
        mTopLeftShape.setVisibility(View.GONE);
        mTopRightShape.setVisibility(View.GONE);
        mBottomLeftShape.setVisibility(View.GONE);
        mBottomRightShape.setVisibility(View.GONE);
        mCenterCircle.setVisibility(View.GONE);
        mTextViewTapOrShakeText.setVisibility(View.GONE);
        mTextViewCurrentlyEnabledActionText.setVisibility(View.GONE);
        mBottomButtonLayout.setVisibility(View.GONE);

        restartCounter();
    }

    public void restartCounter() {
        // Play sound
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm);
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
        mediaPlayer.start();


        timer = new CountDownTimer(REMAIN_TIME, 1000) {
            @Override
            public void onTick(long l) {
                long seconds = l / 1000;
                Log.e("seconds", l + ":" + seconds + "");
                mTextViewTimerOnFiveSecondsScreen.setText(seconds + "");


                if (seconds == 1) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Stop alarm
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                            mTextViewTimerOnFiveSecondsScreen.setVisibility(View.GONE);
                        }
                    }, 50);
                }
            }

            @Override
            public void onFinish() {
                // Turn off torch
                torchOff();
                // Stop alarm
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                UserPreferences preferences = UserPreferences.sharedInstance();
                String eventId = preferences.eventId();
                String userId = preferences.currentProfileId();
                LatLng pos = locationManager.getCurrentLocation();
                double lat = pos.latitude;
                double lng = pos.latitude;
                FwiJson requestInfo = null;
                if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(userId)) {
                    requestInfo = FwiJson.Object(
                            "id", FwiJson.String(eventId),
                            "name", "New Event",
                            "initialLat", FwiJson.Double(lat),
                            "initialLong", FwiJson.Double(lng)
                    );
                } else {
                    requestInfo = FwiJson.Object(
                            "name", "New Event",
                            "initialLat", FwiJson.Double(lat),
                            "initialLong", FwiJson.Double(lng)
                    );
                }

                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                        Configuration.kService_Event,
                        Configuration.kHostname
                );

                request.setDataParam(FwiDataParam.paramWithJson(requestInfo));

                BackgroundTask task = new BackgroundTask(request);
                task.run(new TaskDelegate() {

                    @Override
                    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                            UserPreferences preferences = UserPreferences.sharedInstance();
                            String eventId = preferences.eventId();

                            String userId = preferences.currentProfileId();
                            FwiJson requestInfo;
                            FwiJson responseObj = (FwiJson) response;
                            String eventIdResp = responseObj.jsonWithPath("id").getString();

                            FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                                    Configuration.kService_Emergency,
                                    Configuration.kHostname
                            );
                            if (!TextUtils.isEmpty(eventId)) {
                                requestInfo = FwiJson.Object(
                                        "eventId", FwiJson.String(eventIdResp),
                                        "userId", FwiJson.String(userId),
                                        "msgtype", "dangervideo",
                                        "dialno", preferences.getEmergencyNumber()
                                );
                            } else {

                                requestInfo = FwiJson.Object(
                                        "eventId", FwiJson.String(eventIdResp),
                                        "userId", FwiJson.String(userId),
                                        "msgtype", "danger",
                                        "dialno", preferences.getEmergencyNumber()
                                );
                            }

                            request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
                            BackgroundTask task = new BackgroundTask(request);

                            task.run(new TaskDelegate() {
                                @Override
                                public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                                    Log.i("iWitness", response.toString());
                                    if (statusCode == -1) {

                                    }
                                }
                            });
                        } else if (statusCode == -1) {

                        }
                    }
                });

                String emergencyNo = UserPreferences.sharedInstance().getEmergencyNumber(); //"+14255039467";

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel://%s", emergencyNo)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                getActivity().startActivity(intent);

                //if 911 call is initiated then we have to set the timer to 10.00.
                isCallInitiated = true;

                // Remove eventId after stop recording
                UserPreferences.sharedInstance().setEventId(null);

            }
        };
        timer.start();
    }


    // Class's private methods
    private void torchOff() {
        /* Condition validation */
        if (camera == null) return;
        Log.e("iWitness", "Call911Controller - torchOff");

        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);

        camera.stopPreview();
        camera.release();
        camera = null;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    // View.OnTouchListener's members
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN): {
                if (isCameraRecording && userPreferences.enableTap()) {
                    call911Method();
                    return true;
                }

            }
            default: {
                return super.getActivity().onTouchEvent(event);
            }
        }
    }


    // CameraWidget.CameraWidgetDelegate's members
    @Override
    public void cameraManagerDidFinishInitialize(CameraWidget cameraWidget) {
        if (isCameraRecording) {
            this.startRecording();
        }
    }

    @Override
    public void cameraManagerDidBeginRecording(CameraWidget cameraWidget) {


    }

    @Override
    public void cameraManagerDidFinishRecording(CameraWidget cameraWidget, String outputFile) {
        String videoName;
        // Generate video name & storage
        if (locationManager.canGetLocation()) {
            videoName = String.format((videoPartCounter < 10 ? "0%d_%f_%f" : "%d_%f_%f"), videoPartCounter, locationManager.getLatitude(), locationManager.getLongitude());
        } else {
            videoName = String.format((videoPartCounter < 10 ? "0%d" : "%d"), videoPartCounter);
        }
        String videoPath = cacheFolder.readyPathForFilename(videoName);

        // Move video to eventId folder
        File src = new File(outputFile);
        File dst = new File(videoPath);
        if (src.exists()) {
            src.renameTo(dst);
            Log.e(TAG, " destination : " + dst.getAbsolutePath());
        }

        // Start new circle
        videoPartCounter++;
        if (isCameraRecording)
            this.startRecording();
    }

    @Override
    public void cameraMinimizedOrStopped(CameraWidget cameraWidget) {
        isFromPreviousRun = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swEnableTorch: {
                if (cameraView != null) {
                    if (isenableTourch) {
                        cameraView.torchOff();
                        mSwitchTorch.setImageResource(R.drawable.icon_flashlight_off);
                        isenableTourch = false;
                        userPreferences.setEnableTorch(isenableTourch);
                        userPreferences.save();
                    } else {
                        cameraView.torchOn();
                        mSwitchTorch.setImageResource(R.drawable.icon_flashlight_on);
                        isenableTourch = true;
                        userPreferences.setEnableTorch(isenableTourch);
                        userPreferences.save();
                    }
                }

                break;
            }

            case R.id.btnRec: {

                try {
                    if (!doesUserHaveCameraPermission()) {
                        checkPermissionToOpenCamera();
                    } else {
                        if (!doesUserHaveMicroPhonePermission()) {
                            checkPermissionToOpenMicroPhone();
                        } else {
                            //to check subscription account expiration to do renewal
                            checkRenewalRecording();
                        }

                    }
                } catch (Exception e) {
                    Log.e("Exception ", "" + e.fillInStackTrace());
                }
                break;
            }

            case R.id.switch_camera:

                if (isBackCameraOn) {
                    mSwitchCamera.setImageResource(R.drawable.icon_camera_front);
                    isBackCameraOn = false;
                } else {
                    mSwitchCamera.setImageResource(R.drawable.icon_camera_back);
                    isBackCameraOn = true;
                }

                userPreferences.setEnablebackFacingCamera(isenableTourch);
                userPreferences.save();
                cameraView.switchCamera();
                break;

            default:
                // Do nothing
                break;


        }
    }

    // Class's private methods
    private void initializeGoogleMap() {
        CameraUpdate update;
        /* Condition validation */
        if (locationManager != null) {
            locationManager.requestLocationChanged();
            locationManager.updateCoordinate();
            update = CameraUpdateFactory.newLatLng(new LatLng(locationManager.getLatitude(), locationManager.getLongitude()));
        } else {
            update = CameraUpdateFactory.newLatLng(new LatLng(Configuration.US_Latitude, Configuration.US_Logitude));
        }

        if (googleMap != null) {
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(19);
            googleMap.moveCamera(update);
            googleMap.animateCamera(zoom);
        }
    }

    private boolean doesUserHaveCameraPermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean doesUserHaveMicroPhonePermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void startInstalledAppDetailsActivity(Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void checkPermissionToOpenMicroPhone() {
        getPermissionRequestor().request(Manifest.permission.RECORD_AUDIO
                , 10
                , "You need to allow access to record audio"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        handleRecButtonOnPressed();
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

    }

    private void checkPermissionToOpenCamera() {
        getPermissionRequestor().request(Manifest.permission.CAMERA
                , 10
                , "You need to allow access to Camera"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        cameraView.setVisibility(View.VISIBLE);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!doesUserHaveMicroPhonePermission()) {
                                    checkPermissionToOpenMicroPhone();
                                } else {
                                    handleRecButtonOnPressed();
                                }
                            }
                        }, 100);

                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

    }

    public PermissionRequestor getPermissionRequestor() {
        return _requestor;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        _requestor._onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void checkRenewalRecording() {
        if (UserPreferences.sharedInstance().isAccountExpired()) {

            //TODO  temporary code ------- If device is on mute state then it wont record the video.Should we ask user to change the state.

            final Context context = this.getActivity();
            //If account is expired then the record button should display rec text.
            mStartRecordingButton.setChecked(false);

            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.subscription_expired))
                    .setMessage(context.getString(R.string.renew_subscription))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();

                            Intent i = new Intent(context, SubscriptionController.class);
                            i.putExtra("isRenew", true);
                            context.startActivity(i);
                            if (context instanceof Activity) {
                                ((Activity) context).overridePendingTransition(
                                        R.anim.enter_slide_to_left,
                                        R.anim.exit_slide_to_left);
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            }).show();
        } else {
            handleRecButtonOnPressed();
        }
    }

    @Override
    public void setSelectedItemToTextView(String selectedItem) {
        if (null != selectedItem) {
            mTextViewCurrentlyEnabledActionText.setText(selectedItem.toUpperCase());
            mTextViewCurrentlyEnabledActionText.setVisibility(View.VISIBLE);
            mTextViewTapOrShakeText.setVisibility(View.VISIBLE);

        } else {
            mTextViewCurrentlyEnabledActionText.setVisibility(View.GONE);
            mTextViewTapOrShakeText.setVisibility(View.GONE);
        }

        isShakeEnable(userPreferences.enableShake());

    }

    private void disableAuthorityAlertedText() {
        mTextViewAuthoritiesAlertedText.setVisibility(View.GONE);
        mTextViewVideoSent.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    private void enableAuthorityAlertedText() {
        if (userPreferences.enableTap()) {
            mTextViewAuthoritiesAlertedText.setText(getResources().getString(R.string.str_authorities_alerted_text));
        } else if (userPreferences.enableEmergencyContact()) {
            mTextViewAuthoritiesAlertedText.setText(getResources().getString(R.string.str_emergency_contact_notified));
        } else {
            mTextViewAuthoritiesAlertedText.setText(getResources().getString(R.string.str_alarm_triggered));
        }
        mTextViewAuthoritiesAlertedText.setVisibility(View.VISIBLE);
        mTextViewVideoSent.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
    }

    private void checkTimer() {
        if (timerCounter != null) {
            timerCounter.cancel();
            timerCounter = null;
        }
    }

    private void isShakeEnable(boolean isShakeEnable) {
        if (isShakeEnable) {
            if (userPreferences.enableTap()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_or_shake_to_call));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);

            } else if (userPreferences.enableEmergencyContact()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_or_shake_to_call));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);

            } else if (userPreferences.enableSoundAndAlarm()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_or_shake_to_trigger));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);

            } else {
                mTextViewTapOrShakeText.setVisibility(View.GONE);
            }
            mImageViewShakeFunctionality.setImageResource(R.drawable.icon_shake_red);

        } else {
            if (userPreferences.enableTap()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_to_call));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);
            } else if (userPreferences.enableEmergencyContact()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_to_call));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);
            } else if (userPreferences.enableSoundAndAlarm()) {
                mTextViewTapOrShakeText.setText(getResources().getString(R.string.str_tap_to_trigger));
                mTextViewTapOrShakeText.setVisibility(View.VISIBLE);
            } else {
                mTextViewTapOrShakeText.setVisibility(View.GONE);
            }

            mImageViewShakeFunctionality.setImageResource(R.drawable.icon_shake_white);
        }

        if (userPreferences.enableTap()) {
            mCall911BotttomImage.setImageResource(R.drawable.icon_police_red);
        } else {
            mCall911BotttomImage.setImageResource(R.drawable.icon_police_white);
        }

        if (userPreferences.enableEmergencyContact()) {
            mCallEmergencyContactsBotttomImage.setImageResource(R.drawable.icon_emergency_contact_red);
        } else {
            mCallEmergencyContactsBotttomImage.setImageResource(R.drawable.icon_emergency_contact_white);
        }

        if (userPreferences.enableSoundAndAlarm()) {
            mCallAlaramBottomImage.setImageResource(R.drawable.icon_alarm_red);
        } else {
            mCallAlaramBottomImage.setImageResource(R.drawable.icon_alarm_white);
        }

    }

    private void enableCenterText() {
        mTextViewCurrentlyEnabledActionText.setVisibility(View.VISIBLE);
        mTextViewTapOrShakeText.setVisibility(View.VISIBLE);
    }

    private void disableCenterText() {
        mTextViewCurrentlyEnabledActionText.setVisibility(View.GONE);
        mTextViewTapOrShakeText.setVisibility(View.GONE);
    }

}