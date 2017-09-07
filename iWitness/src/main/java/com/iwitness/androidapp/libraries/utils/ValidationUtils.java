package com.iwitness.androidapp.libraries.utils;


import android.text.Editable;

/**
 * Created by samadhanmalpure on 2017-07-18.
 */
public class ValidationUtils {

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isStringEmpty(String value) {
        boolean result = false;

        if (isNull(value) || value.isEmpty()) {
            return true;
        }

        return result;
    }

    public static boolean isCharSequenceEmpty(CharSequence charSequence) {
        boolean result = false;
        if(isNull(charSequence) || charSequence.length() == 0) {
            result = true;
        }

        return result;
    }

    public static boolean isTextFieldEmpty(Editable editable) {
        return isNull(editable) || isStringEmpty(editable.toString());
    }



}
