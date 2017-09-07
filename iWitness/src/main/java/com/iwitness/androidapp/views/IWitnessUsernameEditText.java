package com.iwitness.androidapp.views;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;

import com.iwitness.androidapp.R;


/**
 * Created by samadhanmalpure on 2017-07-19.
 */
public class IWitnessUsernameEditText extends AbstractTextInput {

    public IWitnessUsernameEditText(Context context) {
        super(context);
    }

    public IWitnessUsernameEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IWitnessUsernameEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);

        mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    public void setHint(CharSequence hint, CharSequence defaultValue) {
        defaultValue = getResources().getString(R.string.phone_no);
        super.setHint(hint, defaultValue);
    }

    @Override
    public void setText(String text) {
        super.setText(text != null ? text.trim(): "");
    }

    @Override
    public Editable getText() {
        String text = super.getText().toString();

        if(!text.trim().equals(text)) {
            setText(text.trim());
            mEditText.setSelection(getText().length());
        }

        return super.getText();
    }

}
