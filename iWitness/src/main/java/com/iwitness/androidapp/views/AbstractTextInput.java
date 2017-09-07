package com.iwitness.androidapp.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.ValidationUtils;

import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isCharSequenceEmpty;
import static com.iwitness.androidapp.libraries.utils.ValidationUtils.isNull;


/**
 * Created by samadhanmalpure on 2017-07-19.
 */

public class AbstractTextInput extends LinearLayout implements View.OnKeyListener, TextWatcher{
    protected TextInputLayout mEditTextWrapper;
    protected IWitnessTextInputEditText mEditText;
    protected int mInputType;
    protected int mTextGravity;
    protected String mPlainTextBeforeChange = "";
    protected String mPlainTextAfterChange = "";
    protected boolean mIsBackKeyPressed;
    protected Typeface mTypeface;
    protected int mMaxTextLength;
    protected TextChangeListener mTextChangeListener;
    protected OnIWitnessKeyListener onIWitnessKeyListener;
    protected Handler mHandler;
    protected RelativeLayout mParentRelativeLayout;


    public AbstractTextInput(Context context) {
        this(context, null);
    }

    public AbstractTextInput(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractTextInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     *
     */
    public interface TextChangeListener {
        void onTextChange(AbstractTextInput abstractTextInput, Editable e);
    }

    /**
     *
     */
    public interface OnIWitnessKeyListener{
        void onIWitnessKey(AbstractTextInput abstractTextInput, View view, int i, KeyEvent keyEvent);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr){

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.iwitness_text_input_layout, this, true);

        mHandler = new Handler(Looper.getMainLooper());
        mEditTextWrapper = (TextInputLayout) findViewById(R.id.text_layout_password);
        mEditText = (IWitnessTextInputEditText) findViewById(R.id.edit_text);
        mParentRelativeLayout = (RelativeLayout) findViewById(R.id.parentRelativeLayout);



        mEditText.addTextChangedListener(this);

        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbstractTextInput);

            String hint = typedArray.getString(R.styleable.AbstractTextInput_customHint);
            String customFontSrc = typedArray.getString(R.styleable.AbstractTextInput_customFontSrc);
            int maxLength = typedArray.getInt(R.styleable.AbstractTextInput_customMaxLength, -1);
            int hintTextColor = typedArray.getColor(R.styleable.AbstractTextInput_customTextColorHint, getResources().getColor(R.color.color_hint_text_color));
            int textColor = typedArray.getColor(R.styleable.AbstractTextInput_customTextColor, getResources().getColor(R.color.color_black));

            mInputType = typedArray.getInt(R.styleable.AbstractTextInput_customInputType, InputType.TYPE_CLASS_TEXT);
            mTextGravity = typedArray.getInt(R.styleable.AbstractTextInput_customTextGravity, Gravity.LEFT|Gravity.TOP);
            float textSizeInPixels = typedArray.getDimensionPixelSize(R.styleable.AbstractTextInput_customTextSize, -1);
            boolean isCursorVisible = typedArray.getBoolean(R.styleable.AbstractTextInput_customIsCursorVisible, true);
            mTypeface = FontUtils.getFontAwesome();

            setHint(hint, "");
            mEditText.setTextColor(getResources().getColor(R.color.color_black));
            setBackgroundWithPadding(getResources().getDrawable(R.drawable.circle_edge_edittext));

            mParentRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_edge_edittext));


            setMaxLength(maxLength);

            mEditText.setHintTextColor(hintTextColor);
            mEditText.setTextColor(textColor);
            if(textSizeInPixels != -1) {
                setTextSize(textSizeInPixels);
            }

            mEditText.setGravity(mTextGravity);

            mEditText.setCursorVisible(isCursorVisible);

            typedArray.recycle();
        }

        mEditText.setTypeface(mTypeface);
        mEditText.setOnKeyListener(this);
    }

    private void setTextSize(float textSizeInPixels){
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels);
    }

    protected final void setBackgroundWithPadding(Drawable drawable) {

        int pL = mEditTextWrapper.getPaddingLeft(); // p = padding L= left R = right etc.
        int pT = mEditTextWrapper.getPaddingTop();
        int pR = mEditTextWrapper.getPaddingRight();
        int pB = mEditTextWrapper.getPaddingBottom();

        if(pL == 0) {
            pL = (int) getResources().getDimension(R.dimen.iwitness_password_layout_padding_left);
        }

        if(pR == 0) {
            pR = (int) getResources().getDimension(R.dimen.iwitness_password_layout_padding_right);
        }
        if(pT == 0) {
            pT = (int) getResources().getDimension(R.dimen.iwitness_password_layout_padding_top);
        }
        if(pB == 0) {
            pL = (int) getResources().getDimension(R.dimen.iwitness_password_layout_padding_bottom);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mEditTextWrapper.setBackground(drawable);
        } else {
            mEditTextWrapper.setBackgroundDrawable(drawable);
        }*/


        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mEditTextWrapper.setPadding(pL, pT, pR, pB);
        }
    }

    public void setFocus() {
        mEditText.requestFocus();
        mEditText.setSelection(mEditText.getText().length());
    }

    private InputFilter characterFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            return source;
        }
    };

    public void setMaxLength(int maxLength) {
        this.mMaxTextLength = maxLength;
        if(maxLength > 0) {
            mEditText.setFilters(new InputFilter[]{characterFilter, new InputFilter.LengthFilter(maxLength)});
        }
    }

    /**
     *
     * @param hint Hint text for the custom edit text. This hint text will be used for the floating label.
     */
    protected void setHint(CharSequence hint, CharSequence defaultValue) {

        if(!isCharSequenceEmpty(hint)) {
            setHint(null);
        } else {
            if(isCharSequenceEmpty(defaultValue)) {
                setHint("");
            } else {
                setHint(defaultValue);
            }
        }
    }

    public void setHint(CharSequence hint) {
        mEditTextWrapper.setHint(null);
        mEditTextWrapper.setHintEnabled(false);
    }

    public Editable getText() {
        return SpannableStringBuilder.valueOf(mEditText.getText().toString());
    }

    public String getTextBeforeChange() {
        return mPlainTextBeforeChange;
    }

    public void setText(String text) {
        mEditText.setText(text != null ? text : "");
    }

    public void setInputType(int inputMode) {
        mEditText.setInputType(inputMode);
    }

    public void clear() {
        setText("");
    }

    public void clearFocus() {
        if(ValidationUtils.isTextFieldEmpty(mEditText.getText())) {
            mEditText.clearFocus();
        }
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.mTextChangeListener = textChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int count, int after) {
        mIsBackKeyPressed = (count > after);
        mPlainTextBeforeChange = charSequence.toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mPlainTextAfterChange = editable.toString();
        if (hasTextChangeListener()) {
            mTextChangeListener.onTextChange(this, editable);
        }

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        mIsBackKeyPressed = (keyCode == KeyEvent.KEYCODE_DEL);

        if(hasOnIWitnessKeyListener()) {
            onIWitnessKeyListener.onIWitnessKey(this, view, keyCode, keyEvent);
        }
        return false;
    }

    protected final boolean hasTextChangeListener() {
        return !isNull(mTextChangeListener);
    }

    public void setSelection(int index) {
        mEditText.setSelection(index);
    }

    protected final boolean hasOnIWitnessKeyListener() {
        return !isNull(onIWitnessKeyListener);
    }



    public void setKeyListener(KeyListener keyListener) {
        if(!isNull(keyListener)) {
            mEditText.setKeyListener(keyListener);
        }
    }

    public int getInputType() {
        return mEditText.getInputType();
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public void removeTextChangeListener(TextWatcher textWatcher){
        mEditText.removeTextChangedListener(textWatcher);
    }


}
