package com.iwitness.androidapp.controllers.common;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.model.UserPreferences;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by samadhanmalpure on 2017-08-03.
 */

public class BottomSheetDialogOnRecordVideoScreen extends Dialog{

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

    @BindView(R.id.btnCancel)
    Button mButtonCancel;

    private BottomsheetItemClickListener mItemClickListener;
    private UserPreferences userPreferences;

    public BottomSheetDialogOnRecordVideoScreen(Context context, BottomsheetItemClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    /**
     * This is an interface for detecting the user preferences and depends on his preference, set the text on record video screen.
     */

    public interface BottomsheetItemClickListener {

        void setSelectedItemToTextView(String selectedItem);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_dialog_layout);

        ButterKnife.bind(this);

        userPreferences = UserPreferences.sharedInstance();

        mTextViewCall911.setText(userPreferences.getEmergencyNumber() + " " + getContext().getString(R.string.str_call));

        if (userPreferences.enableTap()) {
            mCall911SwitchButton.setChecked(true);
            mTextViewCall911.setEnabled(true);
            enableCall911Status();
        } else {
            mCall911SwitchButton.setChecked(false);
            mTextViewCall911.setEnabled(false);
            disable911Status();
        }

        if (userPreferences.enableEmergencyContact()) {
            mEmergencyContactButton.setChecked(true);
            mTextViewEmergencyNumber.setEnabled(true);
            enableEmergencyContactStatus();
        } else {
            mEmergencyContactButton.setChecked(false);
            mTextViewEmergencyNumber.setEnabled(false);
            disableEmergencyContactStatus();
        }

        if (userPreferences.enableSoundAndAlarm()) {
            mSoundAndAlaramButton.setChecked(true);
            mTextViewAlarm.setEnabled(true);
            enableAlarmSwitch();
        } else {
            mSoundAndAlaramButton.setChecked(false);
            mTextViewAlarm.setEnabled(false);
            disableAlarmSwitch();
        }

        mTextViewCall911.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewEmergencyNumber.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewAlarm.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonCancel.setTypeface(FontUtils.getFontFabricGloberBold());

    }

    @OnClick(R.id.btnCancel)
    void cancelDialog(){
        this.dismiss();
    }

    @OnClick({R.id.call911SwitchButton , R.id.emergencyNumberSwitchButton, R.id.soundAndLightAlarmSwitchButton})
    void onClick(SwitchCompat switchButton){
        switch (switchButton.getId()) {

            case R.id.call911SwitchButton:
                if (userPreferences.enableTap()) {
                    userPreferences.setEnableTap(false);
                    mCall911SwitchButton.setChecked(false);
                    disable911Status();
                } else {
                    userPreferences.setEnableTap(true);
                    mCall911SwitchButton.setChecked(true);
                    enableCall911Status();
                }
                break;

            case R.id.emergencyNumberSwitchButton:
                if (userPreferences.enableEmergencyContact()) {
                    userPreferences.setEnableEmergencyContact(false);
                    mEmergencyContactButton.setChecked(false);
                    disableEmergencyContactStatus();
                } else {
                    userPreferences.setEnableEmergencyContact(true);
                    mEmergencyContactButton.setChecked(true);
                    enableEmergencyContactStatus();
                }

                break;

            case R.id.soundAndLightAlarmSwitchButton:
                if (userPreferences.enableSoundAndAlarm()) {
                    userPreferences.setEnableSoundAndAlarm(false);
                    mSoundAndAlaramButton.setChecked(false);
                    disableAlarmSwitch();
                } else {
                    userPreferences.setEnableSoundAndAlarm(true);
                    mSoundAndAlaramButton.setChecked(true);
                    enableAlarmSwitch();
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
            mItemClickListener.setSelectedItemToTextView(getContext().getString(R.string.contacts));
        } else if(mSoundAndAlaramButton.isChecked()){
            mItemClickListener.setSelectedItemToTextView(getContext().getString(R.string.title_Alert));
        } else {
            mItemClickListener.setSelectedItemToTextView(null);
        }
    }

    private void enableCall911Status(){
        mImageCall911.setImageResource(R.drawable.icon_police_red);
        mTextViewCall911.setTextColor(Color.parseColor("#4A4A4A"));
    }

    private void disable911Status(){
        mImageCall911.setImageResource(R.drawable.icon_police_grey);
        mTextViewCall911.setTextColor(Color.parseColor("#D1D1D1"));
    }

    private void disableEmergencyContactStatus(){
        mImageCallEmergencyNumber.setImageResource(R.drawable.icon_emergency_contact_grey);
        mTextViewEmergencyNumber.setTextColor(Color.parseColor("#D1D1D1"));
    }

    private void enableEmergencyContactStatus(){
        mImageCallEmergencyNumber.setImageResource(R.drawable.icon_emergency_contact_red);
        mTextViewEmergencyNumber.setTextColor(Color.parseColor("#4A4A4A"));
    }

    private void disableAlarmSwitch(){
        mImageAlarm.setImageResource(R.drawable.icon_alarm_grey);
        mTextViewAlarm.setTextColor(Color.parseColor("#D1D1D1"));
    }

    private void enableAlarmSwitch(){
        mImageAlarm.setImageResource(R.drawable.icon_alarm_red);
        mTextViewAlarm.setTextColor(Color.parseColor("#4A4A4A"));
    }
}
