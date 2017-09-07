package com.iwitness.androidapp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.iwitness.androidapp.R;

/**
 * Created by samadhanmalpure on 2017-07-18.
 */

public class IWitnessBlueButton extends AppCompatButton {

    public IWitnessBlueButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IWitnessBlueButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IWitnessBlueButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackground(getResources().getDrawable(R.drawable.button_background));
        setTextSize(getResources().getDimension(R.dimen.button_text_size));
        setTextColor(getResources().getColor(R.color.white));
    }

}
