package com.iwitness.androidapp.network;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;


public class NetworkStateReceiver extends BroadcastReceiver {


    static private boolean IS_SHOWING = false;


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {

            }
            else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                /* Condition validation */
                if (IS_SHOWING) return;

                Context currentContext = AppDelegate.getCurrentContext();
                IS_SHOWING = true;
                 try{
                     new AlertDialog
                             .Builder(currentContext)
                             .setTitle("Info")
                             .setMessage(currentContext.getString(R.string.network_not_available))
                             .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int whichButton) {
                                     IS_SHOWING = false;
                                     dialog.dismiss();

                                 }
                             })
                             .show();
                 }
                 catch (Exception e){

                 }

            }
        }
    }
}