package com.iwitness.androidapp.controllers.common;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.RecordVideoFragment;
import com.iwitness.androidapp.libraries.utils.CameraUtils;
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
import java.util.List;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Call911Controller extends AppCompatActivity implements View.OnClickListener {
    static public final String CALL911_SHOULD_CREATE_CAMERA = "Should create camera";
    static private final int REMAIN_TIME = 5500;

    @BindView(R.id.btnCancel)
    Button cancelButton;

    @BindView(R.id.lblTimer)
    TextView timerTextView;

    @BindView(R.id.cameraView)
    CameraWidget mCameraView;

    //Actionbar buttons and layouts
    private ToggleButton mStartRecordingButton;
    private ImageView mSwitchTorch;
    private ImageView mSwitchCamera;
    private LinearLayout mRecordButtonLayout;
    private LinearLayout mToggleFlashLayut;
    private LinearLayout mToggleCameraLayout;

    // Global variables
    private Camera camera = null;
    private CountDownTimer timer = null;
    private MediaPlayer mediaPlayer = null;
    private boolean alreadyCalled911AndShouldDismiss = false;
    TextView toolBarTitle;
    private String mEventid;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_call);
        ButterKnife.bind(this);

        initializeToolBarButtons();

       disableActionbarButtons();



        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);


        UserPreferences userPreferences = UserPreferences.sharedInstance();
        toolBarTitle.setText("Calling" + " " + userPreferences.getEmergencyNumber() + "...");
        toolBarTitle.setTextSize(20);
        toolBarTitle.setGravity(Gravity.CENTER);

        timerTextView = (TextView) findViewById(R.id.lblTimer);

        cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(this);
    }

    private void initializeToolBarButtons() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_nav);
        toolBarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));
        mStartRecordingButton = (ToggleButton) toolbar.findViewById(R.id.btnRec);
        mSwitchTorch = (ImageView) toolbar.findViewById(R.id.swEnableTorch);
        mSwitchCamera = (ImageView) toolbar.findViewById(R.id.switch_camera);
        mRecordButtonLayout = (LinearLayout) toolbar.findViewById(R.id.recordButtonLayout);
        mToggleFlashLayut = (LinearLayout) toolbar.findViewById(R.id.toggleFlashLayout);
        mToggleCameraLayout = (LinearLayout) toolbar.findViewById(R.id.toggleCameraLayout);

        setSupportActionBar(toolbar);

    }

    private void disableActionbarButtons() {
        mToggleCameraLayout.setClickable(false);
        mToggleFlashLayut.setClickable(false);
        mRecordButtonLayout.setClickable(false);
        mStartRecordingButton.setClickable(false);
        mSwitchCamera.setClickable(false);
        mSwitchTorch.setClickable(false);
    }

    // Called when the activity will startRecording interacting with the user.
    @Override
    protected void onResume() {

        if (!alreadyCalled911AndShouldDismiss) {
            this.restartTimer();
            this.restartCounter();
        } else {
            //this.onClick(cancelButton);
            this.onBackPressed();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        torchOff();

        // Stop alarm
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Stop timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    // Class's properties

    // Class's public methods
    public void restartTimer() {
        timerTextView.setText(REMAIN_TIME + "");
    }

    public void restartCounter() {
        // Play sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
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

        // Start counter
        final Call911Controller weakThis = this;
        timer = new CountDownTimer(REMAIN_TIME, 1000) {
            @Override
            public void onTick(long l) {
                long seconds = l / 1000;
                Log.e("seconds", l + ":" + seconds + "");
                timerTextView.setText(seconds + "");


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
                            timerTextView.setVisibility(View.GONE);
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
                double lat = getIntent().getDoubleExtra("initialLat", Configuration.US_Latitude);
                double lng = getIntent().getDoubleExtra("initialLong", Configuration.US_Logitude);
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
                            mEventid = eventId;
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

                if (ActivityCompat.checkSelfPermission(Call911Controller.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                weakThis.startActivity(intent);

                //if 911 call is initiated then we have to set the timer to 10.00.
                RecordVideoFragment.isCallInitiated = true;

                // Remove eventId after stop recording
                UserPreferences.sharedInstance().setEventId(null);

                alreadyCalled911AndShouldDismiss = true;
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

    // View.OnClickListener's members
    @Override
    public void onClick(View view) {
        if (view == cancelButton) {

            RecordVideoFragment.isCallInitiated = false;
            UserPreferences.sharedInstance().setEventId(mEventid);
            this.onBackPressed();
        }
    }
}