package com.iwitness.androidapp.controllers.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.model.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by samadhan malpure on 2017-07-19.
 * This is the bottomsheet fragment view which will get called when user press the bottom icons on record vodeio screen.
 * if all the three options are enable then it will display 911 on center of record video screen.
 * else it will check for emergency contact and set the text "Contact" if its enabled
 * else it will check for sound and alarm and set the text "Alert" if its enabled by the user.
 */

public class MyBottomsheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.call911SwitchButton)
    SwitchCompat mCall911SwitchButton;

    @BindView(R.id.emergencyNumberSwitchButton)
    SwitchCompat mEmergencyContactButton;

    @BindView(R.id.soundAndLightAlarmSwitchButton)
    SwitchCompat mSoundAndAlaramButton;

    @BindView(R.id.call911Image)
    ImageView mImageCall911;

    @BindView(R.id.emergencyNumberImage)
    ImageView mImageCallEmergencyNumber;

    @BindView(R.id.soundAndLightAlarmImageView)
    ImageView mImageAlarm;

    @BindView(R.id.call911Text)
    TextView mTextViewCall911;

    @BindView(R.id.emergencyNumberTextView)
    TextView mTextViewEmergencyNumber;

    @BindView(R.id.soundAndLightAlarmTextView)
    TextView mTextViewAlarm;




    private UserPreferences userPreferences;
    private BottomsheetItemClickListener mItemClickListener;

    /**
     * This is an interface for detecting the user preferences and depends on his preference, set the text on record video screen.
     */

    public interface BottomsheetItemClickListener {

        void setSelectedItemToTextView(String selectedItem);
    }

    public void setOnBottomSheetItemSelectedListener(BottomsheetItemClickListener onBottomSheetItemSelectedListener) {
        this.mItemClickListener = onBottomSheetItemSelectedListener;
    }


    public static MyBottomsheetFragment newInstance() {

        MyBottomsheetFragment f = new MyBottomsheetFragment();

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = UserPreferences.sharedInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_my_bottomsheet_fragment, container, false);

        ButterKnife.bind(this,v);

        if (userPreferences.enableTap()) {
            mCall911SwitchButton.setChecked(true);
            mTextViewCall911.setEnabled(true);
        } else {
            mCall911SwitchButton.setChecked(false);
            mTextViewCall911.setEnabled(false);
        }

        if (userPreferences.enableEmergencyContact()) {
            mEmergencyContactButton.setChecked(true);
            mTextViewEmergencyNumber.setEnabled(true);
        } else {
            mEmergencyContactButton.setChecked(false);
            mTextViewEmergencyNumber.setEnabled(false);
        }

        if (userPreferences.enableSoundAndAlarm()) {
            mSoundAndAlaramButton.setChecked(true);
            mTextViewAlarm.setEnabled(true);
        } else {
            mSoundAndAlaramButton.setChecked(false);
            mTextViewAlarm.setEnabled(false);
        }

        return v;
    }

    @OnClick({R.id.call911SwitchButton , R.id.emergencyNumberSwitchButton, R.id.soundAndLightAlarmSwitchButton})
    void onClick(SwitchCompat switchButton){
        switch (switchButton.getId()) {

            case R.id.call911SwitchButton:
                if (userPreferences.enableTap()) {
                    userPreferences.setEnableTap(false);
                    mCall911SwitchButton.setChecked(false);
                    mImageCall911.setEnabled(false);
                    mTextViewCall911.setEnabled(false);
                } else {
                    userPreferences.setEnableTap(true);
                    mCall911SwitchButton.setChecked(true);
                    mImageCall911.setEnabled(true);
                    mTextViewCall911.setEnabled(true);
                }
                break;

            case R.id.emergencyNumberSwitchButton:
                if (userPreferences.enableEmergencyContact()) {
                    userPreferences.setEnableEmergencyContact(false);
                    mEmergencyContactButton.setChecked(false);
                    mImageCallEmergencyNumber.setEnabled(false);
                    mTextViewEmergencyNumber.setEnabled(false);
                } else {
                    userPreferences.setEnableEmergencyContact(true);
                    mEmergencyContactButton.setChecked(true);
                    mImageCallEmergencyNumber.setEnabled(true);
                    mTextViewEmergencyNumber.setEnabled(true);
                }

                break;

            case R.id.soundAndLightAlarmSwitchButton:
                if (userPreferences.enableSoundAndAlarm()) {
                    userPreferences.setEnableSoundAndAlarm(false);
                    mSoundAndAlaramButton.setChecked(false);
                    mImageAlarm.setEnabled(false);
                    mTextViewAlarm.setEnabled(false);
                } else {
                    userPreferences.setEnableSoundAndAlarm(true);
                    mSoundAndAlaramButton.setChecked(true);
                    mImageAlarm.setEnabled(true);
                    mTextViewAlarm.setEnabled(true);
                }

                break;

            default:
                break;

        }

        userPreferences.save();

        setStatusToTextView();
    }

    /**
     * set the text depends on user preferences.
     */
    private void setStatusToTextView() {

        if (mCall911SwitchButton.isChecked()) {
            mItemClickListener.setSelectedItemToTextView("" + userPreferences.getEmergencyNumber());
        } else if (mEmergencyContactButton.isChecked()) {
            mItemClickListener.setSelectedItemToTextView(getResources().getString(R.string.contacts));
        } else if(mSoundAndAlaramButton.isChecked()){
            mItemClickListener.setSelectedItemToTextView(getResources().getString(R.string.title_Alert));
        } else {
            mItemClickListener.setSelectedItemToTextView(null);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
    }
}
