package com.iwitness.androidapp.controllers.authentication;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.HomeContainerController;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.libraries.utils.ValidationUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.iwitness.androidapp.network.TaskLogin;
import com.iwitness.androidapp.views.AbstractTextInput;
import com.iwitness.androidapp.views.IWitnessPasswordEditText;
import com.iwitness.androidapp.views.IWitnessUsernameEditText;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isStringEmpty;


public class LoginController extends FragmentActivity implements TaskDelegate {
    static private final int REGISTER_PROFILE_CODE = 1;

    @BindView(R.id.phoneEditText)
    EditText phoneEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.btnLogin)
    Button mLoginButton;

    @BindView(R.id.tvForgotPassword)
    TextView mTextViewForgetPassword;

    @BindView(R.id.buttonCreateAccount)
    Button mButtonCreateNewAccount;


    private UUID getLogOutTaskIdLogout;
    private ForegroundTask task;
    protected IWitnessUsernameEditText mUserName;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_login);

        ButterKnife.bind(this);

        init();


        AppDelegate.isLoginScreen = true;

        try {

            String Logoutmessage = null, fromLogout = null, logoutAll = null;

            if (getIntent().hasExtra("fromLogout")) {
                fromLogout = getIntent().getExtras().getString("fromLogout");
            }

            if (getIntent().hasExtra("userNotloggedin")) {
                Logoutmessage = getIntent().getExtras().getString("userNotloggedin");
            }

            if (getIntent().hasExtra("logoutAll")) {
                logoutAll = getIntent().getExtras().getString("logoutAll");
            }

            UserPreferences userPreferences = UserPreferences.sharedInstance();
            if (fromLogout == null && Logoutmessage != null && Logoutmessage.equalsIgnoreCase("User not logged in")) {
                userPreferences.reset();
                showAlertLogutALLMessage("iWitness", "Your iWitness account has been logged in on different device, if you did not authorize this change please go to www.iWitness.com and reset your password and log-in on your device using your updated credentials.");

            }
            if (logoutAll != null && logoutAll.equalsIgnoreCase("logoutAll")) {
                phoneEditText.setText(userPreferences.currentUsername());
                passwordEditText.setText(userPreferences.getCurrentPasword());
            }
        } catch (Exception e) {

        }
    }

    private void init() {
      /*  phoneEditText.setTypeface(FontUtils.getFontSFUITextBold());
        passwordEditText.setTypeface(FontUtils.getFontSFUITextBold());*/
        mLoginButton.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewForgetPassword.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonCreateNewAccount.setTypeface(FontUtils.getFontFabricGloberBold());
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(LoginController.this);
        }

        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(phoneEditText.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed", "onBackPressed");
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REGISTER_PROFILE_CODE: {
                if (resultCode == RESULT_OK) {
                    onBackPressed();
                }
                break;
            }
        }
    }

    @OnClick(R.id.btnLogin)
    void handleLoginButtonOnPressed() {
        String username = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        UserPreferences userPreferences = UserPreferences.sharedInstance();
        userPreferences.setCurrentUsername(username);
        userPreferences.setCurrentUserPassword(password);
        userPreferences.save();

        // Condition validation:Validate phone number

        if (TextUtils.isEmpty(username)) {
            showAlert("iWitness", "Please enter mobile number.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showAlert("iWitness", "Please enter password.");
            return;
        }

        userPreferences = UserPreferences.sharedInstance();

        if (AppDelegate.isNetworkAvailable(this)) {
            FwiJson requestInfo = FwiJson.Object(
                    "grant_type", FwiJson.String("password"),
                    "username", FwiJson.String(username),
                    "password", FwiJson.String(password),
                    "client_id", FwiJson.String(userPreferences.clientId()),
                    "client_secret", FwiJson.String(userPreferences.clientSecret())
            );

            FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost, Configuration.kService_Authorization, Configuration.kHostname);
            request.setDataParam(FwiDataParam.paramWithJson(requestInfo));

            TaskLogin task = new TaskLogin(this, request, username);
            final LoginController weakThis = this;
            task.run(new TaskDelegate() {

                @Override
                public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                    /* Condition validation */
//                    Log.e("statusCode", response.toString());
                    if (HttpStatus.SC_OK <= statusCode && statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
                        UserPreferences userPreferences = UserPreferences.sharedInstance();
                        //                    userPreferences.setLogoutFlag(0);
                        //AppDelegate.isLoginScreen = false;
                        Intent intent = new Intent(weakThis, HomeContainerController.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (statusCode == 400) {
                        FwiJson profile = (FwiJson) response;
                        String detail = profile.jsonWithPath("detail").getString();
                        String title = profile.jsonWithPath("title").getString();
                        if (title.equalsIgnoreCase("Already Loggedin")) {
                            String[] detailLogedIn = detail.split(":");
                            Log.e("userid...", detailLogedIn[1] + "");
                            if (detailLogedIn[0] != null && detailLogedIn[1] != null) {
                                showAlertLogin(title, detailLogedIn[0], detailLogedIn[1]);
                            }
                        }
                    } else if (statusCode == -1) {
                        showAlert("Warning", "Could not connect to server at the moment");
                    }

                }
            });

        } else {
            showAlert("Warning", "Could not connect to server at the moment");
        }
    }

    private void getLogOutAll(String UserId) {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_User_LogOutAll,
                Configuration.kHostname,
                UserId.trim()
        );

        getLogOutTaskIdLogout = FwiIdUtils.generateUUID();
        task = new ForegroundTask(this, request, getLogOutTaskIdLogout);
        task.run(LoginController.this);
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        // Log.e("statusCode",statusCode +response.toString());
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            if (taskId == getLogOutTaskIdLogout) {
                String responseLogout = ((FwiJson) response).toString();
                if (responseLogout != null) {
//                    UserPreferences.sharedInstance().reset();
                    LoginController weakThis = this;
                    Intent intent = new Intent(weakThis, LoginController.class);
                    intent.putExtra("logoutAll", "logoutAll");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            }
        } else if (statusCode == -1) {

        }
//        else{
//            Toast.makeText(this,"No response"+"", Toast.LENGTH_LONG).show();
//        }
    }

    @OnClick(R.id.buttonCreateAccount)
    void handleRegisterButtonOnPressed() {
        Intent intent = new Intent(this, SubscriptionController.class);
        startActivityForResult(intent, REGISTER_PROFILE_CODE);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    public void handleForgotPasswordButtonOnPressed(View view) {
        Intent intent = new Intent(this, ForgotPasswordController.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    //    // </editor-fold>
    public void showAlert(String title, String Message) {
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

    public void showAlertLogutALLMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        dialogInterface.dismiss();
                        UserPreferences.sharedInstance().reset();
                    }
                });

        builder.create().show();
    }


    public void showAlertLogin(String title, String Message, final String UserId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton("LogOut", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        dialogInterface.dismiss();
                        getLogOutAll(UserId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                    UserPreferences.sharedInstance().reset();
                        phoneEditText.setText("");
                        passwordEditText.setText("");
                        dialogInterface.dismiss();
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
    }

}