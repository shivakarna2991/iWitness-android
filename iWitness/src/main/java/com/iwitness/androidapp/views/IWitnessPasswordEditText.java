package com.iwitness.androidapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.ValidationUtils;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * Created by samadhanmalpure on 2017-07-19.
 */

public class IWitnessPasswordEditText extends AbstractTextInput implements View.OnClickListener {

    private PasswordMode mPasswordMode;
    private ToggleButton mTextHideShowToggle;
    private Runnable mRunnablePostDelayedTask;

    private static final int SHOW_HIDE_TOGGLE_DELAY_IN_MILLI_SECONDS = 250;

    public IWitnessPasswordEditText(Context context) {
        this(context, null);
    }

    public IWitnessPasswordEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IWitnessPasswordEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     *
     */
    public enum PasswordMode {
        SHOW, HIDE
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);

        mTextHideShowToggle = (ToggleButton) findViewById(R.id.text_password_toggle);

        mTextHideShowToggle.setVisibility(VISIBLE);
        mTextHideShowToggle.setOnClickListener(this);
        mTextHideShowToggle.setChecked(false);


        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordStyle);

            typedArray.recycle();
        }

        mEditText.setInputType(mInputType);
        mEditText.setTypeface(mTypeface);
        mEditText.setLongClickable(false);
        mEditText.setTextIsSelectable(false);
        setPasswordMode(PasswordMode.HIDE);

        mEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });


    }



    /**
     *
     * @param passwordMode
     */
    public void setPasswordMode(PasswordMode passwordMode) {
        this.mPasswordMode = passwordMode;
        if (!ValidationUtils.isNull(passwordMode)) {
            switch (passwordMode) {
                case SHOW:
                  // mTextHideShowToggle.setText("HIDE");
                    mTextHideShowToggle.setChecked(false);
                    mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|mInputType);
                    break;
                case HIDE:
                    //mTextHideShowToggle.setText("SHOW");
                    mTextHideShowToggle.setChecked(true);
                    mEditText.setInputType(mInputType);
                    break;
            }
            mEditText.setTypeface(mTypeface);
        }
    }

    @Override
    public void onClick(View view) {
        setPasswordMode(mPasswordMode == PasswordMode.SHOW ? PasswordMode.HIDE: PasswordMode.SHOW);
        mEditText.setSelection(mEditText.length());
    }

    @Override
    public void clearFocus() {
        super.clearFocus();
        setPasswordMode(PasswordMode.HIDE);
    }

    /**
     * Sets the hint text and if it is null or empty, then the defaultValue text will be used.
     *
     * @param hint Hint text for the custom edit text. This hint text will be used for the floating label.
     * @param defaultValue
     */
    @Override
    public void setHint(CharSequence hint, CharSequence defaultValue) {
        defaultValue = getResources().getString(R.string.password);
        super.setHint(hint, "");
    }

    /**
     *
     * @param maxLength
     */
    @Override
    public void setMaxLength(int maxLength) {
        if(maxLength <= 0) {
           // maxLength = getResources().getInteger(R.integer.password_max_length);
        }
        super.setMaxLength(maxLength);
    }

    /**
     *
     * @param charSequence
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, final int start, final int count, final int after) {
        super.beforeTextChanged(charSequence, start, count, after);

        mHandler.removeCallbacks(mRunnablePostDelayedTask);

        mRunnablePostDelayedTask = new Runnable() {
            @Override
            public void run() {
                if (start == 0
                        && count == 1
                        && after == 0) {
                    setPasswordMode(PasswordMode.HIDE);
                }
            }
        };

        mHandler.postDelayed(mRunnablePostDelayedTask, SHOW_HIDE_TOGGLE_DELAY_IN_MILLI_SECONDS);
    }


}
