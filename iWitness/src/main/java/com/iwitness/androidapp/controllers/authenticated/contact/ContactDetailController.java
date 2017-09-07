//  Project name: Workspace
//  File name   : ContactDetailController.java
//
//  Author      : Phuc
//  Created date: 6/4/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.authenticated.contact;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.ContactStatus;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactDetailController extends AppCompatActivity implements TaskDelegate, View.OnClickListener, DialogInterface.OnClickListener {


    private UUID _saveContactTaskId, _deleteContact;
    private EditText _firstName, _lastName, _email, _phone, _phone2, _relationship;
    private Button _btnDeleteContact, _btnSaveContact;
    private TextView _contactStatus;
    private ForegroundTask task;

    private View _deleteContactContainer;
    private FwiJson _contact = null;
    private Toast _message;
    boolean isHideMenu = false;
    TextView toolBarTitle;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_contact_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        _firstName = (EditText) findViewById(R.id.txtFirstName);
        _lastName = (EditText) findViewById(R.id.txtLastName);
        _email = (EditText) findViewById(R.id.txtEmailAddress);
        _phone = (EditText) findViewById(R.id.phoneEditText);
        _phone2 = (EditText) findViewById(R.id.txtPhone2);
        _relationship = (EditText) findViewById(R.id.txtRelationship);
        _contactStatus = (TextView) findViewById(R.id.tvContactStatus);
        _btnDeleteContact = (Button) findViewById(R.id.btnDeleteContact);
        _btnSaveContact = (Button) findViewById(R.id.btn_saveContact);

        _btnDeleteContact.setOnClickListener(this);
        _btnSaveContact.setOnClickListener(this);

        _deleteContactContainer = findViewById(R.id.deleteContactContainer);
        _message = Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG);

        Intent intent = getIntent();
        if (intent.hasExtra("contact")) { //edit
            Serializable pContact = intent.getSerializableExtra("contact");
            if (pContact != null) {
                _contact = (FwiJson) pContact;
                loadContact(_contact);
            }
            _deleteContactContainer.setVisibility(View.VISIBLE);
            toolBarTitle.setText("");
        } else { //add
            Bundle extras = intent.getExtras();
            if (intent.hasExtra("firstName")) {
                _firstName.setText(extras.getString("firstName"));
            }

            if (intent.hasExtra("lastName")) {
                _lastName.setText(extras.getString("lastName"));
            }

            if (intent.hasExtra("email")) {
                _email.setText(extras.getString("email"));
            }

            if (intent.hasExtra("phone")) {
                _phone.setText(extras.getString("phone"));
            }
            toolBarTitle.setText("Add Contact");
            _deleteContactContainer.setVisibility(View.GONE);
            isHideMenu = true; // setting state
            invalidateOptionsMenu();

        }
    }

    private boolean validate() {
        _firstName.setError(null);
        _lastName.setError(null);
        _email.setError(null);
        _phone.setError(null);
        _relationship.setError(null);

        String EMAIL_PATTERN = getString(R.string.EmailPattern);
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(_email.getText().toString());

        if (_firstName.getText().toString().isEmpty() && _lastName.getText().toString().isEmpty() &&
                _phone.getText().toString().isEmpty()) {
            showAlert("iWitness", getString(R.string.enter_required));
            return false;
        } else if (_firstName.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter first name.");
            return false;
        } else if (_lastName.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter last name.");
            return false;
        } else if (_phone.getText().toString().isEmpty()) {
            showAlert("iWitness", "Please enter mobile number.");
            return false;
        } else if (!isPhoneNumber(_phone.getText().toString())) {
            showAlert("iWitness", "Invalid mobile number format.");
            return false;
        }
        return true;
    }

    private boolean isPhoneNumber(String number) {
        return Pattern.compile(getString(R.string.phone_pattern)).matcher(number).matches();
    }

    private void loadContact(FwiJson contact) {
        _firstName.setText(contact.jsonWithPath("firstName").getString());
        _lastName.setText(contact.jsonWithPath("lastName").getString());
        _phone.setText(contact.jsonWithPath("phone").getString());
        _phone2.setText(contact.jsonWithPath("phoneAlt").getString());
        _email.setText(contact.jsonWithPath("email").getString());
        _relationship.setText(contact.jsonWithPath("relationType").getString());
        BigInteger status = contact.jsonWithPath("flags").getInteger();
        _contactStatus.setText(getStatus(status));
        _contactStatus.setCompoundDrawablesWithIntrinsicBounds(
                getStatusIcon(status), //left
                0, //top
                0, //right
                0); //bottom
    }

    private int getStatusIcon(BigInteger status) {
        int result = R.drawable.icn_contact_pending;
        switch (ContactStatus.fromValue(status)) {
            case ACCEPTED:
                result = R.drawable.icn_contact_accepted;
                break;
            case DECLINED:
                result = R.drawable.icn_contact_rejected;
                break;
        }

        return result;
    }

    private String getStatus(BigInteger flags) {
        String result = "Pending";
        switch (ContactStatus.fromValue(flags)) {
            case ACCEPTED:
                result = "Accepted";
                break;
            case DECLINED:
                result = "Declined";
                break;
        }
        return result;
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
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's properties">
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's override public methods">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        if (isHideMenu) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
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
            case R.id.actionDelete: {
                if (_contact != null) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setTitle("iWitness");
                    ab.setMessage(getString(R.string.confirm_delete))
                            .setPositiveButton(getString(R.string.yes), this)
                            .setNegativeButton(getString(R.string.no), this)
                            .show();
                }

                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

        FwiJson jsonRes = (FwiJson) response;
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            if (taskId == _saveContactTaskId) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("contact", jsonRes);

                if (getIntent().hasExtra("index")) {
                    returnIntent.putExtra("index", getIntent().getExtras().getInt("index"));
                }
                setResult(RESULT_OK, returnIntent);
            } else if (taskId == _deleteContact) {
                Intent returnIntent = new Intent();
                if (getIntent().hasExtra("index")) {
                    returnIntent.putExtra("deletedIndex", getIntent().getExtras().getInt("index"));
                }
                setResult(RESULT_OK, returnIntent);
            }

            finish();
            overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);

        } else if (statusCode == -1) {
            if (taskId == _saveContactTaskId) {

            } else if (taskId == _deleteContact) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDeleteContact: {
                if (_contact != null) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setMessage(getString(R.string.confirm_delete))
                            .setPositiveButton(getString(R.string.yes), this)
                            .setNegativeButton(getString(R.string.no), this)
                            .show();
                }
                break;
            }
            case R.id.btn_saveContact: {
                if (validate()) {
                    FwiRequest request;
                    if (AppDelegate.isNetworkAvailable(this)) {
                        if (_contact != null) {
                            request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPatch,
                                    Configuration.kService_Update_Contact,
                                    Configuration.kHostname,
                                    _contact.jsonWithPath("id").getString()
                            );
                        } else {
                            request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                                    Configuration.kService_Contact,
                                    Configuration.kHostname
                            );
                        }

                        FwiJson requestInfo = FwiJson.Object(
                                "userId", FwiJson.String(UserPreferences.sharedInstance().currentProfileId()),
                                "firstName", FwiJson.String(_firstName.getText().toString()),
                                "lastName", FwiJson.String(_lastName.getText().toString()),
                                "email", FwiJson.String(_email.getText().toString()),
                                "phone", FwiJson.String(_phone.getText().toString()),
                                "phoneAlt", FwiJson.String(_phone2.getText().toString()),
                                "relationType", FwiJson.String(_relationship.getText().toString())
                        );

                        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
                        _saveContactTaskId = FwiIdUtils.generateUUID();
                        task = new ForegroundTask(this, request, _saveContactTaskId);
                        task.run(this);
                    } else {
                        showAlert("Warning", "Could not create emergency contact at the moment.");
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kDelete,
                        Configuration.kService_Update_Contact,
                        Configuration.kHostname,
                        _contact.jsonWithPath("id").getString()
                );

                _deleteContact = FwiIdUtils.generateUUID();
                task = new ForegroundTask(this, request, _deleteContact);
                task.run(this);
                dialog.dismiss();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }

    public void showAlert(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.create().show();
    }

}