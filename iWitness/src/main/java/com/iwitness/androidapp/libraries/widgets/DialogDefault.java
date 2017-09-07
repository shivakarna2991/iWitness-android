package com.iwitness.androidapp.libraries.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iwitness.androidapp.R;

public class DialogDefault extends Dialog {

    private Context _context;

    public DialogDefault(Context context) {
        super(context);
        _context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add more buttons, if it not exists then we do nothing
//        setEventListener(context, R.id.btnConfirm);
    }

    public void setEvents(View.OnClickListener context) {
        setEventListener(context, R.id.btnNo);
        setEventListener(context, R.id.btnYes);
    }

    public void setEventListener(View.OnClickListener context, int viewId) {
            View view = findViewById(viewId);
            if (view != null) {
                view.setOnClickListener(context);
            }
    }

    public void setText(String message) {
        TextView text = (TextView) findViewById(R.id.message);
        text.setText(message);
    }

    public void setCustomTitle(String title) {
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(title);
    }

    public void setOKButtonText(String value) {
        Button btn = (Button) findViewById(R.id.btnYes);
        btn.setText(value);
    }
}
