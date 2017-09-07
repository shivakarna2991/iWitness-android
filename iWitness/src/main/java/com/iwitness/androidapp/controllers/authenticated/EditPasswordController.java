//  Project name: Workspace
//  File name   : EditPasswordController.java
//
//  Author      : Phuc
//  Created date: 6/4/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.authenticated;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
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
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.util.UUID;
import java.util.regex.Pattern;

public class EditPasswordController extends AppCompatActivity implements TaskDelegate {

    private EditText _currentPassword, _newPassword, _confirmPassword;
    private ForegroundTask task;
    TextView toolBarTitle;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_edit_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolBarTitle.setText("Update Password");
        _currentPassword = (EditText) findViewById(R.id.txtCurrentPassword);
        _newPassword = (EditText) findViewById(R.id.txtNewPassword);
        _confirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        _currentPassword.setTypeface(Typeface.DEFAULT);
        _currentPassword.setTransformationMethod(new PasswordTransformationMethod());

        _newPassword.setTypeface(Typeface.DEFAULT);
        _newPassword.setTransformationMethod(new PasswordTransformationMethod());

        _confirmPassword.setTypeface(Typeface.DEFAULT);
        _confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    // Called when the activity is becoming visible to the user.
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Called when the activity will startRecording interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Called when the system is about to startRecording resuming a previous activity.
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

    // <editor-fold defaultstate="collapsed" desc="Class's properties">
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's override public methods">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);

        return super.onCreateOptionsMenu(menu);

    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's event handlers">
    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            this.finish();
            overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                return false;
            }
            case R.id.actionSave: {
                if (validate()) {
                    UserPreferences userPreferences = UserPreferences.sharedInstance();
                    FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPatch,
                            Configuration.kService_User,
                            Configuration.kHostname,
                            userPreferences.currentProfileId()
                    );

                    FwiJson requestInfo = FwiJson.Object(
                            "password", FwiJson.String(_currentPassword.getText().toString()),
                            "newPassword", FwiJson.String(_newPassword.getText().toString())
                    );

                    request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
                    task = new ForegroundTask(this, request);
                    task.run(this);
                }
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean validate() {
        boolean isSuccess = true;
        _currentPassword.setError(null);
        _newPassword.setError(null);
        _confirmPassword.setError(null);

        UserPreferences userPreferences = UserPreferences.sharedInstance();
        String password = userPreferences.getCurrentPasword();
        Log.e("password", "TEST" + password);

        if (_currentPassword.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter current password.");
            isSuccess = false;
        } else if (!isValidpassword(_currentPassword.getText().toString())) {
            showAlert("iWitness", getString(R.string.CurrentPasswordNotMatch));
            isSuccess = false;
        } else if (_newPassword.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter new password.");
            isSuccess = false;
        } else if (!isValidpassword(_newPassword.getText().toString())) {
            showAlert("iWitness", getString(R.string.ConfirmPasswordNotMatch));
            isSuccess = false;
        } else if (_currentPassword.getText().toString().trim().equals(_newPassword.getText().toString().trim())) {
            showAlert("iWitness", getString(R.string.old_new_pwd_notsame));
            isSuccess = false;
        } else if (_confirmPassword.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter confirm password.");
            isSuccess = false;
        }
//        else if (!isValidpassword(_confirmPassword.getText().toString())) {
//            showAlert("iWitness",getString(R.string.ConfirmPasswordNotMatch));
//            isSuccess = false;
//        }
        else if (!_newPassword.getText().toString().trim().equals(_confirmPassword.getText().toString().trim())) {
            showAlert("iWitness", getString(R.string.current_new_passwordnotmatch));
            isSuccess = false;
        }


//        else if (!password.toString().trim().equalsIgnoreCase(_currentPassword.getText().toString().trim())) {
//            showAlert("iWitness",getString(R.string.CurrentPasswordNotMatch));
////            Toast.makeText(getBaseContext(), getString(R.string.PasswordRequired), Toast.LENGTH_LONG).show();
//            isSuccess = false;
//        }

        return isSuccess;
    }

    private boolean isValidpassword(String password) {
        return Pattern.compile(getString(R.string.password_pattern)).matcher(password).matches();
    }

    public void showAlert(String title, String Message) {
//        ContextThemeWrapper cw = new ContextThemeWrapper( this, R.style.AlertDialogTheme );
//        AlertDialog.Builder builder = new AlertDialog.Builder(cw)
//        ContextThemeWrapper cw = new ContextThemeWrapper( this, R.style.AlertDialogTheme );
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
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

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
//            Toast.makeText(getBaseContext(), "Pass updated successfully.", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
        } else {
            if (statusCode == 422) {
                showAlert("Failed validation", "Old password does not match.");
            } else if (statusCode == -1) {
                showAlert("No Internet", "Could now update your password right now");
            } else {
                FwiJson profile = (FwiJson) response;
                String detail = profile.jsonWithPath("detail").getString();
                String title = profile.jsonWithPath("title").getString();
                showAlert(title, detail);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    }
    // </editor-fold>
}