//  Project name: Workspace
//  File name   : SlideMenuAdapter.java
//
//  Author      : Phuc
//  Created date: 8/12/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.common.listeners.UpdateSideMenuPosFragmentsListener;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.model.SlideMenuItem;

import java.util.ArrayList;


public class SlideMenuAdapter extends ArrayAdapter<SlideMenuItem> {

    UpdateSideMenuPosFragmentsListener listener;

    private LinearLayout mParentLinearLayout;


    int selectedPos = 0;
    public SlideMenuAdapter(Context context, int resourceId, ArrayList<SlideMenuItem> items) {
        super(context, resourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.slide_menu_item_new, parent, false);
        }
        listener = (UpdateSideMenuPosFragmentsListener) getContext();
         ImageView iconTextView  = (ImageView) convertView.findViewById(R.id.iv_icon);
         TextView titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        mParentLinearLayout = (LinearLayout) convertView.findViewById(R.id.parentLinearLayout);
         titleTextView.setTypeface(FontUtils.getFontFabricGloberBold());

        SlideMenuItem item = super.getItem(position);
        titleTextView.setText(item.getTitle());
        iconTextView.setBackgroundResource(item.getIcon());
        iconTextView.setImageResource(item.getIcon());

        if(selectedPos == position) {

            titleTextView.setTextColor(Color.WHITE);
            mParentLinearLayout.setBackgroundColor(getContext().getResources().getColor(R.color.color_current_selected_item_background_in_drawer_layout));
        }
        else{

            titleTextView.setTextColor(Color.WHITE);
            mParentLinearLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        //iconTextView.setBackgroundResource(menu_icons[position]);
        return convertView;
    }

    public void updatePosition(int position) {
        selectedPos = position;

    }
}
