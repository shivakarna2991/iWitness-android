
package com.iwitness.androidapp.controllers.adapters;

import android.text.*;
import android.widget.*;

import java.util.regex.*;

public class ZipCodeAdapter implements TextWatcher {

    EditText editText;
    int len = 0;
    int splitPos = 5;

    public ZipCodeAdapter(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void afterTextChanged(Editable s) {
        int pos = editText.getSelectionEnd();
        String original = editText.getText().toString();
        Pattern p = Pattern.compile("[^0-9-]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(original);
        boolean b = m.find();
        boolean isEnd = pos == original.length();
        boolean isChanged = false;
        //replace special characters
        if (b) {
            isChanged = true;
            original = original.replaceAll("[^0-9-]", "");
            pos = pos > original.length() ? original.length() : pos;
        }

        //validate zip code
        boolean isShortZipCode = original.matches("\\d{0,5}");
        boolean isLongZipCode = original.matches("\\d{5}-\\d{1,4}");

        if (!isLongZipCode && !isShortZipCode) {
            isChanged = true;
            original = original.replace("-", "");

            if (original.length() > splitPos) {
                original = String.format("%s-%s",
                        original.substring(0, splitPos),
                        original.substring(splitPos, original.length()));
            }

            if (isEnd) {
                pos = original.length();
            } else {
                pos = pos > original.length() ? original.length() : pos;
            }
        }

        if (isChanged) {
            editText.setText(original);
            editText.setSelection(pos);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        String str = editText.getText().toString();
        len = str.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
