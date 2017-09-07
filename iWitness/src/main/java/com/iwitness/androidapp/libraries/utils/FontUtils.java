//  Project name: Workspace
//  File name   : FontUtils.java
//
//  Author      : Phuc
//  Created date: 8/12/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.libraries.utils;


import android.content.res.*;
import android.graphics.*;
import com.iwitness.androidapp.*;


public final class FontUtils {

    static private final String FONT_PATH = "fonts/fontfabric_glober_regular.ttf";

    private static final String FONT_GLOBER_BOLD = "fonts/fontfabric_glober_bold.ttf";

    private static final String FONT_GLOBER_REGULAR = "fonts/fontfabric_glober_regular.ttf";

    private static final String FONT_SFUITEXT_BOLD = "fonts/sf_ui_text_bold.ttf";

    private static final String FONT_SFUITEXT_MEDIUM= "fonts/SF-UI-Text-Medium.ttf";

    private static final String FONT_SFUITEXT_REGULAR = "fonts/SF-UI-Text-Regular.ttf";

    static public Typeface getFontAwesome() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_PATH);
    }

    static public Typeface getFontFabricGloberRegular() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_GLOBER_REGULAR);
    }

    static public Typeface getFontFabricGloberBold() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_GLOBER_BOLD);
    }

    static public Typeface getFontSFUITextBold() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_SFUITEXT_BOLD);
    }

    static public Typeface getFontSFUITextMedium() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_SFUITEXT_MEDIUM);
    }

    static public Typeface getFontSFUITextRegular() {
        AssetManager assetManager = AppDelegate.getAppContext().getAssets();
        return Typeface.createFromAsset(assetManager, FONT_SFUITEXT_REGULAR);
    }


}
