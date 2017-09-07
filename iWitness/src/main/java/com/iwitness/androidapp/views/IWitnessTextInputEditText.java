package com.iwitness.androidapp.views;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isNull;

/**
 * Created by samadhanmalpure on 2017-07-19.
 */

public class IWitnessTextInputEditText extends TextInputEditText {

    private boolean mIsCursorSelectionEnabled = true;
    private int mTextSelectionLength;

    public IWitnessTextInputEditText(Context context) {
        super(context);
    }

    public IWitnessTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IWitnessTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCursorSelection(boolean isCursorSelectionEnabled) {
        mIsCursorSelectionEnabled = isCursorSelectionEnabled;
    }

    @Override
    public void setSelection(int index) {
        mTextSelectionLength = index;
        super.setSelection(index);
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        CharSequence text = getText();
        if (!mIsCursorSelectionEnabled
                && !isNull(text)) {
            if (mTextSelectionLength > 0
                    && mTextSelectionLength <= text.length()) {
                setSelection(mTextSelectionLength, mTextSelectionLength);
            } else {
                if (start != text.length() || end != text.length()) {
                    setSelection(text.length(), text.length());
                }
            }
            return;
        }

        super.onSelectionChanged(start, end);
    }
}
