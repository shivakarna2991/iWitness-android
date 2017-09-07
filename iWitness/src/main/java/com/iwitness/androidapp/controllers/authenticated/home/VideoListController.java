package com.iwitness.androidapp.controllers.authenticated.home;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.VideosFragment;
import com.iwitness.androidapp.controllers.common.Call911Controller;
import com.iwitness.androidapp.model.UserPreferences;

public class VideoListController extends AppCompatActivity {
    TextView toolbar_title;
    RelativeLayout llBaseMid_Videos;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_notifications_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar_title  =((TextView)toolbar.findViewById(R.id.toolbar_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText("Videos");

        llBaseMid_Videos =(RelativeLayout)findViewById(R.id.llBaseMid_Videos);

        FragmentManager fragmentManager = getFragmentManager();
        VideosFragment videoFragment = new VideosFragment();
        videoFragment.passData(this, "record");
        fragmentManager.beginTransaction()
                .add(R.id.llBaseMid_Videos, videoFragment,"VideosFragment")
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        MenuItem item = menu.getItem(0);
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                return false;
            }
            case R.id.call911Item: {
                Intent intent = new Intent(this, Call911Controller.class);
                intent.setAction(Call911Controller.CALL911_SHOULD_CREATE_CAMERA);
                startActivity(intent);
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
            return false;
        } else return super.onKeyDown(keyCode, event);
    }
}