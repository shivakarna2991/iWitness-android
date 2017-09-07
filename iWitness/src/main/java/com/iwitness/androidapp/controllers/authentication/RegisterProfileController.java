package com.iwitness.androidapp.controllers.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiFormParam;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;




public class RegisterProfileController extends AppCompatActivity {


    // View's controls
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    TextView toolBarTitle;
    UserPreferences userPreferences;
    private FwiJson profile;
    // Global variables
    private ForegroundTask task;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_register_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolBarTitle.setText("Setup Profile");
        firstnameEditText = (EditText) findViewById(R.id.txtFirstName);
        lastnameEditText = (EditText) findViewById(R.id.txtLastName);
        emailEditText = (EditText) findViewById(R.id.txtEmailAddress);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmEditText = (EditText) findViewById(R.id.txtConfirmPassword);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }
        else return super.onKeyDown(keyCode, event);
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


    public void handleSignUpButtonOnClicked(View view) {
        final String firstname = firstnameEditText.getText().toString().trim();
        final String lastname  = lastnameEditText.getText().toString().trim();
        final String email     = emailEditText.getText().toString().trim();
        final String phone     = phoneEditText.getText().toString().trim();
        final String password  = passwordEditText.getText().toString().trim();
        final String confirm   = confirmEditText.getText().toString().trim();

        /* Condition validation:Validate first name */
        if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(this, getString(R.string.FirstNameRequired), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Condition validation:Validate last name */
        if (TextUtils.isEmpty(lastname)) {
            Toast.makeText(this, getString(R.string.LastNameRequired), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Condition validation:Validate email address */
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.EmailRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!email.matches(getString(R.string.EmailPattern))) {
            Toast.makeText(this, getString(R.string.InvalidEmailAddress), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Condition validation:Validate phone number */
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.PhoneRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!phone.matches(getString(R.string.phone_pattern))) {
            Toast.makeText(this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Condition validation: Validate password */
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.PasswordRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!password.matches(getString(R.string.password_pattern))) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Condition validation: Validate confirm password */
        else if (confirm.compareTo(password) != 0) {
            Toast.makeText(this, getString(R.string.ConfirmPasswordNotMatch), Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign up process
        FwiJson requestInfo = FwiJson.Object(
                "subscriptionUuid", FwiJson.String(getIntent().getStringExtra("subscriptionUuid")),
                "phone", FwiJson.String(phone),
                "firstName", FwiJson.String(firstname),
                "lastName", FwiJson.String(lastname),
                "email", FwiJson.String(email),
                "password", FwiJson.String(password)
        );

        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                Configuration.kService_Register,
                Configuration.kHostname
        );
        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));

        final RegisterProfileController weakThis = this;
        task = new ForegroundTask(this, request);
        task.run(new TaskDelegate() {

            @Override
            public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                   try{
                       if(statusCode == 422){
                           FwiJson profile1 = (FwiJson) response;
                           FwiJson validation = profile1.jsonWithPath("validation_messages");
                           Iterator<String> iterator = validation.enumerateKeysAndValues();
                           String keys =  iterator.next();
                           FwiJson details =  validation.jsonWithPath(keys);
                           String values = details.jsonAtIndex(0).getString();
                           Toast.makeText(RegisterProfileController.this, values, Toast.LENGTH_LONG).show();
                           return;
                       }
                   }
                   catch (Exception e){

                   }
                /* Condition validation */
                if (!(HttpStatus.SC_OK <= statusCode && statusCode < HttpStatus.SC_MULTIPLE_CHOICES))
                    return;
                if(statusCode == -1){
                    //not sure if we should immediately return from here
                    return;
                }
                     profile = (FwiJson) response;
                     userPreferences = UserPreferences.sharedInstance();
                     userPreferences.setUserProfile(profile);

                // 1. Request OAuth2
                FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost, Configuration.kService_Authorization, Configuration.kHostname);
                request.addFormParams(
                        FwiFormParam.param("grant_type", "password"),
                        FwiFormParam.param("username", phone),
                        FwiFormParam.param("password", password),
                        FwiFormParam.param("client_id", userPreferences.clientId()),
                        FwiFormParam.param("client_secret", userPreferences.clientSecret())
                );
                task = new ForegroundTask(weakThis, request);
                task.run(new TaskDelegate() {

                    @Override
                    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

                        /* Condition validation */
                        if (!(HttpStatus.SC_OK <= statusCode && statusCode < HttpStatus.SC_MULTIPLE_CHOICES)) return;
                        if(statusCode == -1){
                            //not sure if we should immediately return from here
                            return;
                        }

                        userPreferences = UserPreferences.sharedInstance();
                        FwiJson authentication = (FwiJson) response;

                        // Persist authorization code
                        userPreferences.setTokenType(authentication.jsonWithPath("token_type").getString());
                        userPreferences.setAccessToken(authentication.jsonWithPath("access_token").getString());
                        userPreferences.setRefreshToken(authentication.jsonWithPath("refresh_token").getString());

                        // Expire time
                        Date now = new Date();
                        Date expiredTime = new Date(now.getTime() + (authentication.jsonWithPath("expires_in").getInteger().longValue() * 1000));
                        userPreferences.setExpiredTime(expiredTime);
                        // Save
                        userPreferences.save();
                        // 2. Save user's profile
                        userPreferences.setCurrentUsername(profile.jsonWithPath("phone").getString());
                        userPreferences.setCurrentProfileId(profile.jsonWithPath("id").getString());
                        userPreferences.setFirstRegistered(true);

                        // Save
                        userPreferences.save();

                        // 3. Move to authenticated view
                        //move to adding emergency contacts
                        Intent intent = new Intent(weakThis, RegisterContactsController.class);
                        intent.putExtra("fromscreen","Register");
                        startActivity(intent);
                        setResult(RESULT_OK, new Intent());
                        weakThis.finish();
                        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                    }
                });
            }
        });
    }
}