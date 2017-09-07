package com.iwitness.androidapp.controllers.common;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
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

public class CommonAlertDialog extends Dialog {

    private CommonAlertDialogButtonClickListener mItemClickListener;
    private UserPreferences userPreferences;

    @BindView(R.id.btnSend)
    Button mButtonSend;

    @BindView(R.id.btnCancel)
    Button mButtonCalcel;

    @BindView(R.id.tvStreamingStopped)
    TextView mTextViewStreamingStopped;

    @BindView(R.id.tvRecordingStored)
    TextView mTextViewRecordingIsBeingStored;

    @BindView(R.id.tvSenfToEmergencyContacts)
    TextView mTextViewSendToEmergencyContacts;

    public CommonAlertDialog(Context context, CommonAlertDialogButtonClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    /**
     * This is an interface for initializing the counter to initial amount when user either click on cancel button or clicks outside the dialog. on record video screen.
     */

    public interface CommonAlertDialogButtonClickListener {
        void setCounterToInitialAmount();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_alert_dialog);

        ButterKnife.bind(this);

        init();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);

        userPreferences = UserPreferences.sharedInstance();
    }

    private void init(){
        mButtonSend.setTypeface(FontUtils.getFontSFUITextMedium());
        mButtonCalcel.setTypeface(FontUtils.getFontSFUITextRegular());
        mTextViewStreamingStopped.setTypeface(FontUtils.getFontSFUITextMedium());
        mTextViewRecordingIsBeingStored.setTypeface(FontUtils.getFontSFUITextRegular());
        mTextViewSendToEmergencyContacts.setTypeface(FontUtils.getFontSFUITextRegular());

    }


    @OnClick(R.id.btnCancel)
    void cancelDialog() {
        this.dismiss();
        mItemClickListener.setCounterToInitialAmount();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mItemClickListener.setCounterToInitialAmount();
    }
}
