//  Project name: Workspace
//  File name   : AppDelegate.java
//
//  Author      : Phuc
//  Created date: 6/4/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.libraries.cache.FwiCacheHandler;
import com.perpcast.lib.key.FwiKeystore;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AppDelegate extends Application implements Application.ActivityLifecycleCallbacks {
    static public final String kTag = "iWitness";
    static private Context _appContext = null;
    static private Context currentContext = null;
    static private AppDelegate _sharedInstance = null;
    static private ThreadPoolExecutor _operationQueue = null;
    static private String _regId = null;
    static private FwiCacheHandler _handler = null;
    static private final String APP_VERSION = "appVersion";
    static private AlertDialog.Builder alertBuilder;
    static private AlertDialog alertDialog;
    public static boolean isLoginScreen = false;

    // <editor-fold defaultstate="collapsed" desc="Application's lifecycle">
    @Override
    public void onCreate() {
        super.onCreate();
        _sharedInstance = this;

        // Define application's context
        _appContext = this.getApplicationContext();

        // Initialize keystore
        FwiKeystore.initialize(_appContext);

        // Initialize Crittercism
//        Crittercism.initialize(getApplicationContext(), "53b3c55e83fb794597000002");
        MultiDex.install(this);
        // Define activities monitor
        this.registerActivityLifecycleCallbacks(this);
    }

    public void onTerminate() {
        super.onTerminate();
    }

    // <editor-fold defaultstate="collapsed" desc="Class's static properties">
    static public AppDelegate sharedInstance() {
        return _sharedInstance;
    }

    static public Context getAppContext() {
        return _appContext;
    }
    static public Context getCurrentContext() {
        return currentContext;
    }

    static public String getDeviceToken() {
        return _regId;
    }

    static public synchronized ThreadPoolExecutor getThreadPool() {
        if (_operationQueue != null) return _operationQueue;

        _operationQueue = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        return _operationQueue;
    }

    // <editor-fold defaultstate="collapsed" desc="ActivityLifecycleCallbacks's members">
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstance) {
        Log.i(kTag, String.format("%s: created", activity.getClass().getName()));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentContext = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(kTag, String.format("%s: destroyed", activity.getClass().getName()));
    }
    // </editor-fold>


    // Class's public methods
    public boolean isServiceRunning(Class<?> service) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo serviceActivity : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service == serviceActivity.service.getClass()) {
                return true;
            }
        }
        return false;
    }


    // Class's private methods
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showAlertWithTitle(Context context,String button1, String button2, String title, String Message, final String text)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(button1, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(button2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
//        builder.setCancelable(false);
        builder.create().show();
    }

    public static void showAlertWithLogOut(final Context context, String button1, String button2, String title, String Message, final String text)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(button1, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(context, LoginController.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(button2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
//        builder.setCancelable(false);
        builder.create().show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    public void showAlertDialog(String strTitle, String strMessage,
//                                String firstBtnName, String secondBtnName, String from) {
//        runOnUiThread(new RunshowCustomDialogs(strTitle, strMessage,
//                firstBtnName, secondBtnName, from));
//    }
//
//    class RunshowCustomDialogs implements Runnable {
//        private String strTitle;// Title of the dialog
//        private String strMessage;// Message to be shown in dialog
//        private String firstBtnName;
//        private String secondBtnName;
//        private String from;
//
//        public RunshowCustomDialogs(String strTitle, String strMessage,
//                                    String firstBtnName, String secondBtnName, String from) {
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//
//            this.strTitle = strTitle;
//            this.strMessage = strMessage;
//            this.firstBtnName = firstBtnName;
//            this.secondBtnName = secondBtnName;
//            if (from != null)
//                this.from = from;
//            else
//                this.from = "";
//        }
//
//        @SuppressLint("NewApi")
//        @Override
//        public void run() {
//            if (alertDialog != null && alertDialog.isShowing())
//                alertDialog.dismiss();
//
//            alertBuilder = new AlertDialog.Builder(BaseActivity.this);
//            alertBuilder.setCancelable(false);
//
//            final LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
//                    .inflate(R.layout.common_dailog, null);
//
//            LinearLayout llAlertDialogMain = (LinearLayout) linearLayout
//                    .findViewById(R.id.llAlertDialogMain);
//            TextView dialogtvTitle = (TextView) linearLayout
//                    .findViewById(R.id.tvTitle);
//            TextView tvMessage = (TextView) linearLayout
//                    .findViewById(R.id.tvMessage);
//            Button btnYes = (Button) linearLayout.findViewById(R.id.btnYes);
//            Button btn_ok = (Button) linearLayout.findViewById(R.id.btn_ok);
//            Button btnNo = (Button) linearLayout.findViewById(R.id.btnNo);
//
//            View viewLine = (View) linearLayout.findViewById(R.id.viewLine);
//            LinearLayout llTitle = (LinearLayout) linearLayout
//                    .findViewById(R.id.llTitle);
//            llAlertDialogMain.setLayoutParams(new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            btnYes.setTypeface(Typeface.DEFAULT_BOLD);
//            btnNo.setTypeface(Typeface.DEFAULT_BOLD);
//            btnNo.setVisibility(View.GONE);
//            if (strTitle != null && !strTitle.equalsIgnoreCase("")) {
//                llTitle.setVisibility(View.VISIBLE);
//                viewLine.setVisibility(View.VISIBLE);
//                dialogtvTitle.setText("" + strTitle);
//            }
//            if (strTitle != null && strTitle.equalsIgnoreCase("Image/Video")) {
//                btnNo.setVisibility(View.VISIBLE);
//            }
//
//            tvMessage.setText("" + strMessage);
//
//            btnYes.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    alertDialog.cancel();
//                }
//            });
//            btn_ok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.cancel();
//                    onButtonYesClick(from);
//                }
//            });
//
//            btnNo.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    onButtonNoClick(from);
//                    alertDialog.cancel();
//                }
//            });
//
//
//            try {
//                alertDialog = alertBuilder.create();
//                alertDialog.setView(linearLayout, 0, 0, 0, 0);
//                alertDialog.setInverseBackgroundForced(true);
//                alertDialog.show();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void onButtonYesClick(String from) {
//    }
//
//    public void onButtonNoClick(String from) {
//
//    }
}
