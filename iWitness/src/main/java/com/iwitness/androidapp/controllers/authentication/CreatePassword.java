package com.iwitness.androidapp.controllers.authentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.views.IWitnessPasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isNull;

public class CreatePassword extends AppCompatActivity {

    @BindView(R.id.btnLogin)
    Button mButtonSend;

    @BindView(R.id.passwordField1)
    IWitnessPasswordEditText mPasswordField;

    @BindView(R.id.passwordField2)
    IWitnessPasswordEditText mConfirmPasswordField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    void onSendButtonClick(){
        if(!isNull(mPasswordField.getText().toString())
                && !isNull(mConfirmPasswordField.getText().toString())
                && !(mPasswordField.getText().toString().trim()).equals(mConfirmPasswordField.getText().toString().trim())){
            Toast.makeText(this,"Password Missmatch",Toast.LENGTH_LONG).show();
        }
    }
}
