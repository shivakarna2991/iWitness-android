//  Project name: Workspace
//  File name   : MenuItem.java
//
//  Author      : Phuc
//  Created date: 8/12/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.model;


public class SlideMenuItem {

    private int icon;
    private String title;

    public SlideMenuItem() {
    }

    public SlideMenuItem(String title, int icon) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
