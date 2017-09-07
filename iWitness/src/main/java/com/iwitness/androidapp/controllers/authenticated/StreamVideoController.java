//  Project name: Workspace
//  File name   : StreamVideoController.java
//
//  Author      : Phuc
//  Created date: 7/1/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.authenticated;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.model.UserPreferences;
import com.perpcast.lib.foundation.FwiJson;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class StreamVideoController extends AppCompatActivity{ //FragmentActivity {


    private VideoView _vwVideo = null;
    static private final String TAG = "StreamVideoController";
    ProgressBar progress;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_stream_video);
        // Retrieve video info
        FwiJson videoInfo = (FwiJson) getIntent().getSerializableExtra("videoInfo");

        // Retrieve video layer
        _vwVideo = (VideoView) findViewById(R.id.vwVideo);
        progress =(ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(this._vwVideo);
        controller.setMediaPlayer(this._vwVideo);
        this._vwVideo.setMediaController(controller);
        _vwVideo.setZOrderOnTop(true);
        progress.setVisibility(View.VISIBLE);
        // Prepare for streaming
        try {
            Method setVideoURIMethod = _vwVideo.getClass().getMethod("setVideoURI", Uri.class, Map.class);

            UserPreferences userPreferences = UserPreferences.sharedInstance();
            Map<String, String> params = new HashMap<String, String>(1);

            if (userPreferences.accessToken() != null) {
                params.put("Authorization", String.format("%s %s", userPreferences.tokenType(), userPreferences.accessToken()));
            }
            setVideoURIMethod.invoke(_vwVideo, Uri.parse(videoInfo.jsonWithPath("videoUrl").getString()), params);
            _vwVideo.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Called when the activity is becoming visible to the user.
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Called when the system is about to start resuming a previous activity.
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
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu., menu);

        return super.onCreateOptionsMenu(menu);
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's event handlers">

    @Override
    public void onBackPressed() {
        _vwVideo.stopPlayback();
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    }
    // </editor-fold>
}