package com.iwitness.androidapp.controllers.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.model.Recipient;

import java.util.ArrayList;


public class RecipientAdapter extends ArrayAdapter<Recipient> {


    private EditText currentFocus;


    // Class's constructors
    public RecipientAdapter(Context context, int layoutId, ArrayList<Recipient> items) {
        super(context, layoutId, items);
    }


    // Class's public methods
    public EditText getCurrentFocus() {
        return currentFocus;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.cell_recipient, null);
        }

        EditText recipientEditText = (EditText) view.findViewById(R.id.recipientEditText);
        EditText emailEditText = (EditText) view.findViewById(R.id.emailEditText);

        // Init value
        Recipient recipient = this.getItem(position);
        recipientEditText.setText(recipient.getRecipient(), TextView.BufferType.EDITABLE);
        emailEditText.setText(recipient.getEmail(), TextView.BufferType.EDITABLE);

        // Define action listener
        final int positionCopy = position;
        final RecipientAdapter weakThis = this;

        recipientEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Recipient recipient = weakThis.getItem(positionCopy);
                    recipient.setRecipient(((EditText) view).getText().toString());
                }
                else {
                    currentFocus = (EditText) view;
                }
            }
        });
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Recipient recipient = weakThis.getItem(positionCopy);
                    recipient.setEmail(((EditText) view).getText().toString());
                }
                else {
                    currentFocus = (EditText) view;
                }
            }
        });
        return view;
    }
}
