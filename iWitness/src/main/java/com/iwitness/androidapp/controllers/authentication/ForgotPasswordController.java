//  Project name: Workspace
//  File name   : ForgotPasswordController.java
//
//  Author      : Phuc
//  Created date: 8/15/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.authentication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.UUID;
import java.util.regex.Pattern;


public class ForgotPasswordController extends AppCompatActivity implements TaskDelegate {

    // View's controls
    private EditText emailEditText;
    // Global variables
    private UUID taskId;
    private ForegroundTask task;
    Toolbar toolbar;
    TextView toolBarTitle;
    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolBarTitle.setText("Forgot Password");
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        emailEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
        if (task != null) {
            task.cancel(true);
        }
        super.onDestroy();
    }

    // Called after your activity has been stopped, prior to it being started again.
    @Override
    protected void onRestart() {
        super.onRestart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forgot_password, menu);

        return super.onCreateOptionsMenu(menu);
    }


    // <editor-fold defaultstate="collapsed" desc="Class's event handlers">
    @Override
    public void onBackPressed() {
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
            case R.id.sendItem: {
                String email = emailEditText.getText().toString().trim();
                if(!(isValidEmaillPhone(email))) {
                    showAlert("Failed Validation", "Please enter a valid email or mobile number.");
                }
                else{
                    taskId = FwiIdUtils.generateUUID();

                    FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                            Configuration.kService_ResetPassword,
                            Configuration.kHostname,
                            emailEditText.getText().toString().trim()
                    );

                    Log.e("request","TEST"+request.toString());
                    task = new ForegroundTask(this, request, taskId);
                    task.run(this);
                    return false;
                }
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    //validate email
    private boolean isValidEmaillPhone(String email){
        boolean isValidorNot = false;
        isValidorNot = Pattern.compile(getString(R.string.EmailPattern)).matcher(email).matches();
        if(!isValidorNot) {
            isValidorNot = isValidPhone(email);
        }
        return isValidorNot;
    }
    //validate phone
    private boolean isValidPhone(String phone){
        return Pattern.compile(getString(R.string.phone_pattern)).matcher(phone).matches();
    }

    // TaskDelegate's members
    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if (this.taskId.equals(taskId) && statusCode == HttpStatus.SC_OK) {
            showAlertOnBack("iWitness",getString(R.string.success_forgot_password));
        }
        else if (statusCode == -1) {
            showAlert("Warning", "Could not send email to restore password at the moment.");
        }
        else {
            FwiJson profile = (FwiJson) response;
            String detail = profile.jsonWithPath("detail").getString();
            String title = profile.jsonWithPath("title").getString();
//            showAlert(title,detail);
        }
    }
    public void showAlert(String title,String Message)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    public void showAlertOnBack(String title,String Message)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
        alertDialog.show();
    }



}