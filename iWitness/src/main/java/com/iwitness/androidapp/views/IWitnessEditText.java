package com.iwitness.androidapp.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by samadhanmalpure on 2017-07-19.
 */

public class IWitnessEditText extends AbstractTextInput {

    public IWitnessEditText(Context context) {
        this(context,null);
    }

    public IWitnessEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public IWitnessEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCursorSelection(boolean isCursorSelectionEnabled) {
        mEditText.setCursorSelection(isCursorSelectionEnabled);
    }
}
