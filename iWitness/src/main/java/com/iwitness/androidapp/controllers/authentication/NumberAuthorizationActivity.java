package com.iwitness.androidapp.controllers.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.iwitness.androidapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isNull;

public class NumberAuthorizationActivity extends AppCompatActivity implements EditText.OnKeyListener{

    @BindView(R.id.edittextAuthorizationField1)
    EditText mEditText1;

    @BindView(R.id.edittextAuthorizationField2)
    EditText mEditText2;

    @BindView(R.id.edittextAuthorizationField3)
    EditText mEditText3;

    @BindView(R.id.edittextAuthorizationField4)
    EditText mEditText4;

    @BindView(R.id.btnSubmit)
    Button mButtonSend;

    private EditText[] mArrEditTextVerifyPin = new EditText[]{};//empty array to avoid nullpointerexception

    private final int MAXLENGTH = 1;

    private int mBackKeyPressCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_authorization);

        ButterKnife.bind(this);

        mArrEditTextVerifyPin = new EditText[]{mEditText1, mEditText2, mEditText3, mEditText4};

        mArrEditTextVerifyPin[0].requestFocus();

        mEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEditText1.getText().toString().length() == MAXLENGTH){
                    mEditText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEditText2.getText().toString().length() == MAXLENGTH){
                    mEditText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEditText3.getText().toString().length() == MAXLENGTH){
                    mEditText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditText1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            onBackKeyPress(mEditText1);
                            break;
                    }
                }
                return false;
            }
        });

        mEditText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            onBackKeyPress(mEditText2);
                            break;
                    }
                }
                return false;
            }
        });

        mEditText3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            onBackKeyPress(mEditText3);
                            break;
                    }
                }
                return false;
            }
        });

        mEditText4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            onBackKeyPress(mEditText4);
                            break;
                    }
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btnSubmit)
    void onSubmitButtonClick(){
        Intent in = new Intent(NumberAuthorizationActivity.this, CreatePassword.class);
        startActivity(in);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    private void onBackKeyPress(EditText editText) {
        if(!isNull(editText)) {

            mBackKeyPressCount++;

            if(editText.getText().toString().trim().length() > 0){
                editText.setText("");
                editText.requestFocus();
            }

            if(mBackKeyPressCount == 2) {

                if(editText.getId() == R.id.edittextAuthorizationField1) {
                    mEditText1.requestFocus();
                    mBackKeyPressCount = 0;
                } else if(editText.getId() == R.id.edittextAuthorizationField2){
                    mEditText1.requestFocus();
                    mBackKeyPressCount = 0;
                }else if(editText.getId() == R.id.edittextAuthorizationField3){
                    mEditText2.requestFocus();
                    mBackKeyPressCount = 0;
                } else if(editText.getId() == R.id.edittextAuthorizationField4) {
                    mEditText3.requestFocus();
                    mBackKeyPressCount = 0;
                }

            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }
}
