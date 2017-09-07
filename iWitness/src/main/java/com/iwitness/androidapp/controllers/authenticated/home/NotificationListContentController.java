package com.iwitness.androidapp.controllers.authenticated.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.common.Call911Controller;
import com.iwitness.androidapp.libraries.utils.SharedPrefUtils;
import com.iwitness.androidapp.model.NotificationsObj;
import com.iwitness.androidapp.model.UserPreferences;

import java.util.HashMap;

public class NotificationListContentController extends AppCompatActivity {
    UserPreferences userPreferences;
    String[] notificationNames = {
            AppDelegate.getAppContext().getResources().getString(R.string.optimize),
            AppDelegate.getAppContext().getResources().getString(R.string.tip),
            AppDelegate.getAppContext().getResources().getString(R.string.disclimer)};
    RelativeLayout llBaseMid_Videos;
    String Name;
    TextView toolBarTitle;
    Menu menu;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_notifications_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBarTitle.setText("Notifications");


        Name = getIntent().getExtras().getString("page");
        String isReadUnread = getIntent().getExtras().getString("isReadUnread");
        NotificationsObj NotificationsObj = (com.iwitness.androidapp.model.NotificationsObj) getIntent().getExtras().getSerializable("NotificationsObj");

        userPreferences = UserPreferences.sharedInstance();
        HashMap<String,String> hashNotifications = null;
        hashNotifications = SharedPrefUtils.getHashmapValues(SharedPrefUtils.IWITNESS_PREF);

        llBaseMid_Videos =(RelativeLayout) findViewById(R.id.llBaseMid_Videos);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (NotificationsObj.notificationName.equalsIgnoreCase(notificationNames[0])) {
            hashNotifications.put(Name, isReadUnread);
            SharedPrefUtils.setHashMapValues(SharedPrefUtils.IWITNESS_PREF, hashNotifications);

            LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notifications_page2,
                    null);
            llBaseMid_Videos.addView(linearLayout, layoutParams);
        }

        else if (NotificationsObj.notificationName.equalsIgnoreCase(notificationNames[1])) {
            hashNotifications.put(Name, isReadUnread);
            SharedPrefUtils.setHashMapValues(SharedPrefUtils.IWITNESS_PREF, hashNotifications);
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.notifications_page3,
                    null);
            llBaseMid_Videos.addView(linearLayout, layoutParams);
        }
        else if (NotificationsObj.notificationName.equalsIgnoreCase(notificationNames[2])){
            hashNotifications.put(Name, isReadUnread);
            SharedPrefUtils.setHashMapValues(SharedPrefUtils.IWITNESS_PREF, hashNotifications);
            LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.notifications_page1,
                    null);
            llBaseMid_Videos.addView(linearLayout, layoutParams);
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
}