package com.iwitness.androidapp.controllers.authenticated.home.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.KeyValuePairs;
import com.iwitness.androidapp.controllers.authenticated.EditPasswordController;
import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.controllers.common.CircleTransform;
import com.iwitness.androidapp.controllers.common.FragmentPermissionRequestor;
import com.iwitness.androidapp.controllers.common.PermissionListener;
import com.iwitness.androidapp.controllers.common.PermissionRequestor;
import com.iwitness.androidapp.controllers.common.SelectProfilePicDialog;
import com.iwitness.androidapp.libraries.ConfigurationFactory;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiData;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiMultipartParam;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;
import com.squareup.picasso.Picasso;
import org.apache.http.HttpStatus;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileFragment extends Fragment implements View.OnClickListener, TaskDelegate{

    static private final int SELECT_PICTURE_FROM_CAMERA = 16;
    static private final int SELECT_PICTURE_FROM_GALLERY = 8;
    static private final int PIC_CROP = 32;
    static private final int MALE = 0;

    @BindView(R.id.addressTextView)
    TextView mTextViewAddressLabel;

    @BindView(R.id.distinguishTextView)
    TextView mTextViewPersonalFeatures;

    @BindView(R.id.genderTextView)
    TextView mTextViewGenderHeader;

    @BindView(R.id.genderRadioGroup)
    RadioGroup mRadioGroupGenderHeader;

    @BindView(R.id.femaleRadioButton)
    RadioButton mRadioButtonFemale;

    @BindView(R.id.maleRadioButton)
    RadioButton mRadioButtonMale;

    @BindView(R.id.updatePasswordButton)
    TextView mTextViewUpdatePassword;

    @BindView(R.id.buttonSave)
    Button mButtonSave;

    @BindView(R.id.buttonLogin)
    Button mButtonLogin;

    private UUID getLogOutTaskId;
    private boolean isLogOutButtonClicked = false;
    private SelectProfilePicDialog.SelectProfilePicDialogDialogButtonClickListener mAlertDialogButtonClickListerer;


    private EditText firstnameEditText,
            phoneEditText, emailEditText, addressEditText, addressEditText_2,
            cityEditText, stateEditText, zipEditText,
            distinguishEditText, heightEditText, weightEditText, birthDate,
            eyesEditText, hairEditText, ethnicityEditText, timezoneEditText;
    private RadioButton maleRadioButton, femaleRadioButton;
    private ImageView photoImageWidget;
    private ForegroundTask task;
    long timeInMilliseconds;
    private Menu menu;
    ScrollView main_screen;
    private ArrayList<View> touchables;
    private KeyValuePairs ethnicities, eyeColors, hairColors, states, timezones, weights;
    private boolean isMenuDisplay;
    private boolean isPopupPresented;
    private int ethnicityIndex;
    private int eyeColorIndex;
    private int feetIndex;
    private int hairColorIndex;
    private int inchesIndex;
    private int stateIndex;
    private int weightIndex;
    private int timezoneIndex;

    private String stateCode;

    private UUID uploadPhotoTaskId;

    Uri fileUri;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String weightValue;
    RelativeLayout rl_Main;
    private PermissionRequestor _requestor;
    BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _requestor = new FragmentPermissionRequestor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_profile_new, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    // Called when the activity is first created.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initControls();

        setTextFont();

        if (AppDelegate.isNetworkAvailable(getActivity())) {
            // Load user's profile
            FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet, Configuration.kService_User, Configuration.kHostname, UserPreferences.sharedInstance().currentProfileId());
            ForegroundTask task = new ForegroundTask(this.getActivity(), request);
            task.run(new TaskDelegate() {
                @Override
                public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                    prepareData();
                    UserPreferences userPreferences = UserPreferences.sharedInstance();

                    //                Log.e("onActivityCreated",response.toString());
                    if (200 <= statusCode && statusCode <= 299) {
                        FwiJson profile = (FwiJson) response;
                        displayProfile(profile);
                        userPreferences.setUserProfile(profile);
                    } else if (statusCode == -1) {

                    } else {
                        FwiJson profile = userPreferences.userProfile();
                        displayProfile(profile);
                    }
                }
            });
        } else {
            prepareData();
            showAlert("iWitness", "Network is not available!");
        }



        mAlertDialogButtonClickListerer = new SelectProfilePicDialog.SelectProfilePicDialogDialogButtonClickListener() {
            @Override
            public void getSelectedPosition(int selectedPosition) {
                if(selectedPosition == 0){
                    if (!doesUserHaveCameraPermission()) {
                        checkPermissionToOpenCamera();
                    } else {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp + ".jpg");

                        fileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE_FROM_CAMERA);
                    }
                } else {
                    if (!doesUserHavePhotoGalleryPermission()) {
                        checkPermissionToOpenStorage();
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE_FROM_GALLERY);
                    }
                }
            }
        };
    }

    private void getLogOut() {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_User_LogOut,
                Configuration.kHostname,
                UserPreferences.sharedInstance().currentProfileId()
        );

        getLogOutTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(getActivity(), request, getLogOutTaskId);
        task.run(this);
    }


    private void setTextFont(){
        firstnameEditText.setTypeface(FontUtils.getFontFabricGloberRegular());
        phoneEditText.setTypeface(FontUtils.getFontFabricGloberBold());
        emailEditText.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewAddressLabel.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewPersonalFeatures.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewGenderHeader.setTypeface(FontUtils.getFontFabricGloberRegular());
        mRadioButtonFemale.setTypeface(FontUtils.getFontFabricGloberRegular());
        mRadioButtonMale.setTypeface(FontUtils.getFontFabricGloberRegular());
        mTextViewUpdatePassword.setTypeface(FontUtils.getFontSFUITextMedium());
    }

    // The final call you receive before your activity is destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLogOutButtonClicked = false;
        unregisterForPhoneChangeBroadcast();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE_FROM_GALLERY:
                    fileUri = data.getData();
                    cropImage(fileUri);
                    break;
                case SELECT_PICTURE_FROM_CAMERA:
                    cropImage(fileUri);
                    break;
                case PIC_CROP:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] inputData = stream.toByteArray();
                        updateProfileImage(inputData);

                    }
                    File f = new File(fileUri.getPath());
                    if (f.exists()) f.delete();
                    break;
            }
        }
    }


    private void initControls() {
        photoImageWidget = (ImageView) getView().findViewById(R.id.imgPhoto);
        firstnameEditText = (EditText) getView().findViewById(R.id.firstnameEditText);
        phoneEditText = (EditText) getView().findViewById(R.id.phoneEditText);
        emailEditText = (EditText) getView().findViewById(R.id.emailEditText);
        addressEditText = (EditText) getView().findViewById(R.id.addressEditText);
        addressEditText_2 = (EditText) getView().findViewById(R.id.addressEditText_2);
        cityEditText = (EditText) getView().findViewById(R.id.cityEditText);
        stateEditText = (EditText) getView().findViewById(R.id.stateEditText);
        zipEditText = (EditText) getView().findViewById(R.id.zipEditText);
        distinguishEditText = (EditText) getView().findViewById(R.id.distinguishEditText);
        heightEditText = (EditText) getView().findViewById(R.id.heightEditText);
        weightEditText = (EditText) getView().findViewById(R.id.weightEditText);
        birthDate = (EditText) getView().findViewById(R.id.birthDate);
        eyesEditText = (EditText) getView().findViewById(R.id.eyesEditText);
        hairEditText = (EditText) getView().findViewById(R.id.hairEditText);
        ethnicityEditText = (EditText) getView().findViewById(R.id.ethnicityEditText);

        rl_Main = (RelativeLayout) getView().findViewById(R.id.rl_Main);
        maleRadioButton = (RadioButton) getView().findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) getView().findViewById(R.id.femaleRadioButton);

        timezoneEditText = (EditText) getView().findViewById(R.id.timeZoneEditText);
        main_screen = (ScrollView) getView().findViewById(R.id.main_screen);

        // Disable all text edit
        if (touchables == null) {
            touchables = getView().getTouchables();

            // Add event listener
            for (View view : touchables) {
                view.setOnClickListener(this);
            }

            TextView updatePasswordButton = (TextView) getView().findViewById(R.id.updatePasswordButton);
            updatePasswordButton.setOnClickListener(this);
            this.enableEditMode(true);
        }

        photoImageWidget.setOnClickListener(this);
        birthDate.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
    }

    private void prepareData() {
        // Initialize data
        ConfigurationFactory configurationFactory = new ConfigurationFactory(getActivity());
        if (states == null) states = configurationFactory.getState();
        if (eyeColors == null) eyeColors = configurationFactory.getEyeColors();
        if (hairColors == null) hairColors = configurationFactory.getHairColors();
        if (ethnicities == null) ethnicities = configurationFactory.getEthnicities();
        if (timezones == null) timezones = configurationFactory.getTimeZones();
        if (weights == null) weights = configurationFactory.getWeights();
    }

    private AlertDialog.Builder getDialog(KeyValuePairs list, int index, int titleId) {
        return new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setTitle(titleId)
                .setSingleChoiceItems(new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, list.getValues()), index, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        //Handle the back button
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            isPopupPresented = false;
                        }
                        return false;
                    }
                });

    }

    private void cropImage(Uri fileUri) {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(fileUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //startRecording the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "oops - your device doesn't support the crop action!";
            showAlert("iWitness", errorMessage);
        } catch (Exception e) {

            // display an error message
            String errorMessage = e.toString();
            showAlert("iWitness", errorMessage);
        }
    }

    private void enableEditMode(boolean isEnabled) {
        for (View touchable : touchables) {
            touchable.setEnabled(isEnabled);
            touchable.setClickable(isEnabled);

            int id = touchable.getId();
            if (isEnabled) {
                if (!(id == R.id.stateEditText ||
                        id == R.id.heightEditText ||
                        id == R.id.eyesEditText ||
                        id == R.id.hairEditText ||
                        id == R.id.ethnicityEditText ||
                        id == R.id.maleRadioButton ||
                        id == R.id.femaleRadioButton ||
                        id == R.id.timeZoneEditText ||
                        id == R.id.birthDate ||
                        id == R.id.weightEditText
                )) {
                    touchable.setFocusable(true);
                    touchable.setFocusableInTouchMode(true);
                }
            } else {
                touchable.setFocusable(false);
                touchable.setFocusableInTouchMode(false);
            }
        }
    }

    private void displayProfile(FwiJson user) {
        /* Condition validation */
        if (user == null) return;

        try {
            firstnameEditText.setText(user.jsonWithPath("firstName").getString().trim()
                    + " " + user.jsonWithPath("lastName").getString().trim());
            phoneEditText.setText(user.jsonWithPath("phone").getString());
            emailEditText.setText(user.jsonWithPath("email").getString());
            addressEditText.setText(user.jsonWithPath("address1").getString());
            addressEditText_2.setText(user.jsonWithPath("address2").getString());
            cityEditText.setText(user.jsonWithPath("city").getString());
            stateEditText.setText(user.jsonWithPath("state").getString());
            zipEditText.setText(user.jsonWithPath("zip").getString());
            distinguishEditText.setText(user.jsonWithPath("distFeature").getString());
            weightEditText.setText(user.jsonWithPath("weight").getString() + " " + "lb");
            eyesEditText.setText(user.jsonWithPath("eyeColor").getString());
            hairEditText.setText(user.jsonWithPath("hairColor").getString());
            ethnicityEditText.setText(user.jsonWithPath("ethnicity").getString());
            birthDate.setText(getFormattedDateFromMillisecs(Long.parseLong(user.jsonWithPath("birthDate").getString())));
        } catch (Exception e) {

        }
        // Height
        feetIndex = user.jsonWithPath("heightFeet").getInteger().intValue();
        inchesIndex = user.jsonWithPath("heightInches").getInteger().intValue();
        this.displayHeight(feetIndex, inchesIndex);

        String weightCode = user.jsonWithPath("weight").getString();
        //weightIndex
        weightIndex = weights.getKeys().indexOf(weightCode);
        // Gender
        FwiJson gender = user.jsonWithPath("gender");

        if (!gender.isLike(FwiJson.Null())) {
            int genderType = gender.getInteger().intValue();

            if (genderType == MALE) {
                maleRadioButton.setChecked(true);
                femaleRadioButton.setChecked(false);
            } else {
                maleRadioButton.setChecked(false);
                femaleRadioButton.setChecked(true);
            }
        } else {
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(false);
        }

        //State Index
        stateCode = user.jsonWithPath("state").getString();
        stateIndex = states.getKeys().indexOf(stateCode);

        //Weight Index
        weightIndex = weights.getKeys().indexOf(weightEditText.getText().toString());
        weightIndex = (weightIndex < 0 ? 0 : weightIndex);
        // Eyes color index
        eyeColorIndex = eyeColors.getKeys().indexOf(eyesEditText.getText().toString());
        eyeColorIndex = (eyeColorIndex < 0 ? 0 : eyeColorIndex);

        // Hair color index
        hairColorIndex = hairColors.getKeys().indexOf(hairEditText.getText().toString());
        hairColorIndex = (hairColorIndex < 0 ? 0 : hairColorIndex);

        // Ethnicity color index
        ethnicityIndex = ethnicities.getKeys().indexOf(ethnicityEditText.getText().toString());
        ethnicityIndex = (ethnicityIndex < 0 ? 0 : ethnicityIndex);

        String timezoneKey = UserPreferences.sharedInstance().getTimeZone();
        KeyValuePairs timeZone = timezones.getList(timezoneKey);
        if (timeZone != null) {
            timezoneEditText.setText(timeZone.getDisplayName());
            timezoneIndex = timezones.getKeys().indexOf(timezoneKey);
        }

        try {
            String photoUrl = user.jsonWithPath("photoUrl").getString();
            if (photoUrl != null || (!photoUrl.equalsIgnoreCase(""))) {
                Picasso.Builder builder = new Picasso.Builder(getActivity());
                Picasso picasso = builder.build();
                picasso.load(photoUrl).placeholder(R.drawable.button_profile_picture1x)
                        .transform(new CircleTransform())
                        .resize(150, 150)
                        .into(photoImageWidget);

            }
        } catch (IllegalArgumentException e) {
            Picasso.with(getActivity()).load(R.drawable.button_profile_picture1x).into(photoImageWidget);
        } catch (Exception e) {
            Picasso.with(getActivity()).load(R.drawable.button_profile_picture1x).into(photoImageWidget);
        }


    }

    public static String getFormattedDateFromMillisecs(long timestampInMilliSeconds) {
        String date;
        try {
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar calendar = Calendar.getInstance();
            timestampInMilliSeconds = timestampInMilliSeconds * 1000;
            calendar.setTimeInMillis(timestampInMilliSeconds);
            date = formatter.format(calendar.getTime());

            if (date.equalsIgnoreCase("01/01/1970")) {
                Calendar c = Calendar.getInstance();
                date = formatter.format(c.getTime());
            }
            return date;
        } catch (Exception e) {

        }
        return null;
    }


    private void displayHeight(int heightFeet, int heightInch) {
        heightEditText.setText(String.format("%s' %s\"", heightFeet, heightInch));
    }

    private void presentHeightPickerPopup(View v) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        View convertView = getActivity().getLayoutInflater().inflate(R.layout.number_picker_2_columns, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(convertView)
                .setTitle(R.string.height_select)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        AlertDialog current = (AlertDialog) dialog;
                        NumberPicker npFeet = (NumberPicker) current.findViewById(R.id.np1);
                        feetIndex = npFeet.getValue();

                        NumberPicker npInch = (NumberPicker) current.findViewById(R.id.np2);
                        inchesIndex = npInch.getValue();

                        displayHeight(feetIndex, inchesIndex);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        //Handle the back button
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            isPopupPresented = false;
                        }
                        return false;
                    }
                });

        NumberPicker npFeet = (NumberPicker) convertView.findViewById(R.id.np1);
        npFeet.setDisplayedValues(new String[]{"0'", "1'", "2'", "3'", "4'", "5'", "6'", "7'"});
        npFeet.setMinValue(0);
        npFeet.setMaxValue(7);
        npFeet.setValue(feetIndex);


        NumberPicker npInch = (NumberPicker) convertView.findViewById(R.id.np2);
        npInch.setMinValue(0);
        npInch.setDisplayedValues(new String[]{"0\"", "1\"", "2\"", "3\"", "4\"", "5\"", "6\"", "7\"", "8\"", "9\"", "10\"", "11\""});
        npInch.setMaxValue(11);
        npInch.setValue(inchesIndex);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void presentStatePickerPopup(View view) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(states, stateIndex, R.string.state_select)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        stateIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        if (stateIndex == -1) {
                            stateIndex = 0;
                        }
                        KeyValuePairs item = states.list.get(stateIndex);
                        stateEditText.setText(item.getKey());
                        stateCode = item.getKey();

                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void WeighPickerPopup(View view) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(weights, weightIndex, R.string.weight)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        weightIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        KeyValuePairs item = weights.list.get(weightIndex);
                        weightEditText.setText(item.getKey() + " " + "lb");
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void presentTimezonePickerPopup(View view) {
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(timezones, timezoneIndex, R.string.timezone_select)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        timezoneIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        KeyValuePairs item = timezones.list.get(timezoneIndex);
                        timezoneEditText.setText(item.getDisplayName());

                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void presentEyesColorPickerPopup(View v) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(eyeColors, eyeColorIndex, R.string.eye_color)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        eyeColorIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        KeyValuePairs item = eyeColors.list.get(eyeColorIndex);
                        eyesEditText.setText(item.getDisplayName());

                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void presentHairColorPickerPopup(View v) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(hairColors, hairColorIndex, R.string.hair_color)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        hairColorIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        KeyValuePairs item = hairColors.list.get(hairColorIndex);
                        hairEditText.setText(item.getDisplayName());

                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void presentEthnicityPickerPopup(View v) {
        /* Condition validation */
        if (isPopupPresented) return;
        isPopupPresented = true;

        this.getDialog(ethnicities, ethnicityIndex, R.string.ethnicity)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupPresented = false;
                        ethnicityIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        KeyValuePairs item = ethnicities.list.get(ethnicityIndex);
                        ethnicityEditText.setText(item.getDisplayName());

                        dialog.dismiss();
                    }
                })
                .show();

    }

    void registerForPhoneChangeBroadcast() {
        // Register broadcast emergency phone number change
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (menu != null) {
                    MenuItem item = menu.getItem(0);
                    UserPreferences userPreferences = UserPreferences.sharedInstance();
                    item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(UserPreferences.class.getName()));
    }

    void unregisterForPhoneChangeBroadcast() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void updateProfileImage(byte[] inputData) {

        UserPreferences userPreferences = UserPreferences.sharedInstance();

        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                Configuration.kService_UploadPhoto,
                Configuration.kHostname,
                userPreferences.currentProfileId()
        );
        request.setMultipartParams(FwiMultipartParam.param("photo", "photo", new FwiData(inputData), "image/jpeg"));

        uploadPhotoTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(getActivity(), request, uploadPhotoTaskId);
        task.run(this);
    }

    private void updateUserProfile(String password) {
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPatch,
                Configuration.kService_User,
                Configuration.kHostname,
                userPreferences.currentProfileId()
        );

        //ZipCode
        String zipCode = zipEditText.getText().toString();
        if (!TextUtils.isEmpty(zipCode)) {
            zipCode = zipCode.replace("-", "");
        }

        try {
            //Birth Date
            String givenDateString = birthDate.getText().toString();
            String[] values = givenDateString.split("/");
            int year = Integer.valueOf(values[2]);
            int month = Integer.valueOf(values[0]) - 1;
            int day = Integer.valueOf(values[1]);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            timeInMilliseconds = cal.getTimeInMillis();
            timeInMilliseconds = timeInMilliseconds / 1000;
            String date = getFormattedDateFromMillisecs(timeInMilliseconds);
            KeyValuePairs item = weights.list.get(weightIndex);
            weightValue = item.getKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fullName = firstnameEditText.getText().toString();
        String[] NamesArray = fullName.split(" ");
        String firstName = NamesArray[0];
        String lastName = NamesArray[1];

        FwiJson requestInfo = FwiJson.Object(
                "firstName", FwiJson.String(firstName),
                "lastName", FwiJson.String(lastName),
                "email", FwiJson.String(emailEditText.getText().toString()),
                "phoneAlt", FwiJson.String(""),
                "address1", FwiJson.String(addressEditText.getText().toString()),
                "address2", FwiJson.String(addressEditText_2.getText().toString()),
                "birthDate", Double.valueOf(Long.toString(timeInMilliseconds)),
                "city", FwiJson.String(cityEditText.getText().toString()),
                "state", FwiJson.String(stateCode),
                "zip", FwiJson.String(zipCode),
                "distFeature", FwiJson.String(distinguishEditText.getText().toString()),
                "heightFeet", FwiJson.String(Integer.toString(feetIndex)),
                "heightInches", FwiJson.String(Integer.toString(inchesIndex)),
                "weight", FwiJson.String(weightValue),
                "eyeColor", FwiJson.String(eyesEditText.getText().toString()),
                "hairColor", FwiJson.String(hairEditText.getText().toString()),
                "ethnicity", FwiJson.String(ethnicityEditText.getText().toString()),
                "timezone", FwiJson.String(timezones.list.get(timezoneIndex).getKey())
        );

        if (maleRadioButton.isChecked()) {
            requestInfo.addKeysAndJsons("gender", FwiJson.Integer(0));
        } else if (femaleRadioButton.isChecked()) {
            requestInfo.addKeysAndJsons("gender", FwiJson.Integer(1));
        } else {
            requestInfo.addKeysAndJsons("gender", null);
        }

        if (!TextUtils.isEmpty(password)) {
            requestInfo.addKeysAndJsons(
                    "phonePassword", FwiJson.String(password),
                    "phone", FwiJson.String(phoneEditText.getText().toString()));
        }

        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
        task = new ForegroundTask(getActivity(), request, FwiIdUtils.generateUUID());
        task.run(this);
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                fromDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                birthDate.setText(dateFormatter.format(calendar.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.show();
    }

    // View.OnClickListener's members
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPhoto: {
                SelectProfilePicDialog dialog = new SelectProfilePicDialog(getActivity(), mAlertDialogButtonClickListerer);
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                break;
            }

            case R.id.buttonSave:
                saveUserData();
                break;

            case R.id.buttonLogin:
                isLogOutButtonClicked = true;
                getLogOut();
                break;

            case R.id.weightEditText: {
                WeighPickerPopup(view);
                break;
            }
            case R.id.stateEditText: {
                presentStatePickerPopup(view);
                break;
            }
            case R.id.heightEditText: {
                presentHeightPickerPopup(view);
                break;
            }
            case R.id.eyesEditText: {
                presentEyesColorPickerPopup(view);
                break;
            }
            case R.id.hairEditText: {
                presentHairColorPickerPopup(view);
                break;
            }
            case R.id.ethnicityEditText: {
                presentEthnicityPickerPopup(view);
                break;
            }
            case R.id.updatePasswordButton: {
                Intent i = new Intent(getActivity(), EditPasswordController.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                break;
            }
            case R.id.timeZoneEditText: {
                presentTimezonePickerPopup(view);
                break;
            }
            case R.id.birthDate: {
                setDateTimeField();
                break;
            }
        }
    }

    private void saveUserData(){
        this.enableEditMode(true);
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        String firstName = null, lastName = null;
        try {
            String Name = firstnameEditText.getText().toString().trim();
            String FullName[] = Name.split(" ");
            firstName = FullName[0];
            if (FullName[1] != null) {
                lastName = FullName[1];
            } else {
                lastName = "";
            }
        } catch (Exception e) {
            lastName = "";
        }
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        String phone = phoneEditText.getText().toString().trim();
        String previousPhone = userPreferences.phone();

        // Strip all prefix number for previous phone number
        if (previousPhone != null && previousPhone.length() > 10) {
            previousPhone = previousPhone.substring(previousPhone.length() - 10);
        }
        // Strip all prefix number for phone number
        if (phone != null && phone.length() > 10) {
            phone = phone.substring(phone.length() - 10);
        }

        String EMAIL_PATTERN = getString(R.string.EmailPattern);
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (firstName.equalsIgnoreCase("")) {
            showAlert("iWitness", "UserName should not be empty.");
        } else if (lastName == null || lastName.equalsIgnoreCase("")) {
            showAlert("iWitness", "Please enter LastName.");
        } else if (email.equalsIgnoreCase("")) {
            showAlert("iWitness", "Please enter email address.");
        } else if (!matcher.matches()) {
            showAlert("iWitness", "Invalid email address format.");
        } else if (phone.equalsIgnoreCase("")) {
            showAlert("iWitness", "Please enter mobile number.");
        } else if (!isPhoneNumber(phone)) {
            showAlert("iWitness", "Invalid mobile number format.");
        } else if (address.equalsIgnoreCase("")) {
            showAlert("iWitness", "Address field should not be empty.");
        } else if (phone.equalsIgnoreCase(previousPhone)) {
            updateUserProfile(null);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.customdilaogue_alert, null);
            dialogBuilder.setView(dialogView);

            final EditText ed_password = (EditText) dialogView.findViewById(R.id.ed_password);
            ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            dialogBuilder.setTitle(getString(R.string.password_required));
            dialogBuilder.setMessage(getString(R.string.update_phone_password_required));
            dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                    String value = ed_password.getText().toString();
                    if (value.equalsIgnoreCase("")) {
                        showAlert("iWitness", "Please enter password");
                    } else {
                        updateUserProfile(value.toString());
                    }
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UserPreferences userPreferences = UserPreferences.sharedInstance();
                    String previousPhone = userPreferences.phone();
                    phoneEditText.setText(previousPhone);
                    dialog.dismiss();
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.setCancelable(false);
            b.show();

        }
    }

    private boolean isPhoneNumber(String number) {
        return Pattern.compile(getString(R.string.phone_pattern)).matcher(number).matches();
    }

    private boolean doesUserHaveCameraPermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean doesUserHaveWritePermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean doesUserHavePhotoGalleryPermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // TaskDelegate's members
    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {

            if(isLogOutButtonClicked){
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                    String responseLogout = ((FwiJson) response).toString();
                    if (responseLogout != null) {
                        UserPreferences.sharedInstance().reset();
                        ProfileFragment weakThis = this;
                        Intent intent = new Intent(getActivity(), LoginController.class);
                        intent.putExtra("fromLogout", "fromLogout");
                        startActivity(intent);
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                } else if (statusCode == -1) {

                }
            } else {
                UserPreferences userPreferences = UserPreferences.sharedInstance();
                FwiJson profile = userPreferences.userProfile();
                FwiJson updatedProfile = (FwiJson) response;
                if (response != null) {
                    if (taskId == uploadPhotoTaskId) {
                        //update photo URL
                        String photoUrl = updatedProfile.jsonWithPath("url").getString();
                        Picasso.Builder builder = new Picasso.Builder(getActivity());
                        Picasso picasso = builder.build();
                        picasso.load(photoUrl).placeholder(R.drawable.button_profile_picture1x)
                                .noFade().transform(new CircleTransform())
                                .resize(150, 150)
                                .into(photoImageWidget);
                    } else {
                        profile = updatedProfile;
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle("iWitness")
                                .setMessage("Profile updated successfully.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        getActivity().onBackPressed();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                    userPreferences.setUserProfile(profile);
                    userPreferences.setCurrentUsername(profile.jsonWithPath("phone").getString());
                    userPreferences.save();
                }
            }
        } else if (statusCode == -1) {

        } else {
            try {
                if (statusCode == 422) {
                    FwiJson profile1 = (FwiJson) response;
                    FwiJson validation = profile1.jsonWithPath("validation_messages");
                    Iterator<String> iterator = validation.enumerateKeysAndValues();
                    String keys = iterator.next();
                    FwiJson details = validation.jsonWithPath(keys);
                    String values = details.jsonAtIndex(0).getString();
                    showAlert("iWitness", values);
                    return;
                }
            } catch (Exception e) {
                Log.e("Exception",""+e.fillInStackTrace());
            }
        }
    }

    public void showAlert(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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

    private void checkPermissionToOpenCamera() {
        getPermissionRequestor().request(Manifest.permission.CAMERA
                , 10
                , "You need to allow access to contacts"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (!doesUserHaveWritePermission()) {
                            checkPermissionTowriteStorage();
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

    }

    private void checkPermissionToOpenStorage() {
        getPermissionRequestor().request(Manifest.permission.READ_EXTERNAL_STORAGE
                , 10
                , "You need to allow access to contacts"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE_FROM_GALLERY);
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

    }

    private void checkPermissionTowriteStorage() {
        getPermissionRequestor().request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , 10
                , "You need to allow access to contacts"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp + ".jpg");

                        fileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE_FROM_CAMERA);
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });

    }

    public PermissionRequestor getPermissionRequestor() {
        return _requestor;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        _requestor._onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
