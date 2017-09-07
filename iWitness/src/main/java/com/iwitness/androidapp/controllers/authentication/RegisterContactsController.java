package com.iwitness.androidapp.controllers.authentication;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.RegisterNotificationsController;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.ContactsFragment;

public class RegisterContactsController extends AppCompatActivity {
    RelativeLayout llBaseMid_Videos;
    TextView toolBarTitle;
    Menu menu;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_notifications_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        AppDelegate.isLoginScreen = true;
        toolBarTitle.setText("Contacts");
        llBaseMid_Videos =(RelativeLayout) findViewById(R.id.llBaseMid_Videos);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("fromscreen", "Register");
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.llBaseMid_Videos, contactsFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppDelegate.isLoginScreen = false;
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
        inflater.inflate(R.menu.register_emergency_contacts, menu);
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
            case R.id.nextcontact: {
                Intent intent = new Intent(this, RegisterNotificationsController.class);
                startActivity(intent);
                finish();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}