package com.iwitness.androidapp.controllers.authentication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwitness.androidapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberAuthenticationActivity extends AppCompatActivity {

    @BindView(R.id.ivLogo)
    ImageView imageViewLogo;

    @BindView(R.id.textViewEnterNumber)
    TextView mTextViewEnterNumber;

    @BindView(R.id.phoneEditText)
    EditText mEditTextEnterPhoneNumber;

    @BindView(R.id.textViewYouWillReceiveTextMessage)
    TextView mTextViewYouWillReceiveTextMessage;

    @BindView(R.id.btnSend)
    Button mButtonSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_authentication);

        ButterKnife.bind(this);

        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(tMgr.getLine1Number() != null){
            mEditTextEnterPhoneNumber.setText(tMgr.getLine1Number());
        }
    }

    @OnClick(R.id.btnSend)
    void onClickOfSendButton(){
        Intent in = new Intent(NumberAuthenticationActivity.this, NumberAuthorizationActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }


}
