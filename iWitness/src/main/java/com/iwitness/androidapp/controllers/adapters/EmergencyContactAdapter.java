//  Project name: Workspace
//  File name   : EmergencyContactAdapter.java
//
//  Author      : Phuc
//  Created date: 6/7/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.adapters;


import android.content.*;
import android.view.*;
import android.widget.*;
import com.iwitness.androidapp.*;
import com.perpcast.lib.foundation.*;

import java.math.*;
import java.util.*;


public class EmergencyContactAdapter extends ArrayAdapter<FwiJson> {
    public static final String PENDING = "Pending";
    public static final String ACCEPTED = "Accepted";
    public static final String DECLINED = "Declined";


    public EmergencyContactAdapter(Context context, List<FwiJson> values) {
        super(context, R.layout.cell_emergency_contact, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_emergency_contact, parent, false);
        }

        TextView contactName = (TextView) convertView.findViewById(R.id.tvContactName);
        TextView contactStatus = (TextView) convertView.findViewById(R.id.tvContactStatus);

        FwiJson contact = super.getItem(position);
        contactName.setText(String.format("%s %s",
                contact.jsonWithPath("firstName").getString(),
                contact.jsonWithPath("lastName").getString()));
        BigInteger status = contact.jsonWithPath("flags").getInteger();
        contactStatus.setText(getStatus(status));
        return convertView;
    }

    private String getStatus(BigInteger flags) {
        String result = PENDING;
        switch (ContactStatus.fromValue(flags)) {
            case ACCEPTED:
                result = EmergencyContactAdapter.ACCEPTED;
                break;
            case DECLINED:
                result = EmergencyContactAdapter.DECLINED;
                break;
        }
        return result;
    }

//    public FwiJson getContact(int position) {
//        return values.get(position);
//    }
}
